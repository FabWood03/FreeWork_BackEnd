package org.elis.progettoing.enumeration;

/**
 * Enumeration representing the different types of tickets.
 * <p>
 * The possible ticket types are:
 * <ul>
 *     <li><strong>SELLER_REQUEST</strong> - A ticket issued by a buyer to become a seller</li>
 *     <li><strong>REPORT_REVIEWS</strong> - A ticket for reporting inappropriate or false reviews.</li>
 *     <li><strong>REPORT_USER</strong> - A ticket for reporting problematic or violating user behavior.</li>
 *     <li><strong>REPORT_PRODUCT</strong> - A ticket for reporting issues with a product.</li>
 * </ul>
 * </p>
 */
public enum TicketType {
    SELLER_REQUEST,

    REPORT_REVIEWS,

    REPORT_USER,

    REPORT_PRODUCT
}
