package org.elis.progettoing.service.implementation;

import jakarta.annotation.PreDestroy;
import org.elis.progettoing.exception.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class LocalStorageService {

    private static final String BASE_DIRECTORY = "storage/images/";
    private static final String MACRO_FOLDER_PRODUCT = "products/";
    private static final String MACRO_FOLDER_USER_PHOTO = "user_photos/";
    private static final String MACRO_FOLDER_REVIEW_PHOTO = "review_photos/";
    private static final String MACRO_FOLDER_PORTFOLIO = "portfolio/";

    private final ThreadPoolExecutor executorService;

    /**
     * Constructor for initializing the local storage service.
     * Creates the necessary base directories and configures the thread pool.
     * @throws StorageInitializationException If an error occurs during storage directory initialization.
     */
    public LocalStorageService() {
        try {
            createDirectory(BASE_DIRECTORY + MACRO_FOLDER_PRODUCT);
            createDirectory(BASE_DIRECTORY + MACRO_FOLDER_USER_PHOTO);
            createDirectory(BASE_DIRECTORY + MACRO_FOLDER_REVIEW_PHOTO);
            createDirectory(BASE_DIRECTORY + MACRO_FOLDER_PORTFOLIO);
        } catch (StorageInitializationException e) {
            throw new StorageInitializationException("Error during storage directory initialization.", e);
        }

        this.executorService = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * Gracefully shuts down the thread pool on application shutdown.
     */
    @PreDestroy
    public void onDestroy() {
        shutdown();
    }

    /**
     * Shuts down the thread pool executor.
     * Waits for up to 60 seconds for the threads to finish their tasks.
     * If the threads do not finish in time, forcefully shuts them down.
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Saves a user profile photo.
     *
     * @param imageFile The image file to save.
     * @return The relative path to the saved image.
     */
    public String saveUserProfilePhoto(MultipartFile imageFile) {
        return saveImage(imageFile, MACRO_FOLDER_USER_PHOTO);
    }

    /**
     * Saves product images for a specific user.
     *
     * @param imageFiles List of image files to save.
     * @param userId     ID of the user.
     * @return List of relative paths to the saved images.
     */
    public List<String> saveProductImages(List<MultipartFile> imageFiles, long userId) {
        String dynamicFolder = MACRO_FOLDER_PRODUCT + "Product_withUserId_" + userId + "/";
        createDirectory(BASE_DIRECTORY + dynamicFolder);
        return saveImagesInParallel(imageFiles, dynamicFolder);
    }

    /**
     * Saves review images for a specific product and review.
     *
     * @param imageFiles List of image files to save.
     * @param productId  ID of the product.
     * @param reviewId   ID of the review.
     * @return List of relative paths to the saved images.
     */
    public List<String> saveReviewImages(List<MultipartFile> imageFiles, long productId, long reviewId) {
        String dynamicFolder = MACRO_FOLDER_REVIEW_PHOTO + "Product_" + productId + "/Review_" + reviewId + "/";
        createDirectory(BASE_DIRECTORY + dynamicFolder);
        return saveImagesInParallel(imageFiles, dynamicFolder);
    }

    /**
     * Saves portfolio images for a specific user.
     *
     * @param imageFiles List of image files to save.
     * @param userId     ID of the user.
     * @return List of relative paths to the saved images.
     */
    public List<String> savePortfolioImages(List<MultipartFile> imageFiles, long userId) {
        String dynamicFolder = MACRO_FOLDER_PORTFOLIO + "Portfolio_WithUserId_" + userId + "/";
        createDirectory(BASE_DIRECTORY + dynamicFolder);
        return saveImagesInParallel(imageFiles, dynamicFolder);
    }

    /**
     * Deletes a single image by its relative path.
     *
     * @param relativePath The relative path of the image to delete.
     * @throws FileNotFoundException If the file does not exist and cannot be deleted.
     * @throws DirectoryNotEmptyException If the directory is not empty and cannot be deleted.
     * @throws FileDeletionException If an error occurs during file deletion.
     */
    public void deleteImage(String relativePath) {
        try {
            Path filePath = Paths.get(BASE_DIRECTORY + relativePath);
            Files.delete(filePath);
        } catch (NoSuchFileException e) {
            throw new FileNotFoundException("The file does not exist and cannot be deleted: " + relativePath);
        } catch (DirectoryNotEmptyException e) {
            throw new DirectoryNotEmptyException("The directory is not empty and cannot be deleted: " + relativePath);
        } catch (IOException e) {
            throw new FileDeletionException("Error occurred while deleting the file: " + relativePath);
        }
    }

    /**
     * Deletes multiple images by their relative paths.
     *
     * @param relativePaths List of relative paths of the images to delete.
     */
    public void deleteImages(List<String> relativePaths) {
        for (String relativePath : relativePaths) {
            deleteImage(relativePath);
        }
    }

    /**
     * Updates a user's profile photo.
     *
     * @param newPhoto     The new photo file to save.
     * @param oldPhotoPath The relative path of the old photo to delete.
     * @return The relative path to the newly saved photo.
     */
    public String updateUserProfilePhoto(MultipartFile newPhoto, String oldPhotoPath) {
        if (oldPhotoPath != null && !oldPhotoPath.isEmpty()) {
            deleteImage(oldPhotoPath);
        }
        return saveUserProfilePhoto(newPhoto);
    }

    /**
     * Saves a single image to the specified subdirectory.
     *
     * @param imageFile   The image file to save.
     * @param subDirectory The subdirectory where the image will be saved.
     * @throws FileStorageException If an error occurs during file storage.
     * @return The relative path to the saved image.
     */
    private String saveImage(MultipartFile imageFile, String subDirectory) {
        try {
            String directoryPath = BASE_DIRECTORY + subDirectory;
            Path directory = Paths.get(directoryPath);

            String uniqueFileName = generateUniqueFileName(imageFile.getOriginalFilename());
            Path filePath = directory.resolve(uniqueFileName);

            imageFile.transferTo(filePath);

            return subDirectory + uniqueFileName;
        } catch (IOException e) {
            throw new FileStorageException("Errore durante il salvataggio dell'immagine nella directory: " + subDirectory);
        }
    }

    /**
     * Saves multiple images in parallel to the specified subdirectory.
     *
     * @param imageFiles  List of image files to save.
     * @param subDirectory The subdirectory where the images will be saved.
     * @throws FileStorageException If an error occurs during file storage.
     * @return List of relative paths to the saved images.
     */
    private List<String> saveImagesInParallel(List<MultipartFile> imageFiles, String subDirectory) {
        List<Future<String>> futures = new ArrayList<>();

        for (MultipartFile imageFile : imageFiles) {
            futures.add(executorService.submit(() -> saveImage(imageFile, subDirectory)));
        }

        List<String> filePaths = new ArrayList<>();
        for (Future<String> future : futures) {
            try {
                filePaths.add(future.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new FileStorageException("Salvataggio interrotto durante l'esecuzione parallela.");
            } catch (ExecutionException e) {
                throw new FileStorageException("Errore durante il salvataggio parallelo dei file.");
            }
        }

        return filePaths;
    }

    /**
     * Generates a unique file name based on the original file name.
     *
     * @param originalFilename The original file name.
     * @throws FileNameGenerationException If an error occurs during file name generation.
     * @return The unique file name.
     */
    private String generateUniqueFileName(String originalFilename) {
        try {
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String timestamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
            String uniqueID = UUID.randomUUID().toString();

            return timestamp + "_" + uniqueID + extension;
        } catch (Exception e) {
            throw new FileNameGenerationException("Errore durante la generazione del nome file unico.", e);
        }
    }

    /**
     * Creates a directory at the specified path.
     *
     * @param path The path of the directory to create.
     * @throws StorageInitializationException If an error occurs during directory creation.
     * @throws StorageInitializationException If a file with the same name already exists but is not a directory.
     * @throws StorageInitializationException If an error occurs during directory creation.
     */
    private void createDirectory(String path) {
        File directory = new File(path);

        // Controlla se la directory esiste già
        if (directory.exists()) {
            if (directory.isDirectory()) {
                // La directory esiste già e va bene così
                return;
            } else {
                // Esiste un file con lo stesso nome, quindi lancia un'eccezione
                throw new StorageInitializationException("Un file con lo stesso nome esiste già ma non è una directory: " + path);
            }
        }

        // Prova a creare la directory
        try {
            if (!directory.mkdirs()) {
                throw new StorageInitializationException("Impossibile creare la directory: " + path);
            }
        } catch (Exception e) {
            throw new StorageInitializationException("Errore durante la creazione della directory: " + path, e);
        }
    }
}
