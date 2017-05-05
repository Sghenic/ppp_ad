package com.ylfcf.ppp.ui;

import com.ylfcf.ppp.R;
import com.ylfcf.ppp.entity.ProductInfo;
import com.ylfcf.ppp.util.SettingsManager;
import com.ylfcf.ppp.util.URLGenerator;
import com.ylfcf.ppp.widget.LoadingDialog;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Ԫ��ӯ��������
 * @author Mr.liu
 *
 */
public class YYYProductCJWTActivity extends BaseActivity implements OnClickListener{
	private LinearLayout topLeftBtn;
	private TextView topTitleTV;
	
	private WebView webview;
	private LoadingDialog loadingDialog;
	private String fromWhere;
	private ProductInfo mProductInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.yyy_cjwt_activity);
		fromWhere = getIntent().getStringExtra("from_where");
		mProductInfo = (ProductInfo) getIntent().getSerializableExtra("PRODUCT_INFO");
		loadingDialog = new LoadingDialog(YYYProductCJWTActivity.this, "���ڼ���...", R.anim.loading);
		findViews();
	}

	private void findViews(){
		topLeftBtn = (LinearLayout) findViewById(R.id.common_topbar_left_layout);
		topLeftBtn.setOnClickListener(this);
		topTitleTV = (TextView) findViewById(R.id.common_page_title);
		if("yyy".equals(fromWhere)){
			topTitleTV.setText("Ԫ��ӯ��������");
		}else if("wdy".equals(fromWhere)){
			topTitleTV.setText("нӯ�ƻ���������");
		}else{
			topTitleTV.setText("��������");
		}
		
		
		webview = (WebView) findViewById(R.id.yyycjwt_activity_wv);
		this.webview.getSettings().setSupportZoom(false);  
        this.webview.getSettings().setJavaScriptEnabled(true);  //֧��js
        this.webview.getSettings().setDomStorageEnabled(true); 
		webview.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//����URL ����activity����ת
				boolean isLogin = !SettingsManager.getLoginPassword(YYYProductCJWTActivity.this).isEmpty()
						&& !SettingsManager.getUser(YYYProductCJWTActivity.this).isEmpty();
				Intent intent = new Intent();
				if(isLogin){
					if("yyy".equals(fromWhere)){
						intent.setClass(YYYProductCJWTActivity.this,BidYYYActivity.class);
						intent.putExtra("PRODUCT_INFO", mProductInfo);
						startActivity(intent);
					}else if("wdy".equals(fromWhere)){
						intent.setClass(YYYProductCJWTActivity.this,BidWDYActivity.class);
						intent.putExtra("PRODUCT_INFO", mProductInfo);
						startActivity(intent);
					}
				}else{
					intent.setClass(YYYProductCJWTActivity.this,LoginActivity.class);
					startActivity(intent);
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
		if("yyy".equals(fromWhere)){
			webview.loadUrl(URLGenerator.YYY_CJWT_URL);
		}else if("wdy".equals(fromWhere)){
			webview.loadUrl(URLGenerator.XYJH_CJWT_URL.replace("borrowid", mProductInfo.getId()));
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
}
