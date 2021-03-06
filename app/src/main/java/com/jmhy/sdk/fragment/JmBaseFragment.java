package com.jmhy.sdk.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jmhy.sdk.activity.FloatUserInfoActivity;
import com.jmhy.sdk.activity.JmAutoLoginActivity;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.model.LoginMessageinfo;
import com.jmhy.sdk.model.Registermsg;
import com.jmhy.sdk.utils.DialogUtils;
import com.jmhy.sdk.utils.Seference;
import com.jmhy.sdk.utils.UserInfo;
import com.jmhy.sdk.utils.Utils;

public class JmBaseFragment extends Fragment implements View.OnTouchListener {
    public Seference mSeference;
    public UserInfo mUserinfo;
    private FloatUserInfoActivity floatUserInfoActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mSeference = new Seference(getActivity());
        mUserinfo = new UserInfo();
        Utils.getSeferencegame(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    FragmentTransaction transaction;

    public void addFragmentToActivity(FragmentManager fragmentManager,
                                      Fragment fragment, int frameId) {
        Utils.checkNotNull(fragmentManager);
        Utils.checkNotNull(fragment);
        transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment).addToBackStack(null);
        transaction.commit();
    }

    public void replaceFragmentToActivity(FragmentManager fragmentManager,
                                          Fragment fragment, int frameId) {
        Utils.checkNotNull(fragmentManager);
        Utils.checkNotNull(fragment);
        transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment).addToBackStack(null);
        transaction.commit();
    }

    public void callKefu() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("url", AppConfig.KEFU);
        intent.putExtra("notice", true);
        intent.setClass(getActivity(), JmUserinfoActivity.class);

        startActivity(intent);
//        turnToIntent(AppConfig.KEFU);
    }

    public void removeFragmentToActivity(FragmentManager fragmentManager, Fragment fragment) {
        Utils.checkNotNull(fragmentManager);
        Utils.checkNotNull(fragment);
        transaction = fragmentManager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }


    public void wrapaLoginInfo(String result, String msg, String userName,
                               String openid, String gametoken) {
        Log.i("JiMiSDK", "wrapaLoginInfo result=" + result + ",msg=" + msg + ",openid=" + openid + ",gametoken=" + gametoken + ",userName=" + userName);

        if (TextUtils.isEmpty(openid) || TextUtils.isEmpty(gametoken)) {
            Log.w("JiMiSDK", "wrapaLoginInfo openid or gametoken is null");
            return;
        }

        if (result.equals("success")) {
            AppConfig.isShow = true;
        }

        JiMiSDK.getStatisticsSDK().onLogin(openid);

        LoginMessageinfo login = new LoginMessageinfo();
        AppConfig.openid = openid;
        login.setMsg(msg);
        login.setResult(result);
        login.setGametoken(gametoken);
        login.setUname(openid);
        login.setOpenid(openid);
        Message mssg = new Message();
        mssg.obj = login;
        mssg.what = 1;
        JiMiSDK.handler.sendMessage(mssg);
    }

    /**
     * 接口返回数据处理
     */
    public void sendData(int num, Object data, Handler callback) {
        Message msg = callback.obtainMessage();
        msg.what = num;
        msg.obj = data;
        msg.sendToTarget();
    }

    /**
     * toast 提示信息
     *
     * @param msg
     */
    public void showUserMsg(String msg) {
        String s = "";
        if (msg.length() > 8) {
            s = msg.substring(0, 8) + "...";
        } else {
            s = msg;
        }
        Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        View layout = View.inflate(getActivity(),
                AppConfig.resourceId(getActivity(), "jmtoast", "layout"), null);
        TextView user = (TextView) layout.findViewById(AppConfig.resourceId(getActivity(), "tvuser", "id"));
        // 设置toast文本，把设置好的布局传进来
        user.setText(s + "");
        toast.setView(layout);
        toast.setGravity(Gravity.TOP, 0, 50);
        toast.show();
    }

    /**
     * 皮肤9切换提示信息
     *
     * @param msg
     */
    public void showUserMsg_skin9(String msg) {
        String s = "";
        if (msg.length() > 8) {
            s = msg.substring(0, 8) + "...";
        } else {
            s = msg;
        }
        Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
        View layout = View.inflate(getActivity(),
                AppConfig.resourceId(getActivity(), "jmautologin_9", "layout"), null);
        TextView user = (TextView) layout.findViewById(AppConfig.resourceId(getActivity(), "tvusername", "id"));
        TextView loginHomePage = (TextView) layout.findViewById(AppConfig.resourceId(getActivity(), "btbacklogin", "id"));
        loginHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//				Intent intent = new Intent(getActivity(), JmSwitchLogin9Fragment.class);
//				startActivity(intent);
            }
        });
        // 设置toast文本，把设置好的布局传进来
        user.setText(s + "");
        toast.setView(layout);
        toast.setGravity(Gravity.TOP, 0, 50);
        toast.show();
    }


    public void showMsg(String msg) {
        DialogUtils.showTip(getActivity(), msg);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // 拦截触摸事件，防止泄露下去
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        return true;
    }

    public void turnToIntent(String url) {
        if (TextUtils.isEmpty(url)) {
            // Toast.makeText(mContext, "此功能暂未开通", Toast.LENGTH_SHORT).show();
            return;
        }
        if (floatUserInfoActivity == null) {

            floatUserInfoActivity = new FloatUserInfoActivity((Activity) JiMiSDK.mContext, new FloatUserInfoActivity.CloseFloatListener() {
                @Override
                public void closeFloat() {
                }
            });
        }
        floatUserInfoActivity.notice = true;
        floatUserInfoActivity.setViews(url);
        floatUserInfoActivity.show();

    }

    public void turnToNotice(final String url) {
        if (TextUtils.isEmpty(url)) {
            // Toast.makeText(mContext, "此功能暂未开通", Toast.LENGTH_SHORT).show();
            return;
        }

        JiMiSDK.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (floatUserInfoActivity == null) {
                    floatUserInfoActivity = new FloatUserInfoActivity((Activity) JiMiSDK.mContext, new FloatUserInfoActivity.CloseFloatListener() {
                        @Override
                        public void closeFloat() {
                        }
                    });
                }
                floatUserInfoActivity.notice = true;
                floatUserInfoActivity.setViews(url);
                floatUserInfoActivity.show();
            }
        }, 1000);
    }

    public void toAutologin(Registermsg registermsg) {
        Intent intent = new Intent(getActivity(), JmAutoLoginActivity.class);
        startActivity(intent);
    }

}
