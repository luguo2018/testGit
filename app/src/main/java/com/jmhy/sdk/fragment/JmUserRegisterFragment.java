package com.jmhy.sdk.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.jmhy.sdk.activity.JmLoginActivity;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.model.Guest;
import com.jmhy.sdk.model.Registermsg;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.FragmentUtils;
import com.jmhy.sdk.utils.Utils;

public class JmUserRegisterFragment extends JmBaseFragment implements
		OnClickListener {

	// 吉米
	private TextView mIvvisitor;
	private TextView mIvdirect;
	private EditText mEtuser;
	private EditText mEpwd;
	private String user;
	private String password;
	private Button mBtsubmit;
	private Call mRegisterTask;
	private TextView mTvagreement;
	private Call mGuestTask;
	private ImageView mIvkefu;
	private TextView mTvversion;
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

				JiMiSDK.getStatisticsSDK().onRegister("JiMiSDK", true);

				toAutologin(registermsg);

				getActivity().finish();
				break;
			case AppConfig.GUEST_lOGIN_SUCCESS:
				Guest guest = (Guest) msg.obj;
				String murl = Utils
						.toBase64url(guest.getShow_url_after_login());

				JiMiSDK.getStatisticsSDK().onRegister("JiMiSDK", true);

				if (!TextUtils.isEmpty(guest.getUpass())) {

					Bundle args = new Bundle();
					// Log.i("kk",mobileUser.getMoblie())
					args.putString("username", guest.getUname());
					args.putString("upass", guest.getUpass());
					args.putString("msg", guest.getMessage());
					args.putString("gametoken", guest.getGame_token());
					args.putString("openid", guest.getOpenid());
					args.putString("url", murl);
					Fragment mJmSetUserFragment = FragmentUtils.getJmSetUserFragment(getActivity(), args);
					addFragmentToActivity(getFragmentManager(),
							mJmSetUserFragment, AppConfig.resourceId(
									getActivity(), "content", "id"));
				} else {

					wrapaLoginInfo("success", guest.getMessage(),
							guest.getUname(), guest.getOpenid(),
							guest.getGame_token());

					turnToNotice(murl);
					getActivity().finish();
				}
				break;
			}

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = null;
		view = inflater.inflate(AppConfig.resourceId(getActivity(),
				"jmuserregister", "layout"), container, false);
		view.setClickable(true);
		return view;
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

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();

		if(savedInstanceState != null){
			String account = savedInstanceState.getString("account");
			String password = savedInstanceState.getString("password");
			mEtuser.setText(account);
			mEpwd.setText(password);
		}
	}

	private void initView() {
		// TODO Auto-generated method stub

		mIvvisitor = (TextView) getView().findViewById(
				AppConfig.resourceId(getActivity(), "ivvisitor", "id"));
		mIvvisitor.setOnClickListener(this);
		mIvdirect = (TextView) getView().findViewById(
				AppConfig.resourceId(getActivity(), "ivdirect", "id"));
		mIvdirect.setOnClickListener(this);
		mEtuser = (EditText) getView().findViewById(
				AppConfig.resourceId(getActivity(), "edit_usera", "id"));
		mEtuser.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		mEpwd = (EditText) getView().findViewById(
				AppConfig.resourceId(getActivity(), "edit_pwa", "id"));
		mEpwd.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI
				| EditorInfo.IME_ACTION_DONE);
		mBtsubmit = (Button) getView().findViewById(
				AppConfig.resourceId(getActivity(), "btsubmit", "id"));
		mBtsubmit.setOnClickListener(this);
		mTvagreement = (TextView) getView().findViewById(
				AppConfig.resourceId(getActivity(), "tvurl", "id"));
		mTvagreement.setOnClickListener(this);
		mIvkefu = (ImageView) getView().findViewById(
				AppConfig.resourceId(getActivity(), "ivkefu", "id"));
		mIvkefu.setOnClickListener(this);
		mTvversion = (TextView)getView().findViewById(AppConfig.resourceId(getActivity(),
				"tvversion", "id"));
		mTvversion.setText("v"+ AppConfig.SDK_VER);

		contentView = getView().findViewById(
				AppConfig.resourceId(getActivity(), "content_view", "id"));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if (id == AppConfig.resourceId(getActivity(), "btsubmit", "id")) {
			// 注册
			user = mEtuser.getText().toString().trim();
			password = mEpwd.getText().toString().trim();
			if (TextUtils.isEmpty(user)) {
				showMsg(AppConfig.getString(getActivity(), "user_hintuser_msg"));
				return;
			}
			if (TextUtils.isEmpty(password)) {
				showMsg(AppConfig.getString(getActivity(), "user_edit_pwdhint"));
				return;
			}
			getRegister(user, password);
		} else if (id == AppConfig.resourceId(getActivity(), "qy_ivback", "id")) {
			// 返回上一层
			getActivity().onBackPressed();
		} else if (id == AppConfig.resourceId(getActivity(), "ivvisitor", "id")) {
			// 游客
			/*
			 * JmSetUserFragment mJmSetUserFragment = new JmSetUserFragment();
			 * addFragmentToActivity(getFragmentManager(), mJmSetUserFragment,
			 * AppConfig.resourceId(getActivity(), "content", "id"));
			 */
			getGuest();

		} else if (id == AppConfig.resourceId(getActivity(), "ivdirect", "id")) {
			// 手机登录
			if (AppConfig.isgoto) {
				Fragment mJmPhonerLoginFragment = FragmentUtils.getJmPhonerLoginFragment(getActivity());
				addFragmentToActivity(getFragmentManager(),
						mJmPhonerLoginFragment,
						AppConfig.resourceId(getActivity(), "content", "id"));
			} else {
				Fragment mJmUserLoginFragment = FragmentUtils.getJmUserLoginFragment(getActivity());
				addFragmentToActivity(getFragmentManager(),
						mJmUserLoginFragment,
						AppConfig.resourceId(getActivity(), "content", "id"));

			}
		} else if (id == AppConfig.resourceId(getActivity(), "tvurl", "id")) {
			// 用户协议
			// Log.i("kk","用户协议"+AppConfig.USERAGREEMENTURL);
			turnToIntent(AppConfig.USERAGREEMENTURL);
		}else if (id == AppConfig.resourceId(getActivity(), "ivkefu", "id")) {
			// 客服

			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra("url", AppConfig.KEFU);
			intent.setClass(getActivity(), JmUserinfoActivity.class);
			startActivity(intent);

		}
	}

	/**
	 * 注册
	 * 
	 * @param username
	 *            账号 或手机号
	 * @param password
	 *            密码
	 */
	public void getRegister(final String username, final String password) {
		mRegisterTask = JmhyApi.get().startRegister( username, password, new ApiRequestListener() {

					@Override
					public void onSuccess(Object obj) {
						// TODO Auto-generated method stub
						if (obj != null) {
							Registermsg registermsg = (Registermsg) obj;
							if (registermsg.getCode().equals("0")) {

								mSeference.saveAccount(username, "~~test",
										registermsg.getAuto_login_token());
								AppConfig.saveMap(username, "~~test",
										registermsg.getAuto_login_token());
								sendData(AppConfig.REGISTER_SUCCESS, obj,
										handler);
							} else {
								sendData(AppConfig.FLAG_FAIL,
										registermsg.getMessage(), handler);
							}
						} else {
							sendData(AppConfig.FLAG_FAIL, AppConfig.getString(
									getActivity(), "http_rror_msg"), handler);
						}
					}

					@Override
					public void onError(int statusCode) {
						// TODO Auto-generated method stub
						sendData(
								AppConfig.FLAG_FAIL,
								AppConfig.getString(getActivity(),
										"http_rror_msg"), handler);
					}
				});
	}

	/**
	 * 游客登录
	 */
	public void getGuest() {
		mGuestTask = JmhyApi.get().starguestLogin(getActivity(),
				AppConfig.appKey, new ApiRequestListener() {

					@Override
					public void onSuccess(Object obj) {
						// TODO Auto-generated method stub
						if (obj != null) {
							Guest guest = (Guest) obj;
							if (guest.getCode().equals("0")) {
								mSeference.saveAccount(guest.getUname(),
										"~~test", guest.getLogin_token());
								AppConfig.saveMap(guest.getUname(), "~~test",
										guest.getLogin_token());
								Utils.saveUserToSd(getActivity());
								sendData(AppConfig.GUEST_lOGIN_SUCCESS, obj,
										handler);
							} else {
								sendData(AppConfig.FLAG_FAIL,
										guest.getMessage(), handler);
							}
						} else {
							sendData(AppConfig.FLAG_FAIL, AppConfig.getString(
									getActivity(), "http_rror_msg"), handler);
						}
					}

					@Override
					public void onError(int statusCode) {
						// TODO Auto-generated method stub
						sendData(
								AppConfig.FLAG_FAIL,
								AppConfig.getString(getActivity(),
										"http_rror_msg"), handler);
					}
				});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		String account = mEtuser.getText().toString();
		String password = mEpwd.getText().toString();
		outState.putString("account", account);
		outState.putString("password", password);

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		if(mGuestTask != null){
			mGuestTask.cancel();
		}
		if(mRegisterTask != null){
			mRegisterTask.cancel();
		}

		super.onDestroy();
	}
}
