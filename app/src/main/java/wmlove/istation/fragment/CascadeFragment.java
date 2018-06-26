package wmlove.istation.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.baseadapter.BGADivider;
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.baseadapter.BGARVVerticalScrollHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wmlove.istation.GoodDetailActivity;
import wmlove.istation.R;
import wmlove.istation.SearchResultActivity;
import wmlove.istation.adapter.CategoryAdapter;
import wmlove.istation.adapter.GoodsAdapter;
import wmlove.istation.entity.CategoryModel;
import wmlove.istation.entity.GoodsModel;
import wmlove.istation.network.APIInterface;
import wmlove.istation.network.model.Category;
import wmlove.istation.network.model.ResponseModel;
import wmlove.istation.utils.Common;
import wmlove.istation.utils.ResponseUtil;


public class CascadeFragment extends MvcFragment {

    private final String TAG = "CascadeFragment";

    private RecyclerView mCategoryRv;
    private RecyclerView mGoodsRv;
    private CategoryAdapter mCategoryAdapter;
    private GoodsAdapter mGoodsAdapter;
    private Toolbar toolbar;
    private BGARVVerticalScrollHelper mGoodsScrollHelper; // 商品列表滚动帮助类

    private String token;

    @Override
    protected int getRootLayoutResID() {
        return R.layout.fragment_cascade;
    }

    public static CascadeFragment newInstance(String param1, String param2) {
        CascadeFragment fragment = new CascadeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        token = getActivity().getSharedPreferences("APP", Context.MODE_PRIVATE).getString("tokenStr", "");
        mCategoryRv = getViewById(R.id.rv_cascade_category);
        mGoodsRv = getViewById(R.id.rv_cascade_goods);
        toolbar = getViewById(R.id.fragment_cascade_toolbar);
    }

