package com.ylfcf.ppp.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.ylfcf.ppp.R;
import com.ylfcf.ppp.adapter.RedBagAdapter;
import com.ylfcf.ppp.async.AsyncRedbgList;
import com.ylfcf.ppp.entity.BaseInfo;
import com.ylfcf.ppp.entity.RedBagInfo;
import com.ylfcf.ppp.inter.Inter.OnCommonInter;
import com.ylfcf.ppp.ui.MyHongbaoActivity;
import com.ylfcf.ppp.util.SettingsManager;

/**
 * �ҵĺ�� ---- ��ʹ��
 * 
 * @author Administrator
 * 
 */
public class MyHBUsedFragment extends BaseFragment {
	public final int REQUEST_HB_LIST_WHAT = 1800;
	private final int REQUEST_HB_LIST_SUCCESS = 1801;
	private final int REQUEST_HB_LIST_FAILE = 1802;

	private MyHongbaoActivity mainActivity;
	private View rootView;

	private RedBagAdapter redBagAdapter;
	private PullToRefreshListView pullToRefreshListView;
	private TextView nodataText;
	private List<RedBagInfo> redbagList = new ArrayList<RedBagInfo>();

	private int pageNo = 0;
	private int pageSize = 10;
	private boolean isFirst = true;
	private boolean isLoadMore = false;// ���ظ���

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REQUEST_HB_LIST_WHAT:
				requestHBList(SettingsManager.getUserId(mainActivity
						.getApplicationContext()), "2");
				break;
			case REQUEST_HB_LIST_SUCCESS:
				nodataText.setVisibility(View.GONE);
				pullToRefreshListView.setVisibility(View.VISIBLE);
				BaseInfo baseInfo = (BaseInfo) msg.obj;
				if (baseInfo != null) {
					if (!isLoadMore) {
						redbagList.clear();
					}
					redbagList.addAll(baseInfo.getmRedBagPageInfo()
							.getRedbagList());
					redBagAdapter.setItems(redbagList);
				}
				isLoadMore = false;
				pullToRefreshListView.onRefreshComplete();
				break;
			case REQUEST_HB_LIST_FAILE:
				nodataText.setVisibility(View.VISIBLE);
				pullToRefreshListView.setVisibility(View.GONE);
				pullToRefreshListView.onRefreshComplete();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainActivity = (MyHongbaoActivity) getActivity();
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.myhb_used_fragment, null);
			findViews(rootView);
		}
		// �����rootView��Ҫ�ж��Ƿ��Ѿ����ӹ�parent��
		// �����parent��Ҫ��parentɾ����Ҫ��Ȼ�ᷢ�����rootview�Ѿ���parent�Ĵ���
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		handler.sendEmptyMessage(REQUEST_HB_LIST_WHAT);
		return rootView;
	}
	
	private void initListeners() {
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// ����ˢ��
						pageNo = 0;
						handler.sendEmptyMessage(REQUEST_HB_LIST_WHAT);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// �������ظ���
						pageNo++;
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								handler.sendEmptyMessage(REQUEST_HB_LIST_WHAT);
							}
						}, 1000L);

					}

				});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
	}

	private void findViews(View view) {
		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.myhb_used_fragment_pull_refresh_list);
		nodataText = (TextView) view
				.findViewById(R.id.myhb_used_fragment_nodata);
		redBagAdapter = new RedBagAdapter(mainActivity, null);
		pullToRefreshListView.setAdapter(redBagAdapter);
		initListeners();
	}

	private void requestHBList(String userId, String flag) {
		AsyncRedbgList redbagTask = new AsyncRedbgList(mainActivity, userId,
				flag, new OnCommonInter() {
					@Override
					public void back(BaseInfo baseInfo) {
						if (baseInfo != null) {
							int resultCode = SettingsManager
									.getResultCode(baseInfo);
							if (resultCode == 0) {
								Message msg = handler
										.obtainMessage(REQUEST_HB_LIST_SUCCESS);
								msg.obj = baseInfo;
								handler.sendMessage(msg);
							} else {
								Message msg = handler
										.obtainMessage(REQUEST_HB_LIST_FAILE);
								msg.obj = baseInfo.getError();
								handler.sendMessage(msg);
							}
						} else {
							Message msg = handler
									.obtainMessage(REQUEST_HB_LIST_FAILE);
							handler.sendMessage(msg);
						}
					}
				});
		redbagTask.executeAsyncTask(SettingsManager.FULL_TASK_EXECUTOR);
	}
}
