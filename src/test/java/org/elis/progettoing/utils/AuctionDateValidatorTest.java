package org.elis.progettoing.utils;

import jakarta.validation.ConstraintValidatorContext;
import org.elis.progettoing.dto.request.auction.AuctionRequestDTO;
import org.elis.progettoing.utils.customAnnotation.AuctionDateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuctionDateValidatorTest {

    private AuctionDateValidator auctionDateValidator;

    private ConstraintValidatorContext context;

    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        auctionDateValidator = new AuctionDateValidator();
        context = Mockito.mock(ConstraintValidatorContext.class);
        violationBuilder = Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(violationBuilder);

        Mockito.when(violationBuilder.addConstraintViolation()).thenReturn(context);  // Restituisci il context per il chaining
    }

    @Test
    void testValidAuctionDates() {
        // Arrangiamento: Crea un oggetto AuctionRequestDTO con date valide
        AuctionRequestDTO dto = new AuctionRequestDTO();
        dto.setStartAuctionDate(LocalDateTime.now().plusHours(1)); // Inizio fra 1 ora
        dto.setEndAuctionDate(LocalDateTime.now().plusHours(25));   // Fine fra 25 ore

        // Azione: Verifica la validità
        boolean isValid = auctionDateValidator.isValid(dto, context);

        // Asserzione: La validazione dovrebbe passare
        assertTrue(isValid);
    }

    @Test
    void testStartDateNull() {
        // Arrangiamento: Crea un oggetto con la data di inizio null
        AuctionRequestDTO dto = new AuctionRequestDTO();
        dto.setStartAuctionDate(null);  // Data di inizio null
        dto.setEndAuctionDate(LocalDateTime.now().plusHours(25)); // Fine fra 25 ore

        // Azione: Verifica la validità
        boolean isValid = auctionDateValidator.isValid(dto, context);

        // Asserzione: La validazione dovrebbe fallire
        assertFalse(isValid);
        Mockito.verify(context).buildConstraintViolationWithTemplate("Le date di inizio e fine dell'asta sono obbligatorie.");
        Mockito.verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void testEndDateNull() {
        // Arrangiamento: Crea un oggetto con la data di fine null
        AuctionRequestDTO dto = new AuctionRequestDTO();
        dto.setStartAuctionDate(LocalDateTime.now().plusHours(1));  // Inizio fra 1 ora
        dto.setEndAuctionDate(null);  // Data di fine null

        // Azione: Verifica la validità
        boolean isValid = auctionDateValidator.isValid(dto, context);

        // Asserzione: La validazione dovrebbe fallire
        assertFalse(isValid);
        Mockito.verify(context).buildConstraintViolationWithTemplate("Le date di inizio e fine dell'asta sono obbligatorie.");
        Mockito.verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void testStartEqualsEndDate() {
        // Arrangiamento: Crea un oggetto con data di inizio uguale alla data di fine
        AuctionRequestDTO dto = new AuctionRequestDTO();
        dto.setStartAuctionDate(LocalDateTime.now().plusHours(1));
        dto.setEndAuctionDate(dto.getStartAuctionDate()); // Inizio uguale a fine

        // Azione: Verifica la validità
        boolean isValid = auctionDateValidator.isValid(dto, context);

        // Asserzione: La validazione dovrebbe fallire
        assertFalse(isValid);
        Mockito.verify(context).buildConstraintViolationWithTemplate("La data di inizio e fine non possono coincidere.");
        Mockito.verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void testStartInThePast() {
        // Arrangiamento: Crea un oggetto con la data di inizio nel passato
        AuctionRequestDTO dto = new AuctionRequestDTO();
        dto.setStartAuctionDate(LocalDateTime.now().minusHours(1)); // Inizio nel passato
        dto.setEndAuctionDate(LocalDateTime.now().plusHours(25));  // Fine fra 25 ore

        // Azione: Verifica la validità
        boolean isValid = auctionDateValidator.isValid(dto, context);

        // Asserzione: La validazione dovrebbe fallire
        assertFalse(isValid);
        Mockito.verify(context).buildConstraintViolationWithTemplate("La data di inizio dell'asta non può essere nel passato.");
        Mockito.verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void testEndDateBeforeStartDate() {
        // Arrangiamento: Crea un oggetto con la data di fine prima di quella di inizio
        AuctionRequestDTO dto = new AuctionRequestDTO();
        dto.setStartAuctionDate(LocalDateTime.now().plusDays(1)); // Inizio fra 1 giorno
        dto.setEndAuctionDate(LocalDateTime.now().plusHours(5));  // Fine fra 5 ore (prima della data di inizio)

        // Azione: Verifica la validità
        boolean isValid = auctionDateValidator.isValid(dto, context);

        // Asserzione: La validazione dovrebbe fallire e il messaggio dovrebbe essere "La data di fine deve essere successiva alla data di inizio."
        assertFalse(isValid);
        Mockito.verify(context).buildConstraintViolationWithTemplate("La data di fine deve essere successiva alla data di inizio.");
        Mockito.verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void testDurationLessThan24Hours() {
        // Arrangiamento: Crea un oggetto con durata inferiore a 24 ore
        AuctionRequestDTO dto = new AuctionRequestDTO();
        dto.setStartAuctionDate(LocalDateTime.now().plusHours(1)); // Inizio fra 1 ora
        dto.setEndAuctionDate(LocalDateTime.now().plusHours(23));  // Fine fra 23 ore

        // Azione: Verifica la validità
        boolean isValid = auctionDateValidator.isValid(dto, context);

        // Asserzione: La validazione dovrebbe fallire
        assertFalse(isValid);
        Mockito.verify(context).buildConstraintViolationWithTemplate("La durata dell'asta deve essere di almeno 24 ore.");
        Mockito.verify(violationBuilder).addConstraintViolation();
    }
}
