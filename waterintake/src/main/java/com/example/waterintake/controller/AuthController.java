package com.example.waterintake.controller;

import com.example.waterintake.model.User;
import com.example.waterintake.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        if (userService.registerUser(user.getUsername(), user.getPassword())) {
            redirectAttributes.addFlashAttribute("message", "Registration successful! Please login.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("message", "Username already exists. Please choose another.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/signup";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("message", "Invalid username or password.");
            model.addAttribute("alertClass", "alert-danger");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out.");
            model.addAttribute("alertClass", "alert-info");
        }
        return "login";
    }
}