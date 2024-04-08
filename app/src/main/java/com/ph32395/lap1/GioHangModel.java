package com.ph32395.lap1;
public class GioHangModel {
    private String id;
    private String tensp;
    private double giatien;
    private String hang;
    private String image;
    private String status;
    private int quantity;

    public GioHangModel() {
        // Empty constructor required for Firebase
    }

    public GioHangModel(String id, String tensp, double giatien, String hang, String image, String status, int quantity) {
        this.id = id;
        this.tensp = tensp;
        this.giatien = giatien;
        this.hang = hang;
        this.image = image;
        this.status = status;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTensp() {
        return tensp;
    }

    public void setTensp(String tensp) {
        this.tensp = tensp;
    }

    public double getGiatien() {
        return giatien;
    }

    public void setGiatien(double giatien) {
        this.giatien = giatien;
    }

    public String getHang() {
        return hang;
    }

    public void setHang(String hang) {
        this.hang = hang;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}