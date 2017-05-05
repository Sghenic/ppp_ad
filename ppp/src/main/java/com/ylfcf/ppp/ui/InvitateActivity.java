package com.ylfcf.ppp.ui;

import java.util.Hashtable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.umeng.socialize.UMShareAPI;
import com.ylfcf.ppp.R;
import com.ylfcf.ppp.async.AsyncExtensionNewPageInfo;
import com.ylfcf.ppp.async.AsyncUserSelectOne;
import com.ylfcf.ppp.entity.BannerInfo;
import com.ylfcf.ppp.entity.BaseInfo;
import com.ylfcf.ppp.entity.ExtensionNewPageInfo;
import com.ylfcf.ppp.entity.UserInfo;
import com.ylfcf.ppp.inter.Inter.OnCommonInter;
import com.ylfcf.ppp.inter.Inter.OnGetUserInfoByPhone;
import com.ylfcf.ppp.util.Constants.TopicType;
import com.ylfcf.ppp.util.SettingsManager;
import com.ylfcf.ppp.util.URLGenerator;
import com.ylfcf.ppp.view.InvitateFriendsPopupwindow;
import com.ylfcf.ppp.widget.LoadingDialog;

/**
 * �����н�
 * �û��Ѿ�ʵ����֮��ſ���������ѣ�δʵ���Ļ������û�����ʵ����֤��
 * @author jianbing
 * 
 */
public class InvitateActivity extends BaseActivity implements OnClickListener {
	private LinearLayout topLeftBtn;
	private TextView topTitleTV;

	private LinearLayout mainLayout, verifyLayout,unVerifyLayout;

	private ImageView qrCodeImage;// ��ά��
	private Button invitateBtn;
	private Button verifyBtn;
	private TextView extensionCodeTV;//������
	private LinearLayout friendsCountLayout;
	private TextView friendsCount;
	private TextView knowMoreTV;//�˽����

	private int page = 0;
	private int pageSize = 20;
	private ExtensionNewPageInfo pageInfo;
	private LoadingDialog loadingDialog;
	private String promotedURL = null;
	private int QR_WIDTH = 0;
	private int QR_HEIGHT = 0;

