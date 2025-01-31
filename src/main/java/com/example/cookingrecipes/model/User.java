package com.example.cookingrecipes.model;

import com.example.cookingrecipes.model.enums.AccountStatus;
import com.example.cookingrecipes.model.enums.Gender;
import com.example.cookingrecipes.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Pattern(regexp = "\\w{1,15}", message = "Username must be up to 15 word characters")
    @NotBlank
    private String username;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[^A-Za-z0-9])(?=\\S+$).{8,}$",
            message = "Password must have at least 8 characters, one digit and one special character")
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;

    @Size(max = 512)
    private String bio;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime modified;
}