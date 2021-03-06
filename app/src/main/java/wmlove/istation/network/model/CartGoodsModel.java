package wmlove.istation.network.model;

import java.util.List;

public class CartGoodsModel {

    private String id;
    private String category1;
    private String category2;

    private int shop_id;
    private String name;
    private String sales_num;

    private String desc_phrase;
    private String price;

    private int num;

    private String selected;

    private String path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSales_num() {
        return sales_num;
    }

    public void setSales_num(String sales_num) {
        this.sales_num = sales_num;
    }

    public String getDesc_phrase() {
        return desc_phrase;
    }

    public void setDesc_phrase(String desc_phrase) {
        this.desc_phrase = desc_phrase;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "CartGoodsModel{" +
                "id='" + id + '\'' +
                ", category1='" + category1 + '\'' +
                ", category2='" + category2 + '\'' +
                ", shop_id=" + shop_id +
                ", name='" + name + '\'' +
                ", sales_num='" + sales_num + '\'' +
                ", desc_phrase='" + desc_phrase + '\'' +
                ", price='" + price + '\'' +
                ", num=" + num +
                ", selected='" + selected + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
