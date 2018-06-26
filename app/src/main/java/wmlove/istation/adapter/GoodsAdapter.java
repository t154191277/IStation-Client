package wmlove.istation.adapter;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import wmlove.istation.entity.GoodsModel;
import wmlove.istation.R;
import wmlove.istation.utils.Common;

/**
 * Created by wmlove on 2018/4/8.
 */

public class GoodsAdapter extends BGARecyclerViewAdapter<GoodsModel> {

    public GoodsAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_cascade_good);
    }

    @Override
    public void fillData(BGAViewHolderHelper helper, int position, GoodsModel model) {
        helper.setText(R.id.tv_item_cascade_category_name, model.getName());

        if (model.mImages == null || model.mImages.size() == 0) {
            return;
        }

        int[] ids = {R.id.img1, R.id.img2};
        try {
            for (int i = 0; i < model.mImages.size(); i++) {
                String url = model.mImages.get(i).getPath();
                Glide.with(mContext)
                        .asBitmap()
                        .load(Common.API_IMAGE_DOMAIN_URL
                                + model.mImages.get(i).getPath().split("www/")[1])
                        .apply(new RequestOptions()
                                        .override(300,300)
                                        .placeholder(R.drawable.momo))
                        .into(helper.getImageView(ids[i]));

//                FutureTarget<Bitmap> futureTarget =
//
//                                .submit(300, 300);
//
//                Bitmap bitmap = futureTarget.get();
//
//                helper.getImageView(ids[i]).setImageBitmap(bitmap);
//                if (url.equals(helper.getImageView(ids[i]).getTag())){
//
//                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

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

        long currentCategoryId = getItem(position).getCategoryId();
        long preCategoryId = getItem(position - 1).getCategoryId();
        // 当前条目的分类 id 和上一个条目的分类 id 不相等时，当前条目为该分类下的第一个条目
        if (currentCategoryId != preCategoryId) {
            return true;
        }

        return false;
    }

    /**
     * 根据分类id获取该分类下的第一个商品在商品列表中的位置
     *
     * @param categoryId
     * @return
     */
    public int getFirstPositionByCategoryId(long categoryId) {
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            if (getItem(i).getCategoryId() == categoryId) {
                return i;
            }
        }
        return -1;
    }
}
