package wmlove.istation.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
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
import wmlove.istation.adapter.ShopcatAdapter;
import wmlove.istation.entity.GoodsInfo;
import wmlove.istation.entity.StoreInfo;
import wmlove.istation.network.APIInterface;
import wmlove.istation.network.model.Cart;
import wmlove.istation.network.model.CartGoodsModel;
import wmlove.istation.network.model.ResponseModel;
import wmlove.istation.utils.Common;
import wmlove.istation.utils.ResponseUtil;
import wmlove.istation.utils.UtilTool;
import wmlove.istation.utils.UtilsLog;

import static in.srain.cube.views.ptr.util.PtrLocalDisplay.dp2px;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends MvcFragment implements View.OnClickListener, ShopcatAdapter.CheckInterface, ShopcatAdapter.ModifyCountInterface, ShopcatAdapter.GroupEditorListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private final String TAG = CartFragment.class.getName();

    private String phone;

    @BindView(R.id.listView)
    ExpandableListView listView;
    @BindView(R.id.all_checkBox)
    CheckBox allCheckBox;
    @BindView(R.id.total_price)
    TextView totalPrice;
    @BindView(R.id.go_pay)
    TextView goPay;
    @BindView(R.id.order_info)
    LinearLayout orderInfo;
    @BindView(R.id.share_goods)
    TextView shareGoods;
    @BindView(R.id.collect_goods)
    TextView collectGoods;
    @BindView(R.id.del_goods)
    TextView delGoods;
    @BindView(R.id.share_info)
    LinearLayout shareInfo;
    @BindView(R.id.ll_cart)
    LinearLayout llCart;
    @BindView(R.id.mPtrframe)
    PtrFrameLayout mPtrFrame;
    @BindView(R.id.shoppingcat_num)
    TextView shoppingcatNum;
    @BindView(R.id.actionBar_edit)
    Button actionBarEdit;
    @BindView(R.id.layout_empty_shopcart)
    LinearLayout empty_shopcart;

    private SharedPreferences mSharedPreferences;


    private Context mcontext;
    private double mtotalPrice = 0.00;
    private int mtotalCount = 0;
    //false就是编辑，ture就是完成
    private boolean flag = false;
    private ShopcatAdapter adapter;
    private List<StoreInfo> groups = new ArrayList<>(); //组元素的列表
    private Map<String, List<GoodsInfo>> childs = new HashMap<String, List<GoodsInfo>>(); //子元素的列表

    private String token;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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

        View view = inflater.inflate(R.layout.main, container, false);
        ButterKnife.bind(this, view);

        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("APP", Context.MODE_PRIVATE);
        phone = mSharedPreferences.getString("phoneStr", "");
        token = getActivity().getSharedPreferences("APP", Context.MODE_PRIVATE).getString("tokenStr", "");
        loadGoodsDataFromServer(phone);
//        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        initPtrFrame();
//        initData();

