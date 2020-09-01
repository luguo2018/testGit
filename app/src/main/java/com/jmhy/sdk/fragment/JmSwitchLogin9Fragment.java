package com.jmhy.sdk.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jmhy.sdk.activity.JmAutoLoginActivity;
import com.jmhy.sdk.activity.JmLoginActivity;
import com.jmhy.sdk.activity.JmTopLoginTipActivity;
import com.jmhy.sdk.adapter.SwitchAccountAdapter9;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.config.WebApi;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.model.LoginMessage;
import com.jmhy.sdk.model.Msg;
import com.jmhy.sdk.push.PushService;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.FloatUtils;
import com.jmhy.sdk.utils.FragmentUtils;
import com.jmhy.sdk.utils.Seference;
import com.jmhy.sdk.utils.UserInfo;
import com.jmhy.sdk.utils.Utils;
import com.jmhy.sdk.view.CustomerCodeView;
import com.jmhy.sdk.view.DeleteDialog;
import com.jmhy.sdk.view.Exitdialog;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.TokenResultListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jmhy.sdk.config.AppConfig.skin9_switch_showDelete;

public class JmSwitchLogin9Fragment extends JmBaseFragment implements
        OnClickListener{

    // 吉米
    private String TAG = "jimisdk";
    private View mBtuser;
    private TextView to_home_login;
    private TextView mIvregister;
    private TextView mTitleTv;
    private EditText mEdphone;
    private String phoneNumber = "18126789165";
    private String code;
    private String autoLoginType;
    private TextView mIbcode, call_kefu, gray_phone_tv;
    private TextView mBtmobilelg;
    private LinearLayout mLinearUl;
    private ImageView changeItem;
    private TextView mTvistor;
    private ImageView mIvkefu, back;
    private TextView mTvagreement;

    private boolean flag = true;
    private boolean showDelete = false;
    private int j = 0;
    private int auto_clickPosition=999;
    private int count_down = 20;

    private ApiAsyncTask mautoLoginTask;

    static List<String> moreAccountList = new ArrayList<String>();
    static List<String> morePwdList = new ArrayList<String>();
    static List<String> moreUidList = new ArrayList<String>();
    static List<String> moreTimeList = new ArrayList<String>();
    static List<String> moreTypeList = new ArrayList<String>();
    static List<HashMap<String, String>> contentList = new ArrayList<HashMap<String, String>>();
    static List<HashMap<String, String>> timeAndTypecontentList = new ArrayList<HashMap<String, String>>();
    private PhoneNumberAuthHelper mAlicomAuthHelper;
    private TokenResultListener mTokenListener;
    private String token;
    private final static int oneKeyLoginFail = 250;
    private int mScreenWidthDp;
    private int mScreenHeightDp;
    private boolean showJimiLogin = false;
    View view = null;
    private EditText editText;
    private TextView complete;
    private StringBuffer stringBuffer = new StringBuffer();
    private int count = 6;
    private ViewGroup container;
    private ListView myListView;
    private CustomerCodeView mySmsView;
    private SwitchAccountAdapter9 mUserAdapter;
    private Context context;
    private String account;

    public JmSwitchLogin9Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stubl
        // 登录
        this.container = container;
        view = inflater.inflate(AppConfig.resourceId(getActivity(), "jm_switch_login", "layout"), container, false);
        view.setClickable(true);//设置可点击的
        view.setVisibility(View.VISIBLE);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        context=getActivity();
//        intView();
        initView();
        skin9_switch_showDelete=false;
        setListInfo(skin9_switch_showDelete);
    }


    private void initView() {
        to_home_login = (TextView) getView().findViewById(AppConfig.resourceId(getActivity(), "to_home_login", "id"));
        to_home_login.setOnClickListener(this);
        myListView = (ListView) getView().findViewById(AppConfig.resourceId(getActivity(), "switch_listView", "id"));
        changeItem = (ImageView) getView().findViewById(AppConfig.resourceId(getActivity(), "changeItem", "id"));
        complete = (TextView) getView().findViewById(AppConfig.resourceId(getActivity(), "changeItem_tv", "id"));
        complete.setOnClickListener(this);
        changeItem.setOnClickListener(this);

//        myListView.setOnItemClickListener(this);
    }



    private void setListInfo(boolean showDelete) {
        Log.i("jimi数据111",moreAccountList+"\n"+moreTimeList+"\n"+moreTypeList);
        if (mSeference.isExitData()) {
            insertDataFromSerference();
        } else {
            insertDataFromFile();
        }
        Log.i("jimi数据",moreAccountList+"\n"+moreTimeList+"\n"+moreTypeList);
        mUserAdapter = new SwitchAccountAdapter9(getActivity(), moreAccountList, moreTimeList, moreTypeList);
        myListView.setAdapter(mUserAdapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!skin9_switch_showDelete) {
                    auto_clickPosition = position;
                    account=moreAccountList.get(position);
                    if (moreTypeList.size()!=0){
                        autoLogin(moreUidList.get(position), moreTypeList.get(position));
                    }else{
                        autoLogin(moreUidList.get(position), "");
                    }
                }
            }
        });
        mUserAdapter.setOnInnerItemOnClickListener(new SwitchAccountAdapter9.InnerItemOnclickListener() {

                    @Override
                    public void itemClick(final int position) {
                        // TODO Auto-generated method stub
                        DeleteDialog exitdialog = new DeleteDialog(getActivity(), AppConfig.resourceId(getActivity(), "jm_MyDialog", "style"), new DeleteDialog.DeleteDialogListener() {
                            @Override
                            public void onDelete() {
                                Log.i("jimi","删除"+moreAccountList.get(position));
                                mSeference.clearingTimeAndType(moreAccountList.get(position));
                                mSeference.clearingAccount(moreAccountList.get(position));
                                moreAccountList.remove(position);
                                moreUidList.remove(position);
                                morePwdList.remove(position);

                                moreTimeList.remove(position);
                                moreTypeList.remove(position);

                                mUserAdapter.notifyDataSetChanged();
                                Utils.saveUserToSd(getActivity());
                                Utils.saveTimeAndTypeToSd(getActivity());
                            }

                            @Override
                            public void onCancel() {
                                Log.i("jimi","取消删除");
                            }
                        });
                        exitdialog.show();

                    }
                });




    }



    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            switch (msg.what) {
                case AppConfig.AUTO_LOGIN_SUCCESS:
                    LoginMessage result = (LoginMessage) msg.obj;
                    Intent autoLoginIntent = new Intent(getActivity(), JmTopLoginTipActivity.class);
                    autoLoginIntent.putExtra("message", result.getMessage());
                    autoLoginIntent.putExtra("openId", result.getOpenid());
                    autoLoginIntent.putExtra("uName", result.getUname());
                    autoLoginIntent.putExtra("token", result.getGame_token());
                    autoLoginIntent.putExtra("url", Utils.toBase64url(result.getShow_url_after_login()));
                    autoLoginIntent.putExtra("type", AppConfig.AUTO_LOGIN_SUCCESS);
                    startActivity(autoLoginIntent);
                    getActivity().finish();


                    break;
                case AppConfig.FLAG_FAIL:
                    String resultmsg = (String) msg.obj;
                    showMsg(resultmsg);
                    JmSwitchLogin9Fragment.deleteAccount(getActivity(),true,account);
                    AppConfig.ismobillg = false;
                    Fragment mJmUserLoginFragment = FragmentUtils.getJmUserLoginFragment(getActivity());
                    replaceFragmentToActivity(getFragmentManager(), mJmUserLoginFragment, AppConfig.resourceId(getActivity(), "content", "id"));
                    break;
                case AppConfig.CODE_SUCCESS:
                    flag = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            while (flag) {

                                handler.sendEmptyMessage(AppConfig.CODE_BUTTON);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
//                                     TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    Msg msg2 = (Msg) msg.obj;
                    showMsg(msg2.getMessage());
                    break;
                case AppConfig.CODE_FAIL:
                    String fail_result = (String) msg.obj;
                    showMsg(fail_result);

                    mSeference.clearingAccount(moreAccountList.get(auto_clickPosition));
                    moreAccountList.remove(auto_clickPosition);
                    moreUidList.remove(auto_clickPosition);
                    morePwdList.remove(auto_clickPosition);

                    moreTimeList.remove(auto_clickPosition);
                    moreTypeList.remove(auto_clickPosition);

                    mUserAdapter.notifyDataSetChanged();
                    Utils.saveUserToSd(getActivity());
                    Utils.saveTimeAndTypeToSd(getActivity());

                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        if (id == AppConfig.resourceId(getActivity(), "changeItem", "id")) {//删除item
            setListInfo(!skin9_switch_showDelete);
            skin9_switch_showDelete=!skin9_switch_showDelete;
            changeItem.setVisibility(skin9_switch_showDelete?View.GONE:View.VISIBLE);
            complete.setVisibility(skin9_switch_showDelete?View.VISIBLE:View.GONE);
        }
        else if (id == AppConfig.resourceId(getActivity(), "changeItem_tv", "id")) {
            setListInfo(!skin9_switch_showDelete);
            skin9_switch_showDelete=!skin9_switch_showDelete;
            complete.setVisibility(skin9_switch_showDelete?View.VISIBLE:View.GONE);
            changeItem.setVisibility(skin9_switch_showDelete?View.GONE:View.VISIBLE);
        }
        else if (id == AppConfig.resourceId(getActivity(), "to_home_login", "id")) {//返回登录首页
            AppConfig.skin9_is_switch = false;
            Fragment mJmUserLoginFragment = Fragment.instantiate(getActivity(), JmLoginHomePage9Fragment.class.getName());
            replaceFragmentToActivity(getFragmentManager(), mJmUserLoginFragment, AppConfig.resourceId(getActivity(), "content", "id"));
        }
    }

    /**
     * 从文件中获取数据
     */
    private void insertDataFromFile() {
        // TODO Auto-generated method stub
        moreAccountList.clear();
        morePwdList.clear();
        moreUidList.clear();
        moreTimeList.clear();
        moreTypeList.clear();
        Map<String, String> map = new HashMap<String, String>();
        map = mUserinfo.userMap();
        // 判断由于程序出现什么异常导致某些信息没有写入文件系统
        for (int i = 0; i < map.size(); i++) {
            String tU = map.get("user" + i);
            String tempUser = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[0] : "empty");
            String tempPwd = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[1] : "empty");
            String tempUid = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[2] : "empty");

            if (!tempUid.equals("empty") && !tempUser.equals("empty") && !tempPwd.equals("empty")) {
                moreAccountList.add(tempUser);
                morePwdList.add(tempPwd);
                moreUidList.add(tempUid);
            }
            if (AppConfig.skin==9) {
                if (tU.length() > 3) {
                    String time = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[3] : "empty");
                    String loginType = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[4] : "empty");
                    if (time.equals("empty") && !loginType.equals("empty")) {
                        moreTimeList.add(time);
                        moreTypeList.add(loginType);
                    }
                }
            }
        }
        for (int i = map.size() - 1; i >= 0; i--) {
            String tU = map.get("user" + i);
            assert tU != null;
            String tempUser = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[0] : "empty");
            String tempPwd = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[1] : "empty");
            String tempUid = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[2] : "empty");
            if (tU.length() > 3) {
                String time = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[3] : "empty");
                String loginType = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[4] : "empty");
                if (time.equals("empty") && !loginType.equals("empty")) {
                    mSeference.saveTimeAndType(tempUser,time, loginType);
                }
            }
            if (!tempUser.equals("empty") && !tempPwd.equals("empty") && !tempUid.equals("empty")) {
                mSeference.saveAccount(tempUser, tempPwd, tempUid);
            }
        }
    }

    /**
     * 从preference获取数据
     */
    public boolean insertDataFromSerference() {
        moreAccountList.clear();
        morePwdList.clear();
        moreUidList.clear();
        moreTimeList.clear();
        moreTypeList.clear();
        contentList = mSeference.getContentList();
        if (contentList == null)
            return false;
        for (int i = 0; i < contentList.size(); i++) {
            moreAccountList.add(contentList.get(i).get("account"));
            morePwdList.add(contentList.get(i).get("password"));
            moreUidList.add(contentList.get(i).get("uid"));
        }

        timeAndTypecontentList = mSeference.getTimeAndTypeContentList();
        Log.i("jimi测试", "timeAndTypecontentList数据：" + timeAndTypecontentList);
        if (timeAndTypecontentList == null)
            return false;
        Log.i("jimi测试", "--------数据：" + timeAndTypecontentList+"---"+contentList);
        if (timeAndTypecontentList.size()<contentList.size()){//账号数据多  时间类型数据少  旧版→新版
            for (int i = 0; i < (timeAndTypecontentList.size()+contentList.size()); i++) {
                moreTimeList.add(new SimpleDateFormat("MM月dd日 HH:mm:ss").format(new Date()));
                moreTypeList.add("");
            }
        }else {
            for (int i = 0; i < timeAndTypecontentList.size(); i++) {
                moreTimeList.add(timeAndTypecontentList.get(i).get("time"));
                moreTypeList.add(timeAndTypecontentList.get(i).get("loginType"));
            }
        }
        return true;
    }


    @Override
    public void onDestroy() {
        if (mautoLoginTask != null) {
            mautoLoginTask.cancel(false);
        }
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
        flag = false;

        j = 0;
    }
    public void autoLogin(String logintoken, final String type) {
        mautoLoginTask = JmhyApi.get().starlAutoLogin(getActivity(), AppConfig.appKey, logintoken, new ApiRequestListener() {

            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    LoginMessage loginMessage = (LoginMessage) obj;

                    if (loginMessage.getCode().equals("0")) {
                        Log.i("jimi","账号"+loginMessage.getUname()+"类型"+type);
                        mSeference.saveTimeAndType(loginMessage.getUname(),new SimpleDateFormat("MM月dd日 HH:mm:ss").format(new Date()),type);

                        mSeference.saveAccount(loginMessage.getUname(), "~~test",
                                loginMessage.getLogin_token());
                        AppConfig.saveMap(loginMessage.getUname(), "~~test",
                                loginMessage.getLogin_token());
                        Utils.saveUserToSd(getActivity());
                        Utils.saveTimeAndTypeToSd(getActivity());
                        sendData(AppConfig.AUTO_LOGIN_SUCCESS, obj,
                                handler);

                    } else {
                        sendData(AppConfig.FLAG_FAIL, loginMessage.getMessage(),
                                handler);
                    }
                } else {

                    sendData(AppConfig.FLAG_FAIL, AppConfig.getString(getActivity(), "http_rror_msg"),
                            handler);
                }
            }

            @Override
            public void onError(int statusCode) {
                sendData(AppConfig.FLAG_FAIL, AppConfig.getString(getActivity(), "http_rror_msg"),
                        handler);
            }
        });
    }

    public static void deleteAccount(Activity activity,boolean isJSChange,String deleteAccount) {
        Seference mSeference = new Seference(activity);
        if (mSeference.isExitData()) {
            moreAccountList.clear();
            morePwdList.clear();
            moreUidList.clear();
            moreTimeList.clear();
            moreTypeList.clear();
            contentList = mSeference.getContentList();
            if (contentList == null)
                return;
            for (int i = 0; i < contentList.size(); i++) {
                moreAccountList.add(contentList.get(i).get("account"));
                morePwdList.add(contentList.get(i).get("password"));
                moreUidList.add(contentList.get(i).get("uid"));
            }

            timeAndTypecontentList = mSeference.getTimeAndTypeContentList();
            Log.i("jimi测试", "timeAndTypecontentList数据：" + timeAndTypecontentList);
            if (timeAndTypecontentList == null)
                return ;
            for (int i = 0; i < timeAndTypecontentList.size(); i++) {
                moreTimeList.add(timeAndTypecontentList.get(i).get("time"));
                moreTypeList.add(timeAndTypecontentList.get(i).get("loginType"));
            }
        }
        else {
            moreAccountList.clear();
            morePwdList.clear();
            moreUidList.clear();
            moreTimeList.clear();
            moreTypeList.clear();
            Map<String, String> map = new HashMap<String, String>();
            map = new UserInfo().userMap();
            // 判断由于程序出现什么异常导致某些信息没有写入文件系统
            for (int i = 0; i < map.size(); i++) {
                String tU = map.get("user" + i);
                String tempUser = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[0] : "empty");
                String tempPwd = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[1] : "empty");
                String tempUid = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[2] : "empty");

                if (!tempUid.equals("empty") && !tempUser.equals("empty") && !tempPwd.equals("empty")) {
                    moreAccountList.add(tempUser);
                    morePwdList.add(tempPwd);
                    moreUidList.add(tempUid);
                }
                if (tU.length() > 3) {
                    String time = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[3] : "empty");
                    String loginType = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[4] : "empty");
                    if (time.equals("empty") && !loginType.equals("empty")) {
                        moreTimeList.add(time);
                        moreTypeList.add(loginType);
                    }
                }
            }
            for (int i = map.size() - 1; i >= 0; i--) {
                String tU = map.get("user" + i);
                assert tU != null;
                String tempUser = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[0] : "empty");
                String tempPwd = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[1] : "empty");
                String tempUid = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[2] : "empty");
                if (tU.length() > 3) {
                    String time = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[3] : "empty");
                    String loginType = ((tU != null && tU.split(":").length == 3) ? tU.split(":")[4] : "empty");
                    if (time.equals("empty") && !loginType.equals("empty")) {
                        mSeference.saveTimeAndType(tempUser,time, loginType);
                    }
                }
                if (!tempUser.equals("empty") && !tempPwd.equals("empty") && !tempUid.equals("empty")) {
                    mSeference.saveAccount(tempUser, tempPwd, tempUid);
                }
            }

        }

        int position = -1;
        if (isJSChange){//h5改号去账号里比对删除该账号
            for(int i=0;i<moreAccountList.size();i++){
                if (deleteAccount.equals(moreAccountList.get(i))){
                    position=i;
                }
            }
        }else{//原生登录后才会触发   直接取第0个记录去删
            position=0;
        }
        if (position != -1) {
            mSeference.clearingTimeAndType(moreAccountList.get(position));
            mSeference.clearingAccount(moreAccountList.get(position));
            moreAccountList.remove(position);
            moreUidList.remove(position);
            morePwdList.remove(position);
            moreTimeList.remove(position);
            moreTypeList.remove(position);
        }
        Utils.saveUserToSd(activity);
        Utils.saveTimeAndTypeToSd(activity);
    }

}
