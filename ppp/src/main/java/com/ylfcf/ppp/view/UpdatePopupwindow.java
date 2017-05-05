package com.ylfcf.ppp.view;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ylfcf.ppp.R;
import com.ylfcf.ppp.entity.AppInfo;
import com.ylfcf.ppp.ui.MainFragmentActivity.OnDownLoadListener;
import com.ylfcf.ppp.util.MarketUtils;
import com.ylfcf.ppp.util.SettingsManager;
import com.ylfcf.ppp.util.Util;

/**
 * �汾���µ�window
 * 
 * @author jianbing
 * 
 */
public class UpdatePopupwindow extends PopupWindow implements OnClickListener {
	private Activity context;
	private Button okBtn;
	private Button cancelBtn;
	private TextView updateContent;
	private View line;

	private int width;
	private int height;
	private RelativeLayout mainLayout;
	private AppInfo info;
	private WindowManager.LayoutParams lp = null;

	DownloadManager downManager;
	OnDownLoadListener ondownloadListener;
	public UpdatePopupwindow(Context context) {
		super(context);
	}

	public UpdatePopupwindow(Context context, View convertView, int width,
			int height, AppInfo info, DownloadManager downloadManager,OnDownLoadListener onDownLoadListener) {
		super(convertView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.context = (Activity) context;
		this.downManager = downloadManager;
		this.width = width;
		this.height = height;
		this.info = info;
		this.ondownloadListener = onDownLoadListener;
		findViews(convertView);
	}

	private void findViews(View popView) {
		lp = context.getWindow().getAttributes();
		lp.alpha = 0.4f;
		context.getWindow().setAttributes(lp);

		mainLayout = (RelativeLayout) popView
				.findViewById(R.id.update_window_mainlayout);
		mainLayout.setLayoutParams(new RelativeLayout.LayoutParams(width,
				height));
		okBtn = (Button) popView.findViewById(R.id.update_window_sure_btn);
		okBtn.setOnClickListener(this);
		cancelBtn = (Button) popView
				.findViewById(R.id.update_window_cancel_btn);
		cancelBtn.setOnClickListener(this);
		updateContent = (TextView) popView
				.findViewById(R.id.update_window_content);
		updateContent.setText(info.getBrief());
		line = popView.findViewById(R.id.update_window_line);
		if ("1".equals(info.getForce_update())) {
			// ǿ�Ƹ���
			cancelBtn.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
		} else {
			cancelBtn.setVisibility(View.VISIBLE);
			line.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		lp.alpha = 1.0f;
		context.getWindow().setAttributes(lp);
	}

	@SuppressWarnings("deprecation")
	public void show(View parentView) {
		if ("1".equals(info.getForce_update())) {

		} else {
			this.setBackgroundDrawable(new BitmapDrawable()); // ʹ�÷��ؼ���Ч
		}
		context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); 
		this.setAnimationStyle(R.style.rechargeMsgPopwindowStyle);
		this.setOutsideTouchable(false);
		this.setFocusable(true);
		this.showAtLocation(parentView, Gravity.CENTER, 0, 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_window_cancel_btn:
			this.dismiss();
			break;
		case R.id.update_window_sure_btn:
			requestDownloadApk();
			break;
		default:
			break;
		}
	}

	private void requestDownloadApk() {
		try {
			MarketUtils.launchAppDetail(context, "com.ylfcf.ppp", "");//��תӦ���̵�
		} catch (Exception e) {
			startDownloadAPK();
		}
	}
	
	/**
	 * �ӹ�������apk
	 */
	private void startDownloadAPK(){
		long myDwonloadID = SettingsManager.getLong(context, SettingsManager.DOWNLOAD_APK_NUM, 0);
        Intent install = new Intent(Intent.ACTION_VIEW);
		Uri downloadFileUri = downManager
				.getUriForDownloadedFile(myDwonloadID);
		if(downloadFileUri != null){
			install.setDataAndType(downloadFileUri,
					"application/vnd.android.package-archive");
			install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(install);
			return;
		}
		
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(info.getNew_version_url()));
		// ������ʲô��������½�������
//		request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
//		request.setAllowedNetworkTypes(Request.NETWORK_MOBILE);
		// ����֪ͨ������
		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
		request.setTitle("Ԫ�������");
		request.setDescription("��������...");
		request.setAllowedOverRoaming(false);
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(info.getNew_version_url()));
        request.setMimeType(mimeString);
		// �����ļ����Ŀ¼
		if(Build.VERSION.SDK_INT >= 23){
			request.setDestinationInExternalPublicDir("/apk/", "ylfcf.apk");//6.0�Ժ��ϵͳ��Ҫ�Զ�������Ŀ¼�����򲻵���������ʾ��
		}else{
			request.setDestinationInExternalFilesDir(context,
					Environment.DIRECTORY_DOWNLOADS, "ylfcf");
		}
		
//		int idx = info.getNew_version_url().lastIndexOf("/");  
//        String apkName = info.getNew_version_url().substring(idx+1);  
		long id = downManager.enqueue(request);// ���صķ�����̺�
		SettingsManager.setLong(context, SettingsManager.DOWNLOAD_APK_NUM, id);
		if ("1".equals(info.getForce_update())) { 
			ondownloadListener.onDownLoad(id);//��ʾ���صĽ�����
		} else {
		}
		dismiss();
	}
}
