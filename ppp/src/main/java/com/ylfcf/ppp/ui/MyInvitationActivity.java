package com.ylfcf.ppp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ylfcf.ppp.R;
import com.ylfcf.ppp.adapter.ExtensionAdapter;
import com.ylfcf.ppp.async.AsyncExtensionNewPageInfo;
import com.ylfcf.ppp.async.AsyncGetLCSName;
import com.ylfcf.ppp.entity.BannerInfo;
import com.ylfcf.ppp.entity.BaseInfo;
import com.ylfcf.ppp.entity.ExtensionNewInfo;
import com.ylfcf.ppp.entity.ExtensionNewPageInfo;
import com.ylfcf.ppp.inter.Inter;
import com.ylfcf.ppp.inter.Inter.OnCommonInter;
import com.ylfcf.ppp.util.Constants.TopicType;
import com.ylfcf.ppp.util.SettingsManager;
import com.ylfcf.ppp.util.URLGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * �ҵ�����
 * 
 * @author jianbing
 * 
 */
public class MyInvitationActivity extends BaseActivity implements
		OnClickListener {
	private static final int REQUEST_EXTENSION_WHAT = 1200;
	private static final int REQUEST_EXTENSION_SUCCESS_WHAT = 1201;

	private static final int REQUEST_LCS_WHAT = 1202;
	
	private LinearLayout topLeftBtn;
	private TextView topTitleTV;
	private TextView totalMoneyTV;
	private TextView oneMoneyTV;
	private TextView twoMoneyTV;
	private TextView otherMoneyTV;
	private LinearLayout totalMoneyLayout,oneMoneyLayout,twoMoneyLayout,otherMoneyLayout;
	private Button qsztcBtn;//����׬���
	private Button catTipsBtn;//�鿴��ʾ
	private PullToRefreshListView mListView;
	private TextView nodataTV;//��������
	private ExtensionNewPageInfo mExtensionPageInfo;
	private ExtensionAdapter adapter;
	private int page = 0;
	private int pageSize = 20;
	private List<ExtensionNewInfo> extensionList = new ArrayList<ExtensionNewInfo>();
	private boolean isLoadMore = false;
	private boolean isLcs = false;
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REQUEST_EXTENSION_WHAT:
				requestExtension(SettingsManager.getUserId(getApplicationContext()));
				break;
			case REQUEST_EXTENSION_SUCCESS_WHAT:
				ExtensionNewPageInfo pageInfo = (ExtensionNewPageInfo) msg.obj;
				if(pageInfo != null){
					if(isLoadMore){
						extensionList.addAll(pageInfo.getExtensionList());
					}else{
						extensionList.clear();
						extensionList.addAll(pageInfo.getExtensionList());
					}
					updateAdapter(extensionList);
				}
				break;
				case REQUEST_LCS_WHAT:
					requestLcsName(SettingsManager.getUser(getApplicationContext()));
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
		setContentView(R.layout.myinvitation_activity);
		mExtensionPageInfo = (ExtensionNewPageInfo) getIntent()
				.getSerializableExtra("ExtensionPageInfo");
		findViews();
		handler.sendEmptyMessage(REQUEST_LCS_WHAT);
	}

	private void findViews() {
		topLeftBtn = (LinearLayout) findViewById(R.id.common_topbar_left_layout);
		topLeftBtn.setOnClickListener(this);
		topTitleTV = (TextView) findViewById(R.id.common_page_title);
		topTitleTV.setText("��������");

		qsztcBtn = (Button) findViewById(R.id.myinvitation_activity_top_btn);
		qsztcBtn.setOnClickListener(this);
		catTipsBtn = (Button) findViewById(R.id.myinvitation_activity_top_cat_tipsbtn);
		catTipsBtn.setOnClickListener(this);
		totalMoneyTV = (TextView) findViewById(R.id.myinvitation_activity_totalmoney);
		oneMoneyTV = (TextView) findViewById(R.id.myinvitation_activity_onemoney);
		twoMoneyTV = (TextView) findViewById(R.id.myinvitation_activity_twomoney);
		otherMoneyTV = (TextView) findViewById(R.id.myinvitation_activity_othermoney);
		totalMoneyLayout = (LinearLayout) findViewById(R.id.myinvitation_activity_totalmoney_layout);
		oneMoneyLayout = (LinearLayout) findViewById(R.id.myinvitation_activity_onemoney_layout);
		twoMoneyLayout = (LinearLayout) findViewById(R.id.myinvitation_activity_twomoney_layout);
		otherMoneyLayout = (LinearLayout) findViewById(R.id.myinvitation_activity_othermoney_layout);
		nodataTV = (TextView) findViewById(R.id.myinvitation_activity_nodata);
		mListView = (PullToRefreshListView) findViewById(R.id.myinvitation_listview);
		mListView.setMode(Mode.BOTH);
		initListeners();
	}

	private void initListeners() {
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>(){
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// ����ˢ��
				page = 0;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						isLoadMore = false;
						handler.sendEmptyMessage(REQUEST_EXTENSION_WHAT);
					}
				}, 1000L);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// �������ظ���
				page++;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						isLoadMore = true;
						handler.sendEmptyMessage(REQUEST_EXTENSION_WHAT);
					}
				}, 1000L);
			}
		});
	}
	
	private void initData(){
		if(isLcs){
			totalMoneyLayout.setVisibility(View.VISIBLE);
			totalMoneyLayout.setGravity(Gravity.LEFT);
			oneMoneyLayout.setVisibility(View.VISIBLE);
			twoMoneyLayout.setVisibility(View.VISIBLE);
			double otherTotalD = 0d;
			try{
				otherTotalD = Double.parseDouble(mExtensionPageInfo.getOther_total());
			}catch (Exception e){
				e.printStackTrace();
			}
			if(otherTotalD > 0){
				otherMoneyLayout.setVisibility(View.VISIBLE);
			}else{
				otherMoneyLayout.setVisibility(View.GONE);
			}
		}else{
			totalMoneyLayout.setVisibility(View.VISIBLE);
			totalMoneyLayout.setGravity(Gravity.CENTER_HORIZONTAL);
			oneMoneyLayout.setVisibility(View.GONE);
			twoMoneyLayout.setVisibility(View.GONE);
			otherMoneyLayout.setVisibility(View.GONE);
		}
		if(mExtensionPageInfo == null){
			totalMoneyTV.setText("0");
			oneMoneyTV.setText("0");
			twoMoneyTV.setText("0");
			otherMoneyTV.setText("0");
			return;
		}
		totalMoneyTV.setText(mExtensionPageInfo.getReward_total());
		oneMoneyTV.setText(mExtensionPageInfo.getOne_total());
		twoMoneyTV.setText(mExtensionPageInfo.getSecond_total());
		otherMoneyTV.setText(mExtensionPageInfo.getOther_total());

		if(mExtensionPageInfo.getExtensionList() == null || mExtensionPageInfo.getExtensionList().size() <= 0){
			mListView.setVisibility(View.GONE);
			nodataTV.setVisibility(View.VISIBLE);
			return;
		}
		mListView.setVisibility(View.VISIBLE);
		nodataTV.setVisibility(View.GONE);
		extensionList.addAll(mExtensionPageInfo.getExtensionList());
		try {
			initAdapter(mExtensionPageInfo.getExtensionList());
		} catch (Exception e) {
		}
	}
	
	private void initAdapter(List<ExtensionNewInfo> list) {
		adapter = new ExtensionAdapter(MyInvitationActivity.this);
		mListView.setAdapter(adapter);
		if (list != null) {
			updateAdapter(list);
		}
	}

	private void updateAdapter(List<ExtensionNewInfo> list) {
		adapter.setItems(list);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_topbar_left_layout:
			finish();
			break;
		case R.id.myinvitation_activity_top_btn:
			//����׬���
			Intent intentBanner = new Intent(MyInvitationActivity.this,BannerTopicActivity.class);
			BannerInfo bannerInfo = new BannerInfo();
			bannerInfo.setArticle_id(TopicType.TUIGUANGYUAN);
			bannerInfo.setLink_url(URLGenerator.PROMOTER_URL);
			bannerInfo.setFrom_where("��������");
			intentBanner.putExtra("BannerInfo", bannerInfo);
			startActivityForResult(intentBanner, 100);
			break;
		case R.id.myinvitation_activity_top_cat_tipsbtn:
			//�鿴��ʾ
			showTipsDialog();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 100:
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * �˳���¼��Dialog
	 */
	private void showTipsDialog(){
		View contentView = LayoutInflater.from(MyInvitationActivity.this).inflate(R.layout.myinvitation_tips_dialog_layout, null);
		final Button okBtn = (Button) contentView.findViewById(R.id.myinvitation_tips_dialog_sure_btn);
		final TextView contentTV = (TextView) contentView.findViewById(R.id.myinvitation_tips_content);
		if(isLcs){
			//���ʦ
			double otherTotalD = 0d;
			try{
				otherTotalD = Double.parseDouble(mExtensionPageInfo.getOther_total());
			}catch (Exception e){
				e.printStackTrace();
			}
			if(otherTotalD <= 0){
				//���ʦ��ֱ�Ӻ�������û�н���Ϊ���ʦ�����
				contentTV.setText(getResources().getString(R.string.myinvitation_tips_string));
			}else{
				//���ʦ��ֱ�Ӻ��������н���Ϊ���ʦ�����
				contentTV.setText(getResources().getString(R.string.myinvitation_tips_string1));
			}
		}else{
			//�����ʦ
			contentTV.setText(getResources().getString(R.string.myinvitation_tips_string2));
		}
		AlertDialog.Builder builder=new AlertDialog.Builder(MyInvitationActivity.this, R.style.Dialog_Transparent);  //�ȵõ�������
		builder.setView(contentView);
		builder.setCancelable(true);
		final AlertDialog dialog = builder.create();
		okBtn.setOnClickListener(new OnClickListener() {
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
		lp.width = display.getWidth()*4/5;
		dialog.getWindow().setAttributes(lp);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
	}
	
	/**
	 * ������ѵ���Ϣ
	 * @param userId
	 */
	private void requestExtension(String userId) {
		if(mLoadingDialog != null){
			mLoadingDialog.show();
		}
		AsyncExtensionNewPageInfo taks = new AsyncExtensionNewPageInfo(
				MyInvitationActivity.this, userId, String.valueOf(page),
				String.valueOf(pageSize), new OnCommonInter() {
					@Override
					public void back(BaseInfo baseInfo) {
						if(mLoadingDialog != null && mLoadingDialog.isShowing())
						mLoadingDialog.dismiss();
						mListView.onRefreshComplete();
						if(baseInfo == null){
							return;
						}
						int resultCode = SettingsManager
								.getResultCode(baseInfo);
						if (resultCode == 1) {
							Message msg = handler.obtainMessage(REQUEST_EXTENSION_SUCCESS_WHAT);
							msg.obj = baseInfo.getExtensionNewPageInfo();
							handler.sendMessage(msg);
						}else{
//							Util.toastLong(MyInvitationActivity.this, baseInfo.getMsg());
						}
					}
				});
		taks.executeAsyncTask(SettingsManager.FULL_TASK_EXECUTOR);
	}

	/**
	 * ��ȡ
	 * @param phone
	 */
	private void requestLcsName(String phone){
		if(mLoadingDialog != null){
			mLoadingDialog.show();
		}
		AsyncGetLCSName lcsTask = new AsyncGetLCSName(MyInvitationActivity.this, phone, new Inter.OnCommonInter() {
			@Override
			public void back(BaseInfo baseInfo) {
				if(mLoadingDialog != null && mLoadingDialog.isShowing()){
					mLoadingDialog.dismiss();
				}
				if(baseInfo != null){
					int resultCode = SettingsManager.getResultCode(baseInfo);
					if(resultCode == 0){
						//�����ʦ
						isLcs = true;
					}else{
						//�����ʦ
						isLcs = false;
					}
				}else{
					isLcs = false;
				}
				initData();
			}
		});
		lcsTask.executeAsyncTask(SettingsManager.FULL_TASK_EXECUTOR);
	}
}
