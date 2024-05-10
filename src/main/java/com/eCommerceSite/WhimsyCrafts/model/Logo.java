package com.eCommerceSite.WhimsyCrafts.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "logos")
public class Logo {

    @Id
    private String id;
    private String name;
    private String url; // URL of the logo stored in Cloudinary

    public Logo() {
        // Default constructor
    }

    public Logo(String name, String url) {
        this.name = name;
        this.url = url;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Logo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