	private UserInfo userInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.invitate_activity);
		boolean isVerify = getIntent().getBooleanExtra("is_verify", false);
		findViews(isVerify);
		QR_WIDTH = getResources().getDimensionPixelSize(R.dimen.common_measure_170dp);
		QR_HEIGHT = getResources().getDimensionPixelSize(R.dimen.common_measure_170dp);
		loadingDialog = new LoadingDialog(InvitateActivity.this, "���ڼ���...",
				R.anim.loading);
		requestExtension(SettingsManager.getUserId(getApplicationContext()));
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				requestUserInfo(SettingsManager.getUserId(getApplicationContext()),true);
			}
		}, 300L);
	}

	private void findViews(boolean flag) {
		topLeftBtn = (LinearLayout) findViewById(R.id.common_topbar_left_layout);
		topLeftBtn.setOnClickListener(this);
		topTitleTV = (TextView) findViewById(R.id.common_page_title);
		topTitleTV.setText("�����Ƽ�");

		mainLayout = (LinearLayout) findViewById(R.id.invitate_activity_main_layout);
		verifyLayout = (LinearLayout) findViewById(R.id.invitate_activity_verify_layout);
		unVerifyLayout = (LinearLayout) findViewById(R.id.invitate_activity_unverify_layout);
		qrCodeImage = (ImageView) findViewById(R.id.invitate_activity_qrcode);
		qrCodeImage.setOnClickListener(this);
		invitateBtn = (Button) findViewById(R.id.invitate_activity_btn);
		invitateBtn.setOnClickListener(this);
		verifyBtn = (Button) findViewById(R.id.invitate_activity_verify_btn);
		verifyBtn.setOnClickListener(this);
		extensionCodeTV = (TextView) findViewById(R.id.invitate_activity_code);
		friendsCountLayout = (LinearLayout) findViewById(R.id.invitate_activity_friends_count_layout);
		friendsCountLayout.setOnClickListener(this);
		friendsCount = (TextView) findViewById(R.id.invitate_activity_count_tv);
		knowMoreTV = (TextView) findViewById(R.id.invitate_activity_know_more);
		knowMoreTV.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //�»���
		knowMoreTV.getPaint().setAntiAlias(true);//�����
		knowMoreTV.setOnClickListener(this);
		if(flag){
			verifyLayout.setVisibility(View.VISIBLE);
		}else{
			unVerifyLayout.setVisibility(View.VISIBLE);
		}
	}

	private void initQRCode(String url) {
		createQRImage(url);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_topbar_left_layout:
			finish();
			break;
		case R.id.invitate_activity_btn:
			showFriendsInvitaWindow();
			break;
		case R.id.invitate_activity_friends_count_layout:
			Intent intent = new Intent(InvitateActivity.this,
					MyInvitationActivity.class);
			intent.putExtra("ExtensionPageInfo", pageInfo);
			startActivity(intent);
			break;
		case R.id.invitate_activity_qrcode:
			showBigEWM();
			break;
		case R.id.invitate_activity_know_more:
			//��ת���ƹ���ר��ҳ��
			Intent intentBanner = new Intent(InvitateActivity.this,BannerTopicActivity.class);
			BannerInfo bannerInfo = new BannerInfo();
			bannerInfo.setArticle_id(TopicType.TUIGUANGYUAN);
			bannerInfo.setLink_url(URLGenerator.PROMOTER_URL);
			bannerInfo.setFrom_where("�����Ƽ�");
			intentBanner.putExtra("BannerInfo", bannerInfo);
			startActivity(intentBanner);
			break;
		case R.id.invitate_activity_verify_btn:
			//����ʵ����֤
			Intent intentVerify = new Intent(InvitateActivity.this,UserVerifyActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("type", "�����н�");
			intentVerify.putExtra("bundle", bundle);
			startActivity(intentVerify);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult( requestCode, resultCode, data);
	}

	/**
	 * ������ʾ��
	 */
	private void showFriendsInvitaWindow() {
		View popView = LayoutInflater.from(this).inflate(
				R.layout.invitate_friends_popupwindow, null);
		int[] screen = SettingsManager.getScreenDispaly(InvitateActivity.this);
		int width = screen[0];
		int height = screen[1] / 5 * 2;
		InvitateFriendsPopupwindow popwindow = new InvitateFriendsPopupwindow(InvitateActivity.this,
				popView, width, height);
		popwindow.show(mainLayout,promotedURL,"�����н�");
	}

	/**
	 * ȫ����ʾ��ά��
	 */
	private void showBigEWM(){
		View contentView = LayoutInflater.from(InvitateActivity.this).inflate(R.layout.yqyj_ewm_dialog, null);
		ImageView img = (ImageView) contentView.findViewById(R.id.yqyj_ewm_img);
		img.setImageBitmap((Bitmap)qrCodeImage.getTag());
		AlertDialog.Builder builder=new AlertDialog.Builder(InvitateActivity.this, R.style.Dialog_Transparent);  //�ȵõ�������  
        builder.setView(contentView);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        //��������������ˣ���������ʾ����  
        dialog.show();  
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth()*6/7;
        lp.height = display.getWidth()*6/7;
        dialog.getWindow().setAttributes(lp);
	}
	
	public void createQRImage(String url) {
		try {
			// �ж�URL�Ϸ���
			if (url == null || "".equals(url) || url.length() < 1) {
				return;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// ͼ������ת����ʹ���˾���ת��
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			// �������ﰴ�ն�ά����㷨��������ɶ�ά���ͼƬ��
			// ����forѭ����ͼƬ����ɨ��Ľ��
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// ���ɶ�ά��ͼƬ�ĸ�ʽ��ʹ��ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			// ��ʾ��һ��ImageView����
			qrCodeImage.setImageBitmap(bitmap);
			qrCodeImage.setClickable(true);
			qrCodeImage.setTag(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ������ѵ���Ϣ
	 * @param userId
	 */
	private void requestExtension(String userId) {
		loadingDialog.show();
		AsyncExtensionNewPageInfo taks = new AsyncExtensionNewPageInfo(
				InvitateActivity.this, userId, String.valueOf(page),
				String.valueOf(pageSize), new OnCommonInter() {
					@Override
					public void back(BaseInfo baseInfo) {
						loadingDialog.dismiss();
						int resultCode = SettingsManager
								.getResultCode(baseInfo);
						if (resultCode == 1 || resultCode == -1) {
							pageInfo = baseInfo.getExtensionNewPageInfo();
							friendsCount.setText(pageInfo.getExtension_user_count() + "λ");
						}
					}
				});
		taks.executeAsyncTask(SettingsManager.FULL_TASK_EXECUTOR);
	}

	/**
	 * ���ض�ά��
	 * @param userId
	 * @param flag true��ʾ�������˻������Ѿ��󶨹���
	 */
	private void requestUserInfo(String userId,final boolean flag) {
		AsyncUserSelectOne task = new AsyncUserSelectOne(InvitateActivity.this,
				userId, "", "", new OnGetUserInfoByPhone() {
					@Override
					public void back(BaseInfo baseInfo) {
						if(loadingDialog != null && loadingDialog.isShowing()){
							loadingDialog.dismiss();
						}
						if (baseInfo != null) {
							int resultCode = SettingsManager
									.getResultCode(baseInfo);
							if (resultCode == 0) {
								UserInfo info = baseInfo.getUserInfo();
									//�л㸶�˻���˵���Ѿ�ʵ����
									promotedURL = URLGenerator.PROMOTED_BASE_URL
											+ "?extension_code="
											+ info.getPhone();
									initQRCode(promotedURL);
									extensionCodeTV.setText(info.getPromoted_code());
							}else{
								initQRCode(URLGenerator.PROMOTED_BASE_URL);
							}
						}else{
							initQRCode(URLGenerator.PROMOTED_BASE_URL);
						}
					}
				});
		task.executeAsyncTask(SettingsManager.FULL_TASK_EXECUTOR);
	}
}
