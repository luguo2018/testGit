package com.jmhy.sdk.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
			if (AppConfig.ismobillg || AppConfig.skin == 4 || AppConfig.skin == 5) {
				Fragment mUserLoginFragment = FragmentUtils.getJmPhonerLoginFragment(this);
				addFragmentToActivity(getFragmentManager(), mUserLoginFragment,
						AppConfig.resourceId(this, "content", "id"));
			} else {
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			wrapaLoginInfo("登录取消", "back", "", "","");
		}
		return super.onKeyDown(keyCode, event);
	}*/

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