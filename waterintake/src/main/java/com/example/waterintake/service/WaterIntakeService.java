package com.example.waterintake.service;

import com.example.waterintake.model.User;
import com.example.waterintake.model.WaterIntake;
import com.example.waterintake.repository.WaterIntakeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WaterIntakeService {

    private final WaterIntakeRepository waterIntakeRepository;

    public WaterIntakeService(WaterIntakeRepository waterIntakeRepository) {
        this.waterIntakeRepository = waterIntakeRepository;
    }

    // Add new water intake entry
    public boolean addWaterIntake(WaterIntake waterIntake, User user) {
        // Check if an entry already exists for the current date for this user
        Optional<WaterIntake> existingEntry = waterIntakeRepository.findByUserAndEntryDate(user, LocalDate.now());
        if (existingEntry.isPresent()) {
            return false; // An entry already exists for today
        }
        waterIntake.setUser(user);
        waterIntake.setEntryDate(LocalDate.now()); // Ensure the date is set to current date
        waterIntake.setLoggedAt(LocalDateTime.now()); // Ensure the time is set to current time
        waterIntakeRepository.save(waterIntake);
        return true;
    }

    // Get a single water intake entry by ID and user (for edit/delete)
    public Optional<WaterIntake> getWaterIntakeByIdAndUser(Long id, User user) {
        return waterIntakeRepository.findByIdAndUser(id, user);
    }

    // Get all water intake entries for a user with pagination
    public Page<WaterIntake> getWaterIntakesForUser(User user, int pageNum, int pageSize) {
        // Sort by loggedAt in descending order (most recent first)
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("loggedAt").descending());
        return waterIntakeRepository.findByUserOrderByLoggedAtDesc(user, pageable);
    }

    // Update an existing water intake entry
    public boolean updateWaterIntake(WaterIntake waterIntake, User user) {
        Optional<WaterIntake> existing = waterIntakeRepository.findByIdAndUser(waterIntake.getId(), user);
        if (existing.isPresent()) {
            WaterIntake toUpdate = existing.get();
            toUpdate.setQuantityMl(waterIntake.getQuantityMl());
            // Preserve original entryDate and loggedAt if they are not meant to change on edit
            // For simplicity, we just update quantity here. If you need to change date/time, add fields to form.
            waterIntakeRepository.save(toUpdate);
            return true;
        }
        return false; // Intake not found or does not belong to the user
    }

    // Delete a water intake entry
    public boolean deleteWaterIntake(Long id, User user) {
        Optional<WaterIntake> existing = waterIntakeRepository.findByIdAndUser(id, user);
        if (existing.isPresent()) {
            waterIntakeRepository.delete(existing.get());
            return true;
        }
        return false; 
    }

    // Calculate total water intake between two dates for a user
    public Integer getTotalWaterIntakeBetweenDates(User user, LocalDate startDate, LocalDate endDate) {
        Integer total = waterIntakeRepository.sumQuantityMlByUserAndEntryDateBetween(user, startDate, endDate);
        return total != null ? total : 0; // Return 0 if no entries found
    }
}