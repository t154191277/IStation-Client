package wmlove.istation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gcssloop.widget.RCRelativeLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
import wmlove.istation.network.APIInterface;
import wmlove.istation.network.model.GoodsImage;
import wmlove.istation.network.model.GoodsModel;
import wmlove.istation.network.model.ResponseModel;
import wmlove.istation.utils.Common;
import wmlove.istation.utils.ResponseUtil;

public class GoodDetailActivity extends AppCompatActivity {

    private final String TAG = "GoodDetailActivity";

    @BindView(R.id.activity_gooddetail_banner)
    Banner banner;
    @BindView(R.id.activity_gooddetail_back)
    RCRelativeLayout back;
    @BindView(R.id.activity_gooddetail_salesNum)
    TextView tv_salesNum;
    @BindView(R.id.activity_gooddetail_price)
    TextView tv_price;
    @BindView(R.id.activity_gooddetail_name)
    TextView tv_name;
    @BindView(R.id.activity_gooddetail_selected)
    TextView tv_selected;
    @BindView(R.id.activity_gooddetail_rl_progressbar)
    RelativeLayout rl_progressbar;
    @BindView(R.id.activity_gooddetail_frame)
    FrameLayout fl_root;
    @BindView(R.id.activity_gooddetail_btn_addcart)
    Button btn_addcart;

    private TextView mTvShopName;
    private RelativeLayout mRLShop;

    private List<String> imagesURL = new ArrayList<>();
    private String userid;
    private String token;
    private Integer mShopId = 0;


    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }


    private void loadGoodsDataFromServer(String id) {

        rl_progressbar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Common.API_DOMAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIInterface service = retrofit.create(APIInterface.class);
        Call<ResponseModel<GoodsModel>> model = service.getGoodsByID(token, id);
        Log.d(TAG, model.request().url().toString());

        model.enqueue(new Callback<ResponseModel<GoodsModel>>() {

            @Override
            public void onResponse(Call<ResponseModel<GoodsModel>> call, Response<ResponseModel<GoodsModel>> response) {
                Log.d(TAG, String.valueOf(response.body().getCode()));

                ResponseUtil.onResponseError(GoodDetailActivity.this, response.body().getCode());
                if (response.body().getCode() == 200 && response.body().getDesc().equals("success")) {
                    Log.d(TAG, String.valueOf(response.body().getData()));

                    final GoodsModel data = response.body().getData();

                    Disposable dis = Observable.create(new ObservableOnSubscribe<GoodsModel>() {
                        @Override
                        public void subscribe(ObservableEmitter<GoodsModel> emitter) {
                            emitter.onNext(data);
                        }
                    }).subscribe(new Consumer<GoodsModel>() {
                        @Override
                        public void accept(GoodsModel goods) throws Exception {
                            tv_name.setText(goods.getName());
                            tv_price.setText(goods.getPrice());
                            tv_salesNum.setText("已售" + goods.getSales_num() + "笔");
                            tv_selected.setText("已选" + goods.getSelected());

                            mTvShopName.setText(goods.shop);
                            mShopId = goods.getShop_id();
                            String mainUrl = "";
                            for (GoodsImage image : goods.getImages()) {
                                if ("full".equals(image.getType())) {
                                    imagesURL.add(Common.API_IMAGE_DOMAIN_URL + image.getPath().split("www/")[1]);
                                    Log.d(TAG, Common.API_IMAGE_DOMAIN_URL + image.getPath());
                                }

                                if ("main".equals(image.getType())) {
                                    mainUrl = Common.API_IMAGE_DOMAIN_URL + image.getPath().split("www/")[1];
                                }
                            }

                            if (imagesURL.size() == 0) {
                                imagesURL.add(mainUrl);
                            }

                            banner.setBannerStyle(BannerConfig.NUM_INDICATOR);
                            //设置图片加载器
                            banner.setImageLoader(new GlideImageLoader());
                            //设置图片集合

                            banner.setImages(imagesURL);
                            //设置banner动画效果
                            banner.setBannerAnimation(Transformer.DepthPage);
                            //设置自动轮播，默认为true
                            banner.isAutoPlay(false);
                            //设置轮播时间
//        banner.setDelayTime(3500);
                            //设置指示器位置（当banner模式中有指示器时）
                            banner.setIndicatorGravity(BannerConfig.CENTER);
                            //banner设置方法全部调用完毕时最后调用
                            banner.start();

                            rl_progressbar.setVisibility(View.GONE);

                            fl_root.setVisibility(View.VISIBLE);
                        }
                    });
//                    categoryModelList = DataEngine.loadCategoryData();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<GoodsModel>> call, Throwable t) {
                ResponseUtil.onResponseError(GoodDetailActivity.this, t);
                System.out.print(t.getMessage());
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fullScreen(this);

        userid = getSharedPreferences(TAG, MODE_PRIVATE).getString("userid", "15650799265");
        token = getSharedPreferences("APP", MODE_PRIVATE).getString("tokenStr", "");

        View view = getLayoutInflater().inflate(R.layout.activity_good_detail, null);
        setContentView(view);

        ButterKnife.bind(this, view);

        mTvShopName = findViewById(R.id.shop_name);
        mRLShop = findViewById(R.id.rl_shop);
        mRLShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoodDetailActivity.this, ShopActivity.class);
                intent.putExtra("shopId",mShopId);
                startActivity(intent);
            }
        });

        final String id = getIntent().getStringExtra("goodID");

        loadGoodsDataFromServer(id);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_addcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Common.API_DOMAIN_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                APIInterface service = retrofit.create(APIInterface.class);
                Call<ResponseModel> model = service.insertCart(token, id, 1);
                Log.d(TAG, model.request().url().toString());

                model.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        ResponseUtil.onResponseError(GoodDetailActivity.this, response.body().getCode());
                        if (response.body().toString().contains("success")) {
                            Toast.makeText(getApplicationContext(), "已加入购物车", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        ResponseUtil.onResponseError(GoodDetailActivity.this, t);
                    }
                });
            }
        });

    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            /**
             注意：
             1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
             2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
             传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
             切记不要胡乱强转！
             */

            //Glide 加载图片简单用法
            Glide.with(context).load(path)
                    .apply(new RequestOptions()
                            .override(450, 450)
                            .placeholder(R.drawable.momo))
                    .into(imageView);
        }
    }
}
