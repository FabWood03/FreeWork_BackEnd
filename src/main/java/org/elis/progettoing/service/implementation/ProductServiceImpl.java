package org.elis.progettoing.service.implementation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.elis.progettoing.dto.request.product.ProductRequestDTO;
import org.elis.progettoing.dto.request.product.TagDTO;
import org.elis.progettoing.dto.response.product.ProductDetailsDTO;
import org.elis.progettoing.dto.response.product.ProductSummaryDTO;
import org.elis.progettoing.dto.response.product.TagResponseDTO;
import org.elis.progettoing.enumeration.PackageType;
import org.elis.progettoing.exception.entity.EntityCreationException;
import org.elis.progettoing.exception.entity.EntityDeletionException;
import org.elis.progettoing.exception.entity.EntityNotFoundException;
import org.elis.progettoing.exception.entity.InvalidEntityDataException;
import org.elis.progettoing.mapper.definition.ProductMapper;
import org.elis.progettoing.mapper.definition.ProductPackageMapper;
import org.elis.progettoing.mapper.definition.TagMapper;
import org.elis.progettoing.models.Tag;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.PackageAttribute;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.models.product.ProductPackage;
import org.elis.progettoing.repository.*;
import org.elis.progettoing.service.definition.ProductService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the product management service.
 * Manages the creation, search and deletion of products.
 */
@Service
public class ProductServiceImpl implements ProductService {
    private final TicketRepository ticketRepository;
    private final LocalStorageService localStorageService;
    private final EntityManager entityManager;
    private final ProductRepository productRepository;
    private final PackageAttributeRepository packageAttributeRepository;
    private final ProductPackageRepository productPackageRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final ProductPackageMapper productPackageMapper;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    /**
     * Constructor for ProductServiceImpl.
     *
     * @param productRepository          the repository for accessing products.
     * @param packageAttributeRepository the repository for accessing package attributes.
     * @param productPackageRepository   the repository for accessing product packages.
     * @param userRepository             the repository for accessing users.
     * @param productMapper              the mapper for conversion between entities and DTOs.
     * @param productPackageMapper       the mapper for conversion between entities and DTOs.
     * @param tagRepository              the repository for accessing tags.
     * @param tagMapper                  the mapper for conversion between entities and DTOs.
     * @param ticketRepository           the repository for accessing tickets.
     * @param localStorageService        the service for managing local storage.
     * @param entityManager              the EntityManager for interaction with the database.
     */
    public ProductServiceImpl(ProductRepository productRepository, PackageAttributeRepository packageAttributeRepository, ProductPackageRepository productPackageRepository, UserRepository userRepository, ProductMapper productMapper, ProductPackageMapper productPackageMapper, TagRepository tagRepository, TagMapper tagMapper, TicketRepository ticketRepository, LocalStorageService localStorageService, EntityManager entityManager) {
        this.productRepository = productRepository;
        this.packageAttributeRepository = packageAttributeRepository;
        this.productPackageRepository = productPackageRepository;
        this.userRepository = userRepository;
        this.productMapper = productMapper;
        this.productPackageMapper = productPackageMapper;
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
        this.ticketRepository = ticketRepository;
        this.localStorageService = localStorageService;
        this.entityManager = entityManager;
    }

