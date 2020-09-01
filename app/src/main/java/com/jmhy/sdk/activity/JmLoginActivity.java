package com.jmhy.sdk.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.fragment.EmailLoginFragment;
import com.jmhy.sdk.utils.FragmentUtils;
import com.jmhy.sdk.utils.Utils;

import java.lang.ref.WeakReference;

public class JmLoginActivity extends JmBaseActivity {
	private static WeakReference<Activity> reference;
	private View content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		reference = new WeakReference<Activity>(this);
		super.onCreate(savedInstanceState);
		setContentView(AppConfig.resourceId(this, "jmloginbase", "layout"));
		if (AppConfig.skin==9){
			String message = getIntent().getStringExtra("message");
			if (message!=null&&!message.equals("")){
				showMsg(message);
			}
		}
		if(savedInstanceState == null) {
			initView();
		}

		content = findViewById(AppConfig.resourceId(this, "content", "id"));
	}

	private void initView() {
		if(Utils.showEnglishUI(this)){
			Fragment fragment = Fragment.instantiate(this, EmailLoginFragment.class.getName());
			addFragmentToActivity(getFragmentManager(), fragment,
					AppConfig.resourceId(this, "content", "id"));
		}else{
			if (AppConfig.ismobillg) {
				Fragment mUserLoginFragment = FragmentUtils.getJmPhonerLoginFragment(this);
				addFragmentToActivity(getFragmentManager(), mUserLoginFragment,
						AppConfig.resourceId(this, "content", "id"));
			} else {//自动登录失败 进入账号登录页
				AppConfig.ismobillg=true;
				Fragment mJmUserLoginFragment = FragmentUtils.getJmUserLoginFragment(this);
				addFragmentToActivity(getFragmentManager(), mJmUserLoginFragment,
						AppConfig.resourceId(this, "content", "id"));
			}
		}
	}

	@Override
	protected void onDestroy() {
		reference = null;
		super.onDestroy();
	}

	public void resetContentViewSize(int height){
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)content.getLayoutParams();
		layoutParams.height = height;
	}

	public static void closeActivity(){
		if(reference == null){
			return;
		}

		Activity activity = reference.get();
		if(activity == null){
			return;
		}

		activity.finish();
	}
}