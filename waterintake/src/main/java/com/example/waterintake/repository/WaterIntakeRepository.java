package com.example.waterintake.repository;

import com.example.waterintake.model.WaterIntake;
import com.example.waterintake.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaterIntakeRepository extends JpaRepository<WaterIntake, Long> {

    // For viewing daily intake with pagination
    Page<WaterIntake> findByUserOrderByLoggedAtDesc(User user, Pageable pageable);

    // For "one entry per day" check
    Optional<WaterIntake> findByUserAndEntryDate(User user, LocalDate entryDate);

    // For difference between two dates
    @Query("SELECT SUM(wi.quantityMl) FROM WaterIntake wi WHERE wi.user = :user AND wi.entryDate BETWEEN :startDate AND :endDate")
    Integer sumQuantityMlByUserAndEntryDateBetween(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // For checking if the user owns the intake before editing/deleting
    Optional<WaterIntake> findByIdAndUser(Long id, User user);
}