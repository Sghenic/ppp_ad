package com.ylfcf.ppp.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ylfcf.ppp.R;
import com.ylfcf.ppp.async.AsyncArticle;
import com.ylfcf.ppp.entity.ArticleInfo;
import com.ylfcf.ppp.entity.BannerInfo;
import com.ylfcf.ppp.entity.BaseInfo;
import com.ylfcf.ppp.inter.Inter.OnCommonInter;
import com.ylfcf.ppp.ui.ArticleDetailsActivity.ImageLoadThread;
import com.ylfcf.ppp.util.SettingsManager;
import com.ylfcf.ppp.widget.LoadingDialog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * banner����---����
 * @author jianbing
 *
 */
public class BannerDetailsActivity extends BaseActivity implements OnClickListener{
	private static final int REFRESH_VIEW = 5810;
	
	private BannerInfo mBannerInfo;
	private LinearLayout topLeftBtn;
	private TextView topTitleTV;
	
	private TextView title,time,content;
	private TextView nodataText;
	private LinearLayout contentLayout;//�����������
	
	private LoadingDialog loadingDialog;
	private  ArticleInfo articleInfoTemp;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH_VIEW:
				if(loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				CharSequence text = (CharSequence) msg.obj;
				title.setText(articleInfoTemp.getTitle());
				if(releaseTime != null && !"".equals(releaseTime)){
					time.setText("����ʱ�䣺"+releaseTime);
				}else{
					time.setText("����ʱ�䣺"+articleInfoTemp.getAdd_time());
				}
				
				content.setText(text);
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.banner_details_activity);
		
		mBannerInfo = (BannerInfo) getIntent().getSerializableExtra("BannerInfo");
		loadingDialog = new LoadingDialog(BannerDetailsActivity.this, "���ڼ���...", R.anim.loading);
		findViews();
		if(mBannerInfo != null){
			requestArticle(mBannerInfo.getArticle_id());
		}
	}

	private void findViews(){
		topLeftBtn = (LinearLayout)findViewById(R.id.common_topbar_left_layout);
		topLeftBtn.setOnClickListener(this);
		topTitleTV = (TextView)findViewById(R.id.common_page_title);
		topTitleTV.setText("����");
		
		title = (TextView)findViewById(R.id.banner_details_actiivty_title);
		time = (TextView)findViewById(R.id.banner_details_actiivty_time);
		content = (TextView)findViewById(R.id.banner_details_actiivty_content);
		content.setMovementMethod(LinkMovementMethod.getInstance());//��textview����ĳ����ӵ����Ӧ
		nodataText = (TextView) findViewById(R.id.banner_details_activity_nodata);
		contentLayout = (LinearLayout) findViewById(R.id.banner_details_activity_content_layout);
	}
	
	private String releaseTime = "";//����������ķ���ʱ��
	private void initData(ArticleInfo info){
		if(info == null){
			contentLayout.setVisibility(View.GONE);
			nodataText.setVisibility(View.VISIBLE);
			return;
		}
		try {
			Date date = sdf.parse(info.getAdd_time());
			releaseTime = sdf.format(date);
		} catch (Exception e) {
		}
		new ImageLoadThread().start();
		
		//�ָ��ߡ�
		if(info.getTitle() != null && !"".equals(info.getTitle()) && info.getContent() != null && !"".equals(info.getContent())){
			contentLayout.setVisibility(View.VISIBLE);
			nodataText.setVisibility(View.GONE);
			String addTime = "";
			try {
				Date addDate = sdf.parse(info.getAdd_time());
				addTime = sdf.format(addDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			title.setText(info.getTitle());
			if(addTime != null && !"".equals(addTime)){
				time.setText("����ʱ�䣺"+addTime);
			}else{
				time.setText("����ʱ�䣺"+info.getAdd_time());
			}
			
			content.setText(Html.fromHtml(info.getContent()));
		}else{
			contentLayout.setVisibility(View.GONE);
			nodataText.setVisibility(View.VISIBLE);
		}
		
	}

	class ImageLoadThread extends Thread {
		@Override
		public void run() {
			/**
			 * Ҫʵ��ͼƬ����ʾ��Ҫʹ��Html.fromHtml��һ���ع�������public static Spanned fromHtml
			 * (String source, Html.ImageGetterimageGetter, Html.TagHandler
			 * tagHandler)����Html.ImageGetter��һ���ӿڣ�����Ҫʵ�ִ˽ӿڣ�������getDrawable
			 * (String source)�����з���ͼƬ��Drawable����ſ��ԡ�
			 */
			ImageGetter imageGetter = new ImageGetter() {
				@Override
				public Drawable getDrawable(String source) {
					// TODO Auto-generated method stub
					URL url;
					Drawable drawable = null;
					try {
						url = new URL(source);
						int[] screen = SettingsManager.getScreenDispaly(BannerDetailsActivity.this);
						drawable = Drawable.createFromStream(url.openStream(),null);
						if(drawable != null){
							int imageIntrinsicWidth = drawable.getIntrinsicWidth();
							float imageIntrinsicHeight = (float)drawable.getIntrinsicHeight();
							int curImageHeight = (int) (screen[0]*(imageIntrinsicHeight/imageIntrinsicWidth));
							drawable.setBounds(0, 0, screen[0],curImageHeight);//�ĸ���������Ϊ���Ͻǡ����½�����ȷ����һ�����Σ�ͼƬ����������η�Χ�ڻ�����
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return drawable;
				}
			};
			CharSequence htmlText = Html.fromHtml(articleInfoTemp.getContent(),imageGetter, null);
			Message msg = handler.obtainMessage(REFRESH_VIEW);
			msg.obj = htmlText;
			handler.sendMessage(msg);

		}
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
	 * ������������
	 * @param id
	 */
	private void requestArticle(String id){
		if(loadingDialog != null){
			loadingDialog.show();
		}
		AsyncArticle articleTask = new AsyncArticle(BannerDetailsActivity.this, id,new OnCommonInter() {
			@Override
			public void back(BaseInfo baseInfo) {
				if(loadingDialog != null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				if(baseInfo != null){
					int resultCode = SettingsManager.getResultCode(baseInfo);
					if(resultCode == 0){
						articleInfoTemp = baseInfo.getmArticleInfo();
						initData(articleInfoTemp);
					}else{
						contentLayout.setVisibility(View.GONE);
						nodataText.setVisibility(View.VISIBLE);
					}
				}else{
					contentLayout.setVisibility(View.GONE);
					nodataText.setVisibility(View.VISIBLE);
				}
			}
		});
		articleTask.executeAsyncTask(SettingsManager.FULL_TASK_EXECUTOR);
	}
}
