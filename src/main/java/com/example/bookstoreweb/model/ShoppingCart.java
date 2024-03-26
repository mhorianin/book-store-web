package com.example.bookstoreweb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Data
@Entity
@SQLDelete(sql = "UPDATE books SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Table(name = "shopping_carts")
public class ShoppingCart {
    @Id
    private Long id;
    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "shoppingCart")
    private Set<CartItem> cartItems = new HashSet<>();
    @Column(name = "is_deleted",
            nullable = false)
    private boolean isDeleted;
}
