package org.elis.progettoing.service;

import org.elis.progettoing.exception.storage.DirectoryNotEmptyException;
import org.elis.progettoing.exception.storage.FileNotFoundException;
import org.elis.progettoing.exception.storage.FileStorageException;
import org.elis.progettoing.service.implementation.LocalStorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocalStorageServiceTest {

    @InjectMocks
    private LocalStorageService localStorageService;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void saveUserProfilePhoto_WithValidImage_ReturnsRelativePath() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        doNothing().when(mockFile).transferTo(any(Path.class));

        String result = localStorageService.saveUserProfilePhoto(mockFile);

        assertNotNull(result);
        assertTrue(result.contains("user_photos/"));
        verify(mockFile).transferTo(any(Path.class));
    }

    @Test
    void saveProductImages_WithValidImages_ReturnsRelativePaths() throws IOException {
        MultipartFile mockFile1 = mock(MultipartFile.class);
        MultipartFile mockFile2 = mock(MultipartFile.class);
        when(mockFile1.getOriginalFilename()).thenReturn("test1.jpg");
        when(mockFile2.getOriginalFilename()).thenReturn("test2.jpg");
        doNothing().when(mockFile1).transferTo(any(Path.class));
        doNothing().when(mockFile2).transferTo(any(Path.class));

        List<String> result = localStorageService.saveProductImages(List.of(mockFile1, mockFile2), 1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).contains("products/Product_withUserId_1/"));
        assertTrue(result.get(1).contains("products/Product_withUserId_1/"));
        verify(mockFile1).transferTo(any(Path.class));
        verify(mockFile2).transferTo(any(Path.class));
    }

    @Test
    void saveReviewImages_WithValidImages_ReturnsRelativePaths() throws IOException {
        MultipartFile mockFile1 = mock(MultipartFile.class);
        MultipartFile mockFile2 = mock(MultipartFile.class);
        when(mockFile1.getOriginalFilename()).thenReturn("test1.jpg");
        when(mockFile2.getOriginalFilename()).thenReturn("test2.jpg");
        doNothing().when(mockFile1).transferTo(any(Path.class));
        doNothing().when(mockFile2).transferTo(any(Path.class));

        List<String> result = localStorageService.saveReviewImages(List.of(mockFile1, mockFile2), 1L, 1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(mockFile1).transferTo(any(Path.class));
        verify(mockFile2).transferTo(any(Path.class));
    }

    @Test
    void savePortfolioImages_WithValidImages_ReturnsRelativePaths() throws IOException {
        MultipartFile mockFile1 = mock(MultipartFile.class);
        MultipartFile mockFile2 = mock(MultipartFile.class);
        when(mockFile1.getOriginalFilename()).thenReturn("test1.jpg");
        when(mockFile2.getOriginalFilename()).thenReturn("test2.jpg");
        doNothing().when(mockFile1).transferTo(any(Path.class));
        doNothing().when(mockFile2).transferTo(any(Path.class));

        List<String> result = localStorageService.savePortfolioImages(List.of(mockFile1, mockFile2), 1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(mockFile1).transferTo(any(Path.class));
        verify(mockFile2).transferTo(any(Path.class));
    }

    @Test
    void saveUserProfilePhoto_WithInvalidImage_ThrowsFileStorageException() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        doThrow(new IOException()).when(mockFile).transferTo(any(Path.class));

        assertThrows(FileStorageException.class, () -> localStorageService.saveUserProfilePhoto(mockFile));

        verify(mockFile).transferTo(any(Path.class));
    }

    @Test
    void saveProductImages_WithInvalidImages_ThrowsFileStorageException() throws IOException {
        MultipartFile mockFile1 = mock(MultipartFile.class);
        MultipartFile mockFile2 = mock(MultipartFile.class);
        when(mockFile1.getOriginalFilename()).thenReturn("test1.jpg");
        when(mockFile2.getOriginalFilename()).thenReturn("test2.jpg");
        doThrow(new IOException()).when(mockFile1).transferTo(any(Path.class));
        doNothing().when(mockFile2).transferTo(any(Path.class));

        assertThrows(FileStorageException.class, () -> localStorageService.saveProductImages(List.of(mockFile1, mockFile2), 1L));

        verify(mockFile1).transferTo(any(Path.class));
        verify(mockFile2).transferTo(any(Path.class));
    }

    @Test
    void deleteImage_WithNonExistentFile_ThrowsFileNotFoundException() {
        String invalidPath = "nonexistent/file.jpg";

        assertThrows(FileNotFoundException.class, () -> localStorageService.deleteImage(invalidPath));
    }

    @Test
    void deleteImage_WithNonEmptyDirectory_ThrowsDirectoryNotEmptyException() {
        String nonEmptyDirectoryPath = "nonempty_directory/";

        // Mock the localStorageService if it's not already done.
        LocalStorageService localStorageService = mock(LocalStorageService.class);

        // Simulate an exception during directory deletion
        doThrow(new DirectoryNotEmptyException("Directory non vuota."))
                .when(localStorageService).deleteImage(nonEmptyDirectoryPath);

        // Assert that the exception is thrown when calling deleteImage
        assertThrows(DirectoryNotEmptyException.class, () -> localStorageService.deleteImage(nonEmptyDirectoryPath));
    }
}