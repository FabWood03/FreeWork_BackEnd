package org.elis.progettoing.utils.customAnnotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A custom annotation used for validation of Auction Dates.
 * This annotation is used to ensure that the start and end dates of an auction are valid.
 * It should be applied at the class level.
 *
 * @see AuctionDateValidator
 */
@Constraint(validatedBy = AuctionDateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAuctionDates {

    /**
     * The error message to be used in case of validation failure.
     * Defaults to "Date di inizio e fine non valide" (Italian for "Invalid start and end dates").
     *
     * @return The error message.
     */
    String message() default "Date di inizio e fine non valide";

    /**
     * Specifies validation groups this annotation belongs to.
     *
     * @return An array of Validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * Specifies the payload type of the annotation.
     *
     * @return An array of Payload types.
     */
    Class<? extends Payload>[] payload() default {};
}