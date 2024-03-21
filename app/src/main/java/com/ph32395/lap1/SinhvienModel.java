package com.ph32395.lap1;

public class SinhvienModel {

    private String _id;
    private String name;
    private int age;
    private String msv;
    private boolean status;
    private String image;

    public SinhvienModel(String name, int age, String msv, String image, boolean status) {
        this.name = name;
        this.age = age;
        this.msv = msv;
        this.image = image;
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMsv() {
        return msv;
    }

    public void setMsv(String msv) {
        this.msv = msv;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    public String getStatusText() {
        if (status) {
            return "Đã Ra trường";
        } else {
            return "Chưa ra trường";
        }
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }


}