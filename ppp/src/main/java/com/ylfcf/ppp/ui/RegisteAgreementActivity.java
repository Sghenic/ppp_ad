package com.ylfcf.ppp.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ylfcf.ppp.R;
import com.ylfcf.ppp.util.URLGenerator;

/**
 * ע�����Э��
 * 
 * @author Administrator
 * 
 */
public class RegisteAgreementActivity extends BaseActivity implements
		OnClickListener {
	private LinearLayout topLeftBtn;
	private TextView topTitleTV;
	
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.registe_agreement_activity);

		findViews();
	}

	private void findViews() {
		topLeftBtn = (LinearLayout) findViewById(R.id.common_topbar_left_layout);
		topLeftBtn.setOnClickListener(this);
		topTitleTV = (TextView) findViewById(R.id.common_page_title);
		topTitleTV.setText("ע�����Э��");
		
		webView = (WebView) findViewById(R.id.registe_agreement_activity_wv);
		this.webView.getSettings().setSupportZoom(false);  
        this.webView.getSettings().setJavaScriptEnabled(true);  //֧��js
        this.webView.getSettings().setDomStorageEnabled(true); 
        webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {	
				if(newProgress == 100){
					//��ҳ�������
					mLoadingDialog.dismiss();
				}else{
					//��ҳ������...
					mLoadingDialog.show();
				}
			}
		});
        webView.loadUrl(URLGenerator.REGISTE_AGREEMENT_URL);
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
