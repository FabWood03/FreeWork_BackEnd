package org.elis.progettoing.models;

import jakarta.persistence.*;
import lombok.Data;
import org.elis.progettoing.enumeration.Role;
import org.elis.progettoing.models.auction.Auction;
import org.elis.progettoing.models.auction.AuctionSubscription;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.models.product.PurchasedProduct;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a user of the platform.
 * <p>
 * A user is a person who can access the platform and use its features.
 * </p>
 */
@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "surname", length = 30, nullable = false)
    private String surname;

    @Column(name = "nickname", length = 30, nullable = false, unique = true)
    private String nickname;

    @Column(name = "email", length = 30, unique = true, nullable = false)
    private String email;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "education", length = 750)
    private String education;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "url_user_photo", unique = true)
    private String urlUserPhoto;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @Column(name = "ranking", nullable = false)
    private double ranking = 0.0;

    @Column(name = "based_in", length = 100)
    private String basedIn;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_languages", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "languages")
    private List<String> languages = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
    private List<PurchasedProduct> purchasedProducts = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Auction> auctions = new ArrayList<>();

    @Column(name = "fiscal_code", length = 16)
    private String fiscalCode;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "ticketRequester")
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "winner", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<Auction> winningAuctions = new ArrayList<>();

    @OneToMany(mappedBy = "reportedUser")
    private List<Ticket> reportedTickets = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.PERSIST)
    private List<Offer> offer = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuctionSubscription> subscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skills")
    private List<String> skills = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_portfolio", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "portfolio")
    private List<String> portfolio = new ArrayList<>();

    @Column(name = "bio", length = 1000)
    private String bio;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // Ottiene i ruoli dell'utente autenticato
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() { // Verifica se l'account è scaduto
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() { // Verifica se le credenziali sono scadute
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() { // Verifica se l'account è abilitato
        return active;
    }

    public String getUsername() {
        return email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", birthDate=" + birthDate +
                ", education='" + education + '\'' +
                ", active=" + active +
                ", urlUserPhoto=" + urlUserPhoto +
                ", fiscalCode='" + fiscalCode + '\'' +
                '}';
    }

    /**
     * Default constructor.
     */
    public User() {
    }
}
