package com.jmhy.sdk.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.model.Guest;
import com.jmhy.sdk.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JmSetUser3Fragment extends JmBaseFragment implements
		OnClickListener {
	private EditText mEtusername;
	private EditText mEtpwd;
	private View mBtgetgame;
	private TextView mTvmsg;

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
				AppConfig.resourceId(getActivity(), "jmsetuser_3", "layout"),
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

		//JMSDK.getStatisticsSDK().onRegister("JMSDK", true);
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

	/**
	 * 截屏
	 */
	private void saveCurrentImage() {
		// 获取当前屏幕的大小
		int width = getActivity().getWindow().getDecorView().getRootView()
				.getWidth();
		int height = getActivity().getWindow().getDecorView().getRootView()
				.getHeight();
		// 生成相同大小的图片
		Bitmap temBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		// 找到当前页面的跟布局
		View view = getActivity().getWindow().getDecorView().getRootView();
		// 设置缓存
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		// 从缓存中获取当前屏幕的图片
		temBitmap = view.getDrawingCache();
		path = Environment.getExternalStorageDirectory() + File.separator
				+ "DCIM" + File.separator + "Camera" + File.separator;
		file = new File(path);
		if (!file.exists()) {
			file.mkdirs();

		}

		 SimpleDateFormat formatter = new
		 SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		 Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		 String str = formatter.format(curDate);
		String fname = path + str + ".jpg";
		// 输出到sd卡
		if (temBitmap != null) {
			// System.out.println("bitmapgot!");
			try {

				FileOutputStream out = new FileOutputStream(fname);

				temBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

				// System.out.println("file" + fname + "outputdone.");
				String snapshot = AppConfig.getString(getActivity(), "snapshot_save");
				showMsg(snapshot + path);

			} catch (Exception e) {
			}
			 try {
			        MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
							file.getAbsolutePath(), str + ".jpg", null);
			    } catch (FileNotFoundException e) {
			        e.printStackTrace();
			    }
			    // 最后通知图库更新
			    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fname)));
		}
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
