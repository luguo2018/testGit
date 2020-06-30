package com.jmhy.sdk.fragment;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.jmhy.sdk.activity.PermissionActivity;
import com.jmhy.sdk.activity.PermissionActivity.PermissionResultListener;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.model.Guest;
import com.jmhy.sdk.utils.MediaUtils;
import com.jmhy.sdk.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JmSetUser8Fragment extends JmBaseFragment implements
		OnClickListener {
	private EditText mEtusername;
	private EditText mEtpwd;
	private View mBtgetgame;
	private TextView mTvmsg;
	private View contentView;

	private String path;
	private File file;
	private String url;

	private String password;
	private String result;
	private String msg;
	private String uname;
	private String gametoken;
	private String openid;
	private TimeCount time;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if(getActivity() == null || getActivity().isFinishing()){
				return;
			}
			switch (msg.what) {
			
			case AppConfig.FLAG_FAIL:
				String resultmsg = (String) msg.obj;
				showMsg(resultmsg);
				break;
			case AppConfig.GUEST_lOGIN_SUCCESS:
				Guest guest = (Guest) msg.obj;
				url = Utils.toBase64url(guest.getShow_url_after_login());

				// turnToIntent(loginMessage.getValid());
				// mEtusername.setText(uname);
				/*
				 * if(!TextUtils.isEmpty(password)){ //
				 * mEtpwd.setText(password); //
				 * mTvmsg.setVisibility(View.VISIBLE); // }else{
				 * 
				 * // mEtpwd.setText("123456"); //
				 * mEtpwd.setInputType(InputType.TYPE_CLASS_TEXT |
				 * InputType.TYPE_TEXT_VARIATION_PASSWORD);
				 * wrapaLoginInfo("success", guest.getMessage(),
				 * guest.getUname(),guest.getOpenid(), guest.getGame_token());
				 * 
				 * turnToIntent(url); //getActivity().finish(); }
				 */

			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (getArguments() != null) {
			uname = getArguments().getString("username");
			password = getArguments().getString("upass");
			msg = getArguments().getString("msg");
			gametoken = getArguments().getString("gametoken");
			openid = getArguments().getString("openid");
			url = getArguments().getString("url");
		}
		View view = null;
		view = inflater.inflate(
				AppConfig.resourceId(getActivity(), "jmsetuser_new", "layout"),
				container, false);
		view.setClickable(true);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
		// getGuest();

		//JiMiSDK.getStatisticsSDK().onRegister("JiMiSDK", true);
	}

	private void initView() {
		// TODO Auto-generated method stub
		mEtusername = (EditText) getView().findViewById(
				AppConfig.resourceId(getActivity(), "edit_user", "id"));
		mEtusername.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		mEtpwd = (EditText) getView().findViewById(
				AppConfig.resourceId(getActivity(), "edit_pwd", "id"));
		mEtpwd.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		mBtgetgame = getView().findViewById(
				AppConfig.resourceId(getActivity(), "btgetgame", "id"));

		contentView = getView().findViewById(
				AppConfig.resourceId(getActivity(), "content_view", "id"));

		mBtgetgame.setOnClickListener(this);
		mEtpwd.setText(password);
		mEtusername.setText(uname);
		mTvmsg = (TextView) getView().findViewById(
				AppConfig.resourceId(getActivity(), "tvmsg", "id"));
		time = new TimeCount(1000, 1000);//控制弹窗显示时间
		time.start();
	
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if (id == AppConfig.resourceId(getActivity(), "btgetgame", "id")) {
			// 返回数据给游戏

			wrapaLoginInfo("success", msg, uname, openid, gametoken);

			turnToIntent(url);
			getActivity().finish();
		}
	}

	private void saveCurrentImage() {
		if(VERSION.SDK_INT < VERSION_CODES.Q){
			List<String> permission = new ArrayList<>();
			permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			PermissionActivity.requestPermission(getActivity(), permission, new PermissionResultListener() {
				@Override
				public void onPermissionResult(boolean grant) {
					if(!grant) {
						JiMiSDK.permissionTip(getActivity(), "jm_permission_tip_init");
					}else{
						saveCurrentSnapshot();
					}
				}
			});
		}else{
			saveCurrentSnapshot();
		}
	}

	/**
	 * 截屏
	 */
	private void saveCurrentSnapshot() {
		if(contentView == null){
			return;
		}

		// 找到当前页面的跟布局
		contentView.setDrawingCacheEnabled(true);
		contentView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		Bitmap temBitmap = Bitmap.createBitmap(contentView.getWidth(), contentView.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(temBitmap);
		contentView.draw(canvas);
		contentView.destroyDrawingCache();

		MediaUtils.saveImage(getActivity(), temBitmap);
		String snapshot = AppConfig.getString(getActivity(), "snapshot_save");
		showMsg(snapshot);
	}

	class TimeCount extends CountDownTimer {
		/**
		 * 构造方法
		 * 
		 * @param millisInFuture
		 *            总倒计时长 毫秒
		 * @param countDownInterval
		 *            倒计时间隔
		 */
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}

		@Override
		public void onFinish() {// 计时结束
			saveCurrentImage();
		}
	}

}
