package com.ylfcf.ppp.adapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ylfcf.ppp.R;
import com.ylfcf.ppp.entity.BorrowType;
import com.ylfcf.ppp.entity.MoneyStatus;
import com.ylfcf.ppp.entity.ProductInfo;
import com.ylfcf.ppp.ui.BorrowDetailZXDActivity;
import com.ylfcf.ppp.util.SettingsManager;
import com.ylfcf.ppp.util.Util;
import com.ylfcf.ppp.util.YLFLogger;
import com.ylfcf.ppp.widget.RoundProgressBar;

/**
 * Ͷ����Ŀ�б�ҳ���������
 * @author Waggoner.wang
 *
 */
public class BidListAdapter extends ArrayAdapter<ProductInfo> {
	
	private static final int RESOURCE_ID = R.layout.bid_item;
	private final LayoutInflater mInflater;
    
    private List<ProductInfo> bidItems;
    private Context context;
    
	public BidListAdapter(Context context) {
		super(context, RESOURCE_ID);
		this.context = context;
		mInflater	= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		bidItems	= new ArrayList<ProductInfo>();
	}
	
	/**
	 * ���ⷽ������̬�ı�listview��item������ˢ��
	 * @param productList
	 */
	public void setItems(List<ProductInfo> productList){
		this.bidItems.clear();
		if(productList != null) {
			this.bidItems.addAll(productList);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return bidItems.size();
	}

	@Override
	public ProductInfo getItem(int position) {
		return bidItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * ���þ���һ����ĵ���ʾ����
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		ProductInfo info = bidItems.get(position);
		if (convertView == null){
			viewHolder = new ViewHolder();
			convertView	= mInflater.inflate(RESOURCE_ID, null);
			viewHolder.projectName = (TextView) convertView.findViewById(R.id.bid_item_project_name);
			viewHolder.interestRateMin = (TextView) convertView.findViewById(R.id.bid_item_invest_rate_min);
			viewHolder.middleText = (TextView) convertView.findViewById(R.id.bid_item_text0);
			viewHolder.interestRateMax = (TextView) convertView.findViewById(R.id.bid_item_invest_rate_max);
			viewHolder.timeLimit = (TextView) convertView.findViewById(R.id.bid_item_invest_time_limit);
			viewHolder.totalMoney = (TextView) convertView.findViewById(R.id.bid_item_invest_total_money);
			viewHolder.roundProgressBar = (RoundProgressBar)convertView.findViewById(R.id.bid_item_roundbar);
			viewHolder.angleImg = (ImageView)convertView.findViewById(R.id.bid_item_angle);
			viewHolder.progressTV = (TextView)convertView.findViewById(R.id.bid_item_progresstv);
			viewHolder.extraInterestLayout = (LinearLayout) convertView.findViewById(R.id.bid_item_extra_interest_layout);
			viewHolder.extraInterestText = (TextView) convertView.findViewById(R.id.bid_item_extra_interest_text);
			viewHolder.nhsyText = (TextView) convertView.findViewById(R.id.bid_item_nhsy_text);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
        viewHolder.projectName.setText(info.getBorrow_name());
        
        // Ͷ������
        if(info.getInvest_horizon() == null || "".equals(info.getInvest_horizon())){
        	viewHolder.timeLimit.setText(info.getInterest_period());
        }else{
        	String investHorString = info.getInvest_horizon().replace("��", "");
            viewHolder.timeLimit.setText(investHorString);
        }
        
        
        viewHolder.roundProgressBar.setTextIsDisplayable(false);//����ʾ�м����
        double totalMoneyL = 0d;
        int totalMoneyI = 0;
        try {
        	totalMoneyL = Double.parseDouble(info.getTotal_money());
        	totalMoneyI = (int)totalMoneyL;
		} catch (Exception e) {
		}
        viewHolder.totalMoney.setText(totalMoneyI/10000+"");
        String bite = info.getBite();
        float biteFloat = 0f;
        int biteInt = 0;
        if(bite!=null){
        	bite = bite.replace("%", "");
        }
        try {
        	biteFloat = Float.parseFloat(bite)*100;
        	biteInt = (int)biteFloat;
		} catch (Exception e) {
		}
        viewHolder.roundProgressBar.setMax(10000);
        YLFLogger.d("�ٷֱȣ�"+viewHolder.roundProgressBar.getProgress());
        if(MoneyStatus.NOFULL.equals(info.getMoney_status())){
        	viewHolder.progressTV.setText(info.getBite());
        	viewHolder.progressTV.setTextColor(context.getResources().getColor(R.color.common_topbar_bg_color));
        	viewHolder.roundProgressBar.setProgress(biteInt);
        }else{
        	try {
				viewHolder.progressTV.setText(Util.MoneyStatusCG(info));
			} catch (ParseException e) {
				e.printStackTrace();
			}
        	viewHolder.progressTV.setTextColor(context.getResources().getColor(R.color.gray));
        	viewHolder.roundProgressBar.setProgress(10000);
        }
        if(BorrowType.SUYING.equals(info.getBorrow_type()) || BorrowType.WENYING.equals(info.getBorrow_type())
        		|| BorrowType.BAOYING.equals(info.getBorrow_type())){
        	if(info.getInterest_period().contains("92")){
            	//Ԫ����
            	viewHolder.angleImg.setImageResource(R.drawable.yjr_list_logo);
            	viewHolder.nhsyText.setText("�껯����");
            }else if(info.getInterest_period().contains("32")){
            	//Ԫ��ͨ
            	viewHolder.angleImg.setImageResource(R.drawable.yyt_list_logo);
            	viewHolder.nhsyText.setText("�껯����");
            }else if(info.getInterest_period().contains("182")){
            	//Ԫ����
            	viewHolder.angleImg.setImageResource(R.drawable.ydh_list_logo);
            	viewHolder.nhsyText.setText("�껯����");
            }else if(info.getInterest_period().contains("365")){
            	viewHolder.angleImg.setImageResource(R.drawable.ynx_list_logo);
            	viewHolder.nhsyText.setText("�껯����");
            }else{
            	viewHolder.angleImg.setImageResource(R.color.transparent);
            	viewHolder.nhsyText.setText("�껯����");
            }
        }else if(BorrowType.VIP.equals(info.getBorrow_type())){
        	viewHolder.nhsyText.setText("ҵ���Ƚϻ�׼");
        	viewHolder.angleImg.setScaleType(ScaleType.FIT_CENTER);
        	viewHolder.angleImg.setImageResource(R.drawable.licai_vip_logo);
        }else{
        	viewHolder.angleImg.setImageResource(R.color.transparent);
        	viewHolder.nhsyText.setText("�껯����");
        }
        
        double extraInterestD = 0d;
        String extraInterest = info.getAndroid_interest_rate();
        try {
        	extraInterestD = Double.parseDouble(extraInterest);
		} catch (Exception e) {
		}
        if(extraInterestD > 0){
        	viewHolder.extraInterestLayout.setVisibility(View.VISIBLE);
        	viewHolder.extraInterestText.setText("+"+info.getAndroid_interest_rate());
        }else{
        	viewHolder.extraInterestLayout.setVisibility(View.GONE);
        }
        if(position == 0){
            convertView.setPadding(0, context.getResources().getDimensionPixelSize(R.dimen.common_measure_10dp), 0, 0);
        }else{
        	convertView.setPadding(0, 0, 0, 0);
        }
        if(info.getBorrow_type().contains("Ԫ�ƻ�")){
        	viewHolder.interestRateMin.setVisibility(View.GONE);
        	viewHolder.middleText.setVisibility(View.GONE);
        	viewHolder.interestRateMax.setText(info.getInterest_rate());
        }else if("vip".equals(info.getBorrow_type())){
        	viewHolder.interestRateMin.setVisibility(View.GONE);
        	viewHolder.middleText.setVisibility(View.GONE);
        	viewHolder.interestRateMax.setText(info.getInterest_rate());
        }else if(BorrowType.SUYING.equals(info.getBorrow_type()) || BorrowType.BAOYING.equals(info.getBorrow_type()) || 
        		BorrowType.WENYING.equals(info.getBorrow_type())){
        	float minRateF = 0f;
        	float maxRateF = 0f;
        	try {
        		minRateF = Float.parseFloat(info.getInterest_rate());
        		maxRateF = minRateF + 0.3f;
			} catch (Exception e) {
			}
        	if(SettingsManager.checkFloatRate(info) && !info.getInterest_period().contains("365")){
        		viewHolder.interestRateMin.setVisibility(View.VISIBLE);
            	viewHolder.middleText.setVisibility(View.VISIBLE);
            	viewHolder.interestRateMin.setText(Util.float2PointFloat(minRateF));
            	viewHolder.interestRateMax.setText(Util.float2PointFloat(maxRateF));
        	}else{
        		viewHolder.interestRateMin.setVisibility(View.GONE);
            	viewHolder.middleText.setVisibility(View.GONE);
            	viewHolder.interestRateMax.setText(info.getInterest_rate());
        	}
        }
		return convertView;
	}
	
	/**
	 * �ڲ��࣬������Item��Ԫ��
	 * @author Mr.liu
	 *
	 */
	class ViewHolder{
		ImageView angleImg;
		TextView projectName;
		TextView interestRateMin;//�껯������С����
		TextView middleText;//��������֮��ķ���
		TextView interestRateMax;//�껯�����������
		TextView timeLimit;//����
		TextView totalMoney;//ļ���ܽ��
		TextView progressTV;//����������Ϣ
		TextView nhsyText;//�껯�����ĸ�����VIP��Ʒ�иĳ��ˡ�ҵ���Ƚϻ�׼��
		RoundProgressBar roundProgressBar;
		LinearLayout extraInterestLayout;// ��Ϣ�Ĳ���
		TextView extraInterestText;//��Ϣ��text
	}
	
}
