package com.celfocus.hiring.kickstarter.db.entity;

import com.celfocus.hiring.kickstarter.domain.Product;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.NaturalId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class ProductEntity extends Product implements Serializable {


    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }

    @Id
    public String getSku() {
        return super.getSku();
    }

    public void setSku(String sku) {
        super.setSku(sku);
    }

    public String getDescription() {
        return super.getDescription();
    }

    public void setDescription(String description) {
        super.setDescription(description);
    }

    public BigDecimal getPrice() {
        return super.getPrice();
    }

    public void setPrice(BigDecimal price) {
        super.setPrice(price);
    }

    public String getimageUrl() {
        return super.getimageUrl();
    }

    public void setimageUrl(String imageUrl) {
        super.setimageUrl(imageUrl);
    }
    public void setQuantity(int quantity) { super.setQuantity(quantity);}

    public int getQuantity() { return super.getQuantity(); }
}

