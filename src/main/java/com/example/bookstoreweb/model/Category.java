package com.example.bookstoreweb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Entity
@SQLDelete(sql = "UPDATE categories SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@NoArgsConstructor
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name",
            nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "is_deleted",
            nullable = false)
    private boolean isDeleted;

    public Category(Long id) {
        this.id = id;
    }
}