    /**
     * Create a new product.
     *
     * @param productRequestDTO the product data to create.
     * @return the response containing the details of the created product.
     * @throws EntityCreationException    if an error occurs while creating the product.
     * @throws EntityCreationException    if an error occurs while creating the product package.
     * @throws EntityCreationException    if an error occurs while creating package attributes.
     * @throws InvalidEntityDataException if the specified tag does not exist.
     * @throws EntityNotFoundException    if the authenticated user does not exist.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductDetailsDTO createProduct(ProductRequestDTO productRequestDTO, List<MultipartFile> images) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Product product = productMapper.productRequestDTOToProduct(productRequestDTO);
        product.setPackages(List.of());
        product.setUser(user);
        product.setCreationDate(LocalDate.now());

        List<Tag> tags = tagRepository.findAll();
        List<Tag> validTags = new ArrayList<>();
        for (TagDTO tagDTO : productRequestDTO.getTags()) {
            Optional<Tag> matchingTag = tags
                    .stream()
                    .filter(tag -> tag.getName().equals(tagDTO.getName())).findFirst();
            if (matchingTag.isPresent()) {
                validTags.add(matchingTag.get());
            } else {
                throw new InvalidEntityDataException("tag", "nome", tagDTO.getName(), "Il tag specificato non esiste.");
            }
        }

        List<String> imagesProduct = localStorageService.saveProductImages(images, user.getId());

        product.setTags(validTags);
        product.setUrlProductPhotos(imagesProduct);

        try {
            productRepository.save(product);
        } catch (Exception e) {
            throw new EntityCreationException("prodotto", "email utente", user.getEmail());
        }


        List<ProductPackage> packages = productRequestDTO.getPackages()
                .stream()
                .map(packageRequestDto -> {
                    ProductPackage packageRequest = productPackageMapper.packageRequestDTOToProductPackage(packageRequestDto);

                    packageRequest.setProduct(product);
                    packageRequest.setAttributes(List.of());

                    try {
                        productPackageRepository.save(packageRequest);
                    } catch (Exception e) {
                        throw new EntityCreationException("pacchetti del prodotto", "ID prodotto", product.getId());
                    }


                    List<PackageAttribute> attributes = packageRequestDto.getAttributes().stream().map(attributeRequestDto -> {
                        PackageAttribute attributeRequest = new PackageAttribute();
                        attributeRequest.setKey(attributeRequestDto.getKey());
                        attributeRequest.setValue(attributeRequestDto.getValue());
                        attributeRequest.setProductPackage(packageRequest);

                        try {
                            packageAttributeRepository.save(attributeRequest);
                        } catch (Exception e) {
                            throw new EntityCreationException("attributi del pacchetto", "ID pacchetto", packageRequest.getId());
                        }

                        return attributeRequest;
                    }).toList();

                    packageRequest.setAttributes(attributes);

                    return packageRequest;
                }).toList();

        product.setPackages(packages);

        return productMapper.productToResponseDTO(product);
    }

    /**
     * Returns all products in the system.
     *
     * @return the list of all products present in the system.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductDetailsDTO> findAll() {
        // Retrieve all products
        List<Product> products = productRepository.findAll();

        return productMapper.productsToResponseDTOs(products);
    }

    /**
     * Returns one product per ID.
     *
     * @param id the ID of the product to search for.
     * @return the response containing the details of the product found.
     * @throws EntityNotFoundException if the product with the provided ID does not exist.
     */
    @Override
    @Transactional(readOnly = true)
    public ProductDetailsDTO findWithDetails(long id) {
        // Retrieve the product by ID
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("prodotto", "ID", id));

        return productMapper.productToResponseDTO(product);
    }

    /**
     * Returns all products of a user.
     *
     * @param userId the ID of the user whose products to search for.
     * @return the list of all the user's products.
     * @throws EntityNotFoundException if the user with the provided ID does not exist.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryDTO> findAllSummaryByUserId(long userId) {
        // Verify that the user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("utente", "ID", userId);
        }

        // Retrieve all products of the user
        List<Product> products = productRepository.findAllByUserId(userId);

        if (products == null) {
            products = Collections.emptyList();
        }

        return productMapper.productsToSummaryResponseDTOs(products);
    }

    /**
     * Delete an existing product.
     *
     * @param productId the ID of the product to delete.
     * @return {@code true} if the product was successfully deleted.
     * @throws EntityNotFoundException if the product with the provided ID does not exist.
     * @throws EntityDeletionException if an error occurs while deleting the product.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeProduct(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("prodotto", "ID", productId));

        ticketRepository.unsetProduct(productId);

        Optional.ofNullable(product.getUrlProductPhotos())
                .ifPresent(localStorageService::deleteImages);

        try {
            productRepository.delete(product);
        } catch (Exception e) {
            throw new EntityDeletionException("prodotto", "ID", productId);
        }

        return true;
    }

    /**
     * Returns tags that match the specified filter.
     *
     * @param nameFilter the filter to apply to the tag name.
     * @return the list of tags that match the filter.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TagResponseDTO> getTags(String nameFilter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);

        Root<Tag> root = criteriaQuery.from(Tag.class);

        List<Predicate> predicates = new ArrayList<>();

        if (nameFilter != null && !nameFilter.trim().isEmpty()) {
            Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + nameFilter.trim().toLowerCase() + "%"
            );
            predicates.add(namePredicate);
        }

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        List<Tag> tagList = entityManager.createQuery(criteriaQuery).getResultList();

        return tagMapper.tagToTagListResponseDTO(tagList);
    }

    /**
     * Returns a summary of the products available in the system.
     *
     * @return the list of product summaries.
     */
    @Override
    public List<ProductSummaryDTO> getProductSummary() {
        // Retrieve all products and map them to ProductSummaryDTO
        return productRepository.findAll().stream()
                .map(product -> {
                    ProductPackage basicPackage = product.getPackages().stream()
                            .filter(pkg -> PackageType.BASIC.equals(pkg.getType()))
                            .findFirst()
                            .orElseThrow(() -> new EntityNotFoundException("Pacchetto BASE non trovato per il prodotto", "productId", product.getId()));

                    ProductSummaryDTO dto = productMapper.productToSummaryDTO(product);

                    dto.setStartPrice(basicPackage.getPrice());

                    return dto;
                }).toList();
    }
}
