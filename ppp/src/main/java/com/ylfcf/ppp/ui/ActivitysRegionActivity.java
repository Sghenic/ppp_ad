package com.ylfcf.ppp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ylfcf.ppp.R;
import com.ylfcf.ppp.adapter.ActivitysRegionAdapter;
import com.ylfcf.ppp.async.AsyncActiveList;
import com.ylfcf.ppp.async.AsyncXCFLActiveTime;
import com.ylfcf.ppp.entity.ActiveInfo;
import com.ylfcf.ppp.entity.BannerInfo;
import com.ylfcf.ppp.entity.BaseInfo;
import com.ylfcf.ppp.inter.Inter.OnCommonInter;
import com.ylfcf.ppp.util.SettingsManager;
import com.ylfcf.ppp.widget.LoadingDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * �ר��
 * @author Mr.liu
 *
 */
public class ActivitysRegionActivity extends BaseActivity implements OnClickListener{
	private static final int REQUEST_ACTIVE_SIGN_START_WHAT = 2412;//���·�ǩ���
	private static final int REQUEST_ACTIVE_PRIZEREGION2_START_WHAT = 2414;//����ר��2��
	private static final int REQUEST_ACTIVE_START_SUCCESS = 2413;
	private static final int REQUEST_ACTIVE_YQHY_START_WHAT = 2415;//���·�������ѻ
	private static final int REQUEST_ACTIVE_QXJ5_START_WHAT = 2416;//���·�ÿ��һ���ֽ�

	private static final int REQUEST_ACTIVE_LIST = 2417;//��б�
	private static final int REQUEST_ACTIVE_LIST_SUC = 2418;
	private static final int REQUEST_ACTIVE_LIST_FAILE = 2419;
	
	private LinearLayout topLeftBtn;
	private TextView topTitleTV;
	
	private LinearLayout signLayout;//����ǩ��
	private LinearLayout ysqLayout;//��ѹ��Ǯ
	private LinearLayout mbLayout;//���
	private LinearLayout prizeRegionLayout;//��Ա��Ʒר��
	private LinearLayout xsfxLayout;//��ʱ����
	private LinearLayout prizeRegion2Layout;//��Ա�����ƻ�2��
	private LinearLayout yqhyLayout;//������ѷ��֡�
	private LinearLayout qxj5Layout;//5�·ݻ ÿ��һ���ֽ�
	private TextView signTV;//ǩ��
	private TextView ysqTV;//ѹ��Ǯ
	private TextView mbTV;//���
	private TextView prizeRegionTV;//��Ա��Ʒר��
	private TextView xsfxTV;//��ʾ���
	private TextView prizeRegion2TV;//��Ա�����ƻ�
	private TextView yqhyTV;//������ѷ���
	private TextView qxj5TV;//

	private PullToRefreshListView mPullToRefreshListView;
	private ActivitysRegionAdapter mActivitysRegionAdapter;

