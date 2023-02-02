package com.sia.als.model;

public class MenuHome {
    private int image;
    private String info;
    private boolean state;
    private String title;

    public MenuHome(int image,String info,boolean state,String title)
    {
        this.image = image;
        this.info = info;
        this.state = state;
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
