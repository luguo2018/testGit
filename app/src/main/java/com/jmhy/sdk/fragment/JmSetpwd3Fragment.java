package com.jmhy.sdk.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huosdk.huounion.sdk.okhttp3.Call;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.http.Result;
import com.jmhy.sdk.model.MobileUser;
import com.jmhy.sdk.model.Registermsg;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.FragmentUtils;

public class JmSetpwd3Fragment extends JmBaseFragment implements OnClickListener {

	private Call mRegisterTask;

	// 吉米
	private ImageView mIvvisitor;
	private ImageView mIvdirect;
	private MobileUser mMobileUser;
	private TextView mTvusername;
	private EditText mEtpwd;
	private Button mBtlogin;
	
	private String user;
	private String mobile;
	private String password;
	private String code;
	private String code_area;



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
				AppConfig.resourceId(getActivity(), "jmsetpwd_3", "layout"),
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

	private void initView() {
		// TODO Auto-generated method stub

		mTvusername = (TextView) getView().findViewById(
				AppConfig.resourceId(getActivity(), "tvmobile", "id"));

		mEtpwd = (EditText) getView().findViewById(
				AppConfig.resourceId(getActivity(), "edit_pwd", "id"));
		mEtpwd.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		mBtlogin = (Button) getView().findViewById(
				AppConfig.resourceId(getActivity(), "iphonebtlg", "id"));
		mBtlogin.setOnClickListener(this);
		mTvusername.setText(user);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if (id == AppConfig.resourceId(getActivity(), "iphonebtlg",
				"id")) {
			password = mEtpwd.getText().toString().trim();
			
			if (TextUtils.isEmpty(password)) {
				showMsg(AppConfig.getString(getActivity(), "user_edit_pwdhint"));
				return;
			}
			getMobileRegister(password);
		} else if (id == AppConfig.resourceId(getActivity(), "qy_ivback", "id")) {
			// 返回上一层
			getActivity().onBackPressed();
		} else if (id == AppConfig.resourceId(getActivity(), "ivvisitor", "id")) {
			// 游客
		} else if (id == AppConfig.resourceId(getActivity(), "ivdirect", "id")) {
			// 手机登录
			Fragment mJmPhonerLoginFragment = FragmentUtils.getJmPhonerLoginFragment(getActivity());
			addFragmentToActivity(getFragmentManager(), mJmPhonerLoginFragment,
					AppConfig.resourceId(getActivity(), "content", "id"));
		} else if (id == AppConfig.resourceId(getActivity(), "termsofservice",
				"id")) {
			
		}
	}

	//手机号注册
	public void getMobileRegister( final String password){
		 mRegisterTask = JmhyApi.get().startMobileRegister(
				 user, password, mobile, code, code_area, new ApiRequestListener() {
					
					@Override
					public void onSuccess(Object obj) {
						Result<Registermsg> registermsgResult = (Result<Registermsg>) obj;
						Registermsg registermsg = registermsgResult.data;
						//	Log.i("kk","Auto"+registermsg.getAuto_login_token());
							mSeference.saveAccount(user, "~~test",
									registermsg.getAuto_login_token());
							AppConfig.saveMap(user, "~~test",
									registermsg.getAuto_login_token());
						toAutologin(registermsg);
						getActivity().finish();
					}
					
					@Override
					public void onError(int statusCode) {
						showMsg(statusCode+"");

					}
				});
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		String password = mEtpwd.getText().toString();
		outState.putString("password", password);

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		if(mRegisterTask != null){
			mRegisterTask.cancel();
		}

		super.onDestroy();
	}
}
