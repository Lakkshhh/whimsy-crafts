package com.eCommerceSite.WhimsyCrafts.services;

import com.eCommerceSite.WhimsyCrafts.model.Cart;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.eCommerceSite.WhimsyCrafts.model.Product;
import com.eCommerceSite.WhimsyCrafts.model.User;
import com.eCommerceSite.WhimsyCrafts.repository.CartRepository;
import com.eCommerceSite.WhimsyCrafts.repository.ProductRepository;
import com.eCommerceSite.WhimsyCrafts.repository.UserRepo;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ByteArrayResource;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.io.image.ImageData;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

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

        if (carts.isEmpty()) {
            throw new RuntimeException("No items in the cart to place an order.");
        }

        carts.forEach(cart -> {
            cart.setStatus("order_placed");
            cartRepository.save(cart);
        });

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        try {

            byte[] pdfBytes = generatePdf(user, carts);
            sendEmailWithAttachment(user.getEmail(), "Order Confirmation", "Thank you for your order!", pdfBytes, "Order_Confirmation.pdf");

        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }

    public int getTotalItems(String userId) {
        List<Cart> carts = cartRepository.findByUserIdAndStatus(userId, "in_cart");
        return carts.size();
    }

    private byte[] generatePdf(User user, List<Cart> carts) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dbldemxes",
            "api_key", "666717275591569",
            "api_secret", "9QtJBJGBjpofNYLkIJNHP_I-NEg"
        ));

        // Logo
        String imageUrl = cloudinary.url().generate("HeadLogo.png");
        ImageData imageData = ImageDataFactory.create(imageUrl);
        Image logo = new Image(imageData);
        logo.scaleToFit(100, 100);
        Paragraph header = new Paragraph()
                .add(logo)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(header);

        document.add(new LineSeparator(new SolidLine()));

        // Title
        Paragraph title = new Paragraph("Order Confirmation")
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        // User information table
        Table userInfoTable = new Table(new float[]{1, 1});
        userInfoTable.setWidth(UnitValue.createPercentValue(100));

        // Username cell (left-aligned)
        Cell usernameCell = new Cell()
                .add(new Paragraph("Username: " + user.getUsername()))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT);
        userInfoTable.addCell(usernameCell);

        // Order ID cell (right-aligned)
        Cell orderIdCell = new Cell()
                .add(new Paragraph("Order ID: " + carts.get(0).getId()))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
        userInfoTable.addCell(orderIdCell);

        userInfoTable.setMarginBottom(20);
        document.add(userInfoTable);

        // Products table
        float[] columnWidths = {1, 4, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
        table.setMarginTop(20);

        // Header cells
        Cell designHeader = new Cell().add(new Paragraph("Design"))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setFontSize(12)
                .setFontColor(ColorConstants.BLACK)
                .setBorder(new SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER);
        Cell quantityHeader = new Cell().add(new Paragraph("Quantity"))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setFontSize(12)
                .setFontColor(ColorConstants.BLACK)
                .setBorder(new SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER);
        Cell priceHeader = new Cell().add(new Paragraph("Price"))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setFontSize(12)
                .setFontColor(ColorConstants.BLACK)
                .setBorder(new SolidBorder(1))
                .setTextAlignment(TextAlignment.CENTER);

        table.addHeaderCell(designHeader);
        table.addHeaderCell(quantityHeader);
        table.addHeaderCell(priceHeader);

        // Data cells
        for (Cart cart : carts) {
            Product product = productRepository.findById(cart.getProductId()).orElse(null);
            if (product != null) {
                Cell designCell = new Cell().add(new Paragraph(product.getDesign()))
                        .setFontSize(12)
                        .setBorder(new SolidBorder(1))
                        .setTextAlignment(TextAlignment.CENTER);
                Cell quantityCell = new Cell().add(new Paragraph(String.valueOf(cart.getQuantity())))
                        .setFontSize(12)
                        .setBorder(new SolidBorder(1))
                        .setTextAlignment(TextAlignment.CENTER);
                Cell priceCell = new Cell().add(new Paragraph(String.valueOf(product.getPrice())))
                        .setFontSize(12)
                        .setBorder(new SolidBorder(1))
                        .setTextAlignment(TextAlignment.CENTER);

                table.addCell(designCell);
                table.addCell(quantityCell);
                table.addCell(priceCell);
            }
        }

        document.add(table);

        // Footer with page numbers
        int numberOfPages = pdf.getNumberOfPages();
        for (int i = 1; i <= numberOfPages; i++) {
            document.showTextAligned(new Paragraph(String.format("Page %d of %d", i, numberOfPages)),
                    pdf.getPage(i).getPageSize().getWidth() / 2,
                    pdf.getPage(i).getPageSize().getBottom() + 20, i,
                    TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
        }

        document.close();
        return baos.toByteArray();
    }

    public void sendEmailWithAttachment(String to, String subject, String text, byte[] attachment, String attachmentName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("lakkshtags@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            ByteArrayResource attachmentResource = new ByteArrayResource(attachment);
            helper.addAttachment(attachmentName, attachmentResource);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email with attachment: " + e.getMessage());
        }
    }

}



