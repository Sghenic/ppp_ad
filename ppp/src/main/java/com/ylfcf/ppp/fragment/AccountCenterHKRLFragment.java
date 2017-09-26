package com.ylfcf.ppp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ylfcf.info.DateData;
import com.example.ylfcf.info.MarkStyle;
import com.example.ylfcf.inter.OnExpDateClickListener;
import com.example.ylfcf.inter.OnMonthScrollListener;
import com.example.ylfcf.widget.ExpCalendarView;
import com.ylfcf.ppp.R;
import com.ylfcf.ppp.async.AsyncRepayment;
import com.ylfcf.ppp.entity.BaseInfo;
import com.ylfcf.ppp.entity.RepaymentInfo;
import com.ylfcf.ppp.inter.Inter;
import com.ylfcf.ppp.ui.AccountCenterActivity;
import com.ylfcf.ppp.util.SettingsManager;
import com.ylfcf.ppp.util.UMengStatistics;
import com.ylfcf.ppp.util.Util;
import com.ylfcf.ppp.util.YLFLogger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * �˻����� -- �ؿ�����
 * Created by Administrator on 2017/8/2.
 */

public class AccountCenterHKRLFragment extends BaseFragment implements OnClickListener {
    private static final String className = "AccountCenterHKRLFragment";
    private static final int REQUEST_REPAYMENT_WHAT = 8945;
    private static final int REQUEST_REPAYMENT_SUC = 8946;
    private final Map<String,RepaymentDayData> repaymentDayDataMap = new HashMap<String,RepaymentDayData>();

    private AccountCenterActivity mainActivity;
    private View rootView;

