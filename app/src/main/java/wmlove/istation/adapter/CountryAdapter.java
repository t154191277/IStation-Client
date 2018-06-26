package wmlove.istation.adapter;

import android.support.v7.widget.RecyclerView;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import wmlove.istation.R;
import wmlove.istation.entity.Country;

/**
 * Created by wmlove on 2018/4/8.
 */

public class CountryAdapter extends BGARecyclerViewAdapter<Country.CountryBeanView>{

    public CountryAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_cascade_category);
    }

    @Override
    public void fillData(BGAViewHolderHelper helper, int position, Country.CountryBeanView model) {
        helper.setText(R.id.code, String.format("%s", model.area_code));
        helper.setText(R.id.area, String.format("%s", model.area));
    }

    /**
     * 是否为该分类下的第一个条目
     *
     * @param position
     * @return
     */
    public boolean isCategoryFistItem(int position) {
        // 第一条数据是该分类下的第一个条目
        if (position == 0) {
            return true;
        }

        String currentCategoryId = getItem(position).areaShort;
        String preCategoryId = getItem(position - 1).areaShort;
        // 当前条目的分类 id 和上一个条目的分类 id 不相等时，当前条目为该分类下的第一个条目

        return !currentCategoryId.equals(preCategoryId);
    }

    /**
     * 根据分类id获取该分类下的第一个商品在商品列表中的位置
     *
     * @param areaShort
     * @return
     */
    public int getFirstPositionByAreaShort(String areaShort) {
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            if (getItem(i).areaShort.equals(areaShort)) {
                return i;
            }
        }
        return -1;
    }
}
