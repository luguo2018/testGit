package com.jmhy.sdk.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.game.qyz.jm.R;
import com.jmhy.sdk.common.ApiListenerInfo;
import com.jmhy.sdk.common.ExitListener;
import com.jmhy.sdk.common.InitListener;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.common.UserApiListenerInfo;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.model.LoginMessageinfo;
import com.jmhy.sdk.model.PaymentInfo;
import com.jmhy.sdk.utils.FloatUtils;
import com.jmhy.sdk.utils.HasNotchInScreenUtil;
import com.jmhy.sdk.utils.Utils;
import com.jmhy.sdk.view.AdTipDialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private Button mBtninit, mBtnlogin, mBtninfo, mBtnpay, mBtnexit, mBtnserver, mBtnlevel, mBtnloginout,mHotFix;
    private Button force_exit;
    private LinearLayout mRoleLayout;
    Activity activity;
    private final int appId = 100001;
    private final String appKey = "69a1f04568822163d335aca0564fd666";

    ApiListenerInfo login = new ApiListenerInfo() {
        @Override
        public void onSuccess(final Object obj) {
            Log.d(TAG, "login Success");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (obj != null) {
                        LoginMessageinfo login = (LoginMessageinfo) obj;
                        if (TextUtils.equals(login.getResult(), "success")) {
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

                        } else {
                            mBtnpay.setVisibility(View.GONE);
                            mRoleLayout.setVisibility(View.GONE);
                            mBtnloginout.setVisibility(View.GONE);
                            force_exit.setVisibility(View.GONE);
                            mBtnlogin.setVisibility(View.VISIBLE);

                            Log.d(TAG, "login Success b");
                        }
                    } else {
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
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.getSdkParams(this);
        /************************
         *    onCreate调用       *
         *    生命周期记得哦^_^  *
         ************************/
        HasNotchInScreenUtil utils=new HasNotchInScreenUtil();

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
        mHotFix=findViewById(R.id.hot_fix);
        /************************
         *    初始化接口调用        *
         *   接口在主线程调用哦^_^  *
         ************************/
        JiMiSDK.initInterface(MainActivity.this, appId, appKey, new InitListener() {
            @Override
            public void Success(String s) {
                Toast.makeText(MainActivity.this, "初始化成功，gif:"+AppConfig.float_icon_url, Toast.LENGTH_SHORT).show();
                String agent=AppConfig.agent;
                Log.d(TAG, "init Success"+agent);


                JiMiSDK.login(MainActivity.this, appId, appKey,login);


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
//                JiMiSDK.login(MainActivity.this, appId, appKey,login);
            }
        });
        activity=this;
        mBtninit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                TelephonyManager tm=(TelephonyManager)activity.getSystemService(TELEPHONY_SERVICE);
//                tm.getDeviceId();
//                /**唯一的dao设备ID：du*GSM手机zhi的daoIMEI和CDMA手机的MEID.*
////                 * ReturnnullifdeviceIDisnotavailable.*/;
//                 /**设备的版软件版本权号：*例如：theIMEI/SV(softwareversion)forGSMphones.*Returnnullifthesoftwareversionisnotavailable.
//                  * */tm.getDeviceSoftwareVersion();//String
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

                String appId2="5112188";
                String adId2="945546866";
//                JmAdSdk.init(activity, appId2);
//                JmAdSdk.loadAd(activity, appId2, adId2,"user123", new AdListener() {
//                    @Override
//                    public void Success(Object var1) {
//                        Log.i("jimi","加载广告回调开始");
//                    }
//
//                    @Override
//                    public void fail(String var1) {
//
//                    }
//                });
            }
        });

        mBtnlogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                JiMiSDK.login(MainActivity.this, appId, appKey,login);

            }
        });

        mBtnexit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                AppConfig.change_game_name = "0";
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
                FloatUtils.showFloatRedDot();
                AppConfig.change_game_name = "1";
            }
        });

        mBtnserver.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("2");
                AppConfig.change_game_name = "0";

            }
        });

        mBtnlevel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendData("3");
                final View rootFloatView = getLayoutInflater().inflate(AppConfig.resourceId(MainActivity.this, "jmlogin_main_9", "layout"), null);
                rootFloatView.findViewById(AppConfig.resourceId(MainActivity.this, "jm_skin9_phone_login_btn", "id")).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("jimi","点击自定");
                        getWindowManager().removeView(rootFloatView);
                    }
                });
                WindowManager.LayoutParams layoutParams=new WindowManager.LayoutParams();
                layoutParams.width= FrameLayout.LayoutParams.MATCH_PARENT;;
                layoutParams.height= FrameLayout.LayoutParams.MATCH_PARENT;;
                getWindowManager().addView(rootFloatView,layoutParams);
            }
        });

        final String data="警告！警告！\n 世界即将毁灭！ \n 请观看广告拯救世界 \n 1秒后开始广告";
        mHotFix.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AdTipDialog exitdialog = new AdTipDialog(activity, AppConfig.resourceId(activity, "jm_MyDialog", "style"),data, new AdTipDialog.AdTipDialogListener() {


                    @Override
                    public void onStratAd() {
                        Toast.makeText(MainActivity.this,"我已经热更新好了",Toast.LENGTH_LONG).show();
                    }

                });
                exitdialog.show();

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
//        JiMiSDK.onStart(this);
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

    WindowManager windowManager;
    WindowManager.LayoutParams windowManagerParams;
    public void createView2(Activity activity) {

        // 1、获取WindowManager对象
        windowManager = (WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);
        // 2、设置LayoutParams(全局变量）相关参数
        windowManagerParams = new WindowManager.LayoutParams();
//        windowManagerParams = ((FloatApplication) getApplication()).getWindowParams();
        //3、设置相关的窗口布局参数 （悬浮窗口效果）
//        windowManagerParams.type = LayoutParams.TYPE_PHONE; // 设置window type
        windowManagerParams.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW; // 设置window type
        windowManagerParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        //4、设置Window flag == 不影响后面的事件  和  不可聚焦
        windowManagerParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * 注意，flag的值可以为：
         * LayoutParams.FLAG_NOT_TOUCH_MODAL 不影响后面的事件
         * LayoutParams.FLAG_NOT_FOCUSABLE  不可聚焦
         * LayoutParams.FLAG_NOT_TOUCHABLE 不可触摸
         */
        //5、 调整悬浮窗口至左上角，便于调整坐标
        windowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值
        windowManagerParams.x = 0;
        windowManagerParams.y = 80;
        //6、设置悬浮窗口长宽数据
        windowManagerParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowManagerParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //获得屏幕的宽高
        Display display = windowManager.getDefaultDisplay();
        final int screenWith = display.getWidth();
        int screenHeight = display.getHeight();
        System.out.println("screenWith="+screenWith+",screenHeight="+screenHeight);

        View view = LayoutInflater.from(activity).inflate(AppConfig.resourceId(this, "en_floating_view", "layout"), null);
        windowManager.addView(view, windowManagerParams); // 显示myFloatView图像
    }

    private String getSignValidString(byte[] paramArrayOfByte) throws NoSuchAlgorithmException {
        MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
        localMessageDigest.update(paramArrayOfByte);
        return toHexString(localMessageDigest.digest());
    }

    public String toHexString(byte[] paramArrayOfByte) {
        if (paramArrayOfByte == null) {
            return null;
        }
        StringBuilder localStringBuilder = new StringBuilder(2 * paramArrayOfByte.length);
        for (int i = 0; ; i++) {
            if (i >= paramArrayOfByte.length) {
                return localStringBuilder.toString();
            }
            String str = Integer.toString(0xFF & paramArrayOfByte[i], 16);
            if (str.length() == 1) {
                str = "0" + str;
            }
            localStringBuilder.append(str);
        }
    }

}
