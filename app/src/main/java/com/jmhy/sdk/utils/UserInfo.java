package com.jmhy.sdk.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.util.Log;

import com.jmhy.sdk.common.JiMiSDK;

public class UserInfo {
	private File dir;
	private File file_name;

	public UserInfo() {
		if(VERSION.SDK_INT < VERSION_CODES.Q){
			dir = new File(Environment.getExternalStorageDirectory(), "jmsdk");
		}else{
			dir = new File(JiMiSDK.context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "jmsdk");
		}
		if (!dir.exists()) {
			dir.mkdir();
		}
		file_name = new File(dir, "jmsdk");

		Log.i("UserInfo", "file = " + file_name.getAbsolutePath());
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @return
	 */
	public boolean isFile() {
		String result = null;
		if (file_name.exists()) {
			result = readUserInfo();
		}
		if (file_name.exists() && result != null) {
			return true;
		} else {
			if (file_name.exists() && result == null) {
				file_name.delete();
			}
			return false;
		}
	}

	/**
	 * 把密码存入文件中
	 */
	public void saveUserInfo(String username, String pwd, String uid,
			String dString) {
		// Log.i("kk","------saveUserInfo-----"+username+"---pwd---"+pwd+"---uid---"+uid+"----dString---"+dString);
		String data = "";
		if (!"".equals(dString)) {
			data = dString;
		} else {
			data = username + ":" + pwd + ":" + uid + "#";
		}
		// 首先判断user的位置和user是否重复格式是user1#user2#user3#
		// if (isFile() && "".equals(dString)) {
		// data = verfyInfo(data);
		// }
		RandomAccessFile rFile = null;
		data = Base64.encode(data.getBytes());
		if (!dir.exists()) {
			dir.mkdirs();
		}

		//FileUtils.saveFile(JiMiSDK.context, file_name, data);

		FileOutputStream fout;
		try {
			fout = new FileOutputStream(file_name);
			byte[] bytes = data.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 验证用户信息是否存在 存在 排在首位 不存在 排在首位+其他信息
	 */
	private String verfyInfo(String data) {
		// TODO Auto-generated method stub
		System.out.println("----verfyInfo-----" + data);
		String username = data.split(":")[0];
		String result = "";
		int j = 0;
		String saveData = readUserInfo();
		Map<String, String> map = new HashMap<String, String>();
		map = userMap();
		if (saveData.contains(username)) {
			// 存在--判断在其中的什么位置
			// System.out.println("------存在------");
			for (int i = 0; i < map.size(); i++) {
				if ((map.get("user" + i)).contains(username)) {
					j = i;
					map.remove("user" + i);
					break;
				}
			}
			if (j == 0) {
				result = data;
			}
			if (j == 1) {
				if (map.size() == 1) {
					result = data + map.get("user0") + "#";
				}
				if (map.size() == 2) {
					result = data + map.get("user0") + "#" + map.get("user2")
							+ "#";
				}
			}
			if (j == 2) {
				result = data + map.get("user0") + "#" + map.get("user1") + "#";
			}

		} else {
			// 不存在
			// System.out.println("------不存在------");
			int num = map.size();
			if (num == 3) {
				result = data + map.get("user0") + "#" + map.get("user1") + "#";
			} else {
				result = data + saveData;
			}
		}
		return result;
	}

	/**
	 * 读取用户信息
	 * 
	 */
	public String readUserInfo() {
		String result = "";
		InputStreamReader inputReader = null;
		InputStream inputStream = null;
		BufferedReader br = null;
		try {
			inputStream = new FileInputStream(file_name);
			inputReader = new InputStreamReader(inputStream);
			br = new BufferedReader(inputReader);
			StringBuffer strBuffer = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				strBuffer.append(line);
			}
			result = strBuffer.toString();
			result = Base64.decode(result);
			br.close();
			inputReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 用户信息排练顺序
	 */
	public Map<String, String> userMap() {
		Map<String, String> map = new HashMap<String, String>();
		try {
			String[] total = readUserInfo().split("#");
			for (int i = 0; i < total.length; i++) {
				map.put("user" + i, total[i]);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}

	// 写数据到SD中的文件
	public void writeFileSdcardFile(String fileName, String write_str)
			throws IOException {
		try {

			FileOutputStream fout = new FileOutputStream(fileName);
			byte[] bytes = write_str.getBytes();

			fout.write(bytes);
			fout.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
     * 删除已存储的文件
     */
    public void deletefile() {
        try {
            // 找到文件所在的路径并删除该文件
            if(file_name.exists()){
            	//Log.i("kk","删除");
            	file_name.delete();}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