    private ImageView arrowLeftImg,arrowRightImg;
    private TextView curMonthTV;//��ǰ�·�
    private TextView hkTotalMoneyTV;//�ؿ��ܶ�
    private ExpCalendarView mExpCalendarView;//�����ؼ�
    private TextView curDateTV;//��ǰ����
    private TextView hkCountTV;//���»������
    private TextView hkMoneyTV;//���ջؿ���
    private Button catInvestRecordBtn;//�鿴�ؿ�����

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
    private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
    private List<String> repaymentDateList;//�ؿ����� yyyyMMdd
    private List<String> repaymentCurDayMoneyList;//���ջؿ���
    private List<String> repaymentCurDayCountList;//���ջؿ����
    private int curYear = 2017;//��ǰ�����
    private int curMonth = 1;//��ǰ���·�


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REQUEST_REPAYMENT_WHAT:
                    requestRepaymentInfo(SettingsManager.getUserId(mainActivity));
                    break;
            }
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
        handler.sendEmptyMessage(REQUEST_REPAYMENT_WHAT);
        return rootView;
    }

    private void findViews(View rootView,LayoutInflater layoutInflater){
        arrowLeftImg = (ImageView)rootView.findViewById(R.id.account_center_hkrl_arrow_left);
        arrowLeftImg.setOnClickListener(this);
        arrowRightImg = (ImageView)rootView.findViewById(R.id.account_center_hkrl_arrow_right);
        arrowRightImg.setOnClickListener(this);
        curMonthTV = (TextView) rootView.findViewById(R.id.account_center_hkrl_date_tv);
        hkTotalMoneyTV = (TextView) rootView.findViewById(R.id.account_center_hkrl_curmonth_totalmoney);
        mExpCalendarView = (ExpCalendarView) rootView.findViewById(R.id.account_center_hkrl_calendarview);
        curDateTV = (TextView) rootView.findViewById(R.id.account_center_hkrl_hkinfo_cur_date_tv);
        hkCountTV = (TextView) rootView.findViewById(R.id.account_center_hkrl_hkinfo_count);
        hkMoneyTV = (TextView) rootView.findViewById(R.id.account_center_hkrl_hkinfo_money);
        catInvestRecordBtn = (Button) rootView.findViewById(R.id.account_center_hkrl_cat_invest_record_btn);
        catInvestRecordBtn.setOnClickListener(this);

        initListners();
    }

    private void initListners(){
        mExpCalendarView.setOnDateClickListener(new OnExpDateClickListener(){
            @Override
            public void onDateClick(View view, DateData date) {
                super.onDateClick(view, date);
                updateCurDayRepayment(date);
            }
        }).setOnMonthScrollListener(new OnMonthScrollListener() {
            @Override
            public void onMonthChange(int year, int month) {
                curMonthTV.setText(String.format("%d��%d��", year, month));
                if(month < 10){
                    getCurMonthRepayment(new StringBuffer().append(year).append("0").append(month).toString());
                }else{
                    getCurMonthRepayment(new StringBuffer().append(year).append(month).toString());
                }
                curYear = year;
                curMonth = month;
            }

            @Override
            public void onMonthScroll(float positionOffset) {
                YLFLogger.i("listener", "onMonthScroll:" + positionOffset);
            }
        });
    }

    private void updateCurDayRepayment(DateData data){
        if(data == null){
            return;
        }
        String key = new StringBuffer().append(data.getYear()).append(data.getMonthString()).append(data.getDayString()).toString();
        RepaymentDayData dayData = repaymentDayDataMap.get(key);
        if(dayData == null){
            curDateTV.setText(data.getMonth()+"��"+data.getDay()+"��");
            hkCountTV.setText("0");
            hkMoneyTV.setText("0");
            return;
        }
        curDateTV.setText(data.getMonth()+"��"+data.getDay()+"��");
        hkCountTV.setText(dayData.getCount());
        hkMoneyTV.setText(dayData.getMoney());
    }

    private void initCurMonthData(String curDateStr){
        if("".equals(curDateStr)){
            curMonthTV.setText(sdf.format(new Date()));
            getCurMonthRepayment(String.valueOf(sdf2.format(new Date())));
            intCurYearAndMonth(sdf2.format(new Date()));
            return;
        }
        try{
            curMonthTV.setText(sdf.format(sdf1.parse(curDateStr)));
            getCurMonthRepayment(sdf2.format(sdf1.parse(curDateStr)));
            intCurYearAndMonth(sdf2.format(sdf1.parse(curDateStr)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * date yyyyMM��ʽ
     * @param date
     */
    private void intCurYearAndMonth(String date){
        String yearS="";
        String monthS="";
        String dayS="";
        try {
            yearS = sdfYear.format(sdf2.parse(date));
            monthS = sdfMonth.format(sdf3.parse(date));
            dayS = sdfDay.format(sdf3.parse(date));
        }catch(Exception e){

        }

        int year = 0;
        int month = 0;
        int day = 0;
        try {
            year = Integer.parseInt(yearS);
            month = Integer.parseInt(monthS.startsWith("0")?monthS.replace("0",""):monthS);
            day = Integer.parseInt(dayS.startsWith("0")?dayS.replace("0",""):dayS);
        }catch (Exception e){

        }
        curYear = year;
        curMonth = month;
    }

    /**
     * ��ÿ��Ļؿ���Ϣ����һ����������
     */
    private SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
    private SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
    private SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
    private void initRepaymentData(){
        if(repaymentDateList == null || repaymentCurDayMoneyList == null
                || repaymentCurDayCountList == null){
            return;
        }
        for(int i=0;i<repaymentDateList.size();i++){
            RepaymentDayData dayData = new RepaymentDayData();
            dayData.setDate(repaymentDateList.get(i));
            dayData.setCount(repaymentCurDayCountList.get(i));
            dayData.setMoney(repaymentCurDayMoneyList.get(i));
            repaymentDayDataMap.put(repaymentDateList.get(i),dayData);
        }
        for (Map.Entry<String, RepaymentDayData> entry : repaymentDayDataMap.entrySet()) {
            String key  = entry.getKey();
            String yearS="";
            String monthS="";
            String dayS="";
            try {
                yearS = sdfYear.format(sdf3.parse(key));
                monthS = sdfMonth.format(sdf3.parse(key));
                dayS = sdfDay.format(sdf3.parse(key));
            }catch(Exception e){

            }

            int year = 0;
            int month = 0;
            int day = 0;
            try {
                year = Integer.parseInt(yearS);
                month = Integer.parseInt(monthS.startsWith("0")?monthS.replace("0",""):monthS);
                day = Integer.parseInt(dayS.startsWith("0")?dayS.replace("0",""):dayS);
            }catch (Exception e){

            }
            DateData data = new DateData(year,month,day);
            data.setMarkStyle(new MarkStyle(MarkStyle.DEFAULT,MarkStyle.defaultColor));
            mExpCalendarView.markDate(data);
        }
    }

    /**
     * ��ȡ���µĻؿ��ܽ��
     * @param curMonth ��ʽyyyy-MM
     */
    private void getCurMonthRepayment(String curMonth){
        List<Double> curMonthRepaymentList = new ArrayList<Double>();
        for (Map.Entry<String, RepaymentDayData> entry : repaymentDayDataMap.entrySet()) {
            if(entry.getKey().contains(curMonth)){
                RepaymentDayData repaymentDayData  = entry.getValue();
                double dayMoneyD = 0d;
                try {
                    dayMoneyD = Double.parseDouble(repaymentDayData.getMoney());
                }catch (Exception e){

                }
                curMonthRepaymentList.add(dayMoneyD);
            }
        }
        double dayMoneyTotalD = 0d;
        for(int i=0;i<curMonthRepaymentList.size();i++){
            dayMoneyTotalD += curMonthRepaymentList.get(i);
        }
        hkTotalMoneyTV.setText(Util.double2PointDouble(dayMoneyTotalD));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.account_center_hkrl_arrow_left:
                //����ļ�ͷ
                if(curMonth == 1){
                    curMonth = 12;
                    curYear --;
                }else{
                    curMonth--;
                }
                DateData data = new DateData(curYear,curMonth,1);
                mExpCalendarView.travelTo(data);
                break;
            case R.id.account_center_hkrl_arrow_right:
                //���ҵļ�ͷ
                if(curMonth == 12){
                    curMonth = 1;
                    curYear ++;
                }else{
                    curMonth++;
                }
                DateData data1 = new DateData(curYear,curMonth,1);
                mExpCalendarView.travelTo(data1);
                break;
        }
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

    /**
     * �ؿ���Ϣ
     * @param userId
     */
    private void requestRepaymentInfo(String userId){
        AsyncRepayment task = new AsyncRepayment(mainActivity, userId, new Inter.OnCommonInter() {
            @Override
            public void back(BaseInfo baseInfo) {
                if(baseInfo != null){
                    int resultCode = SettingsManager.getResultCode(baseInfo);
                    if(resultCode == 0){
                        RepaymentInfo info = baseInfo.getmRepaymentInfo();
                        repaymentDateList = info.getRepaymentDateList();
                        repaymentCurDayMoneyList = info.getRepaymentCurDayMoneyList();
                        repaymentCurDayCountList = info.getRepaymentCurDayCountList();
                        initRepaymentData();
                        initCurMonthData(baseInfo.getTime());
                    }
                }
            }
        });
        task.executeAsyncTask(SettingsManager.FULL_TASK_EXECUTOR);
    }

    class RepaymentDayData{
        String date;
        String money;//����ؿ���
        String count;//����ؿ����

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }
    }
}
