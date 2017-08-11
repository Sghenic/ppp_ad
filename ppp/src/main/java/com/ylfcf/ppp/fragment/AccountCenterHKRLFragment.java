package com.ylfcf.ppp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ylfcf.ppp.R;
import com.ylfcf.ppp.ui.AccountCenterActivity;
import com.ylfcf.ppp.util.UMengStatistics;

/**
 * �˻����� -- �ؿ�����
 * Created by Administrator on 2017/8/2.
 */

public class AccountCenterHKRLFragment extends BaseFragment implements OnClickListener {
    private static final String className = "AccountCenterHKRLFragment";
    private AccountCenterActivity mainActivity;
    private View rootView;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (AccountCenterActivity) getActivity();
        if(rootView==null){
            rootView = inflater.inflate(R.layout.account_center_hkrl_fragment, null);
            findViews(rootView,inflater);
        }
        //�����rootView��Ҫ�ж��Ƿ��Ѿ����ӹ�parent�� �����parent��Ҫ��parentɾ����Ҫ��Ȼ�ᷢ�����rootview�Ѿ���parent�Ĵ���
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void findViews(View rootView,LayoutInflater layoutInflater){

    }

    @Override
    public void onClick(View v) {

    }

    public void onResume() {
        super.onResume();
        UMengStatistics.statisticsOnPageStart(className);
    }
    public void onPause() {
        super.onPause();
        UMengStatistics.statisticsOnPageEnd(className);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
