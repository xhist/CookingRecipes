package com.example.cookingrecipes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Size(max = 80)
    @NotBlank
    private String name;

    @Size(max = 256)
    private String shortDescription;

    @Positive
    private Integer preparationTime;

    @ElementCollection
    private List<String> ingredients;

    @NotBlank
    @Pattern(regexp = "^(data:|https?://|//).*$", message = "Invalid image URL format")
    private String image;

    @Size(max = 2048)
    private String description;

    @ElementCollection
    private List<String> tags;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime modified;
}