package com.example.authapp.web;

import com.example.authapp.repo.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final ProductRepository productRepository;

    public HomeController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/store-home")
    public String home(Model model) {
        model.addAttribute("featured", productRepository.findTop6ByOrderBySoldCountDesc());
        model.addAttribute("newProducts", productRepository.findTop6ByOrderByCreatedAtDesc());
        model.addAttribute("flashDeals", productRepository.findTop6ByFlashDealTrueOrderBySoldCountDesc());
        return "index";
    }
}
