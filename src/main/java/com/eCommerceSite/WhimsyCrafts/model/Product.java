package com.eCommerceSite.WhimsyCrafts.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {

    @Id
    private String id;
    private String design;
    private double price;
    private int quantity;
    private String logo;

    @DBRef
    private ProductType productType;

    public Product() {
        // Default constructor
    }

    public Product(String design, double price, int quantity, ProductType productType, String logo) {
        this.design = design;
        this.price = price;
        this.quantity = quantity;
        this.productType = productType;
        this.logo = logo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesign() {
        return design;
    }

    public void setDesign(String name) {
        this.design = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", design='" + design + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", productType=" + productType +
                ", logo=" + logo +
                '}';
    }
}

