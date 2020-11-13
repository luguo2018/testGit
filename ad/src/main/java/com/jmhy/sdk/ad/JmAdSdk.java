package com.jmhy.sdk.ad;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.jmhy.sdk.ad.callback.AdInitListener;
import com.jmhy.sdk.ad.callback.AdListener;
import com.jmhy.sdk.ad.http.ReportApi;

public class JmAdSdk {
    private static TTAdNative mTTAdNative;
    private static TTAdManager mTTAdManager;
    private static TTRewardVideoAd mttRewardVideoAd;
    private static AdInitListener adInitListener;
    private static boolean mIsExpress = true; //是否请求模板广告
    private static boolean mHasShowDownloadActive = false;
    private static boolean isInit = false;

    public JmAdSdk() {
    }


    public static void loadAd(final Activity activity, final String appId, final String adId, final String userId,final String gameId ,final AdListener adListener) {

        Log.i("jimi", "加载广告");
        AdSlot adSlot;
        if (mIsExpress) {
            //个性化模板广告需要传入期望广告view的宽、高，单位dp，
            adSlot = new AdSlot.Builder()
                    .setCodeId(adId)
                    .setSupportDeepLink(true)
                    .setRewardName("元宝") //奖励的名称
                    .setRewardAmount(1)  //奖励的数量
                    //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
                    .setExpressViewAcceptedSize(500, 500)
                    .setUserID(userId)//用户id,必传参数
                    .setMediaExtra("") //附加参数，可选
                    .setOrientation(TTAdConstant.HORIZONTAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                    .build();
        }
        else {
            //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
            adSlot = new AdSlot.Builder()
                    .setCodeId(adId)
                    .setSupportDeepLink(true)
                    .setRewardName("元宝") //奖励的名称
                    .setRewardAmount(1)  //奖励的数量
                    .setUserID(userId)//用户id,必传参数
                    .setMediaExtra("") //附加参数，可选
                    .setOrientation(TTAdConstant.HORIZONTAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                    .build();
        }
        ReportApi.reportAd( appId, adId, "1", "4",gameId);//4加载广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.i("广告测试日志", code + "onError" + message);
            }

            //视频广告加载后的视频文件资源缓存到本地的回调
            @Override
            public void onRewardVideoCached() {
                Log.i("广告测试日志", "onRewardVideoCached rewardVideoAd video cached");
            }

            //视频广告素材加载到，如title,视频url等，不包括视频文件
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                Log.i("广告测试日志", "onRewardVideoAdLoad rewardVideoAd loaded:" + ad);
                mttRewardVideoAd = ad;
                //mttRewardVideoAd.setShowDownLoadBar(false);
                mttRewardVideoAd.showRewardVideoAd(activity, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        Log.i("广告测试日志", "onAdShow rewardVideoAd show");
                        ReportApi.reportAd( appId, adId, "1", "5",gameId);//5展示广告
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Log.i("广告测试日志", "onAdVideoBarClick rewardVideoAd bar click");
                        ReportApi.reportAd( appId, adId, "1", "1",gameId); //1, 广告点击
                    }

                    @Override
                    public void onAdClose() {
                        Log.i("广告测试日志", "onAdClose rewardVideoAd close");
                        ReportApi.reportAd( appId, adId, "1", "2",gameId); //2广告展示完成、包含播放N秒的
                        adListener.Success("end");
                    }

                    @Override
                    public void onVideoComplete() {
                        Log.i("广告测试日志", "onVideoComplete rewardVideoAd complete");
                    }

                    @Override
                    public void onVideoError() {
                        Log.i("广告测试日志", "onVideoError");
                    }

                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        Log.i("广告测试日志", "onRewardVerify" + "verify:" + rewardVerify + " amount:" + rewardAmount + " name:" + rewardName);
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.i("广告测试日志", "onSkippedVideo");
                    }
                });

                downAdApk();
                try {
                    Log.i("广告测试日志", "回调start");
                    adListener.Success("end");
                    Log.i("广告测试日志", "回调end");
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("广告测试日志", "回调error"+e);
                }
            }
        });
    }

    private static void downAdApk() {
        mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                mHasShowDownloadActive = false;
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                Log.d("广告下载开始日志", "onDownloadActive==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);

                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                Log.d("广告下载暂停日志", "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                Log.d("广告下载失败日志", "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                Log.d("广告下载结束日志", "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                Log.d("广告下载安装日志", "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
            }
        });
    }

    public static void init(final Activity activity, final String appId, AdInitListener mAdInitListener) {
        adInitListener = mAdInitListener;
        init(activity, appId);
        adInitListener.Success("Success");
    }

    public static void init(final Context activity, final String appId) {
        if (!isInit) {
            TTAdManagerHolder.init(activity, appId);
            mTTAdManager = TTAdManagerHolder.get();
            mTTAdNative = mTTAdManager.createAdNative(activity.getApplicationContext());
            isInit=true;
            Log.i("jimi","广告init"+activity);
        }else{
            Log.i("jimi","广告已初始化过，无需再初始化");
        }
    }

}
