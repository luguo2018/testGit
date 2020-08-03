package com.jmhy.sdk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.model.LoginMessage;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.Seference;
import com.jmhy.sdk.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class JmAutoLoginActivity extends JmBaseActivity {
	
	private TextView mTvname;
	private ApiAsyncTask mautoLoginTask;
	private View mBtback;
	List<String> moreCountList = new ArrayList<>();
	List<String> morePwdList = new ArrayList<>();
	List<String> moreUidList = new ArrayList<>();
	List<HashMap<String, String>> contentList = new ArrayList<>();
	TimerTask task;
    Timer timer ;
    String temUid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(AppConfig.resourceId(this, "jmautologin", "layout"));

		switch (AppConfig.skin){
			case 9:
				setContentView(AppConfig.resourceId(this, "jmautologin_new", "layout"));
				break;
			case 8:
				setContentView(AppConfig.resourceId(this, "jmautologin_new", "layout"));
				break;
			case 7:
				setContentView(AppConfig.resourceId(this, "jmautologin_new", "layout"));
				break;
			case 6:
				setContentView(AppConfig.resourceId(this, "jmautologin_6", "layout"));
				break;
			case 5:
			case 4:
				setContentView(AppConfig.resourceId(this, "jmautologin_4", "layout"));
				break;
			case 3:
				setContentView(AppConfig.resourceId(this, "jmautologin_3", "layout"));
				break;
			case 2:
				setContentView(AppConfig.resourceId(this, "jmautologin_new", "layout"));
				break;
			default:
				setContentView(AppConfig.resourceId(this, "jmautologin", "layout"));
		}

		initView();
	}

	@Override
	public void onBackPressed() {
		backListener.onClick(null);
	}

	private OnClickListener backListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if(mautoLoginTask != null){
				mautoLoginTask.cancel(false);
			}
			AppConfig.ismobillg=false;
			timer.cancel();
			Intent intent = new Intent(JmAutoLoginActivity.this, JmLoginActivity.class);
			startActivity(intent);
			finish();
		}
	};

	private void initView() {
		mTvname = (TextView)findViewById(AppConfig.resourceId(this, "tvusername", "id"));
        mBtback = findViewById(AppConfig.resourceId(this, "btbacklogin", "id"));
        mBtback.setOnClickListener(backListener);
        
        if (mSeference.isExitData()) {
			String temUser = mSeference.getPreferenceData(
					Seference.ACCOUNT_FILE_NAME, Seference.ACCOUNT_1);
			String temPwd = mSeference.getContentPW(Seference.PASSWORD_1);
			temUid = mSeference.getPreferenceData(
					Seference.ACCOUNT_FILE_NAME, Seference.UID_1);
			
			AppConfig.saveMap(temUser, temPwd, temUid);
			mTvname.setText(temUser);
			
		} else if (mUserinfo.isFile()) {
			insertDataFromFile();
			String temUser = moreCountList.get(0);
			String temPwd = morePwdList.get(0);
			 temUid = moreUidList.get(0);
			AppConfig.saveMap(temUser, temPwd, temUid);
			mTvname.setText(temUser);
		}
     
        task = new TimerTask() {
            @Override
            public void run() {
            	autoLogin(temUid);
        }};
        timer = new Timer();
        timer.schedule(task, 3000);//设置延迟3秒访问
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			case AppConfig.FLAG_FAIL:

				String resultmsg = (String) msg.obj;
				showMsg(resultmsg);
				AppConfig.ismobillg=false;
				Intent intent = new Intent(JmAutoLoginActivity.this, JmLoginActivity.class);
				startActivity(intent);
				finish();
				break;

			case AppConfig.AUTO_LOGIN_SUCCESS:
				LoginMessage result = (LoginMessage) msg.obj;
				showUserMsg(result.getUname());
				String url = Utils.toBase64url(result.getShow_url_after_login());
				turnToNotice(url);
				finish();
				break;
				}
			}
		};
	/**
	 * 从文件中获取数据
	 */
	private void insertDataFromFile() {
		// TODO Auto-generated method stub
		moreCountList.clear();
		morePwdList.clear();
		moreUidList.clear();
		Map<String, String> map = mUserinfo.userMap();
		// 判断由于程序出现什么异常导致某些信息没有写入文件系统
		for (int i = 0; i < map.size(); i++) {
			String tU = map.get("user" + i);
			String tempUser = ((tU != null && tU.split(":").length == 3) ? tU
					.split(":")[0] : "empty");
			String tempPwd = ((tU != null && tU.split(":").length == 3) ? tU
					.split(":")[1] : "empty");
			String tempUid = ((tU != null && tU.split(":").length == 3) ? tU
					.split(":")[2] : "empty");

			if (!tempUid.equals("empty") && !tempUser.equals("empty")
					&& !tempPwd.equals("empty")) {
				moreCountList.add(tempUser);
				morePwdList.add(tempPwd);
				moreUidList.add(tempUid);
			}
		}
		for (int i = map.size() - 1; i >= 0; i--) {
			String tU = map.get("user" + i);
			String tempUser = ((tU != null && tU.split(":").length == 3) ? tU
					.split(":")[0] : "empty");
			String tempPwd = ((tU != null && tU.split(":").length == 3) ? tU
					.split(":")[1] : "empty");
			String tempUid = ((tU != null && tU.split(":").length == 3) ? tU
					.split(":")[2] : "empty");
			if (!tempUser.equals("empty") && !tempPwd.equals("empty")
					&& !tempUid.equals("empty")) {
				mSeference.saveAccount(tempUser, tempPwd, tempUid);
			}
		}
	}

	/**
	 * 从preference获取数据
	 */
	public boolean insertDataFromSerference() {
		moreCountList.clear();
		morePwdList.clear();
		moreUidList.clear();
		contentList = mSeference.getContentList();
		if (contentList == null)
			return false;
		for (int i = 0; i < contentList.size(); i++) {
			moreCountList.add(contentList.get(i).get("account"));
			morePwdList.add(contentList.get(i).get("password"));
			moreUidList.add(contentList.get(i).get("uid"));
		}
		return true;
	}

	public void autoLogin( String logintoken ){
		mautoLoginTask = JmhyApi.get().starlAutoLogin(this,AppConfig.appKey , logintoken, new ApiRequestListener() {

			@Override
			public void onSuccess(Object obj) {
				if(obj!=null){
					LoginMessage loginMessage = (LoginMessage)obj;

					if(loginMessage.getCode().equals("0")){
						mSeference.saveAccount(loginMessage.getUname(), "~~test",
								loginMessage.getLogin_token());
						AppConfig.saveMap(loginMessage.getUname(), "~~test",
								loginMessage.getLogin_token());
						Utils.saveUserToSd(JmAutoLoginActivity.this);
						wrapaLoginInfo("success", loginMessage.getMessage(),
								loginMessage.getUname(),loginMessage.getOpenid(),
								loginMessage.getGame_token());
						sendData(AppConfig.AUTO_LOGIN_SUCCESS, obj,
								handler);

					}else{

						sendData(AppConfig.FLAG_FAIL, loginMessage.getMessage(),
								handler);
					}
				}else{

					sendData(AppConfig.FLAG_FAIL, AppConfig.getString(JmAutoLoginActivity.this, "http_rror_msg"),
							handler);
				}
			}

			@Override
			public void onError(int statusCode) {
				sendData(AppConfig.FLAG_FAIL,  AppConfig.getString(JmAutoLoginActivity.this, "http_rror_msg"),
						handler);
			}
		});
	}
}
