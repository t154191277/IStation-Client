package wmlove.istation;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.activity.CaptureActivity;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wmlove.istation.adapter.SearchResultListAdapter;
import wmlove.istation.network.APIInterface;
import wmlove.istation.network.model.GoodsModel;
import wmlove.istation.network.model.ResponseModel;
import wmlove.istation.utils.Common;
import wmlove.istation.utils.ResponseUtil;

public class SearchResultActivity extends AppCompatActivity {

    private final String TAG = "SearchResultActivity";

    private class QueryParameter{

        public static final int QUERY_TYPE_CATEGORY = 1;

        public static final int QUERY_TYPE_NAME = 2;
    }

    private class Order{

        public static final int DESC = 1;

        public static final int ASC = 2;
    }


    @BindView(R.id.nice_spinner)
    NiceSpinner spinner;
    @BindView(R.id.img_back)
    ImageView img_back;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.listview)
    ListView listView;
    @BindView(R.id.img_scan)
    ImageView img_scan;
    @BindView(R.id.tv_none_content)
    TextView tv_none_content;

    private List<GoodsModel> goodList = new ArrayList<>();

    private SearchResultListAdapter adapter;

    private String token;

    /**
     * 分页加载还没实现
     * @param parameter
     * @param offset
     * @param limit
     */
    private void loadSearchResultFromNet(final String parameter,int offset,int limit, int order, String by, int queryParameter)
    {
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(Common.API_DOMAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIInterface service = retrofit.create(APIInterface.class);

        Call<ResponseModel<List<GoodsModel>>> model;

        String orderStr = order != 0 ? (order == Order.ASC ? "asc" : "desc") : null;

        if (queryParameter == QueryParameter.QUERY_TYPE_CATEGORY) model = service.selectGoodsByCategory(token, parameter, offset, limit, orderStr, by);
        else if (queryParameter == QueryParameter.QUERY_TYPE_NAME) model = service.selectGoodsByName(token, parameter, offset, limit, orderStr, by);
        else return;

        Log.d(TAG, model.request().url().toString());

        model.enqueue(new Callback<ResponseModel<List<GoodsModel>>>() {

            @Override
            public void onResponse(Call<ResponseModel<List<GoodsModel>>> call, Response<ResponseModel<List<GoodsModel>>> response) {
                Log.d(TAG, String.valueOf(response.body().getCode()));
                ResponseUtil.onResponseError(SearchResultActivity.this, response.body().getCode());
                if (response.body().getCode() == 200 && response.body().getDesc().equals("success"))
                {
                    Log.d(TAG, String.valueOf(response.body().getData()));

                    final List<GoodsModel> data = response.body().getData();

                    //没有搜索结果页面
                    if (data.size() == 0)
                    {
                        tv_none_content.setTextColor(Color.RED);
                        tv_none_content.setVisibility(View.VISIBLE);
                        tv_none_content.setText("没有搜索到\"" + parameter + "\"");
                        return;
                    }


                    Observable.create(new ObservableOnSubscribe<List<GoodsModel>>() {
                        @Override
                        public void subscribe(ObservableEmitter<List<GoodsModel>> emitter) {
                            emitter.onNext(data);
                        }
                    }).subscribe(new Consumer<List<GoodsModel>>() {
                        @Override
                        public void accept(List<GoodsModel> goods) throws Exception {
                            goodList.clear();
                            goodList.addAll(goods);
                            adapter.notifyDataSetChanged();
                        }
                    }).isDisposed();

//                    categoryModelList = DataEngine.loadCategoryData();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<List<GoodsModel>>> call, Throwable t) {
                ResponseUtil.onResponseError(SearchResultActivity.this, t);
                System.out.print(t.getMessage());
            }
        });
    }

    private void initView() {

        final String[] data = getResources().getStringArray(R.array.search_sort_item);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SearchResultActivity.this, CaptureActivity.class),1028);
            }
        });

        final String searchText = getIntent().getStringExtra("name");
        final String category = getIntent().getStringExtra("category");
        et_search.setCursorVisible(false);
        et_search.setFocusable(false);
        et_search.setFocusableInTouchMode(false);
        et_search.setText(TextUtils.isEmpty(searchText) ? category : searchText);
        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResultActivity.this, SearchActivity.class);
                intent.putExtra("searchText", searchText);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchResultActivity.this, GoodDetailActivity.class);
                intent.putExtra("goodID", goodList.get(position).getId());
                startActivity(intent);
            }
        });

        spinner.attachDataSource(new ArrayList<>(Arrays.asList(data)));


        adapter = new SearchResultListAdapter(SearchResultActivity.this, goodList);

        listView.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                        loadData(0, null);
                        break;
                    case 1:
                        loadData(Order.DESC, "item_salesNum");
                        break;
                    case 2:
                        loadData(Order.DESC, "item_price");
                        break;
                    case 3:
                        loadData(Order.ASC, "item_price");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected");
                Intent intent = new Intent(SearchResultActivity.this, CameraActivity.class);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1028 && data != null) {
            String id = data.getStringExtra(CaptureActivity.SCAN_QRCODE_RESULT);
            Intent intent = new Intent(SearchResultActivity.this, GoodDetailActivity.class);
            intent.putExtra("goodID", id);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "识别成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "识别失败", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.activity_search_result,null);

        setContentView(view);
        getWindow().setStatusBarColor(getResources().getColor(R.color.is_bdbdbd));

        ButterKnife.bind(this, view);

        token = getSharedPreferences("APP", MODE_PRIVATE).getString("tokenStr", "");

        initView();

        loadData(0, null);
    }

    private void loadData(int order, String by)
    {
        Intent intent = getIntent();
        String category = intent.getStringExtra("category");
        String name = intent.getStringExtra("name");

        int offset = 0; int limit = 20;

        if (!TextUtils.isEmpty(category)) loadSearchResultFromNet(category, offset, limit, order,by,QueryParameter.QUERY_TYPE_CATEGORY);

        if (!TextUtils.isEmpty(name)) loadSearchResultFromNet(name, offset, limit, order, by, QueryParameter.QUERY_TYPE_NAME);

    }
}
