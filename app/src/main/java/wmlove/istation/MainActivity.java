package wmlove.istation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ycl.tabview.library.TabView;
import com.ycl.tabview.library.TabViewChild;

import java.util.ArrayList;
import java.util.List;

import wmlove.istation.fragment.BlankFragment;
import wmlove.istation.fragment.CartFragment;
import wmlove.istation.fragment.CascadeFragment;
import wmlove.istation.fragment.HomeFragment;
import wmlove.istation.fragment.MeFragment;
import wmlove.istation.fragment.MvcFragment;

public class MainActivity extends AppCompatActivity implements MvcFragment.OnFragmentInteractionListener {

    private final int CAMERA_OK = 1;


    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT>22){
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.CAMERA
                        , Manifest.permission.READ_EXTERNAL_STORAGE},CAMERA_OK);

            }else {
                //说明已经获取到摄像头权限了 想干嘛干嘛
            }
        }else {
            //这个说明系统版本在6.0之下，不需要动态获取权限。
        }


        List<TabViewChild> tabViewChildList = new ArrayList<>();
        TabViewChild tabViewChild01 = new TabViewChild(R.drawable.house_press, R.drawable.house_not_press, "首页", HomeFragment.newInstance("", ""));
        TabViewChild tabViewChild02 = new TabViewChild(R.drawable.arrow_press, R.drawable.arrow_not_press, "分类", CascadeFragment.newInstance("",""));
        TabViewChild tabViewChild04 = new TabViewChild(R.drawable.shopping_cart_press, R.drawable.shopping_cart_not_press, "购物车", CartFragment.newInstance("", ""));
        TabViewChild tabViewChild05 = new TabViewChild(R.drawable.me_press, R.drawable.me_no_press, "我的", MeFragment.newInstance("", ""));
        tabViewChildList.add(tabViewChild01);
        tabViewChildList.add(tabViewChild02);
        tabViewChildList.add(tabViewChild04);
        tabViewChildList.add(tabViewChild05);

        TabView tabView = (TabView) findViewById(R.id.tabView);
        tabView.setTabViewChild(tabViewChildList, getSupportFragmentManager());
        tabView.setOnTabChildClickListener(new TabView.OnTabChildClickListener() {
            @Override
            public void onTabChildClick(int position, ImageView currentImageIcon, TextView currentTextView) {
            }
        });

        tabView.setTextViewSelectedColor(R.color.tabview_text_selected);
        tabView.setTextViewUnSelectedColor(Color.GRAY);
        tabView.setTabViewBackgroundColor(Color.WHITE);
        tabView.setTabViewHeight(dip2px(this, 52));
        tabView.setImageViewTextViewMargin(2);
        tabView.setTextViewSize(14);
        tabView.setImageViewWidth(dip2px(this, 30));
        tabView.setImageViewHeight(dip2px(this, 30));
        tabView.setTabViewGravity(Gravity.TOP);
    }


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_OK:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以

                }else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    Toast.makeText(MainActivity.this,"请手动打开相机权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }
}