//        initEvents();
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LayoutInflater inflator = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        super.onCreateOptionsMenu(menu, inflater);
    }


    private void initPtrFrame() {
        final PtrClassicDefaultHeader header=new PtrClassicDefaultHeader(getContext());
        header.setPadding(dp2px(20), dp2px(20), 0, 0);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
        mPtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                mPtrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadGoodsDataFromServer(phone);
                        mPtrFrame.refreshComplete();
                    }
                },2000);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
    }


    private void loadGoodsDataFromServer(String id)
    {

//        rl_progressbar.setVisibility(View.VISIBLE);

        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(Common.API_DOMAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIInterface service = retrofit.create(APIInterface.class);
        Call<ResponseModel<List<Cart>>> model = service.getCartByID(token);
        Log.d(TAG, model.request().url().toString());

        model.enqueue(new Callback<ResponseModel<List<Cart>>>() {

            @Override
            public void onResponse(Call<ResponseModel<List<Cart>>> call, Response<ResponseModel<List<Cart>>> response) {
                Log.d(TAG, String.valueOf(response.body().getCode()));
                ResponseUtil.onResponseError(getActivity(), response.body().getCode());
                if (response.body().getCode() == 200 && response.body().getDesc().equals("success"))
                {
                    Log.d(TAG, String.valueOf(response.body().getData()));

                    final List<Cart> data = response.body().getData();

                    Observable.create(new ObservableOnSubscribe<List<Cart>>() {
                        @Override
                        public void subscribe(ObservableEmitter<List<Cart>> emitter) {
                            emitter.onNext(data);
                        }
                    }).subscribe(new Consumer<List<Cart>>() {
                        @Override
                        public void accept(List<Cart> goods) throws Exception {
                            initPtrFrame();
                            initDataFromCartList(goods);
                            actionBarEdit.setVisibility(View.VISIBLE);
                            llCart.setVisibility(View.VISIBLE);

                            empty_shopcart.setVisibility(View.GONE);//这里发生过错误
                            initEvents();
                        }
                    }).isDisposed();

//                    categoryModelList = DataEngine.loadCategoryData();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<List<Cart>>> call, Throwable t) {
                ResponseUtil.onResponseError(getActivity(), t);
                System.out.print(t.getMessage());
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            loadGoodsDataFromServer("1");
        }
    }

    private void initEvents() {
        actionBarEdit.setOnClickListener(this);
        adapter = new ShopcatAdapter(groups, childs, mcontext);
        adapter.setCheckInterface(this);//关键步骤1：设置复选框的接口
        adapter.setModifyCountInterface(this); //关键步骤2:设置增删减的接口
        adapter.setGroupEditorListener(this);//关键步骤3:监听组列表的编辑状态
        listView.setGroupIndicator(null); //设置属性 GroupIndicator 去掉向下箭头
        listView.setAdapter(adapter);
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            listView.expandGroup(i); //关键步骤4:初始化，将ExpandableListView以展开的方式显示
        }
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int firstVisiablePostion=view.getFirstVisiblePosition();
                int top=-1;
                View firstView=view.getChildAt(firstVisibleItem);
                UtilsLog.i("childCount="+view.getChildCount());//返回的是显示层面上的所包含的子view的个数
                if(firstView!=null){
                    top=firstView.getTop();
                }
                UtilsLog.i("firstVisiableItem="+firstVisibleItem+",fistVisiablePosition="+firstVisiablePostion+",firstView="+firstView+",top="+top);
                if(firstVisibleItem==0&&top==0){
                    mPtrFrame.setEnabled(true);
                }else{
                    mPtrFrame.setEnabled(false);
                }
            }
        });
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        setCartNum();
    }

    /**
     * 设置购物车的数量
     */
    private void setCartNum() {
        int count = 0;
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            group.setChoosed(allCheckBox.isChecked());
            List<GoodsInfo> Childs = childs.get(group.getId());
            for (GoodsInfo childs : Childs) {
                count++;
            }
        }

        //购物车已经清空
        if (count == 0) {
            clearCart();
        } else {
            shoppingcatNum.setText("购物车(" + count + ")");
        }

    }

    private void clearCart() {
        shoppingcatNum.setText("购物车(0)");
        actionBarEdit.setVisibility(View.GONE);
        llCart.setVisibility(View.GONE);
        empty_shopcart.setVisibility(View.VISIBLE);//这里发生过错误
    }


    private void initDataFromCartList(List<Cart> carts) {
        mcontext = getContext();
        groups = new ArrayList<StoreInfo>();
        childs = new HashMap<String, List<GoodsInfo>>();
        ArrayList<StoreInfo> newList = new ArrayList<StoreInfo>();
        Map<String, List<GoodsInfo>> newMap = new HashMap<String, List<GoodsInfo>>();
        int i = 0;
        for(Cart cart: carts) {
            Log.d(TAG, cart.toString());
            newList.add(new StoreInfo(String.valueOf(i), cart.getShop_name(), cart.getShop_id()));
            List<GoodsInfo> goods = new ArrayList<>();
            for (CartGoodsModel good : cart.getGoods()){
                goods.add(new GoodsInfo(good));
            }
            newMap.put(newList.get(i++).getId(), goods);
        }
        groups.clear();
        groups.addAll(newList);

        childs.clear();
        childs.putAll(newMap);
//        adapter.notifyDataSetChanged();
    }

    /**
     * 删除操作
     * 1.不要边遍历边删除,容易出现数组越界的情况
     * 2.把将要删除的对象放进相应的容器中，待遍历完，用removeAll的方式进行删除
     */
    private void doDelete() {
        List<StoreInfo> toBeDeleteGroups = new ArrayList<StoreInfo>(); //待删除的组元素
        List<String> beMovedGoodsID = new ArrayList<String>();
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            if (group.isChoosed()) {
                toBeDeleteGroups.add(group);
            }
            List<GoodsInfo> toBeDeleteChilds = new ArrayList<GoodsInfo>();//待删除的子元素
            List<GoodsInfo> child = childs.get(group.getId());
            for (int j = 0; j < child.size(); j++) {
                if (child.get(j).isChoosed()) {
                    toBeDeleteChilds.add(child.get(j));

                    beMovedGoodsID.add(child.get(j).getId());
                }
            }
            child.removeAll(toBeDeleteChilds);
        }
        groups.removeAll(toBeDeleteGroups);
        //重新设置购物车
        setCartNum();

        deleteFromServer(beMovedGoodsID);
    }

    private void deleteFromServer(List<String> beMovedGoodsID)
    {
        String beMovedIDStr = "";
        for(String id: beMovedGoodsID)
        {
            beMovedIDStr += id + ",";
        }
        beMovedIDStr = beMovedIDStr.substring(0, beMovedIDStr.length() - 1);
        Log.d(TAG, beMovedIDStr);

        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(Common.API_DOMAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIInterface service = retrofit.create(APIInterface.class);
        Call<ResponseModel> model = service.deleteCart(token, beMovedIDStr);
        Log.d(TAG, model.request().url().toString());

        model.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                ResponseUtil.onResponseError(getActivity(), response.body().getCode());
                Log.d(TAG, response.body().toString());
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                ResponseUtil.onResponseError(getActivity(), t);
            }
        });
    }

    private void updateFromServer(List<String> updateID, List<String> updateNum)
    {
        String beMovedIDStr = "";
        for(String id: updateID)
        {
            beMovedIDStr += id + ",";
        }
        beMovedIDStr = beMovedIDStr.substring(0, beMovedIDStr.length() - 1);
        Log.d(TAG, beMovedIDStr);

        String beMovedNumStr = "";
        for(String num: updateNum)
        {
            beMovedNumStr += num + ",";
        }
        beMovedNumStr = beMovedNumStr.substring(0, beMovedNumStr.length() - 1);
        Log.d(TAG, beMovedNumStr);

        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(Common.API_DOMAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIInterface service = retrofit.create(APIInterface.class);
        Call<ResponseModel> model = service.updateCart(token, beMovedIDStr, beMovedNumStr);
        Log.d(TAG, model.request().url().toString());

        model.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                ResponseUtil.onResponseError(getActivity(),  response.body().getCode());
                Log.d(TAG, response.body().toString());
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                ResponseUtil.onResponseError(getActivity(), t);
            }
        });
    }


    /**
     * @param groupPosition 组元素的位置
     * @param isChecked     组元素的选中与否
     *                      思路:组元素被选中了，那么下辖全部的子元素也被选中
     */
    @Override
    public void checkGroup(int groupPosition, boolean isChecked) {
        StoreInfo group = groups.get(groupPosition);
        List<GoodsInfo> child = childs.get(group.getId());
        for (int i = 0; i < child.size(); i++) {
            child.get(i).setChoosed(isChecked);
        }
        if (isCheckAll()) {
            allCheckBox.setChecked(true);//全选
        } else {
            allCheckBox.setChecked(false);//反选
        }
        adapter.notifyDataSetChanged();
        calulate();
    }

    /**
     * @return 判断组元素是否全选
     */
    private boolean isCheckAll() {
        for (StoreInfo group : groups) {
            if (!group.isChoosed()) {
                return false;
            }
        }
        return true;
    }


    /**
     * @param groupPosition 组元素的位置
     * @param childPosition 子元素的位置
     * @param isChecked     子元素的选中与否
     */
    @Override
    public void checkChild(int groupPosition, int childPosition, boolean isChecked) {
        boolean allChildSameState = true; //判断该组下面的所有子元素是否处于同一状态
        StoreInfo group = groups.get(groupPosition);
        List<GoodsInfo> child = childs.get(group.getId());
        for (int i = 0; i < child.size(); i++) {
            //不选全中
            if (child.get(i).isChoosed() != isChecked) {
                allChildSameState = false;
                break;
            }
        }

        if (allChildSameState) {
            group.setChoosed(isChecked);//如果子元素状态相同，那么对应的组元素也设置成这一种的同一状态
        } else {
            group.setChoosed(false);//否则一律视为未选中
        }

        if (isCheckAll()) {
            allCheckBox.setChecked(true);//全选
        } else {
            allCheckBox.setChecked(false);//反选
        }

        adapter.notifyDataSetChanged();
        calulate();
    }

    @Override
    public void doIncrease(int groupPosition, int childPosition, View showCountView, boolean isChecked) {
        GoodsInfo good = (GoodsInfo) adapter.getChild(groupPosition, childPosition);
        int count = good.getCount();
        count++;

        List<String> idList = new ArrayList<String>();
        idList.add(good.getId());
        List<String> numList = new ArrayList<String>();
        numList.add(String.valueOf(good.getCount() + 1));

        updateFromServer(idList,numList);
        Log.d(TAG, "good.getCount()" + numList.get(0));
        good.setCount(count);
        ((TextView) showCountView).setText(String.valueOf(count));
        adapter.notifyDataSetChanged();
        calulate();
    }


    /**
     * @param groupPosition
     * @param childPosition
     * @param showCountView
     * @param isChecked
     */
    @Override
    public void doDecrease(int groupPosition, int childPosition, View showCountView, boolean isChecked) {
        GoodsInfo good = (GoodsInfo) adapter.getChild(groupPosition, childPosition);
        int count = good.getCount();
        if (count == 1) {
            return;
        }
        List<String> idList = new ArrayList<String>();
        idList.add(good.getId());
        List<String> numList = new ArrayList<String>();
        numList.add(String.valueOf(good.getCount() - 1));

        Log.d(TAG, "good.getCount()" + numList.get(0));
        updateFromServer(idList,numList);

        count--;
        good.setCount(count);
        ((TextView) showCountView).setText("" + count);
        adapter.notifyDataSetChanged();
        calulate();
    }



    @Override
    public void doUpdate(int groupPosition, int childPosition, View showCountView, boolean isChecked) {
        GoodsInfo good = (GoodsInfo) adapter.getChild(groupPosition, childPosition);
        int count = good.getCount();
        List<String> idList = new ArrayList<String>();
        idList.add(good.getId());
        List<String> numList = new ArrayList<String>();
        numList.add(String.valueOf(good.getCount()));

        Log.d(TAG, "good.getCount()" + numList.get(0));
        updateFromServer(idList,numList);

        ((TextView) showCountView).setText(String.valueOf(count));
        adapter.notifyDataSetChanged();
        calulate();
    }


    /**
     * @param groupPosition
     * @param childPosition 思路:当子元素=0，那么组元素也要删除
     */
    @Override
    public void childDelete(int groupPosition, int childPosition) {
        StoreInfo group = groups.get(groupPosition);
        List<GoodsInfo> child = childs.get(group.getId());
        String id = child.get(childPosition).getId();
        child.remove(childPosition);

        List<String> list = new ArrayList<String>();
        list.add(id);
        deleteFromServer(list);
        if (child.size() == 0) {
            groups.remove(groupPosition);
        }
        adapter.notifyDataSetChanged();
        calulate();
    }

    @Override
    public void groupEditor(int groupPosition) {

    }

    @OnClick({R.id.all_checkBox, R.id.go_pay, R.id.share_goods, R.id.collect_goods, R.id.del_goods})
    public void onClick(View view) {
        AlertDialog dialog;
        switch (view.getId()) {
            case R.id.all_checkBox:
                doCheckAll();
                break;
            case R.id.go_pay:
                if (mtotalCount == 0) {
                    UtilTool.toast(mcontext, "请选择要支付的商品");
                    return;
                }
                dialog = new AlertDialog.Builder(mcontext).create();
                dialog.setMessage("总计:" + mtotalCount + "种商品，" + mtotalPrice + "元");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "支付", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                dialog.show();
                break;
            case R.id.share_goods:
                if (mtotalCount == 0) {
                    UtilTool.toast(mcontext, "请选择要分享的商品");
                    return;
                }
                UtilTool.toast(mcontext, "分享成功");
                break;
            case R.id.collect_goods:
                if (mtotalCount == 0) {
                    UtilTool.toast(mcontext, "请选择要收藏的商品");
                    return;
                }
                UtilTool.toast(mcontext, "收藏成功");
                break;
            case R.id.del_goods:
                if (mtotalCount == 0) {
                    UtilTool.toast(mcontext, "请选择要删除的商品");
                    return;
                }
                dialog = new AlertDialog.Builder(mcontext).create();
                dialog.setMessage("确认要删除该商品吗?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doDelete();
                        flag = !flag;
                        setActionBarEditor();
                        setVisiable();
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                dialog.show();
                break;
            case R.id.actionBar_edit:
                flag = !flag;
                setActionBarEditor();
                setVisiable();
                break;
        }
    }

    /**
     * ActionBar标题上点编辑的时候，只显示每一个店铺的商品修改界面
     * ActionBar标题上点完成的时候，只显示每一个店铺的商品信息界面
     */
    private void setActionBarEditor() {
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            if (group.isActionBarEditor()) {
                group.setActionBarEditor(false);
            } else {
                group.setActionBarEditor(true);
            }
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * 全选和反选
     * 错误标记：在这里出现过错误
     */
    private void doCheckAll() {
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            group.setChoosed(allCheckBox.isChecked());
            List<GoodsInfo> child = childs.get(group.getId());
            for (int j = 0; j < child.size(); j++) {
                child.get(j).setChoosed(allCheckBox.isChecked());//这里出现过错误
            }
        }
        adapter.notifyDataSetChanged();
        calulate();
    }

    /**
     * 计算商品总价格，操作步骤
     * 1.先清空全局计价,计数
     * 2.遍历所有的子元素，只要是被选中的，就进行相关的计算操作
     * 3.给textView填充数据
     */
    private void calulate() {
        mtotalPrice = 0.00;
        mtotalCount = 0;
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            List<GoodsInfo> child = childs.get(group.getId());
            for (int j = 0; j < child.size(); j++) {
                GoodsInfo good = child.get(j);
                if (good.isChoosed()) {
                    mtotalCount++;
                    mtotalPrice += good.getPrice() * good.getCount();
                }
            }
        }
        totalPrice.setText("￥" + mtotalPrice + "");
        goPay.setText("去支付(" + mtotalCount + ")");
        if (mtotalCount == 0) {
            setCartNum();
        } else {
            shoppingcatNum.setText("购物车(" + mtotalCount + ")");
        }


    }

    private void setVisiable() {
        if (flag) {
            orderInfo.setVisibility(View.GONE);
            shareInfo.setVisibility(View.VISIBLE);
            actionBarEdit.setText("完成");
        } else {
            orderInfo.setVisibility(View.VISIBLE);
            shareInfo.setVisibility(View.GONE);
            actionBarEdit.setText("编辑");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter = null;
        childs.clear();
        groups.clear();
        mtotalPrice = 0.00;
        mtotalCount = 0;
    }
}