package wmlove.istation;

import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wmlove.istation.network.APIInterface;
import wmlove.istation.network.model.ResponseModel;
import wmlove.istation.network.model.Token;
import wmlove.istation.utils.Common;
import wmlove.istation.view.LoginElasticScrollView;

import static android.Manifest.permission.READ_CONTACTS;


public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final int REQUEST_READ_CONTACTS = 0;

    private AutoCompleteTextView mEtPhoneNum;
    private EditText mPasswordView;
    private TextView mTvArea;
    private TextView mTvVerify;
    private SharedPreferences mSharedPreferences;
    private ImageView mIvBigPic;
    private RelativeLayout mRLBottom;
    private ImageView mIvMy;

    private int mTopWrapInitHeight = 0;


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

        fullScreen();

        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mSharedPreferences = getSharedPreferences("APP", MODE_PRIVATE);

        if (!TextUtils.isEmpty(mSharedPreferences.getString("tokenStr", ""))) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        mTvVerify = findViewById(R.id.verity);
        mPasswordView = findViewById(R.id.password);
        mEtPhoneNum = findViewById(R.id.phone_num);
        mIvBigPic = findViewById(R.id.img);
        mRLBottom = findViewById(R.id.rl_bottom);
        mIvMy = findViewById(R.id.img_my);


        mIvBigPic.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mTopWrapInitHeight == 0) {//只初始化一遍
                    mTopWrapInitHeight = mIvBigPic.getMeasuredHeight();
                }
            }
        });
        mTopWrapInitHeight = mIvBigPic.getMeasuredHeight();

        final LoginElasticScrollView mScMeWrap = findViewById(R.id.login_form);
        mScMeWrap.setBackView(mIvMy)
                .setBottomHalfView(mRLBottom)
                .setTopHalfView(mIvBigPic)
                .show();

        Glide.with(getApplicationContext()).load(getDrawable(R.drawable.momo)).into(mIvBigPic);

        mTvArea = findViewById(R.id.spanner);
        mTvArea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SelectCountryActivity.class);
                startActivityForResult(intent, 0);
            }
        });
//        mTvArea.attachDataSource(Arrays.asList(getResources().getStringArray(R.array.area_item)));

        final Button mSignInButton = findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                attemptLogin();
                submitCode("86", mEtPhoneNum.getText().toString().replace(" ", ""), mPasswordView.getText().toString());
            }
        });


        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });
        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mSignInButton.setClickable(true);
                    mSignInButton.setBackgroundResource(R.color.primary);
                } else {
                    mSignInButton.setClickable(false);
                    mSignInButton.setBackgroundResource(R.color.gray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 12) {
                    mTvVerify.setClickable(true);
                    mTvVerify.setTextColor(getResources().getColor(R.color.black));

                    mPasswordView.setFocusable(true);
                    mPasswordView.setFocusableInTouchMode(true);
                    mPasswordView.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s == null || s.length() == 0) return;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                        continue;
                    } else {
                        sb.append(s.charAt(i));
                        if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                            sb.insert(sb.length() - 1, ' ');
                        }
                    }
                }
                if (!sb.toString().equals(s.toString())) {
                    int index = start + 1;
                    if (sb.charAt(start) == ' ') {
                        if (before == 0) {
                            index++;
                        } else {
                            index--;
                        }
                    } else {
                        if (before == 1) {
                            index--;
                        }
                    }
                    mEtPhoneNum.setText(sb.toString());
                    mEtPhoneNum.setSelection(index);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        populateAutoComplete();

        final TimeCount time = new TimeCount(60000, 1000);

        mTvVerify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEtPhoneNum.getText().toString().length() < 12) {
                    return;
                }
                mPasswordView.setFocusable(true);
                mPasswordView.setFocusableInTouchMode(true);
                mPasswordView.requestFocus();

                mTvVerify.setTextColor(getResources().getColor(R.color.gray));

                sendCode("86", mEtPhoneNum.getText().toString().replace(" ", ""));
                time.start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String area = data.getStringExtra("area");
            if (!TextUtils.isEmpty(area)) {
                mTvArea.setText(area);
            }
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEtPhoneNum, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }


    public void sendCode(Context context) {
        RegisterPage page = new RegisterPage();
        //如果使用我们的ui，没有申请模板编号的情况下需传null
        page.setTempCode(null);
        page.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 处理成功的结果
                    HashMap<String, Object> phoneMap = (HashMap) data;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");
                    // TODO 利用国家代码和手机号码进行后续的操作
                } else {
                    // TODO 处理错误的结果
                }
            }
        });
        page.show(context);
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mTvVerify.setClickable(false);
            mTvVerify.setText("(" + millisUntilFinished / 1000 + ") 可重新发送");
        }

        @Override
        public void onFinish() {
            mTvVerify.setText("获取验证码");
            mTvVerify.setClickable(true);

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEtPhoneNum.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    public void sendCode(String country, String phone) {
        // 注册一个事件回调，用于处理发送验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理成功得到验证码的结果
                    // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
//                    Toast.makeText(getApplicationContext(), "短信验证码已经发送", Toast.LENGTH_SHORT).show();
                    Log.i("LoginActivity", "短信验证码已经发送");
                } else {
//                    Toast.makeText(getApplicationContext(), "短信验证码发送失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    Log.i("LoginActivity", "短信验证码发送失败，请稍后重试");
                }
                Log.i("LoginActivity", data.toString());

            }
        });
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
    }

    // 提交验证码，其中的code表示验证码，如“1357”
    public void submitCode(String country, final String phone, String code) {
        Log.i("LoginActivity", "code:" + code);
        // 注册一个事件回调，用于处理提交验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理验证成功的结果
                    requestServerLogin(phone);
                    Log.i("LoginActivity", "验证码正确");
                } else {
//                    Toast.makeText(getApplicationContext(), "验证码错误，请稍后重试", Toast.LENGTH_SHORT).show();
                    Log.i("LoginActivity", "验证码错误，请稍后重试");
                }
                Log.i("LoginActivity", data.toString());

            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);
    }

    private void requestServerLogin(final String phone) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Common.API_DOMAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIInterface service = retrofit.create(APIInterface.class);
        Call<ResponseModel<Token>> model = service.login(phone);
        Log.d("LoginActivity", model.request().url().toString());

        model.enqueue(new Callback<ResponseModel<Token>>() {
            @Override
            public void onResponse(Call<ResponseModel<Token>> call, Response<ResponseModel<Token>> response) {
                Log.i("LoginActivity", response.toString());
                if (response.body().toString().contains("success")) {
                    String token = response.body().getData().access_token;
                    mSharedPreferences.edit().putString("tokenStr", String.format("Bearer %s", token)).apply();
                    mSharedPreferences.edit().putString("phoneStr", phone).apply();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
//                    Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
//                    Log.i("LoginActivity", token);
                    finish();

                }
            }

            @Override
            public void onFailure(Call<ResponseModel<Token>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "登录失败，稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
}

