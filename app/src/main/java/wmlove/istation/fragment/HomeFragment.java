package wmlove.istation.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gcssloop.widget.RCRelativeLayout;
import com.google.zxing.activity.CaptureActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
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
import wmlove.istation.CameraActivity;
import wmlove.istation.GoodDetailActivity;
import wmlove.istation.R;
import wmlove.istation.SearchActivity;
import wmlove.istation.SearchResultActivity;
import wmlove.istation.WebActivity;
import wmlove.istation.adapter.SearchResultListAdapter;
import wmlove.istation.network.APIInterface;
import wmlove.istation.network.model.GoodsModel;
import wmlove.istation.network.model.ResponseModel;
import wmlove.istation.utils.Common;
import wmlove.istation.utils.ResponseUtil;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends MvcFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.search)
    RelativeLayout search;
    @BindView(R.id.img_QR)
    ImageView img_qr;

    private List<GoodsModel> goodList = new ArrayList<>();
    private SearchResultListAdapter adapter;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getRootLayoutResID() {
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        loadSearchResultFromNet();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.banner1);
        images.add(R.drawable.banner2);
        images.add(R.drawable.banner3);
        images.add(R.drawable.banner4);

        final String [] urlArr = {"https://pro.m.jd.com/mall/active/3kbBwgYZgqw79hKvN4oVeLZxuyFj/index.html",
                "https://pro.m.jd.com/mall/active/3ZYgiYWXttYicmKcMPYVtkfhGydm/index.html",
                "https://pro.m.jd.com/mall/active/2fC5ct5Haqw6cYwqDY6V77yEKweB/index.html",
                "https://pro.m.jd.com/mall/active/3kbBwgYZgqw79hKvN4oVeLZxuyFj/index.html"};

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, view);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        img_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), CaptureActivity.class), 1028);
            }
        });

        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3500);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(mActivity, WebActivity.class);
                intent.putExtra("url", urlArr[position]);
                startActivity(intent);
            }
        });
        //banner设置方法全部调用完毕时最后调用
        banner.start();


        ListView listView = view.findViewById(R.id.listview_home);
        adapter = new SearchResultListAdapter(getActivity(), goodList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), GoodDetailActivity.class);
                intent.putExtra("goodID", goodList.get(position).getId());
                startActivity(intent);
            }
        });

        loadSearchResultFromNet();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1028 && data != null) {
            String id = data.getStringExtra(CaptureActivity.SCAN_QRCODE_RESULT);

            Intent intent = new Intent(getActivity() , GoodDetailActivity.class);
            intent.putExtra("goodID", id);
            startActivity(intent);
            Toast.makeText(getActivity(), "识别成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "识别失败", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            loadSearchResultFromNet();
        }
    }

    private void loadSearchResultFromNet()
    {
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(Common.API_DOMAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIInterface service = retrofit.create(APIInterface.class);

        String token = mActivity.getSharedPreferences("APP", Context.MODE_PRIVATE).getString("tokenStr", "");
        Call<ResponseModel<List<GoodsModel>>> model = service.getRecommend(token, true);
        Log.e("Home", model.request().url().toString());
        model.enqueue(new Callback<ResponseModel<List<GoodsModel>>>() {

            @Override
            public void onResponse(Call<ResponseModel<List<GoodsModel>>> call, Response<ResponseModel<List<GoodsModel>>> response) {
                ResponseUtil.onResponseError(getActivity(), response.body().getCode());
                if (response.body().getCode() == 200 && response.body().getDesc().equals("success"))
                {
                    final List<GoodsModel> data = response.body().getData();
                    Log.e("Home", data.toString());
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
                ResponseUtil.onResponseError(mActivity, t);
                System.out.print(t.getMessage());
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
            Glide.with(context).load(path).into(imageView);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        this.banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();

        this.banner.stopAutoPlay();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
