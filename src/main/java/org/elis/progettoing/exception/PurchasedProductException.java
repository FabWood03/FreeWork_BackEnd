package org.elis.progettoing.exception;

/**
 * Exception thrown when an error occurs while adding a purchased product to the cart.
 */
public class PurchasedProductException extends RuntimeException {
    public PurchasedProductException(String message) {
        super(message);
    }
}
