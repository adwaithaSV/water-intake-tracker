package com.example.waterintake.controller;

import com.example.waterintake.model.User;
import com.example.waterintake.model.WaterIntake;
import com.example.waterintake.service.UserService;
import com.example.waterintake.service.WaterIntakeService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class WaterIntakeController {

    private final WaterIntakeService waterIntakeService;
    private final UserService userService;

    public WaterIntakeController(WaterIntakeService waterIntakeService, UserService userService) {
        this.waterIntakeService = waterIntakeService;
        this.userService = userService;
    }

    // Helper to get current logged-in user
    private User getCurrentUser(UserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Logged in user not found in database. This should not happen."));
    }

    // Dashboard - View daily water intake as a list with pagination
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model,
                            @AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam(defaultValue = "1") int pageNum,
                            @RequestParam(defaultValue = "5") int pageSize) {

        User currentUser = getCurrentUser(userDetails);
        Page<WaterIntake> intakePage = waterIntakeService.getWaterIntakesForUser(currentUser, pageNum, pageSize);

        model.addAttribute("waterIntakes", intakePage.getContent());
        model.addAttribute("currentPage", intakePage.getNumber() + 1);
        model.addAttribute("totalPages", intakePage.getTotalPages());
        model.addAttribute("totalItems", intakePage.getTotalElements());
        model.addAttribute("pageSize", pageSize);

        return "dashboard";
    }

    // Show form to add water intake
    @GetMapping("/add-intake")
    public String showAddIntakeForm(Model model) {
        model.addAttribute("waterIntake", new WaterIntake());
        return "add-intake";
    }

    // Add new water intake entry
    @PostMapping("/add-intake")
    public String addIntake(@ModelAttribute("waterIntake") WaterIntake waterIntake,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);

        if (waterIntakeService.addWaterIntake(waterIntake, currentUser)) {
            redirectAttributes.addFlashAttribute("message", "Water intake added successfully for today!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } else {
            redirectAttributes.addFlashAttribute("message", "You have already logged water intake for today. You can edit the existing entry.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/dashboard";
    }

    // Show form to edit water intake
    @GetMapping("/edit-intake/{id}")
    public String showEditIntakeForm(@PathVariable("id") Long id,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);
        Optional<WaterIntake> waterIntakeOptional = waterIntakeService.getWaterIntakeByIdAndUser(id, currentUser);

        if (waterIntakeOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Water intake entry not found or you don't have permission to edit it.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/dashboard";
        }
        model.addAttribute("waterIntake", waterIntakeOptional.get());
        return "edit-intake";
    }

    // Update water intake entry
    @PostMapping("/edit-intake/{id}")
    public String updateIntake(@PathVariable("id") Long id,
                               @ModelAttribute("waterIntake") WaterIntake waterIntake,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);

        // Set the ID for the object coming from the form
        waterIntake.setId(id);

        if (waterIntakeService.updateWaterIntake(waterIntake, currentUser)) {
            redirectAttributes.addFlashAttribute("message", "Water intake updated successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Failed to update water intake. Entry not found or you don't have permission.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/dashboard";
    }

    // Delete water intake entry
    @PostMapping("/delete-intake/{id}")
    public String deleteIntake(@PathVariable("id") Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);

        if (waterIntakeService.deleteWaterIntake(id, currentUser)) {
            redirectAttributes.addFlashAttribute("message", "Water intake entry deleted successfully!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Failed to delete water intake. Entry not found or you don't have permission.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/dashboard";
    }

    // Show form for calculating water intake difference between dates
    @GetMapping("/intake-difference")
    public String showIntakeDifferenceForm(Model model) {
        // You can pre-fill dates if needed or leave them blank
        model.addAttribute("startDate", LocalDate.now().minusDays(7));
        model.addAttribute("endDate", LocalDate.now());
        model.addAttribute("totalIntake", null); // To display result
        return "intake-difference";
    }

    // Calculate water intake difference between dates
    @PostMapping("/intake-difference")
    public String calculateIntakeDifference(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                            @AuthenticationPrincipal UserDetails userDetails,
                                            Model model,
                                            RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(userDetails);

        if (startDate == null || endDate == null) {
            redirectAttributes.addFlashAttribute("message", "Please select both start and end dates.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/intake-difference";
        }
        if (startDate.isAfter(endDate)) {
            redirectAttributes.addFlashAttribute("message", "Start date cannot be after end date.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/intake-difference";
        }

        Integer totalIntake = waterIntakeService.getTotalWaterIntakeBetweenDates(currentUser, startDate, endDate);

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("totalIntake", totalIntake);
        model.addAttribute("message", "Total water intake between " + startDate + " and " + endDate + " is: " + totalIntake + " ml.");
        model.addAttribute("alertClass", "alert-info");

        return "intake-difference";
    }
}