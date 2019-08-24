package com.jmhy.sdk.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceive extends BroadcastReceiver {

	private static String LOGTAG = "BootReceive";

	@Override
	public void onReceive(Context context, Intent intent) {

		// 接收广播：系统启动完成后运行程序
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

			//BuildConfig.Log.v(LOGTAG, "sijiu boot");
			// 启动push服务
			// push
			//Intent pushIntent = new Intent(context, PushService.class);
		//	context.startService(pushIntent);
		}
		if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
		//	BuildConfig.Log.v(LOGTAG, "sijiu reboot receive");
			// 启动push服务
			// push
			//Intent pushIntent = new Intent(context, PushService.class);
			//context.startService(pushIntent);
		}

	}
}
