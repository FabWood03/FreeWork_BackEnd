package org.elis.progettoing.service;

import org.elis.progettoing.enumeration.PriorityFlag;
import org.elis.progettoing.enumeration.TicketType;
import org.elis.progettoing.models.Review;
import org.elis.progettoing.models.Ticket;
import org.elis.progettoing.models.User;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.models.product.PurchasedProduct;
import org.elis.progettoing.repository.PurchasedProductRepository;
import org.elis.progettoing.repository.ReviewRepository;
import org.elis.progettoing.repository.TicketRepository;
import org.elis.progettoing.service.implementation.TicketPriorityAssigner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


class TicketPriorityAssignerTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PurchasedProductRepository purchasedProductRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private TicketPriorityAssigner ticketPriorityAssigner;

    private Product product;

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        ticket = new Ticket();
        ticket.setType(TicketType.REPORT_PRODUCT);
        ticket.setReportedProduct(product);
        ticket.setCreationDate(LocalDateTime.now().minusDays(1));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testAssignPriorityToTicket_HighPriority() {
        // Setup: Create a high-priority ticket
        Ticket ticket = new Ticket();
        ticket.setType(TicketType.REPORT_PRODUCT);
        ticket.setCreationDate(LocalDateTime.now().minusDays(1)); // Recent ticket
        ticket.setState("In attesa");

        // Mock the reported product
        Product product = new Product();
        User productOwner = new User();
        productOwner.setRanking(0); // Low reputation
        product.setUser(productOwner);
        ticket.setReportedProduct(product);

        // Mock repository responses
        when(ticketRepository.countByReportedProduct(product)).thenReturn(10L); // Many reports
        when(purchasedProductRepository.findByProduct(product)).thenReturn(createMockPurchases()); // Very popular product

        // Act: Call the method
        PriorityFlag result = ticketPriorityAssigner.assignPriorityToTicket(ticket);

        // Assert: Verify the result
        assertEquals(PriorityFlag.HIGH, result);
    }

    // Helper method to create a mock list of purchases
    private List<PurchasedProduct> createMockPurchases() {
        List<PurchasedProduct> purchases = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            purchases.add(new PurchasedProduct());
        }
        return purchases;
    }

    @Test
    void testAssignPriorityToTicket_MediumPriority() {
        // Setup: Create a medium-priority ticket
        Ticket ticket = new Ticket();
        ticket.setType(TicketType.REPORT_USER);
        ticket.setCreationDate(LocalDateTime.now().minusDays(3));
        ticket.setState("In lavorazione");

        User reportedUser = new User();
        reportedUser.setRanking(2);
        ticket.setReportedUser(reportedUser);

        // Mock repository responses
        when(ticketRepository.countByReportedUser(reportedUser)).thenReturn(5L);

        // Act: Call the method
        PriorityFlag result = ticketPriorityAssigner.assignPriorityToTicket(ticket);

        // Assert: Verify the result
        assertEquals(PriorityFlag.MEDIUM, result);
    }

    @Test
    void testAssignPriorityToTicket_LowPriority() {
        // Setup: Create a low-priority ticket
        Ticket ticket = new Ticket();
        ticket.setType(TicketType.SELLER_REQUEST);
        ticket.setCreationDate(LocalDateTime.now().minusDays(7));
        ticket.setState("Completato");

        // Act: Call the method
        PriorityFlag result = ticketPriorityAssigner.assignPriorityToTicket(ticket);

        // Assert: Verify the result
        assertEquals(PriorityFlag.LOW, result);
    }

    @Test
    void testAssignPriorityToTicket_RecentSellerRequest() {
        // Setup: Create a recent seller request ticket
        Ticket ticket = new Ticket();
        ticket.setType(TicketType.SELLER_REQUEST);
        ticket.setCreationDate(LocalDateTime.now().minusHours(12));
        ticket.setState("In attesa");

        // Act: Call the method
        PriorityFlag result = ticketPriorityAssigner.assignPriorityToTicket(ticket);

        // Assert: Verify the result
        assertEquals(PriorityFlag.LOW, result); // Expected priority based on combination score
    }

    @Test
    void testAssignPriorityToTicket_ReportReviews() {
        // Setup: Create a ticket for reporting reviews
        Ticket ticket = new Ticket();
        ticket.setType(TicketType.REPORT_REVIEWS);
        ticket.setCreationDate(LocalDateTime.now().minusDays(2));
        ticket.setState("In attesa");

        Review review = new Review();
        User reviewUser = new User();
        reviewUser.setRanking(1);
        review.setUser(reviewUser);
        ticket.setReportedReview(review);

        // Mock repository responses
        when(ticketRepository.countByReportedReview(review)).thenReturn(3L);
        when(reviewRepository.getAverageRatingForSeller(reviewUser.getId(), LocalDateTime.now().minusDays(30))).thenReturn(2.0);

        // Act: Call the method
        PriorityFlag result = ticketPriorityAssigner.assignPriorityToTicket(ticket);

        // Assert: Verify the result
        assertEquals(PriorityFlag.MEDIUM, result);
    }

    @Test
    public void testAssignPriorityToTicket_ProductPopularityMedium() {
        User reviewUser = new User();
        reviewUser.setRanking(1);
        product.setUser(reviewUser);
        // Simula il comportamento del repository per restituire un numero di ordini tra 51 e 100
        List<PurchasedProduct> purchasedProducts = Arrays.asList(new PurchasedProduct(), new PurchasedProduct(), new PurchasedProduct());
        when(purchasedProductRepository.findByProduct(product)).thenReturn(purchasedProducts);

        // Assegna la priorità al ticket
        PriorityFlag priorityFlag = ticketPriorityAssigner.assignPriorityToTicket(ticket);

        // Verifica che il tipo di priorità sia corretto
        assertEquals(PriorityFlag.MEDIUM, priorityFlag);  // La priorità dipende dal punteggio totale
    }

    @Test
    public void testAssignPriorityToTicket_ProductPopularityLow() {
        ticket.setCreationDate(LocalDateTime.now().minusDays(500));

        User reviewUser = new User();
        reviewUser.setRanking(1);
        product.setUser(reviewUser);
        // Simula il comportamento del repository per restituire un numero di ordini inferiore a 50
        List<PurchasedProduct> purchasedProducts = List.of(new PurchasedProduct());
        when(purchasedProductRepository.findByProduct(product)).thenReturn(purchasedProducts);

        // Assegna la priorità al ticket
        PriorityFlag priorityFlag = ticketPriorityAssigner.assignPriorityToTicket(ticket);

        // Verifica che il tipo di priorità sia corretto
        assertEquals(PriorityFlag.LOW, priorityFlag);  // La priorità dipende dal punteggio totale
    }
}
