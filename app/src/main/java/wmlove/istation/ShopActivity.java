package wmlove.istation;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wmlove.istation.adapter.SearchResultListAdapter;
import wmlove.istation.network.APIInterface;
import wmlove.istation.network.model.GoodsImage;
import wmlove.istation.network.model.GoodsModel;
import wmlove.istation.network.model.ResponseModel;
import wmlove.istation.utils.Common;
import wmlove.istation.utils.ResponseUtil;

public class ShopActivity extends AppCompatActivity {

    private List<GoodsModel> goodList = new ArrayList<>();
    private SearchResultListAdapter adapter;
    private TextView mTVShopName;
    private boolean isLoad = false;
    private int offset = 0;
    private int limit = 10;
    private int shopId =0;

    private void fullScreen() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        fullScreen();

        mTVShopName = findViewById(R.id.shop_name);
        ListView listView = findViewById(R.id.shop_list);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(isLoad && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    offset += 10;
                    limit += 10;
                    loadDataFromNet(shopId, offset, limit);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                isLoad = ((firstVisibleItem + visibleItemCount) == totalItemCount);
            }
        });
        adapter = new SearchResultListAdapter(this, goodList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShopActivity.this, GoodDetailActivity.class);
                intent.putExtra("goodID", goodList.get(position).getId());
                startActivity(intent);
            }
        });

        shopId = getIntent().getIntExtra("shopId", 0);
        loadDataFromNet(shopId, offset, limit);
    }

    private void loadDataFromNet(int shopId, int offset, int limit) {
        String token = getSharedPreferences("APP", MODE_PRIVATE).getString("tokenStr", "");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Common.API_DOMAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIInterface service = retrofit.create(APIInterface.class);
        Call<ResponseModel<List<GoodsModel>>> model = service.getGoodsByShopID(token, shopId, offset, limit);
        Log.d("ShopActivity", String.valueOf(model.request().url()));
        model.enqueue(new Callback<ResponseModel<List<GoodsModel>>>() {

            @Override
            public void onResponse(Call<ResponseModel<List<GoodsModel>>> call, Response<ResponseModel<List<GoodsModel>>> response) {
                Log.d("ShopActivity", String.valueOf(response.body().getCode()));
                ResponseUtil.onResponseError(ShopActivity.this, response.body().getCode());
                if (response.body().getCode() == 200 && response.body().getDesc().equals("success")) {
                    Log.d("ShopActivity", String.valueOf(response.body().getData()));

                    final List<GoodsModel> data = response.body().getData();
                    mTVShopName.setText(data.get(0).shop);

                    Observable.create(new ObservableOnSubscribe<List<GoodsModel>>() {
                        @Override
                        public void subscribe(ObservableEmitter<List<GoodsModel>> emitter) {
                            emitter.onNext(data);
                        }
                    }).subscribe(new Consumer<List<GoodsModel>>() {
                        @Override
                        public void accept(List<GoodsModel> goods) throws Exception {
                            goodList.addAll(goods);
                            adapter.notifyDataSetChanged();
                        }
                    }).isDisposed();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<List<GoodsModel>>> call, Throwable t) {
                ResponseUtil.onResponseError(ShopActivity.this, t);
                System.out.print(t.getMessage());
            }
        });
    }
}
