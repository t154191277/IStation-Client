package wmlove.istation.adapter;

import android.support.v7.widget.RecyclerView;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import wmlove.istation.entity.CategoryModel;
import wmlove.istation.R;

/**
 * Created by wmlove on 2018/4/8.
 */

public class CategoryAdapter extends BGARecyclerViewAdapter<CategoryModel> {

    public CategoryAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_cascade_category_new);
    }

    @Override
    public void fillData(BGAViewHolderHelper helper, int position, CategoryModel model) {
        // 设置选中和未选中的背景
        if (mCheckedPosition == position) {
            helper.setBackgroundColorRes(R.id.tv_item_cascade_category_name, android.R.color.white);
        } else {
            helper.setBackgroundRes(R.id.tv_item_cascade_category_name, R.color.bga_adapter_item_pressed);
        }

        helper.setText(R.id.tv_item_cascade_category_name, model.getName());
    }

    /**
     * 根据分类id获取分类索引
     *
     * @param categoryId
     * @return
     */
    public int getPositionByCategoryId(int categoryId) {
        if (categoryId < 0) {
            return 0;
        }

        int itemCount = getItemCount();
        for (int i = 0; i < itemCount; i++) {
            if (getItem(i).getId() == categoryId) {
                return i;
            }
        }
        return 0;
    }
}
