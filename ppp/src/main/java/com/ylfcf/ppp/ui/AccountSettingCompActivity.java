package com.ylfcf.ppp.ui;

import com.ylfcf.ppp.R;
import com.ylfcf.ppp.async.AsyncUserSelectOne;
import com.ylfcf.ppp.entity.BaseInfo;
import com.ylfcf.ppp.entity.UserInfo;
import com.ylfcf.ppp.inter.Inter.OnGetUserInfoByPhone;
import com.ylfcf.ppp.util.SettingsManager;
import com.ylfcf.ppp.util.Util;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ��ҵ�û����˻�����ҳ��
 * @author Mr.liu
 *
 */
public class AccountSettingCompActivity extends BaseActivity implements
			OnClickListener{
	private final int REQUEST_GET_USERINFO_SUCCESS = 2601;
	private LinearLayout topLeftBtn;
	private TextView topTitleTV;

	private TextView usernameTV;
	private TextView phoneTV;

	private LinearLayout loginPwdLayout;
	private LinearLayout transPwdLayout;//��������
	private View line2;
	private LinearLayout gesturePwdLayout;
	private UserInfo userInfo = null;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REQUEST_GET_USERINFO_SUCCESS:
				userInfo = (UserInfo) msg.obj;
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
		setContentView(R.layout.account_setting_comp_activity);
		requestUserInfo(SettingsManager.getUserId(getApplicationContext()),"");
		findViews();
	}

	private void findViews() {
		topLeftBtn = (LinearLayout) findViewById(R.id.common_topbar_left_layout);
		topLeftBtn.setOnClickListener(this);
		topTitleTV = (TextView) findViewById(R.id.common_page_title);
		topTitleTV.setText("�˻�����");

		usernameTV = (TextView) findViewById(R.id.account_setting_comp_username_text);
		usernameTV.setText(SettingsManager.getUserName(getApplicationContext()));
		phoneTV = (TextView) findViewById(R.id.account_setting_comp_phone_text);
		phoneTV.setText(SettingsManager.getCompPhone(getApplicationContext()));

		loginPwdLayout = (LinearLayout) findViewById(R.id.account_setting_comp_login_pwd_layout);
		loginPwdLayout.setOnClickListener(this);
		transPwdLayout = (LinearLayout) findViewById(R.id.account_setting_comp_trans_pwd_layout);
		transPwdLayout.setOnClickListener(this);
		line2 = findViewById(R.id.account_setting_comp_activity_line2);
		transPwdLayout.setVisibility(View.VISIBLE);
		line2.setVisibility(View.VISIBLE);
		gesturePwdLayout = (LinearLayout) findViewById(R.id.account_setting_comp_gesture_pwd_layout);
		gesturePwdLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_topbar_left_layout:
			finish();
			break;
		case R.id.account_setting_comp_login_pwd_layout:
			Intent intent = new Intent(AccountSettingCompActivity.this,
					ModifyLoginPwdActivity.class);
			intent.putExtra("USERINFO", userInfo);
			startActivity(intent);
			break;
		case R.id.account_setting_comp_trans_pwd_layout:
			Intent intentTrans = new Intent(AccountSettingCompActivity.this,
					WithdrawPwdOptionActivity.class);
			intentTrans.putExtra("USERINFO", userInfo);
			startActivity(intentTrans);

			break;
		case R.id.account_setting_comp_gesture_pwd_layout:
			Intent intentSetting = new Intent(AccountSettingCompActivity.this,
					GestureSettingActivity.class);
			startActivity(intentSetting);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
	}
	
	/**
	 * �����û���Ϣ������hf_user_id�ֶ��ж��û��Ƿ��л㸶�˻�
	 * 
	 * @param userId
	 * @param phone
	 */
	private void requestUserInfo(String userId, String phone) {
		AsyncUserSelectOne userTask = new AsyncUserSelectOne(
				AccountSettingCompActivity.this, userId, phone, "",
				new OnGetUserInfoByPhone() {
					@Override
					public void back(BaseInfo baseInfo) {
						if (baseInfo != null) {
							int resultCode = SettingsManager
									.getResultCode(baseInfo);
							if (resultCode == 0) {
								UserInfo userInfo = baseInfo.getUserInfo();
								Message msg = handler
										.obtainMessage(REQUEST_GET_USERINFO_SUCCESS);
								msg.obj = userInfo;
								handler.sendMessage(msg);
							}
						}
					}
				});
		userTask.executeAsyncTask(SettingsManager.FULL_TASK_EXECUTOR);
	}
}
