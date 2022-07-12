package com.rsmnm.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Document {
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("expiry_date")
    @Expose
    private int expiryDate;
    @SerializedName("updated_at")
    @Expose
    private int updatedAt;
    @SerializedName("uploaded_at")
    @Expose
    private int uploadedAt;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(int expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(int uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
