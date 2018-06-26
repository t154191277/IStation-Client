package wmlove.istation.network.model;

import java.util.List;

public class Cart {
    private int shop_id;
    private String shop_name;
    private List<CartGoodsModel> goods;

    public int getShop_id() {
        return shop_id;
    }
    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }
    public String getShop_name() {
        return shop_name;
    }
    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }
    public List<CartGoodsModel> getGoods() {
        return goods;
    }
    public void setGoods(List<CartGoodsModel> goods) {
        this.goods = goods;
    }
    @Override
    public String toString() {
        return "Cart [shop_id=" + shop_id + ", shop_name=" + shop_name
                + ", goods=" + goods + "]";
    }

}
