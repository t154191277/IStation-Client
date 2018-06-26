package wmlove.istation.engine;

import java.util.ArrayList;
import java.util.List;
import wmlove.istation.entity.CategoryModel;
import wmlove.istation.entity.GoodsModel;

public class DataEngine {

    public static List<CategoryModel> getCategoryData()
    {
        List<CategoryModel> categoryModelList = new ArrayList<>();
        CategoryModel categoryModel;

        String[] categry1 = {"大家电", "居家日用", "手机", "服饰鞋包", "母婴", "生活家电","纸品洗护","美妆","食品","车品", "家装"};

        categoryModel = new CategoryModel(1,"");

        return categoryModelList;
    }


    public static List<CategoryModel> loadCategoryData() {
        List<CategoryModel> categoryModelList = new ArrayList<>();
        CategoryModel categoryModel;
        int categoryCount = 20;
        for (int i = 0; i < categoryCount; i++) {
            categoryModel = new CategoryModel(i, "分类" + i);
            List list = new ArrayList<>();

            int goodsCount = categoryCount % (i + 1) + 1;

            for (int j = 0; j < goodsCount; j++) {
                list.add(new GoodsModel(j,"商品" + i + j, null));
            }

            categoryModel.setGoodsModelList(list);
            categoryModelList.add(categoryModel);
        }
        return categoryModelList;
    }
}