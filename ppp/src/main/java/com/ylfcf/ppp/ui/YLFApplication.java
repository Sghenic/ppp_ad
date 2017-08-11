package com.ylfcf.ppp.ui;

import android.app.Activity;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.ylfcf.ppp.util.ImageLoaderManager;
import com.ylfcf.ppp.util.UMengStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Administrator
 * 
 */
public class YLFApplication extends android.app.Application {
	private List<Activity> activityList;
	private static YLFApplication theApplication;

	@Override
	public void onCreate() {
		super.onCreate();
		theApplication = this;
		activityList = new ArrayList<Activity>();
		UMengStatistics.statisticsInit();//��ֹĬ�ϵ�ҳ��ͳ�Ʒ�ʽ
		UMShareAPI.get(this);
		// ΢�� appid appsecret
		PlatformConfig.setWeixin("wx9b6d21b05d725f48",
				"a6c9bdc0cf7eda498049a9f1b9fbb380");
		// ����΢�� appkey appsecret
		PlatformConfig.setSinaWeibo("2704475990",
				"b9fba6681da2f894a6f9a2705b38dd28",
				"http://sns.whalecloud.com/sina2/callback");
		// QQ��Qzone appid appkey
		PlatformConfig.setQQZone("1105044430", "gAWcQhAJpPebuG7y");
		ImageLoaderManager.configurationImageLoader(this);
	}

	/**
	 * ��ʼ��
	 */
	private static void initialize() {
		theApplication = new YLFApplication();
		theApplication.onCreate();
	}

	/**
	 * ��ȡ����Applicationʵ��
	 * 
	 * @return
	 */
	public static YLFApplication getApplication() {
		if (theApplication == null)
			initialize();
		return theApplication;
	}

	public MainFragmentActivity getMainFragmentActivity() {
		MainFragmentActivity mainActivity = null;
		for (Activity activity : activityList) {
			if (activity instanceof MainFragmentActivity) {
				mainActivity = (MainFragmentActivity) activity;
				break;
			}
		}
		return mainActivity;
	}

	/**
	 * �жϻר���Ƿ��Ѵ���
	 * @return
	 */
	public boolean getActivitysRegionActivity(){
		boolean flag = false;
		for (Activity activity : activityList) {
			if (activity instanceof ActivitysRegionActivity) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * ��ȡContext
	 * 
	 * @return
	 */
	public static YLFApplication getContext() {
		return theApplication;
	}

	// activity�������б����Ƴ�activity
	public void removeActivity(Activity activity) {
		activityList.remove(activity);
	}

	// activity�������activity���б�
	public void addActivity(Activity activity) {
		if (!activityList.contains(activity)) {
			activityList.add(activity);
		}
	}

	// activity������������activity
	public void finishAllActivity() {
		for (Activity activity : activityList) {
			if (null != activity) {
				activity.finish();
			}
		}
	}

	/**
	 * �������е�activity����ȥMainFragmentActivity
	 */
	public void finishAllActivityExceptMain() {
		for (Activity activity : activityList) {
			if (null != activity && !(activity instanceof MainFragmentActivity)) {
				activity.finish();
			}
		}
	}

}
