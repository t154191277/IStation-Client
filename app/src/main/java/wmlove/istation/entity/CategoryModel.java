package wmlove.istation.entity;

import java.util.List;

/**
 * Created by wmlove on 2018/4/8.
 */

public class CategoryModel {

    private int id;
    private String name;
    private List<GoodsModel> goodsModelList;

    public CategoryModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GoodsModel> getGoodsModelList() {
        return goodsModelList;
    }

    public void setGoodsModelList(List<GoodsModel> goodsModelList) {
        this.goodsModelList = goodsModelList;
    }

}
