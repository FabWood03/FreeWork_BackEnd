package org.elis.progettoing.enumeration;

/**
 * Enumeration representing the different statuses of an order.
 * <p>
 * The possible states are:
 * <ul>
 *     <li><strong>PENDING</strong> - The order is waiting to be processed.</li>
 *     <li><strong>IN_PROGRESS</strong> - The order is currently being processed.</li>
 *     <li><strong>DELIVERED</strong> - The order has been delivered to the customer.</li>
 *     <li><strong>REFUSED</strong> - The order has been refused.</li>
 *     <li><strong>LATE_DELIVERY</strong> - The order is being delivered later than expected.</li>
 * </ul>
 * </p>
 */
public enum OrderProductStatus {
    PENDING,

    IN_PROGRESS,

    DELIVERED,

    REFUSED,

    LATE_DELIVERY
}
