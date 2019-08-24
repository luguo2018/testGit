package com.jmhy.sdk.sample;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.common.ApiListenerInfo;
import com.jmhy.sdk.common.ExitListener;
import com.jmhy.sdk.common.InitListener;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.common.UserApiListenerInfo;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.model.LoginMessageinfo;
import com.jmhy.sdk.model.PaymentInfo;

public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private Button mBtninit, mBtnlogin, mBtninfo, mBtnpay, mBtnexit, mBtnserver, mBtnlevel, mBtnloginout;
    private View force_exit;

    /*private final int appId = 100001;
    private final String appKey = "69a1f04568822163d335aca0564fd666";*/

    private final int appId = 103609;
    private final String appKey = "24687b687f6f858182b26dc5180258a0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtninit = (Button) findViewById(R.id.initbt);
        mBtnlogin = (Button) findViewById(R.id.loginbt);
        mBtninfo = (Button) findViewById(R.id.info);
        mBtnpay = (Button) findViewById(R.id.paybt);
        mBtnexit = (Button) findViewById(R.id.exitbt);
        mBtnserver = (Button)findViewById(R.id.server);
        mBtnlevel = (Button)findViewById(R.id.level);
        mBtnloginout = (Button)findViewById(R.id.loginout);

        force_exit = findViewById(R.id.force_exit);
        force_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                JiMiSDK.forceLogout("账号禁止登录，请联系客服人员");
            }
        });

        JiMiSDK.setUserListener(new UserApiListenerInfo(){
            @Override
            public void onLogout(Object obj) {
                mBtnpay.setVisibility(View.GONE);
                mBtnloginout.setVisibility(View.GONE);
                force_exit.setVisibility(View.GONE);
                mBtnlogin.setVisibility(View.VISIBLE);
            }
        });

        mBtninit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                JiMiSDK.initInterface(MainActivity.this, appId, appKey, new InitListener() {
                    @Override
                    public void Success(String s) {
                        Toast.makeText(MainActivity.this, "init Success", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "init Success");
                    }

                    @Override
                    public void fail(String s) {
                        Toast.makeText(MainActivity.this, "init failure", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "init fail");
                    }
                });
            }
        });

        mBtnlogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                JiMiSDK.login(MainActivity.this, appId, appKey, new ApiListenerInfo(){
                    @Override
                    public void onSuccess(final Object obj) {
                        Log.d(TAG, "login Success");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(obj != null){
                                    LoginMessageinfo login = (LoginMessageinfo)obj;
                                    if(TextUtils.equals(login.getResult(), "success")) {
                                        mBtnpay.setVisibility(View.VISIBLE);
                                        mBtnloginout.setVisibility(View.VISIBLE);
                                        force_exit.setVisibility(View.VISIBLE);
                                        mBtnlogin.setVisibility(View.GONE);

                                        Log.d(TAG, "login Success a");

                                        if(TextUtils.equals(AppConfig.is_sdk_float_on, "1")) {
                                            JiMiSDK.showFloat();
                                        }


                                        /*Intent intent = new Intent();
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.putExtra("url", "https://test.172jm.com/community/dist/notice_discount.html");
                                        intent.putExtra("notice", true);
                                        intent.setClass(JiMiSDK.context, JmUserinfoActivity.class);
                                        JiMiSDK.context.startActivity(intent);*/
                                    }else{
                                        mBtnpay.setVisibility(View.GONE);
                                        mBtnloginout.setVisibility(View.GONE);
                                        force_exit.setVisibility(View.GONE);
                                        mBtnlogin.setVisibility(View.VISIBLE);

                                        Log.d(TAG, "login Success b");
                                    }
                                }else{
                                    mBtnpay.setVisibility(View.GONE);
                                    mBtnloginout.setVisibility(View.GONE);
                                    force_exit.setVisibility(View.GONE);
                                    mBtnlogin.setVisibility(View.VISIBLE);

                                    Log.d(TAG, "login Success c");
                                }
                            }
                        });
                    }
                });
            }
        });

        mBtnexit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                JiMiSDK.exit(MainActivity.this, new ExitListener() {
                    @Override
                    public void ExitSuccess(String s) {
                        Log.d(TAG, "exit Success");

                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }

                    @Override
                    public void fail(String s) {
                        Log.d(TAG, "exit fail");
                    }
                });
            }
        });

        mBtnloginout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                JiMiSDK.switchAccount(MainActivity.this);
            }
        });

        mBtnpay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentInfo paymentInfo = new PaymentInfo();
                //单位分
                paymentInfo.setAmount("1");
                paymentInfo.setBalance("100元宝");
                paymentInfo.setCporderid(System.currentTimeMillis()+"");
                paymentInfo.setExt(System.currentTimeMillis()+"");
                paymentInfo.setGender("男");
                paymentInfo.setLevel("99");
                paymentInfo.setOrdername("10元宝");
                paymentInfo.setPower("100000");
                paymentInfo.setRoleid("99");
                paymentInfo.setRolename("极米");
                paymentInfo.setServerno("2000");
                paymentInfo.setZoneName("极米1区");
                paymentInfo.setViplevel("12");

                JiMiSDK.payment(MainActivity.this, paymentInfo, new ApiListenerInfo() {

                    @Override
                    public void onSuccess(Object obj) {
                        super.onSuccess(obj);
                        if (obj != null) {
                            Log.w(TAG, "exit fail");
                            return;
                        }
                        Log.d(TAG, "exit success");
                    }
                });
            }
        });

        JiMiSDK.onCreate(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        JiMiSDK.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //JiMiSDK.onStart(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        JiMiSDK.onRestart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JiMiSDK.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JiMiSDK.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        JiMiSDK.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JiMiSDK.onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JiMiSDK.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }
}
