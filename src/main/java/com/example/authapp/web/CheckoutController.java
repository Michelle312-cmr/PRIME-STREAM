package com.example.authapp.web;

import com.example.authapp.dto.PaymentRequest;
import com.example.authapp.model.CustomerOrder.PaymentStatus;
import com.example.authapp.model.CustomerOrder;
import com.example.authapp.model.OrderItem;
import com.example.authapp.repo.OrderRepository;
import com.example.authapp.repo.UserRepository;
import com.example.authapp.service.CartService;
import com.example.authapp.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CheckoutController {
    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    public CheckoutController(CartService cartService, UserRepository userRepository, OrderRepository orderRepository,
                              PaymentService paymentService) {
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Authentication authentication, Model model) {
        var user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        model.addAttribute("cart", cartService.summary(session, user));
        return "cart/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam("address") String address,
                             @RequestParam("paymentMethod") String paymentMethod,
                             @RequestParam(value = "cardholderName", required = false) String cardholderName,
                             @RequestParam(value = "cardNumber", required = false) String cardNumber,
                             @RequestParam(value = "expiryMonth", required = false) String expiryMonth,
                             @RequestParam(value = "expiryYear", required = false) String expiryYear,
                             @RequestParam(value = "cvv", required = false) String cvv,
                             @RequestParam(value = "paypalEmail", required = false) String paypalEmail,
                             @RequestParam(value = "mobileMoneyPhone", required = false) String mobileMoneyPhone,
                             HttpSession session, Authentication authentication, RedirectAttributes redirectAttributes) {
        var user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        var summary = cartService.summary(session, user);
        if (summary.lines().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Votre panier est vide.");
            return "redirect:/cart";
        }

        var payment = paymentService.charge(
                new PaymentRequest(paymentMethod, cardholderName, cardNumber, expiryMonth, expiryYear, cvv, paypalEmail, mobileMoneyPhone),
                summary.total());
        if (!payment.approved()) {
            redirectAttributes.addFlashAttribute("error", payment.message());
            return "redirect:/checkout";
        }

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(user);
        order.setSubtotal(summary.subtotal());
        order.setTax(summary.tax());
        order.setShipping(summary.shipping());
        order.setDiscount(summary.discount());
        order.setTotal(summary.total());
        order.setAddress(address);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setPaymentStatusMessage(payment.message());
        order.setTransactionId(payment.transactionId());
        order.setCardLast4(payment.last4());
        summary.lines().forEach(line -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(line.product());
            item.setQuantity(line.quantity());
            item.setUnitPrice(line.product().getPrice());
            order.getItems().add(item);
        });
        orderRepository.save(order);
        cartService.clear(session);
        redirectAttributes.addFlashAttribute("success", "Commande #" + order.getId() + " payee. Transaction " + payment.transactionId() + ".");
        return "redirect:/orders/" + order.getId() + "/invoice";
    }

    @GetMapping("/orders/{id}/invoice")
    public String invoice(@PathVariable Long id, Authentication authentication, Model model) {
        var user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        var order = orderRepository.findById(id).orElseThrow();
        if (!order.getCustomer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Facture non autorisee");
        }
        model.addAttribute("order", order);
        model.addAttribute("user", user);
        return "cart/invoice";
    }
}
