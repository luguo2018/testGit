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
import android.widget.LinearLayout;
import android.widget.Toast;

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
    private LinearLayout mRoleLayout;

    private final int appId = 100001;
    private final String appKey = "69a1f04568822163d335aca0564fd666";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /************************
         *    onCreate调用       *
         *    生命周期记得哦^_^  *
         ************************/
        JiMiSDK.onCreate(this);

        mBtninit = (Button) findViewById(R.id.initbt);
        mBtnlogin = (Button) findViewById(R.id.loginbt);
        mBtninfo = (Button) findViewById(R.id.info);
        mBtnpay = (Button) findViewById(R.id.paybt);
        mBtnexit = (Button) findViewById(R.id.exitbt);
        mBtnserver = (Button)findViewById(R.id.server);
        mBtnlevel = (Button)findViewById(R.id.level);
        mBtnloginout = (Button)findViewById(R.id.loginout);
        mRoleLayout = findViewById(R.id.role_ll);

        force_exit = findViewById(R.id.force_exit);
        force_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                JiMiSDK.forceLogout("账号禁止登录，请联系客服人员");
            }
        });
        /************************
         *    初始化接口调用        *
         *   接口在主线程调用哦^_^  *
         ************************/
        JiMiSDK.initInterface(MainActivity.this, appId, appKey, new InitListener() {
            @Override
            public void Success(String s) {
                Toast.makeText(MainActivity.this, "init success", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "init Success");
            }

            @Override
            public void fail(String s) {
                Toast.makeText(MainActivity.this, "init failure", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "init fail");
            }
        });

        JiMiSDK.setUserListener(new UserApiListenerInfo(){
            @Override
            public void onLogout(Object obj) {
                mBtnpay.setVisibility(View.GONE);
                mRoleLayout.setVisibility(View.GONE);
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
                                        mRoleLayout.setVisibility(View.VISIBLE);
                                        mBtnloginout.setVisibility(View.VISIBLE);
                                        force_exit.setVisibility(View.VISIBLE);
                                        mBtnlogin.setVisibility(View.GONE);

                                        Log.d(TAG, "login Success a");
                                        /************************
                                         *         显示浮点         *
                                         *   接口在主线程调用哦^_^  *
                                         ************************/
                                        JiMiSDK.showFloat();

                                    }else{
                                        mBtnpay.setVisibility(View.GONE);
                                        mRoleLayout.setVisibility(View.GONE);
                                        mBtnloginout.setVisibility(View.GONE);
                                        force_exit.setVisibility(View.GONE);
                                        mBtnlogin.setVisibility(View.VISIBLE);

                                        Log.d(TAG, "login Success b");
                                    }
                                }else{
                                    mBtnpay.setVisibility(View.GONE);
                                    mRoleLayout.setVisibility(View.GONE);
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
                            /************************
                             *   支付回调
                             *   成功: success
                             *   失败：fail
                             *   取消: cancel
                             ************************/
                            String str = (String)obj;
                            switch (str) {
                                case "success":
                                    Toast.makeText(MainActivity.this,"支付成功",Toast.LENGTH_SHORT).show();
                                    break;
                                case "fail":
                                    Toast.makeText(MainActivity.this,"支付失败",Toast.LENGTH_SHORT).show();
                                    break;
                                case "cancel":
                                    Toast.makeText(MainActivity.this,"支付取消",Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return;
                        }
                    }
                });
            }
        });


        mBtninfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("1");

            }
        });

        mBtnserver.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("2");


            }
        });

        mBtnlevel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("3");

            }
        });

    }

    private void sendData(String type) {
        /************************
         *     信息上报接口        *
         *   type 创建用户角色(1)
         *         进入服务器(2)
         *          玩家升级(3)
         ************************/
        String roleid = "99";
        String rolename = "极米";
        String level = "0";
        String gender = "男";
        String serverno = "2000";
        String zoneName = "极米1区";
        String balance = "100元宝";
        String power = "16000";
        String viplevel = "12";
        String roleCTime = System.currentTimeMillis() + "";
        String roleLevelMTime = System.currentTimeMillis() + "";
        String ext = "";
        JiMiSDK.setExtData(MainActivity.this,type,roleid,rolename,level,gender,serverno,zoneName,balance,power,viplevel,roleCTime,roleLevelMTime,ext);
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

    @Override
    public void onBackPressed() {
        mBtnexit.performClick();
    }
}
