package org.elis.progettoing.utils.customAnnotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.elis.progettoing.dto.request.auction.AuctionRequestDTO;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Validator class for {@link ValidAuctionDates} annotation.
 * This class implements the {@link ConstraintValidator} interface to validate the start and end dates of an auction
 * specified in an {@link AuctionRequestDTO}. It ensures that the dates are valid according to the following rules:
 * <ul>
 *     <li>Both start and end dates must be present.</li>
 *     <li>Start and end dates must not be the same.</li>
 *     <li>The start date must not be in the past.</li>
 *     <li>The end date must be after the start date.</li>
 *  <li>The end date must not be in the past.</li>
 *     <li>The duration of the auction must be at least 24 hours.</li>
 * </ul>
 */
public class AuctionDateValidator implements ConstraintValidator<ValidAuctionDates, AuctionRequestDTO> {

    /**
     * Validates the given {@link AuctionRequestDTO} object.
     *
     * @param dto     The object to validate.
     * @param context The context in which the constraint is evaluated.
     * @return {@code true} if the object is valid, {@code false} otherwise.
     */
    @Override
    public boolean isValid(AuctionRequestDTO dto, ConstraintValidatorContext context) {
        LocalDateTime startAuctionDate = dto.getStartAuctionDate();
        LocalDateTime endAuctionDate = dto.getEndAuctionDate();

        // Disable default constraint violation message to provide custom messages.
        context.disableDefaultConstraintViolation();

        // Check if both dates are present.
        if (startAuctionDate == null || endAuctionDate == null) {
            context.buildConstraintViolationWithTemplate("Le date di inizio e fine dell'asta sono obbligatorie.")
                    .addConstraintViolation();
            return false;
        }

        // Check if start and end dates are the same.
        if (startAuctionDate.isEqual(endAuctionDate)) {
            context.buildConstraintViolationWithTemplate("La data di inizio e fine non possono coincidere.")
                    .addConstraintViolation();
            return false;
        }

        // Check if the start date is in the past.
        if (startAuctionDate.isBefore(LocalDateTime.now())) {
            context.buildConstraintViolationWithTemplate("La data di inizio dell'asta non può essere nel passato.")
                    .addConstraintViolation();
            return false;
        }

        // Check if the end date is before the start date.
        if (endAuctionDate.isBefore(startAuctionDate)) {
            context.buildConstraintViolationWithTemplate("La data di fine deve essere successiva alla data di inizio.")
                    .addConstraintViolation();
            return false;
        }

        //Check if the end date is in the past.
        if (endAuctionDate.isBefore(LocalDateTime.now())) {
            context.buildConstraintViolationWithTemplate("La data di fine deve essere nel futuro.")
                    .addConstraintViolation();
            return false;
        }

        // Check if the duration of the auction is at least 24 hours.
        if (Duration.between(startAuctionDate, endAuctionDate).toHours() < 24) {
            context.buildConstraintViolationWithTemplate("La durata dell'asta deve essere di almeno 24 ore.")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}