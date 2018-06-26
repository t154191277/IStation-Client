package wmlove.istation.entity;

import java.util.List;

import wmlove.istation.network.model.Category;

/**
 * Created by Wmlove on 2018/4/8.
 */

public class GoodsModel {

    private int categoryId;

    private String name;

    public List<Category.Category2Bean.ImagesBean> mImages;
    public GoodsModel(int categoryId, String name, List<Category.Category2Bean.ImagesBean> images) {
        this.categoryId = categoryId;
        this.name = name;
        this.mImages = images;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GoodsModel)) return false;

        GoodsModel that = (GoodsModel) o;

        if (getCategoryId() != that.getCategoryId()) return false;
        return getName() != null ? getName().equals(that.getName()) : that.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (getCategoryId() ^ (getCategoryId() >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