    @Override
    protected void setListener() {

        // 分类
        mCategoryAdapter = new CategoryAdapter(mCategoryRv);
        mCategoryAdapter.setOnRVItemClickListener(new BGAOnRVItemClickListener() {
            @Override
            public void onRVItemClick(ViewGroup parent, View itemView, int position) {
                mCategoryAdapter.setCheckedPosition(position);
                long categoryId = mCategoryAdapter.getItem(position).getId();
                int goodsPosition = mGoodsAdapter.getFirstPositionByCategoryId(categoryId);

                mGoodsScrollHelper.smoothScrollToPosition(goodsPosition);
//                mGoodsScrollHelper.scrollToPosition(goodsPosition);
            }
        });
        mCategoryRv.addItemDecoration(BGADivider.newShapeDivider()
                .setMarginLeftResource(R.dimen.size_level3)
                .setMarginRightResource(R.dimen.size_level3)
                .setDelegate(new BGADivider.SimpleDelegate() {
                    @Override
                    public boolean isNeedCustom(int position, int itemCount) {
                        // 选中的条目和下一个条目自定义，但不绘制分割线
                        return position == mCategoryAdapter.getCheckedPosition() || position == mCategoryAdapter.getCheckedPosition() + 1;
                    }

                    @Override
                    public void getItemOffsets(BGADivider divider, int position, int itemCount, Rect outRect) {
                        // 选中的条目和下一个条目自定义占用分割线高度，但不绘制分割线
                        divider.getVerticalItemOffsets(outRect);
                    }
                }));
        mCategoryRv.setAdapter(mCategoryAdapter);


        // 商品
        final BGADivider.StickyDelegate stickyDelegate = new BGADivider.StickyDelegate() {
            @Override
            protected boolean isCategoryFistItem(int position) {
                return mGoodsAdapter.isCategoryFistItem(position);
            }

            @Override
            protected String getCategoryName(int position) {
                int categoryId = mGoodsAdapter.getItem(position).getCategoryId();
                int categoryPosition = mCategoryAdapter.getPositionByCategoryId(categoryId);
                return mCategoryAdapter.getItem(categoryPosition).getName();
            }

            @Override
            protected int getFirstVisibleItemPosition() {
                return mGoodsScrollHelper.findFirstVisibleItemPosition();
            }
        };
        mGoodsRv.addItemDecoration(BGADivider.newDrawableDivider(R.drawable.shape_divider)
                .setStartSkipCount(0)
                .setMarginLeftResource(R.dimen.size_level3)
                .setMarginRightResource(R.dimen.size_level3)
                .setDelegate(stickyDelegate));
        mGoodsScrollHelper = BGARVVerticalScrollHelper.newInstance(mGoodsRv, new BGARVVerticalScrollHelper.SimpleDelegate() {
            @Override
            public int getCategoryHeight() {
                return stickyDelegate.getCategoryHeight();
            }

            @Override
            public void dragging(int position) {
                int categoryId = mGoodsAdapter.getItem(position).getCategoryId();
                int categoryPosition = mCategoryAdapter.getPositionByCategoryId(categoryId);
                mCategoryAdapter.setCheckedPosition(categoryPosition);

                ((LinearLayoutManager) mCategoryRv.getLayoutManager()).scrollToPositionWithOffset(mCategoryAdapter.getCheckedPosition() - 4, 0);
            }

            @Override
            public void settling(int position) {
                this.dragging(position);
            }
        });
        mGoodsAdapter = new GoodsAdapter(mGoodsRv);
        mGoodsAdapter.setOnRVItemClickListener(new BGAOnRVItemClickListener() {
            @Override
            public void onRVItemClick(ViewGroup parent, View itemView, int position) {
//                ToastUtil.show("点击了" + mGoodsAdapter.getItem(position).getName());

                String category = mGoodsAdapter.getItem(position).getName();
                Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                intent.putExtra("category",category);
                startActivity(intent);
            }
        });
        mGoodsRv.setAdapter(mGoodsAdapter);

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(Common.API_DOMAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIInterface service = retrofit.create(APIInterface.class);
        Call<ResponseModel<List<Category>>> model = service.getCategory(token);

        Log.d(TAG, model.request().url().toString());

        model.enqueue(new Callback<ResponseModel<List<Category>>>() {

            @Override
            public void onResponse(Call<ResponseModel<List<Category>>> call, Response<ResponseModel<List<Category>>> response) {
                Log.d(TAG, String.valueOf(response.body().getCode()));

                ResponseUtil.onResponseError(getActivity(), response.body().getCode());
                if (response.body().getCode() == 200 && response.body().getDesc().equals("success"))
                {
                    Log.d(TAG, String.valueOf(response.body().getData()));

                    final List<Category> data = response.body().getData();

                    Observable.create(new ObservableOnSubscribe<List<Category>>() {
                        @Override
                        public void subscribe(ObservableEmitter<List<Category>> emitter) {
                            emitter.onNext(data);
                        }
                    }).subscribe(new Consumer<List<Category>>() {
                        @Override
                        public void accept(List<Category> categoryList) throws Exception {
                            List<CategoryModel> categoryModelList = new ArrayList<>();
                            List<GoodsModel> goodsModelList = new ArrayList<>();
                            Map<String,String> categ1Categ2Map = new HashMap<>();
                            Map<String,List<Category.Category2Bean.ImagesBean>> category2ImagesMap = new HashMap<>();
                            for(Category category : categoryList)
                            {
                                String category1 = category.getCategory1();
                                for (Category.Category2Bean bean : category.getCategory2()){
                                    String value = bean.getCateg2();
                                    category2ImagesMap.put(value, bean.getImages());

                                    if (categ1Categ2Map.containsKey(category1)) value = categ1Categ2Map.get(category1) + "," + value;
                                    categ1Categ2Map.put(category1, value);
                                }
                            }

                            int categoryID = 0;
                            for (Map.Entry<String,String> entry : categ1Categ2Map.entrySet())
                            {
                                List<GoodsModel> goodsModelListTemp = new ArrayList<>();
                                String key = entry.getKey();
                                String[] values = entry.getValue().split(",");
                                for (String value: values)
                                {
                                    GoodsModel goods = new GoodsModel(categoryID, value, category2ImagesMap.get(value));
                                    goodsModelListTemp.add(goods);
                                }

                                CategoryModel categoryModel = new CategoryModel(categoryID++, key);
                                categoryModel.setGoodsModelList(goodsModelListTemp);

                                categoryModelList.add(categoryModel);
                                goodsModelList.addAll(goodsModelListTemp);
                            }

                            mCategoryAdapter.setData(categoryModelList);
                            mGoodsAdapter.setData(goodsModelList);
                        }
                    }).isDisposed();

//                    categoryModelList = DataEngine.loadCategoryData();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<List<Category>>> call, Throwable t) {
                ResponseUtil.onResponseError(getActivity(), t);
                Log.d(TAG, String.valueOf(t.getMessage()));
            }
        });


//        List<GoodsModel> goodsModelList = new ArrayList<>();
//        for (CategoryModel categoryModel : categoryModelList) {
//            goodsModelList.addAll(categoryModel.getGoodsModelList());
//        }
//        mCategoryAdapter.setData(categoryModelList);
//        mGoodsAdapter.setData(goodsModelList);

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
