package com.example.authapp.web;

import com.example.authapp.model.Product;
import com.example.authapp.repo.ProductRepository;
import com.example.authapp.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class SellerController {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public SellerController(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/seller/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        var seller = userRepository.findByUsername(authentication.getName()).orElseThrow();
        var products = productRepository.findBySellerOrderByCreatedAtDesc(seller);
        model.addAttribute("products", products);
        model.addAttribute("sales", products.stream().mapToInt(Product::getSoldCount).sum());
        model.addAttribute("lowStock", products.stream().filter(product -> product.getStock() < 8).count());
        return "seller/dashboard";
    }

    @PostMapping("/seller/products")
    public String save(@RequestParam(value = "id", required = false) Long id,
                       @RequestParam("name") String name,
                       @RequestParam("category") String category,
                       @RequestParam("price") String priceRaw,
                       @RequestParam("stock") int stock,
                       @RequestParam("imageUrl") String imageUrl,
                       @RequestParam("description") String description,
                       Authentication authentication,
                       RedirectAttributes redirectAttributes) {
        var seller = userRepository.findByUsername(authentication.getName()).orElseThrow();
        Product product = id == null ? new Product() : productRepository.findById(id).orElse(new Product());
        product.setName(name);
        product.setCategory(category);
        try {
            // accept comma or dot as decimal separator
            String normalized = priceRaw == null ? "0" : priceRaw.replaceAll("\\s+",""
            ).replace(',', '.');
            product.setPrice(new java.math.BigDecimal(normalized));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Prix invalide: utilisez un format numerique, ex: 9.99 ou 9,99");
            return "redirect:/seller/dashboard";
        }
        product.setStock(stock);
        product.setImageUrl(imageUrl);
        product.setDescription(description);
        product.setPrimeEligible(true);
        product.setGallery(List.of(imageUrl));
        product.setSeller(seller);
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("success", "Produit enregistre.");
        return "redirect:/seller/dashboard";
    }

    @PostMapping("/seller/products/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Produit supprime.");
        return "redirect:/seller/dashboard";
    }
}
