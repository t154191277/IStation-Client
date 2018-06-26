package wmlove.istation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.activity.CaptureActivity;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    private final String TAG = "SearchActivity";

    private SharedPreferences sp;

    private List<String> mVals;

    @BindView(R.id.activity_search_flowlayout)
    TagFlowLayout mFlowLayout;
    @BindView(R.id.btn_search)
    Button btn_search;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.img_delete_flowlayout)
    ImageView val_delete;
    @BindView(R.id.img_back)
    ImageView img_back;
    @BindView(R.id.img_scan)
    ImageView img_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.activity_search, null);

        setContentView(view);
        getWindow().setStatusBarColor(getResources().getColor(R.color.is_bdbdbd));

        ButterKnife.bind(this, view);


        String searchText = getIntent().getStringExtra("searchText");
        if (!TextUtils.isEmpty(searchText)) et_search.setText(searchText);

        sp = getSharedPreferences(TAG, MODE_PRIVATE);

        final Set<String> keyset = sp.getStringSet("searchKey", new LinkedHashSet<String>());

        img_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SearchActivity.this, CaptureActivity.class),1028);
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        val_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVals.clear();
                sp.edit().putStringSet("searchKey", null).apply();
                mFlowLayout.onChanged();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_search.getText().toString();
                name = TextUtils.isEmpty(name) ? et_search.getHint().toString() : name;
                LinkedHashSet<String> newSet = new LinkedHashSet<>(keyset);
                if (newSet.size() > 8)
                {
                    Iterator<String> iterator = newSet.iterator();
                    String key = "";
                    while(iterator.hasNext())
                    {
                        key = iterator.next();
                    }
                    newSet.remove(key);
                }

                newSet.add(name);
                sp.edit().putStringSet("searchKey", newSet).apply();

                Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });

        mVals = new ArrayList<>(keyset);

        mFlowLayout.setAdapter(new TagAdapter<String>(mVals)
        {
            @Override
            public View getView(FlowLayout parent, int position, String s)
            {
                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.tv_search_flowlayout,
                        mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });

        mFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener()
        {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent)
            {
                Toast.makeText(SearchActivity.this, mVals.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                intent.putExtra("name", mVals.get(position));
                intent.putExtra("searchText", et_search.getText().toString());
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1028 && data != null) {
            String id = data.getStringExtra(CaptureActivity.SCAN_QRCODE_RESULT);
            Intent intent = new Intent(SearchActivity.this, GoodDetailActivity.class);
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
    protected void onRestart() {
        super.onRestart();
        Set<String> keyset = sp.getStringSet("searchKey", new LinkedHashSet<String>());
        mVals.clear();
        mVals.addAll(keyset);
        mFlowLayout.onChanged();
    }
}
