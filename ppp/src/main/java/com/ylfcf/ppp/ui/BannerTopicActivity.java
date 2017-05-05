package com.ylfcf.ppp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ylfcf.ppp.R;
import com.ylfcf.ppp.entity.BannerInfo;
import com.ylfcf.ppp.inter.Inter.OnIsVerifyListener;
import com.ylfcf.ppp.util.Constants.TopicType;
import com.ylfcf.ppp.util.RequestApis;
import com.ylfcf.ppp.util.SettingsManager;
import com.ylfcf.ppp.util.URLGenerator;
import com.ylfcf.ppp.util.Util;
import com.ylfcf.ppp.widget.LoadingDialog;

/**
 * ר��ҳ
 * @author Administrator
 *
 */
public class BannerTopicActivity extends BaseActivity implements OnClickListener{
	private static final String LOTTERY_URL = "http://wap.ylfcf.com/home/index/lottery.html";//	��ת�̵Ļҳ��
	private LinearLayout topLeftBtn;
	private TextView topTitleTV;
	private WebView webview;
	private BannerInfo banner;
	private LoadingDialog loadingDialog;
	private String topicType = "";//ר������֣����ݺ�̨��Լ���ġ�
	private RelativeLayout topLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.banner_topic_activity);
		loadingDialog = new LoadingDialog(BannerTopicActivity.this,"���ڼ���..." , R.anim.loading);
		banner = (BannerInfo) getIntent().getSerializableExtra("BannerInfo");
		if(banner != null){
			topicType = banner.getArticle_id();
		}
		findViews();
	}

	private void findViews(){
		topLeftBtn = (LinearLayout)findViewById(R.id.common_topbar_left_layout);
		topLeftBtn.setOnClickListener(this);
		topTitleTV = (TextView)findViewById(R.id.common_page_title);
		topLayout = (RelativeLayout) findViewById(R.id.banner_topic_activity_toplayout);
		if(TopicType.CHONGZHISONG.equals(topicType)){
			//��ֵ�͵Ļ
			topTitleTV.setText("��ֵ��");
			topLayout.setBackgroundColor(getResources().getColor(R.color.topic_chongzhisong_topcolor));
		}else if(TopicType.ZHUCESONG.equals(topicType)){
			//ע����
			topTitleTV.setText("ע����");
		}else if(TopicType.JIAXI.equals(topicType)){
			topTitleTV.setText("��Ϣˬ����");
		}else if(TopicType.TOUZIFANLI.equals(topicType)){
			topTitleTV.setText("Ͷ�ʷ���");
		}else if(TopicType.XINGYUNZHUANPAN.equals(topicType)){
			topTitleTV.setText("����ת��");
		}else if(TopicType.YYY_JX.equals(topicType)){
			topLayout.setBackgroundColor(getResources().getColor(R.color.topic_yyyjiaxi_topcolor));
			topTitleTV.setText("������� �ӼӼ�Ϣ");
		}else if(TopicType.TUIGUANGYUAN.equals(topicType)){
			topTitleTV.setText("�ƹ�Աר������");
		}else if(TopicType.FRIENDS_CIRCLE.equals(topicType)){
			topTitleTV.setText("��ǿ����Ȧ");
		}else{
			topTitleTV.setText("ר������");
		}
		
		webview = (WebView) findViewById(R.id.banner_topic_activity_webview);
		this.webview.getSettings().setSupportZoom(false);  
        this.webview.getSettings().setJavaScriptEnabled(true);  //֧��js
        this.webview.getSettings().setDomStorageEnabled(true); 
		webview.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//����URL ����activity����ת
				if(TopicType.CHONGZHISONG.equals(topicType)){
					//��ֵ��ר��
					chongzhisong(url);
				}else if(TopicType.ZHUCESONG.equals(topicType)){
					//ע����
					zhucesong(url);
				}else if(TopicType.XINGYUNZHUANPAN.equals(topicType)){
					xingyunzhuanpan(url);
				}else{
					loadURL(url);
				}
				return true;
			}
			
		});
		webview.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {	
				if(newProgress == 100){
					//��ҳ�������
					loadingDialog.dismiss();
				}else{
					//��ҳ������...
					loadingDialog.show();
				}
			}
		});
		if(banner != null){
			webview.loadUrl(banner.getLink_url());
		}
	}
	
	/**
	 * ����URL
	 * @param url
	 */
	private void loadURL(String url){
		if(url == null){
			return;
		}
		if(url.contains("/home/yyy/yyyDetail")){
			//Ԫ��ӯ��Ϣ��ת  Ԫ��ӯ������ҳ��
			Intent intent = new Intent(BannerTopicActivity.this,BorrowDetailYYYActivity.class);
			startActivity(intent);
			finish();
		}else if(url.contains("/home/borrow/borrowlist") || url.contains("/home/borrow/borrowList")){
			//��ת�����Ŵ����б�ҳ��
			Intent intent = new Intent(BannerTopicActivity.this,BorrowListZXDActivity.class);
			startActivity(intent);
		}else if(url.contains("/home/vip/borrowlist") || url.contains("/home/vip/borrowList")){
			//��ת��vip���б�ҳ��
			Intent intent = new Intent(BannerTopicActivity.this,BorrowListVIPActivity.class);
			startActivity(intent);
		}else if(url.contains("/home/borrow/borrowDetail") || url.contains("/home/borrow/borrowdetail")){
			//���ֱ�����
			Intent intent = new Intent(BannerTopicActivity.this,BorrowDetailXSBActivity.class);
			startActivity(intent);
		}else if(url.contains("/home/index/register")){
			//ע��ҳ��
			Intent intent = new Intent(BannerTopicActivity.this,RegisteActivity.class);
			startActivity(intent);
		}else if(url.contains("/home/index/login")){
			//��¼ҳ��
			Intent intent = new Intent(BannerTopicActivity.this,LoginActivity.class);
			startActivity(intent);
		}else if(url.contains("/home/index/vipregister") || url.contains("/home/index/vipRegister")){
			//vip�û�ע��ҳ��
			Intent intent = new Intent(BannerTopicActivity.this,RegisterVIPActivity.class);
			startActivity(intent);
		}else if(url.contains("/home/promotion/hdInvite") || url.contains("/home/promotion/hdinvite")){
			//��ת���������ҳ�棬�����ж���û�е�¼
			shared();
		}else if(url.contains("/home/index/promoter")){
			//�붮ʲô���Ƽ���
			Intent intentBanner = new Intent(BannerTopicActivity.this,BannerTopicActivity.class);
			BannerInfo bannerInfo = new BannerInfo();
			bannerInfo.setArticle_id(TopicType.TUIGUANGYUAN);
			bannerInfo.setLink_url(URLGenerator.PROMOTER_URL);
			intentBanner.putExtra("BannerInfo", bannerInfo);
			startActivity(intentBanner);
			finish();
		}else if(url.contains("/home/borrow")){
			//��ҳ���Ͷ���б�ҳ��
			SettingsManager.setMainProductListFlag(getApplicationContext(), true);
			finish();
		}else if(url.contains("/home/seckill/seckilldetail")){
			//��ת���������ҳ��
			Intent intentMBDetail = new Intent(BannerTopicActivity.this,BorrowDetailXSMBActivity.class);
			startActivity(intentMBDetail);
			finish();
		}else if(url.contains("/home/index/fljh") || url.contains("/home/index/yhfl")){
			//��ȡ�û�����
			Intent intent = new Intent(BannerTopicActivity.this,PrizeRegionTempActivity.class);
			startActivity(intent);
			finish();
		}else if(url.contains("/home/wdy/wdydetail")){
			//нӯ�ƻ�����ҳ��
			Intent intent = new Intent(BannerTopicActivity.this,BorrowDetailWDYActivity.class);
			startActivity(intent);
			finish();
		}else{
			//����������°汾
//			Util.toastLong(BannerTopicActivity.this, "����������°汾");
		}
	}
	
	private void shared(){
		String userId = SettingsManager.getUserId(getApplicationContext());
		if(userId != null && !"".equals(userId)){
			//�ѵ�¼
			if(SettingsManager.isPersonalUser(getApplicationContext())){
				if("�����Ƽ�".equals(banner.getFrom_where()) || "��������".equals(banner.getFrom_where())){
					Intent intent = new Intent();
					setResult(200,intent);
					finish();
				}else{
					checkIsVerify("�����н�");
				}
			}else{
				
			}
		}else{
			//δ��¼
			Intent intent = new Intent(BannerTopicActivity.this,LoginActivity.class);
			intent.putExtra("FLAG", "from_mainfragment_activity_shared");
			startActivity(intent);
		}
	}
	
	/**
	 * ��ֵ��
	 * @param url
	 */
	private  void chongzhisong(String url){
		if(url != null && url.contains("/home/recharge/")){
			String userId = SettingsManager.getUserId(getApplicationContext());
			if(userId == null || "".equals(userId)){
				//δ��½
				Intent intent = new Intent(BannerTopicActivity.this,LoginActivity.class);
				startActivity(intent);
			}else{
				SettingsManager.setMainAccountFlag(getApplicationContext(), true);
				finish();
			}
		}
	}
	
	/**
	 * ע����
	 * @param url
	 */
	private void zhucesong(String url){
		if(url != null && url.contains("/home/borrow")){
			//�����鿴
			SettingsManager.setMainProductListFlag(getApplicationContext(), true);
			finish();
		}else if(url != null && url.contains("/home/index/register")){
			//���ע��
			String userId = SettingsManager.getUserId(getApplicationContext());
			if(userId == null || "".equals(userId)){
				//δ��½
				Intent intent = new Intent(BannerTopicActivity.this,RegisteActivity.class);
				startActivity(intent);
				finish();
			}else{
				SettingsManager.setMainAccountFlag(getApplicationContext(), true);
				finish();
			}
		}
	}
	
	/**
	 * ����ת��
	 * @param url
	 */
	private void xingyunzhuanpan(String url){
		//��ת�����
		if(url != null && url.contains("/home/index/login")){
			showPromptDialog();
		}else{
			intentToLotteryBrowser();
		}
	}
	
	private void intentToLotteryBrowser(){
		Uri uri = Uri.parse(LOTTERY_URL);  
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
        startActivity(intent);  
	}
	
	private void showPromptDialog(){
		View contentView = LayoutInflater.from(BannerTopicActivity.this).inflate(R.layout.banner_prompt_dialog, null);
		final Button sureBtn = (Button) contentView.findViewById(R.id.banner_prompt_dialog_sure_btn);
		final Button cancelBtn = (Button) contentView.findViewById(R.id.banner_prompt_dialog_cancel_btn);
		final TextView contentText = (TextView) contentView.findViewById(R.id.banner_prompt_dialog_content_text);
		sureBtn.setText("ȥ�μ�");
		contentText.setText("ʹ��������򿪻ҳ��");
		AlertDialog.Builder builder=new AlertDialog.Builder(BannerTopicActivity.this, R.style.Dialog_Transparent);  //�ȵõ�������  
        builder.setView(contentView);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        sureBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				intentToLotteryBrowser();
			}
		});
        cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
        //��������������ˣ���������ʾ����  
        dialog.show();  
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth()*6/7;
        dialog.getWindow().setAttributes(lp);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_topbar_left_layout:
			finish();
			break;

		default:
			break;
		}
	}
	
	/**
	 * ��֤�û��Ƿ��Ѿ���֤
	 * @param type ����ֵ��,�����֡�,"�����н�"
	 */
	private void checkIsVerify(final String type){
		RequestApis.requestIsVerify(BannerTopicActivity.this, SettingsManager.getUserId(getApplicationContext()), new OnIsVerifyListener() {
			@Override
			public void isVerify(boolean flag, Object object) {
				if(flag){
					//�û��Ѿ�ʵ��
					Intent intent = new Intent(BannerTopicActivity.this,InvitateActivity.class);
					intent.putExtra("is_verify", true);
					startActivity(intent);
				}else{
					//�û�û��ʵ��
					Intent intent = new Intent(BannerTopicActivity.this,UserVerifyActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("type", type);
					intent.putExtra("bundle", bundle);
					startActivity(intent);
				}
			}

			@Override
			public void isSetWithdrawPwd(boolean flag, Object object) {
			}
		});
	}
}
