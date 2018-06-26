package wmlove.istation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.InputStream;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGADivider;
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.baseadapter.BGARVVerticalScrollHelper;
import wmlove.istation.adapter.CountryAdapter;
import wmlove.istation.entity.Bean;
import wmlove.istation.entity.Country;
import wmlove.istation.utils.GsonUtil;

public class SelectCountryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private BGARVVerticalScrollHelper mHelper;
    private CountryAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country);
        getWindow().setStatusBarColor(getResources().getColor(R.color.is_bdbdbd));
        initView();
        loadDataFromFile();
    }

    private void loadDataFromFile(){
        String json = "";
        try {
            InputStream is = getApplicationContext().getAssets().open("country.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "gbk");
            List<Country.CountryBeanView> beanViews = Country.covert(GsonUtil.parseJsonWithGson(json, Bean.class));
            if (beanViews != null){
                mAdapter.setData(beanViews);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initView(){
        mRecyclerView = findViewById(R.id.rv_cascade_country);

        final BGADivider.StickyDelegate stickyDelegate = new BGADivider.StickyDelegate() {
            @Override
            protected boolean isCategoryFistItem(int position) {
                return mAdapter.isCategoryFistItem(position);
            }

            @Override
            protected String getCategoryName(int position) {
                return mAdapter.getItem(position).areaShort;
            }

            @Override
            protected int getFirstVisibleItemPosition() {
                return mHelper.findFirstVisibleItemPosition();
            }
        };

        mRecyclerView.addItemDecoration(BGADivider.newDrawableDivider(R.drawable.shape_divider)
                .setStartSkipCount(0)
                .setMarginLeftResource(R.dimen.size_level3)
                .setMarginRightResource(R.dimen.size_level3)
                .setDelegate(stickyDelegate));
        mHelper = BGARVVerticalScrollHelper.newInstance(mRecyclerView, new BGARVVerticalScrollHelper.SimpleDelegate() {
            @Override
            public int getCategoryHeight() {
                return stickyDelegate.getCategoryHeight();
            }

            @Override
            public void dragging(int position) {
            }

            @Override
            public void settling(int position) {
                this.dragging(position);
            }
        });
        mAdapter = new CountryAdapter(mRecyclerView);
        mAdapter.setOnRVItemClickListener(new BGAOnRVItemClickListener() {
            @Override
            public void onRVItemClick(ViewGroup parent, View itemView, int position) {
                String category = mAdapter.getItem(position).area_code + mAdapter.getItem(position).area;
                Intent intent = new Intent();
                intent.putExtra("area", category);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }
}
