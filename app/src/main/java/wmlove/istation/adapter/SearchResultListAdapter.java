package wmlove.istation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.zhouwei.library.CustomPopWindow;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.pinnedsection.PinnedSectionListView;
import wmlove.istation.R;
import wmlove.istation.network.model.GoodsModel;
import wmlove.istation.utils.Common;

public class SearchResultListAdapter extends BaseAdapter{

    private LayoutInflater mInflater;

    private Context mContext;

    private List<GoodsModel> goodsModelList;

    public SearchResultListAdapter(Context mContext, List<GoodsModel> goodsModelList) {

        if (goodsModelList == null) goodsModelList =  new ArrayList<>();

        this.mContext = mContext;
        this.goodsModelList = goodsModelList;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return goodsModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        if (convertView == null)
        {
            mHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_search_result_data, null, true);
            mHolder.name = (TextView) convertView.findViewById(R.id.item_search_result_data_name);
            mHolder.price = (TextView) convertView.findViewById(R.id.item_search_result_data_price);
            mHolder.desc = (TextView) convertView.findViewById(R.id.item_search_result_data_desc);
            mHolder.salesNum = (TextView) convertView.findViewById(R.id.item_search_result_data_salesNum);
            mHolder.more = (ImageView) convertView.findViewById(R.id.item_search_result_data_more);

            mHolder.image = (ImageView) convertView.findViewById(R.id.item_search_result_data_image);
            convertView.setTag(mHolder);
        }else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.name.setText(goodsModelList.get(position).getName());
        mHolder.price.setText(goodsModelList.get(position).getPrice());
        mHolder.desc.setText(goodsModelList.get(position).getDesc_phrase());
        mHolder.salesNum.setText("已售：" + goodsModelList.get(position).getSales_num());

        mHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WindowManager wm = (WindowManager) mContext
                        .getSystemService(Context.WINDOW_SERVICE);
                int width = wm.getDefaultDisplay().getWidth();
                int height = wm.getDefaultDisplay().getHeight();

                View view = mInflater.inflate(R.layout.popwindow_qr, null);
                ImageView qr = view.findViewById(R.id.qr);

                String content = goodsModelList.get(position).getId();
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapUtils.create2DCode(content);
                    qr.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                new CustomPopWindow.PopupWindowBuilder(mContext)
                        .setView(view)
                        .enableBackgroundDark(true)//背景是否变暗
                        .setBgDarkAlpha(0.5f) // 控制亮度
                        .size(width,height/3)
                        .create()
                        .showAsDropDown(view,0,height/3 * 2);
            }
        });


        String path = goodsModelList.get(position).getImages().get(0).getPath();
        Glide.with(mContext).load(Common.API_IMAGE_DOMAIN_URL + path.split("www/")[1])
                .apply(new RequestOptions()
                .override(350,350)
                .placeholder(R.drawable.momo))
                .into(mHolder.image);

        return convertView;
    }

    public List<GoodsModel> getGoodsModelList() {
        return goodsModelList;
    }

    class ViewHolder {
        private TextView name;
        private TextView price;
        private TextView desc;
        private TextView salesNum;
        private ImageView image;
        private ImageView more;
    }

}
