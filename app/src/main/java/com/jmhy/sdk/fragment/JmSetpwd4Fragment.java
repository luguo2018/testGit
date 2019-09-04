package com.jmhy.sdk.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import com.jmhy.sdk.activity.JmLoginActivity;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.model.MobileUser;
import com.jmhy.sdk.model.Registermsg;
import com.jmhy.sdk.sdk.JmhyApi;

public class JmSetpwd4Fragment extends JmBaseFragment implements OnClickListener {

	private ApiAsyncTask mRegisterTask;

	// 吉米
	private ImageView mIvvisitor;
	private ImageView mIvdirect;
	private MobileUser mMobileUser;
	private EditText mEtpwd, mEtpwd2;
	private View mBtlogin;
	private ImageView eye1, eye2;
	
	private String user;
	private String mobile;
	private String password;
	private String code;
	private String code_area;
	private View contentView;

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
			case AppConfig.REGISTER_SUCCESS:

				Registermsg registermsg = (Registermsg) msg.obj;

				toAutologin(registermsg);

				getActivity().finish();

			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (getArguments() != null) {
			user = getArguments().getString("username");
			mobile = getArguments().getString("moblie");
			code = getArguments().getString("code");
			code_area = getArguments().getString("code_area");
		}
		View view = inflater.inflate(
				AppConfig.resourceId(getActivity(), "jmsetpwd_4", "layout"),
				container, false);
		view.setClickable(true);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();

		if(savedInstanceState != null){
			String password = savedInstanceState.getString("password");
			mEtpwd.setText(password);
		}

		JiMiSDK.getStatisticsSDK().onRegister("JiMiSDK", true);
	}

	@Override
	public void onResume() {
		super.onResume();

		if(contentView ==  null || contentView.getLayoutParams().height <= 0){
			return;
		}

		JmLoginActivity activity = (JmLoginActivity)getActivity();
		activity.resetContentViewSize(contentView.getLayoutParams().height);
	}

	private void initView() {
		// TODO Auto-generated method stub

		mEtpwd = (EditText) getView().findViewById(
				AppConfig.resourceId(getActivity(), "edit_pwd", "id"));
		mEtpwd.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

		mEtpwd2 = (EditText) getView().findViewById(
				AppConfig.resourceId(getActivity(), "edit_pwd_2", "id"));
		mEtpwd2.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

		mBtlogin = getView().findViewById(
				AppConfig.resourceId(getActivity(), "iphonebtlg", "id"));
		mBtlogin.setOnClickListener(this);

		contentView = getView().findViewById(
				AppConfig.resourceId(getActivity(), "content_view", "id"));

		eye1 = (ImageView) getView().findViewById(
				AppConfig.resourceId(getActivity(), "eye_1", "id"));
		eye1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mEtpwd.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL)){
					mEtpwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					eye1.setImageResource(AppConfig.resourceId(getActivity(), "jm_eye_close_4", "drawable"));
				}else{
					mEtpwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
					eye1.setImageResource(AppConfig.resourceId(getActivity(), "jm_eye_open_4", "drawable"));
				}
			}
		});
		eye2 = (ImageView) getView().findViewById(
				AppConfig.resourceId(getActivity(), "eye_2", "id"));
		eye2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mEtpwd2.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL)){
					mEtpwd2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					eye2.setImageResource(AppConfig.resourceId(getActivity(), "jm_eye_close_4", "drawable"));
				}else{
					mEtpwd2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
					eye2.setImageResource(AppConfig.resourceId(getActivity(), "jm_eye_open_4", "drawable"));
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if (id == AppConfig.resourceId(getActivity(), "iphonebtlg",
				"id")) {
			password = mEtpwd.getText().toString().trim();
			String password2 = mEtpwd2.getText().toString().trim();
			
			if (TextUtils.isEmpty(password)) {
				showMsg(AppConfig.getString(getActivity(), "user_edit_pwdhint"));
				return;
			}
			if (!TextUtils.equals(password, password2)) {
				showMsg(AppConfig.getString(getActivity(), "jm_error_password_confirm"));
				return;
			}
			getMobileRegister(password);
		} else if (id == AppConfig.resourceId(getActivity(), "qy_ivback", "id")) {
			// 返回上一层
			getActivity().onBackPressed();
		}
	}

	//手机号注册
	public void getMobileRegister( final String password){
		 mRegisterTask = JmhyApi.get().startMobileRegister(getActivity(), AppConfig.appKey,
				 user, password, mobile, code, code_area, new ApiRequestListener() {
					
					@Override
					public void onSuccess(Object obj) {
						// TODO Auto-generated method stub
						if(obj!=null){
						Registermsg registermsg = (Registermsg)obj;
						if(registermsg.getCode().equals("0")){
						//	Log.i("kk","Auto"+registermsg.getAuto_login_token());
							mSeference.saveAccount(user, "~~test",
									registermsg.getAuto_login_token());
							AppConfig.saveMap(user, "~~test",
									registermsg.getAuto_login_token());
							sendData(AppConfig.REGISTER_SUCCESS, obj,
									handler);
							
						}else{
							sendData(AppConfig.FLAG_FAIL, registermsg.getMessage(),
									handler);
						}
						}else{
							sendData(AppConfig.FLAG_FAIL,  AppConfig.getString(getActivity(), "http_rror_msg"),
									handler);
						}
					}
					
					@Override
					public void onError(int statusCode) {
						// TODO Auto-generated method stub
						sendData(AppConfig.FLAG_FAIL, AppConfig.getString(getActivity(), "http_rror_msg"), handler);
					}
				});
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		String password = mEtpwd.getText().toString();
		outState.putString("password", password);

		super.onSaveInstanceState(outState);
	}
}
