package com.eCommerceSite.WhimsyCrafts.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "productTypes")
public class ProductType {

    @Id
    private String id;
    private String name;

    public ProductType() {
    }

    public ProductType(String name) {
        this.name = name;
    }

    public static ProductType withName(String name) {
        return new ProductType(name);
    }

    public static ProductType tShirt() {
        return withName("Tshirt");
    }

    public static ProductType cushion() {
        return withName("Cushion");
    }

    public static ProductType bedSheet() {
        return withName("Bedsheet");
    }

    public static ProductType woodenBox() {
        return withName("Wooden Box");
    }

    public static ProductType vase() {
        return withName("Vase");
    }

    public String getName() {
        return name;
    }
}
