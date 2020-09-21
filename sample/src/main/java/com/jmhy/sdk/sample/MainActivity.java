package com.jmhy.sdk.sample;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
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


import com.game.qyz.jm.BuildConfig;
import com.game.qyz.jm.R;
import com.jmhy.sdk.common.ApiListenerInfo;
import com.jmhy.sdk.common.ExitListener;
import com.jmhy.sdk.common.InitListener;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.common.UserApiListenerInfo;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.model.LoginMessageinfo;
import com.jmhy.sdk.model.PaymentInfo;
import com.jmhy.sdk.utils.AppUtils;
import com.jmhy.sdk.utils.FloatUtils;
import com.jmhy.sdk.utils.HasNotchInScreenUtil;
import com.jmhy.sdk.utils.Utils;
import com.taobao.sophix.SophixManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private Button mBtninit, mBtnlogin, mBtninfo, mBtnpay, mBtnexit, mBtnserver, mBtnlevel, mBtnloginout,mHotFix,test;
    private Button force_exit;
    private LinearLayout mRoleLayout;

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
//        FloatView mFloatView = new FloatView(this);
//        mFloatView.createView2(this);

//        createView2(this);
        HasNotchInScreenUtil utils=new HasNotchInScreenUtil();

        Log.i("jimi测试","是否存在刘海屏："+utils.hasNotchInScreen(this));
        Log.i("jimi测试","刘海屏高度："+utils.getNotchHigth());
        Log.i("jimi测试", AppUtils.dp2px(this,375)+"------"+ AppUtils.dp2px(this,315));
        try {   //BuildConfig.APPLICATION_ID   当前应用包名
//            PackageInfo packageInfo = getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
            PackageInfo packageInfo = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            String signValidString = getSignValidString(packageInfo.signatures[0].toByteArray());
            Log.e("获取应用签名", BuildConfig.APPLICATION_ID + "---:" + signValidString);
        } catch (Exception e) {
            Log.e("获取应用签名", "异常:" + e);
        }


//        String url="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAF0AAABdCAMAAADwr5rxAAABCFBMVEUAAAD/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/b0f/////cEn/flr/+/r/8Oz/zb//9/T/7un/2c//yLn/+ff/1Mf/uKX/jm//h2X/g2D/ckv//fz/9PD/taD/rpj/nIH/elb/eVP/dlH/dU7/7Ob/4Nj/yrv/qpL/po3/lnn/gV3/6eP/1sr/knT/5t//49v/3dT/xbX/wrH/wK7/u6j/imr/0sX/oYf/noOWT7n6AAAAKHRSTlMAzNLlDvm3ggYC/L5F8smmcFQ7NfagnJaPQBnp09CriXt1XE0nFQHD+GCwTQAAAypJREFUaN612Ida4kAUhuHjUgXsdd2+lv3PhCZIryJg17Wt938nqxKVCGQyw+S9gO85z2RCZiCH2UQk/gW65mY2loI0ydaPAKa1urBC4wQ3QzAivEMjEgGYEvo5Sw57kRgMCgcdT3MNZn3boncbMO3T+/SLMC/8uvYrIfjg5yAe/Qo/hHbsdfFH+GX0L/DJ81v7G35ZeKrH4ZfVIAVj8M0SbcM/GxSBjtZx7yxTPcqV4GaeFqCunE7yQLINF3P0B6o6t1V+9QBX9A2KLo74XVFS/wQlos5DujBab+3zsL7R+n2WHcom6808O8FgvcEfWcbq4pZHnB8aqos0j5HqV0zUKwc8Xq1gTV3v7PNEyRtLUteM2zJFSV22LO6OhH5dHLBMU7suciyV064XWO5at37MHmQ16yfsRUavflrzsd65Yk9qWvUee3OpU//HHu1r1K0Me/RXvS722auGer3PA71rljlRrlt5+y0X5yzTVq7bH6NuBSn5llGtF+2dbEHIH6py3X6kx0CbZZqq9Qt+cSaAU5ZIHqrWD95GR1n+LinWy+9TyWfvq9YLb1PJ68m2at0+pBfw5FBSP4BivcQDj3iWkewY1XrDcbe4YjcpoVpPOw7oN+ymDtX6peMIXXR9pi3let5Rh9vP2A2U62wr4cVhLjNx9FP9+gls4iQv+QHTWJm+7EiWtzTqZ6PnwyaPU4BNZ0emJOeDbEenfjx6Vb+TfE8V6u0kD/RgE0c8Kg2tOnI8UC1h4HHcurQ16yW2XVXwrJXiEdUiNOtIO268ra7kiKRYLyfZlqo36zXJbUa1jrrsmyGmqYtr93gF09RhpVzivQqmq6NU4wmqDWDaOu4nTJ+9gDc0AxdWd9zgtx14RHHJX3j5kbe/BM9oHe5ad6nhNbkrw7sA/YKMuCg8nGUz2W66cS+gIk6f4Z8IRefgm89Ea/BLYJZ8XJof9GQe/ljdoicJ+GOTXnyHHwLBQX03APNiCbIth2BchN4sxWDY+h4N5Q1PvzlLw5YDMCe0SB/smts5X1doVGIeJgQWozTW8tocphML/47SRNHlX+vh+Rkt8YXIdpAc/gO/whgO+4rh6QAAAABJRU5ErkJggg==";
//        MediaUtils x=new MediaUtils();
//        Log.i("jimi测试日志","保存图片"+x.urlReturnBitMap(url));
//        x.saveImage(MainActivity.this, x.urlReturnBitMap(url));
//        Log.i("jimi测试日志","保存图片");
//        url2bitmap.url2bitmap(url);
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
        test = findViewById(R.id.test);
        test.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"测试成功",Toast.LENGTH_LONG).show();
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
                JiMiSDK.login(MainActivity.this, appId, appKey,login);

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
                FloatUtils.showFloatRedDot();
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
        mHotFix.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SophixManager.getInstance().queryAndLoadNewPatch();
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
