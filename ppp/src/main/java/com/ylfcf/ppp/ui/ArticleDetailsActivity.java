package com.ylfcf.ppp.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ylfcf.ppp.R;
import com.ylfcf.ppp.async.AsyncArticle;
import com.ylfcf.ppp.entity.ArticleInfo;
import com.ylfcf.ppp.entity.BaseInfo;
import com.ylfcf.ppp.inter.Inter.OnCommonInter;
import com.ylfcf.ppp.util.SettingsManager;
import com.ylfcf.ppp.widget.LoadingDialog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ���桢���š���Ѷ����ҳ��
 * @author Administrator
 *
 */
public class ArticleDetailsActivity extends BaseActivity implements OnClickListener{
	private static final int REFRESH_VIEW = 5800;
	private ArticleInfo mArticleInfo;
	private String titleStr;
	
	private LinearLayout topLeftBtn;
	private TextView topTitleTV;
	
	private TextView title,time,content;
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
		setContentView(R.layout.article_detail_activity);
		Bundle bundle = getIntent().getBundleExtra("bundle");
		titleStr = bundle.getString("title");
		mArticleInfo = (ArticleInfo) bundle.getSerializable("ARTICLE_INFO");
		loadingDialog = new LoadingDialog(ArticleDetailsActivity.this, "���ڼ���...", R.anim.loading);
		findViews();
		if(mArticleInfo != null){
			requestArticle(mArticleInfo.getId());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
	}
	
	private void findViews(){
		topLeftBtn = (LinearLayout)findViewById(R.id.common_topbar_left_layout);
		topLeftBtn.setOnClickListener(this);
		topTitleTV = (TextView)findViewById(R.id.common_page_title);
		topTitleTV.setText(titleStr);
		
		title = (TextView)findViewById(R.id.article_details_activity_title);
		time = (TextView)findViewById(R.id.article_details_activity_time);
		content = (TextView)findViewById(R.id.article_details_activity_content);
	}
	
	private String releaseTime = "";//����������ķ���ʱ��
	private void initData(ArticleInfo info){
		if(info == null){
			return;
		}
		try {
			Date date = sdf.parse(info.getAdd_time());
			releaseTime = sdf.format(date);
		} catch (Exception e) {
		}
		new ImageLoadThread().start();
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
						int[] screen = SettingsManager.getScreenDispaly(ArticleDetailsActivity.this);
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
	
	private void requestArticle(String id){
		if(loadingDialog != null){
			loadingDialog.show();
		}
		AsyncArticle articleTask = new AsyncArticle(ArticleDetailsActivity.this, id,new OnCommonInter() {
			@Override
			public void back(BaseInfo baseInfo) {
				if(baseInfo != null){
					int resultCode = SettingsManager.getResultCode(baseInfo);
					if(resultCode == 0){
						articleInfoTemp = baseInfo.getmArticleInfo();
						initData(articleInfoTemp);
					}
				}
			}
		});
		articleTask.executeAsyncTask(SettingsManager.FULL_TASK_EXECUTOR);
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
}
