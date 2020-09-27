package com.jmhy.sdk.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.sdk.JmhyApi;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

/**
 * 单例模式实现程序Crash异常处理
 * 
 * @author Administrator
 * 
 */

public class DealCrash implements UncaughtExceptionHandler {

	private static final String TAG = "DealCrash";
	private Context mContext;
	private UncaughtExceptionHandler mUncaughtExceptionHandler;
    
	// 单例模式中饿汉模式和懒汉模式的区别（此处为饿汉模式）
	private static DealCrash mDealCrash = new DealCrash();
    private ApiAsyncTask bugtask;
    private DeviceInfo mDeviceInfo;
	// 单例模式第一步 私有无参的构造方法；
	private DealCrash() {
	}

	// 对外暴露获得实例的方法
	public static DealCrash getInstance() {
		return mDealCrash;
	}

	// 完成初始化操作
	public void init(Context context) {
		// 获取context方便内部使用
		mContext = context.getApplicationContext();
		// 获取系统默认异常处理器
		mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 将当前实例设置为系统默认异常处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
		mDeviceInfo = new DeviceInfo(context);

		Log.i(TAG, "init");
	}

	/**
	 * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用uncaughtException方法
	 * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Log.w(TAG, "-------------------------------->");

		savaExceptionToSD(ex);// 保存异常信息到sd卡
		/*if(AppConfig.is_log_on.equals("1")){
			upLoadToIntent(thread,ex);// 上传日志到服务器
		}*/
		// 答应出当前调用栈信息
		ex.printStackTrace();

	

	}

	private void savaExceptionToSD(Throwable ex) {
		Log.w(TAG, "savaExceptionToSD");
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/jmsdk/log/";
		String FILE_NAME_SUFFIX = ".txt";
		// 判断SD卡是否可用
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File dir = new File(path);
			if (!dir.exists()) {
				
				dir.mkdirs();
				String TAG ="TAG";
				Log.w(TAG, "sdcard unmounted,skip dump exception");  
				
			}

			long current = System.currentTimeMillis();
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date(current));
			File file = new File(path + time + FILE_NAME_SUFFIX);
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			
			try {
				PrintWriter 	pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				// 导出发生异常的时间
				pw.println(time);

				// 导出手机信息
				dumpPhoneInfo(pw);

				pw.println();
				// 导出异常的调用栈信息
				ex.printStackTrace(pw);

				pw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

	}

	private void dumpPhoneInfo(PrintWriter pw) {
		// 应用的版本名称和版本号
		PackageManager pm = mContext.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(mContext.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			pw.print("App Version: ");
			pw.print(pi.versionName);
			pw.print('_');
			pw.println(pi.versionCode);

			// android版本号
			pw.print("OS Version: ");
			pw.print(Build.VERSION.RELEASE);
			pw.print("_");
			pw.println(Build.VERSION.SDK_INT);

			// 手机制造商
			pw.print("Vendor: ");
			pw.println(Build.MANUFACTURER);

			// 手机型号
			pw.print("Model: ");
			pw.println(Build.MODEL);

			// cpu架构
			pw.print("CPU ABI: ");
			pw.println(Build.CPU_ABI);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void upLoadToIntent(final Thread thread, final Throwable ex) {
	String msg = "OS Version:"+mDeviceInfo.getSystemInfo()+ex;

		 JmhyApi.get().starbug(mContext, AppConfig.appKey, AppConfig.openid,
				msg, new ApiRequestListener() {
					
					@Override
					public void onSuccess(Object obj) {
						// TODO Auto-generated method stub
						// 如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束
						if (mUncaughtExceptionHandler != null) {
							mUncaughtExceptionHandler.uncaughtException(thread, ex);

						} else {
							// 当应用不再使用时，通常需要关闭应用，首先获取当前进程的id，然后杀死该进程。
							Process.killProcess(Process.myPid());
						}
					}
					
					@Override
					public void onError(int statusCode) {
						// TODO Auto-generated method stub
						if (mUncaughtExceptionHandler != null) {
							mUncaughtExceptionHandler.uncaughtException(thread, ex);

						} else {
							// 当应用不再使用时，通常需要关闭应用，首先获取当前进程的id，然后杀死该进程。
							Process.killProcess(Process.myPid());
						}
					}
				});
	}

}
