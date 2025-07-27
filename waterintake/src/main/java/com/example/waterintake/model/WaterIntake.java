package com.example.waterintake.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "water_intakes")
public class WaterIntake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int quantityMl; // Quantity in milliliters

    @Column(nullable = false)
    private LocalDate entryDate; // For daily unique entry check

    @Column(nullable = false)
    private LocalDateTime loggedAt; // Timestamp of when the entry was made

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public WaterIntake() {
        this.loggedAt = LocalDateTime.now();
        this.entryDate = LocalDate.now();
    }

    public WaterIntake(int quantityMl, User user) {
        this.quantityMl = quantityMl;
        this.user = user;
        this.loggedAt = LocalDateTime.now();
        this.entryDate = LocalDate.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantityMl() {
        return quantityMl;
    }

    public void setQuantityMl(int quantityMl) {
        this.quantityMl = quantityMl;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}