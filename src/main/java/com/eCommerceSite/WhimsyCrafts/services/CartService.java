package com.eCommerceSite.WhimsyCrafts.services;


import com.eCommerceSite.WhimsyCrafts.model.Cart;
import com.eCommerceSite.WhimsyCrafts.model.Product;
import com.eCommerceSite.WhimsyCrafts.repository.CartRepository;
import com.eCommerceSite.WhimsyCrafts.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public String addToCart(String productId, int quantity, String userId) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (!productOpt.isPresent()) {
            return "Product doesn't exist.";
        }

        Product product = productOpt.get();

        if (quantity > product.getQuantity()) {
            return "Requested quantity exceeds available quantity.";
        }

        double totalPrice = product.getPrice() * quantity;

        Cart cart = new Cart();
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        cart.setTotalPrice(totalPrice);
        cart.setUserId(userId);
        cart.setStatus("in_cart");

        cartRepository.save(cart);

        return "Product added to cart successfully.";
    }

    public void removeCartItem(String cartId) {
        cartRepository.deleteById(cartId);
    }

    public double getTotalAmount(String userId) {
        List<Cart> carts = cartRepository.findByUserIdAndStatus(userId, "in_cart");
        return carts.stream().mapToDouble(Cart::getTotalPrice).sum();
    }

    public void placeOrder(String userId) {
        List<Cart> carts = cartRepository.findByUserIdAndStatus(userId, "in_cart");
        carts.forEach(cart -> {
            cart.setStatus("order_placed");
            cartRepository.save(cart);
        });
    }

    public int getTotalItems(String userId) {
        List<Cart> carts = cartRepository.findByUserIdAndStatus(userId, "in_cart");
        return carts.size();
    }
}