package org.elis.progettoing.enumeration;

/**
 * Enumeration representing the different roles in the system.
 * <p>
 * The possible roles are:
 * <ul>
 *     <li><strong>BUYER</strong> - A user who can purchase services, create auctions, etc.</li>
 *     <li><strong>SELLER</strong> - A user who has the same capabilities as a BUYER but can create auctions, put services up for sale, etc.</li>
 *     <li><strong>MODERATOR</strong> - A user who moderates content and interactions within the platform.</li>
 *     <li><strong>ADMIN</strong> - A user with administrative privileges to manage the system and users.</li>
 * </ul>
 * </p>
 */
public enum Role {
    BUYER,

    SELLER,

    MODERATOR,

    ADMIN
}
