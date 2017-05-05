package com.ylfcf.ppp.adapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ylfcf.ppp.R;
import com.ylfcf.ppp.entity.RechargeRecordInfo;
import com.ylfcf.ppp.util.Util;
/**
 * ��ֵ��¼
 * @author Mr.liu
 *
 */
public class RechargeRecordAdapter extends ArrayAdapter<RechargeRecordInfo>{
	private static final int RESOURCE_ID = R.layout.wdy_lendrecord_item;  
	private Context context;
	private List<RechargeRecordInfo> rechargeList = null;
	private LayoutInflater layoutInflater = null;
	private DecimalFormat df = new DecimalFormat("#.00");//���ָ�ʽ����#��ʾһ����0������
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private OnRechargeRecordItemClickListener onItemClickListener;
	
	public RechargeRecordAdapter(Context context,OnRechargeRecordItemClickListener onItemClickListener){
		super(context, RESOURCE_ID);
		this.context = context;
		this.onItemClickListener = onItemClickListener;
		rechargeList = new ArrayList<RechargeRecordInfo>();
		layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
	}
	
	/**
	 * ���ⷽ������̬�ı�listview��item������ˢ��
	 * @param recordList
	 */
	public void setItems(List<RechargeRecordInfo> recordList){
		rechargeList.clear();
		if(recordList != null){
			rechargeList.addAll(recordList);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return rechargeList.size();
	}

	@Override
	public RechargeRecordInfo getItem(int position) {
		return rechargeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		double rechargeMoney = 0d;
		final RechargeRecordInfo info = rechargeList.get(position);
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(RESOURCE_ID, null);
			viewHolder.rechargeTime = (TextView)convertView.findViewById(R.id.wdy_lendrecord_item_plandate);
			viewHolder.rechargeMoney = (TextView)convertView.findViewById(R.id.wdy_lendrecord_item_realdate);
			viewHolder.status = (TextView)convertView.findViewById(R.id.wdy_lendrecord_item_money);
			viewHolder.option = (TextView)convertView.findViewById(R.id.wdy_lendrecord_item_option);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.rechargeTime.setText(info.getAdd_time().split(" ")[0]);
		try {
			rechargeMoney = Double.parseDouble(info.getAccount());
			if(rechargeMoney > 1){
				viewHolder.rechargeMoney.setText((int)rechargeMoney+"Ԫ");
			}else{
				viewHolder.rechargeMoney.setText(rechargeMoney+"Ԫ");
			}
			
		} catch (Exception e) {
			viewHolder.rechargeMoney.setText(info.getAccount()+"Ԫ");
		}
		viewHolder.status.setText(info.getStatus());
		if("�ɹ�".equals(info.getStatus())){
			viewHolder.option.setText("�鿴ת��ƾ֤");
			viewHolder.option.setEnabled(true);
			viewHolder.option.setTextColor(context.getResources().getColor(R.color.common_topbar_bg_color));
		}else{
			viewHolder.option.setText("һ һ");
			viewHolder.option.setEnabled(false);
			viewHolder.option.setTextColor(context.getResources().getColor(R.color.gray1));
		}
		viewHolder.option.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onItemClickListener.onClick(v, info);
			}
		});
		return convertView;
	}
	
	/**
	 * �ڲ��࣬����Item��Ԫ��
	 * @author Mr.liu
	 *
	 */
	class ViewHolder{
		TextView rechargeTime;//��ֵʱ��
		TextView rechargeMoney;//��ֵ���
		TextView status;//��ֵ״̬
		TextView option;//����
	}

	public interface OnRechargeRecordItemClickListener{
		void onClick(View v,RechargeRecordInfo info);
	}
}
