package com.need.mymall;

public class CategoryModel {

    public static final int HOMEPAGE = 0;
    public static final int SEARCH = 1;
    public static final int PRICE_LIST = 2;
    public static final int CONTACT = 3;

    private int type;

    private int link;
    private String name;

    public CategoryModel(int link, String name) {
        this.link = link;
        this.name = name;
    }

    public CategoryModel(int type, int link, String name) {
        this.type = type;
        this.link = link;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLink() {
        return link;
    }

    public void setLink(int link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
