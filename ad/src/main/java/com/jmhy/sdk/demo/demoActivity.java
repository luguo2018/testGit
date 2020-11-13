//package com.jmhy.sdk.demo;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Toast;
//
//import com.bytedance.sdk.openadsdk.AdSlot;
//import com.bytedance.sdk.openadsdk.TTAdConstant;
//import com.bytedance.sdk.openadsdk.TTAdManager;
//import com.bytedance.sdk.openadsdk.TTAdNative;
//import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
//import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
//import com.jmhy.sdk.R;
//import com.jmhy.sdk.loadAd.JmAdSdk;
//import com.jmhy.sdk.loadAd.TTAdManagerHolder;
//import com.jmhy.sdk.loadAd.callback.AdInitListener;
//
//public class demoActivity extends Activity {
//
//    private Activity activity;
//    private Context context;
//    private TTAdNative mTTAdNative;
//    private TTAdManager mTTAdManager;
//    private TTRewardVideoAd mttRewardVideoAd;
////    private String mHorizontalCodeId = "901121430";
//    private String mHorizontalCodeId = "945546866";
//    private boolean mIsExpress = true; //是否请求模板广告
//    private boolean mIsLoaded = false; //视频是否加载完成
//    private boolean mHasShowDownloadActive = false;
//    private String appId="5112188";
//    private String adId="945546866";
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.demo);
//        context=this;
//        activity =this;
//        TTAdManagerHolder.init(context);
//        mTTAdManager = TTAdManagerHolder.get();
//        mTTAdNative = mTTAdManager.createAdNative(context.getApplicationContext());
//
//        JmAdSdk.init(activity, appId, adId, new AdInitListener() {
//            @Override
//            public void Success(String var1) {
//
//            }
//
//            @Override
//            public void fail(String var1) {
//
//            }
//        });
//
//
//        findViewById(R.id.jili).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                wendang();
//            }
//        });
//
//        findViewById(R.id.quanpin).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mttRewardVideoAd.showRewardVideoAd(activity, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
//            }
//        });
//    }
//
//    private void wendang() {
//        AdSlot adSlot;
//        if (mIsExpress) {
//            //个性化模板广告需要传入期望广告view的宽、高，单位dp，
//            adSlot = new AdSlot.Builder()
//                    .setCodeId(mHorizontalCodeId)
//                    .setSupportDeepLink(true)
//                    .setRewardName("金币") //奖励的名称
//                    .setRewardAmount(3)  //奖励的数量
//                    //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
//                    .setExpressViewAcceptedSize(500,500)
//                    .setUserID("user123")//用户id,必传参数
//                    .setMediaExtra("media_extra") //附加参数，可选
//                    .setOrientation(Configuration.ORIENTATION_LANDSCAPE) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
//                    .build();
//        } else {
//            //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
//            adSlot = new AdSlot.Builder()
//                    .setCodeId(mHorizontalCodeId)
//                    .setSupportDeepLink(true)
//                    .setRewardName("金币") //奖励的名称
//                    .setRewardAmount(3)  //奖励的数量
//                    .setUserID("user123")//用户id,必传参数
//                    .setMediaExtra("media_extra") //附加参数，可选
//                    .setOrientation(Configuration.ORIENTATION_LANDSCAPE) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
//                    .build();
//        }
//        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
//            @Override
//            public void onError(int code, String message) {
//                Log.i("广告测试日志","onError"+ message);
//            }
//            //视频广告加载后的视频文件资源缓存到本地的回调
//            @Override
//            public void onRewardVideoCached() {
//                Log.i("广告测试日志","onRewardVideoCached"+ "rewardVideoAd video cached");
//            }
//            //视频广告素材加载到，如title,视频url等，不包括视频文件
//            @Override
//            public void onRewardVideoAdLoad(TTRewardVideoAd loadAd) {
//                Log.i("广告测试日志","onRewardVideoAdLoad rewardVideoAd loaded:"+loadAd);
//                mttRewardVideoAd = loadAd;
//                //mttRewardVideoAd.setShowDownLoadBar(false);
//                mttRewardVideoAd.showRewardVideoAd(activity, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
//                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
//
//                    @Override
//                    public void onAdShow() {
//                        Log.i("广告测试日志","onAdShow"+ "rewardVideoAd show");
//                    }
//
//                    @Override
//                    public void onAdVideoBarClick() {
//                        Log.i("广告测试日志","onAdVideoBarClick"+ "rewardVideoAd bar click");
//                    }
//
//                    @Override
//                    public void onAdClose() {
//                        Log.i("广告测试日志","onAdClose"+ "rewardVideoAd close");
//                    }
//
//                    @Override
//                    public void onVideoComplete() {
//                        Log.i("广告测试日志","onVideoComplete"+ "rewardVideoAd complete");
//                    }
//
//                    @Override
//                    public void onVideoError() {
//                        Log.i("广告测试日志","onVideoError");
//                    }
//
//                    @Override
//                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
//                        Log.i("广告测试日志", "onRewardVerify" + "verify:" + rewardVerify + " amount:" + rewardAmount + " name:" + rewardName);
//                    }
//
//                    @Override
//                    public void onSkippedVideo() {
//                        Log.i("广告测试日志","onSkippedVideo");
//                    }
//                });
//
//                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
//                    @Override
//                    public void onIdle() {
//                        mHasShowDownloadActive = false;
//                    }
//
//                    @Override
//                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
//                        Log.d("广告测试日志", "onDownloadActive==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
//
//                        if (!mHasShowDownloadActive) {
//                            mHasShowDownloadActive = true;
//                            Toast.makeText(activity, "下载中，点击下载区域暂停", Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
//                        Log.d("广告测试日志", "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
//                        Toast.makeText(activity, "下载暂停，点击下载区域继续", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                        Log.d("广告测试日志", "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
//                        Toast.makeText(activity, "下载失败，点击下载区域重新下载", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
//                        Log.d("广告测试日志", "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
//                        Toast.makeText(activity, "下载完成，点击下载区域重新下载", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onInstalled(String fileName, String appName) {
//                        Log.d("广告测试日志", "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
//                        Toast.makeText(activity, "安装完成，点击下载区域打开", Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        });
//    }
//
//}
