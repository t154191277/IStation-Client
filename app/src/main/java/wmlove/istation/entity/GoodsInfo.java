package wmlove.istation.entity;

import wmlove.istation.R;
import wmlove.istation.network.model.CartGoodsModel;
import wmlove.istation.network.model.GoodsModel;

/**
 * Created by Administrator on 2017/3/26.
 * 商品信息
 */

public class GoodsInfo {
    private String id;
    private String name;
    private boolean isChoosed;
    private String imageUrl;
    private String desc;
    private double price;
    private double prime_price;
    private int postion;
    private int count;
    private String color;
    private String size;


    public GoodsInfo(CartGoodsModel goodsModel)
    {
        this.id = goodsModel.getId();
        this.name = goodsModel.getName();
        this.desc = goodsModel.getDesc_phrase();
        this.price = Double.valueOf(goodsModel.getPrice());
        this.prime_price = prime_price;
        this.count = goodsModel.getNum();
        this.color = "#66666";
        this.size = goodsModel.getSelected();
        this.imageUrl = goodsModel.getPath();
    }


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

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrime_price() {
        return prime_price;
    }

    public void setPrime_price(double prime_price) {
        this.prime_price = prime_price;
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "GoodsInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isChoosed=" + isChoosed +
                ", imageUrl='" + imageUrl + '\'' +
                ", desc='" + desc + '\'' +
                ", price=" + price +
                ", prime_price=" + prime_price +
                ", postion=" + postion +
                ", count=" + count +
                ", color='" + color + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