	private int pageNo = 0;
	private int pageSize = 10;
	private boolean isLoadMore = false;// ���ظ���
	private List<ActiveInfo> activeListTemp = new ArrayList<ActiveInfo>();
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private LoadingDialog mLoadingDialog;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REQUEST_ACTIVE_SIGN_START_WHAT:
				requestActiveTime("QD_03");
				break;
			case REQUEST_ACTIVE_PRIZEREGION2_START_WHAT:
				requestActiveTime("HYFL_02");
				break;
			case REQUEST_ACTIVE_YQHY_START_WHAT:
				//���·�������ѷ��ֻ
				requestActiveTime("TGY_01");
				break;
			case REQUEST_ACTIVE_QXJ5_START_WHAT:
				requestActiveTime("MONDAY_ROB_CASH");
				break;
			case REQUEST_ACTIVE_START_SUCCESS:
				break;
			case REQUEST_ACTIVE_LIST:
				requestActiveList(pageNo,pageSize,"����","app","��ʾ");
				break;
			case REQUEST_ACTIVE_LIST_SUC:
				BaseInfo baseInfo = (BaseInfo) msg.obj;
				if (baseInfo != null) {
					if (!isLoadMore) {
						activeListTemp.clear();
					}
					activeListTemp.addAll(baseInfo.getmActivePageInfo().getActiveList());
					mActivitysRegionAdapter.setItems(activeListTemp);
				}
				isLoadMore = false;
				mPullToRefreshListView.onRefreshComplete();
				break;
			case REQUEST_ACTIVE_LIST_FAILE:
				mPullToRefreshListView.onRefreshComplete();
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
		setContentView(R.layout.activitys_region_activity);
		mApp.addActivity(this);
		mLoadingDialog = new LoadingDialog(ActivitysRegionActivity.this, "���ڼ���...", R.anim.loading);
		handler.sendEmptyMessage(REQUEST_ACTIVE_SIGN_START_WHAT);
		handler.sendEmptyMessageDelayed(REQUEST_ACTIVE_PRIZEREGION2_START_WHAT, 200L);
		handler.sendEmptyMessageDelayed(REQUEST_ACTIVE_YQHY_START_WHAT, 220L);
		handler.sendEmptyMessageDelayed(REQUEST_ACTIVE_QXJ5_START_WHAT,240L);
		findViews();
		handler.sendEmptyMessage(REQUEST_ACTIVE_LIST);
	}

	private void findViews(){
		topLeftBtn = (LinearLayout)findViewById(R.id.common_topbar_left_layout);
		topLeftBtn.setOnClickListener(this);
		topTitleTV = (TextView)findViewById(R.id.common_page_title);
		topTitleTV.setText("�ר��");

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.activitys_region_activity_listview);
		prizeRegion2Layout = (LinearLayout) findViewById(R.id.activitys_region_activity_prizeregion2_layout);
		prizeRegion2Layout.setOnClickListener(this);
		signLayout = (LinearLayout) findViewById(R.id.activitys_region_activity_sign_layout);
		signLayout.setOnClickListener(this);
		ysqLayout = (LinearLayout) findViewById(R.id.activitys_region_activity_ysq_layout);
		ysqLayout.setOnClickListener(this);
		mbLayout = (LinearLayout) findViewById(R.id.activitys_region_activity_mb_layout);
		mbLayout.setOnClickListener(this);
		prizeRegionLayout = (LinearLayout) findViewById(R.id.activitys_region_activity_prizeregion_layout);
		prizeRegionLayout.setOnClickListener(this);
		xsfxLayout = (LinearLayout) findViewById(R.id.activitys_region_activity_xsfx_layout);
		xsfxLayout.setOnClickListener(this);
		yqhyLayout = (LinearLayout) findViewById(R.id.activitys_region_activity_yqhy_layout);
		yqhyLayout.setOnClickListener(this);
		qxj5Layout = (LinearLayout) findViewById(R.id.activitys_region_activity_qxj5_layout);
		qxj5Layout.setOnClickListener(this);
		
		signTV = (TextView) findViewById(R.id.activitys_region_sign_tv);
		ysqTV = (TextView) findViewById(R.id.activitys_region_ysq_tv);
		mbTV = (TextView) findViewById(R.id.activitys_region_mb_tv);
		prizeRegionTV = (TextView) findViewById(R.id.activitys_region_prizeregion_tv);
		xsfxTV = (TextView) findViewById(R.id.activitys_region_xsfx_tv);
		prizeRegion2TV = (TextView) findViewById(R.id.activitys_region_prizeregion2_tv);
		yqhyTV = (TextView) findViewById(R.id.activitys_region_yqhy_tv);
		qxj5TV = (TextView) findViewById(R.id.activitys_region_qxj5_tv);

		mActivitysRegionAdapter = new ActivitysRegionAdapter(this);
		mPullToRefreshListView.setAdapter(mActivitysRegionAdapter);
		initListeners();
	}

	private void initListeners(){
		mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// index��1��ʼ������
				if (position != 0) {
					ActiveInfo item = (ActiveInfo) parent.getAdapter()
							.getItem(position);
					Intent intent = new Intent();
					if (item != null) {
						if("HYFL_02".equals(item.getActive_title())){
							//��Ա�����ƻ�����
							intent.setClass(ActivitysRegionActivity.this,PrizeRegion2TempActivity.class);
						}else if("MONDAY_ROB_CASH".equals(item.getActive_title())){
							//ÿ��һ���ֽ�
							intent.setClass(ActivitysRegionActivity.this,LXJ5TempActivity.class);
						}else{
							BannerInfo info = new BannerInfo();
							info.setLink_url(item.getUrl_link());
							intent.putExtra("BannerInfo", info);
							intent.setClass(ActivitysRegionActivity.this, BannerTopicActivity.class);
						}
					}
					ActivitysRegionActivity.this.startActivity(intent);
				}
			}
		});

		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// ����ˢ��
				pageNo = 0;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						handler.sendEmptyMessage(REQUEST_ACTIVE_LIST);
					}
				}, 1000L);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// �������ظ���
				pageNo++;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						isLoadMore = true;
						handler.sendEmptyMessage(REQUEST_ACTIVE_LIST);
					}
				}, 1000L);

			}
		});
	}

	private void updateAdapter(List<ActiveInfo> list){
		mActivitysRegionAdapter.setItems(list);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_topbar_left_layout:
			finish();
			break;
		case R.id.activitys_region_activity_prizeregion2_layout:
			//��Ա�����ƻ�2��
			Intent intentPrizeRegion2 = new Intent(ActivitysRegionActivity.this,PrizeRegion2TempActivity.class);
			startActivity(intentPrizeRegion2);
			break;
		case R.id.activitys_region_activity_sign_layout:
			//ǩ��
			Intent intentSign = new Intent(ActivitysRegionActivity.this,SignTopicTempActivity.class);
			startActivity(intentSign);
			break;
		case R.id.activitys_region_activity_ysq_layout:
			//ѹ��Ǯ
//			Intent intentysq = new Intent(ActivitysRegionActivity.this,XCFLTempActivity.class);
//			startActivity(intentysq);
			break;
		case R.id.activitys_region_activity_mb_layout:
			//���
			break;
		case R.id.activitys_region_activity_prizeregion_layout:
			//��Ա�����ƻ�
			break;
		case R.id.activitys_region_activity_xsfx_layout:
			//��ʱ����
			break;
		case R.id.activitys_region_activity_yqhy_layout:
			//������ѷ���
			Intent yqhyIntent = new Intent(ActivitysRegionActivity.this,YQHYTempActivity.class);
			startActivity(yqhyIntent);
//			Intent yqhyIntent = new Intent(ActivitysRegionActivity.this,WebViewCookieTestActivity.class);
//			startActivity(yqhyIntent);
			break;
		case R.id.activitys_region_activity_qxj5_layout:
			Intent intentQXJ5 = new Intent(ActivitysRegionActivity.this,LXJ5TempActivity.class);
			startActivity(intentQXJ5);
			break;
		default:
			break;
		}
	}

	/**
	 * ��ʼ���״̬
	 */
	private void initActivityStatus(int resultCode,String activeTitle){
		//ǩ����Ƿ�ʼ
		if("QD_03".equals(activeTitle)){
			//���·�ǩ���
			if(resultCode == 0){
				signLayout.setEnabled(true);
				signTV.setText("�����μ�");
				signTV.setBackgroundColor(getResources().getColor(R.color.common_topbar_bg_color));
			}else if(resultCode == -3){
				signLayout.setEnabled(false);
				signTV.setText("�����");
				signTV.setBackgroundColor(getResources().getColor(R.color.gray1));
			}else if(resultCode == -2){
				signLayout.setEnabled(true);
				signTV.setText("�����ڴ�");
				signTV.setBackgroundColor(getResources().getColor(R.color.common_topbar_bg_color));
			}
		}else if("HYFL_02".equals(activeTitle)){
			//��Ա�����ƻ�2��
			if(resultCode == 0){
				prizeRegion2Layout.setEnabled(true);
				prizeRegion2TV.setText("�����μ�");
				prizeRegion2TV.setBackgroundColor(getResources().getColor(R.color.common_topbar_bg_color));
			}else if(resultCode == -3){
				prizeRegion2Layout.setEnabled(false);
				prizeRegion2TV.setText("�����");
				prizeRegion2TV.setBackgroundColor(getResources().getColor(R.color.gray1));
			}else if(resultCode == -2){
				prizeRegion2Layout.setEnabled(true);
				prizeRegion2TV.setText("�����ڴ�");
				prizeRegion2TV.setBackgroundColor(getResources().getColor(R.color.common_topbar_bg_color));
			}
		}else if("TGY_01".equals(activeTitle)){
			//���·�������ѷ��ֻ
			if(resultCode == 0){
				yqhyLayout.setEnabled(true);
				yqhyTV.setText("�����μ�");
				yqhyTV.setBackgroundColor(getResources().getColor(R.color.common_topbar_bg_color));
			}else if(resultCode == -3){
				yqhyLayout.setEnabled(false);
				yqhyTV.setText("�����");
				yqhyTV.setBackgroundColor(getResources().getColor(R.color.gray1));
			}else if(resultCode == -2){
				yqhyLayout.setEnabled(true);
				yqhyTV.setText("�����ڴ�");
				yqhyTV.setBackgroundColor(getResources().getColor(R.color.common_topbar_bg_color));
			}
		}else if("MONDAY_ROB_CASH".equals(activeTitle)){
			//5�·�ÿ��һ���ֽ�
			if(resultCode == 0){
				qxj5Layout.setEnabled(true);
				qxj5TV.setText("�����μ�");
				qxj5TV.setBackgroundColor(getResources().getColor(R.color.common_topbar_bg_color));
			}else if(resultCode == -3){
				qxj5Layout.setEnabled(false);
				qxj5TV.setText("�����");
				qxj5TV.setBackgroundColor(getResources().getColor(R.color.gray1));
			}else if(resultCode == -2){
				qxj5Layout.setEnabled(true);
				qxj5TV.setText("�����ڴ�");
				qxj5TV.setBackgroundColor(getResources().getColor(R.color.common_topbar_bg_color));
			}
		}
		
		//�´���� ѹ��Ǯ
		ysqLayout.setEnabled(false);
		ysqTV.setText("�����");
		ysqTV.setBackgroundColor(getResources().getColor(R.color.gray1));
		
		//��ʾ���
		mbLayout.setEnabled(false);
		mbTV.setText("�����");
		mbTV.setBackgroundColor(getResources().getColor(R.color.gray1));
		
		//��Ա��Ʒר��
		prizeRegionLayout.setEnabled(false);
		prizeRegionTV.setText("�����");
		prizeRegionTV.setBackgroundColor(getResources().getColor(R.color.gray1));
		
		xsfxLayout.setEnabled(false);
		xsfxTV.setText("�����");
		xsfxTV.setBackgroundColor(getResources().getColor(R.color.gray1));
	}

	/**
	 * �ר���б�
	 * @param page
	 * @param pageSize
	 * @param status
	 * @param fromWhere
	 * @param picShowStatus
	 */
	private void requestActiveList(int page,int pageSize,
								   String status,String fromWhere,String picShowStatus){
		AsyncActiveList task = new AsyncActiveList(ActivitysRegionActivity.this,String.valueOf(page),String.valueOf(pageSize),status,fromWhere,picShowStatus,
			new OnCommonInter(){
				@Override
				public void back(BaseInfo baseInfo) {
					if(baseInfo != null){
						int resultCode = SettingsManager.getResultCode(baseInfo);
						if(resultCode == 0){
							List<ActiveInfo> activeList = baseInfo.getmActivePageInfo().getActiveList();
							Message msg = handler
									.obtainMessage(REQUEST_ACTIVE_LIST_SUC);
							msg.obj = baseInfo;
							handler.sendMessage(msg);
						}else{
							Message msg = handler
									.obtainMessage(REQUEST_ACTIVE_LIST_FAILE);
							msg.obj = baseInfo;
							handler.sendMessage(msg);
						}
					}
				}
			});
		task.executeAsyncTask(SettingsManager.FULL_TASK_EXECUTOR);
	}

	/**
	 * �ж�ǩ����Ƿ�ʼ
	 * @param activeTitle
	 */
	private void requestActiveTime(final String activeTitle){ 
		if(mLoadingDialog != null){
			mLoadingDialog.show();
		}
		AsyncXCFLActiveTime task = new AsyncXCFLActiveTime(ActivitysRegionActivity.this, activeTitle, 
				new OnCommonInter() {
					@Override
					public void back(BaseInfo baseInfo) {
						if(mLoadingDialog != null){
							mLoadingDialog.dismiss();
						}
						if(baseInfo != null){
							int resultCode = SettingsManager.getResultCode(baseInfo);
							initActivityStatus(resultCode,activeTitle);
						}
					}
				});
		task.executeAsyncTask(SettingsManager.FULL_TASK_EXECUTOR);
	}
}
