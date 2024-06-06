package com.eCommerceSite.WhimsyCrafts.controller;

import com.eCommerceSite.WhimsyCrafts.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/WhimsyCrafts/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/addCart")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<String> addToCart(@RequestParam("productId") String productId,
                                            @RequestParam("quantity") int quantity) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        String response = cartService.addToCart(productId, quantity, userId);
        if (response.equals("Product added to cart successfully.")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(400).body(response);
        }
    }

    @DeleteMapping("/removeCartItem")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<String> removeCartItem(@RequestParam("cartId") String cartId) {
        try {
            cartService.removeCartItem(cartId);
            return ResponseEntity.ok("Cart item removed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing cart item: " + e.getMessage());
        }
    }

    @GetMapping("/getTotalAmount")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Double> getTotalAmount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        double totalAmount = cartService.getTotalAmount(userId);
        return ResponseEntity.ok(totalAmount);
    }

    @PostMapping("/placeOrder")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<String> placeOrder() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        cartService.placeOrder(userId);
        return ResponseEntity.ok("Order placed successfully.");
    }

    @GetMapping("/getTotalItems")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Integer> getTotalItems() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        int totalItems = cartService.getTotalItems(userId);
        return ResponseEntity.ok(totalItems);
    }
}
