package com.example.authapp.web;

import com.example.authapp.model.ProductReview;
import com.example.authapp.repo.ProductRepository;
import com.example.authapp.repo.ProductReviewRepository;
import com.example.authapp.repo.UserRepository;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class StoreController {
    private final ProductRepository productRepository;
    private final ProductReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public StoreController(ProductRepository productRepository, ProductReviewRepository reviewRepository,
                           UserRepository userRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/products")
    public String products(@RequestParam(value = "q", defaultValue = "") String q,
                           @RequestParam(value = "category", defaultValue = "") String category,
                           @RequestParam(value = "sort", defaultValue = "popular") String sort,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           Model model) {
        Sort springSort = switch (sort) {
            case "priceAsc" -> Sort.by("price").ascending();
            case "priceDesc" -> Sort.by("price").descending();
            case "new" -> Sort.by("createdAt").descending();
            default -> Sort.by("soldCount").descending();
        };
        var products = productRepository.findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(
                q, category, PageRequest.of(Math.max(0, page), 9, springSort));
        model.addAttribute("products", products);
        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);
        return "product/list";
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Produit non trouvé");
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewRepository.findByProductOrderByCreatedAtDesc(product));
        model.addAttribute("recommendations", productRepository.findTop4ByCategoryAndIdNotOrderBySoldCountDesc(product.getCategory(), product.getId()));
        return "product/detail";
    }

    @PostMapping("/products/{id}/reviews")
    public String review(@PathVariable Long id, @RequestParam("rating") @Min(1) @Max(5) int rating,
                         @RequestParam("comment") @NotBlank String comment,
                         Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            var product = productRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));
            var user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
            
            ProductReview review = new ProductReview();
            review.setProduct(product);
            review.setAuthor(user);
            review.setRating(rating);
            review.setComment(comment);
            reviewRepository.save(review);
            
            var reviews = reviewRepository.findByProductOrderByCreatedAtDesc(product);
            product.setReviewCount(reviews.size());
            product.setRating(reviews.stream().mapToInt(ProductReview::getRating).average().orElse(product.getRating()));
            productRepository.save(product);
            
            redirectAttributes.addFlashAttribute("success", "Votre avis a ete publie.");
            return "redirect:/products/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/products/" + id;
        }
    }

    @GetMapping("/api/search")
    @ResponseBody
    public Object search(@RequestParam(value = "q", defaultValue = "") String q) {
        return productRepository.findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(
                q, "", PageRequest.of(0, 6, Sort.by("soldCount").descending()))
                .map(product -> Map.of("id", product.getId(), "name", product.getName(), "price", product.getPrice()));
    }
}
