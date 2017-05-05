package com.ylfcf.ppp.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ylfcf.ppp.R;
import com.ylfcf.ppp.adapter.ArticleAdapter.ViewHolder;
import com.ylfcf.ppp.entity.ArticleInfo;
import com.ylfcf.ppp.entity.JiaxiquanInfo;
/**
 * �ҵļ�Ϣȯ�б�
 * @author Mr.liu
 *
 */
public class MyJXQListAdapter extends ArrayAdapter<JiaxiquanInfo>{
	private static final int RESOURCE_ID = R.layout.myjxq_item;
	private List<JiaxiquanInfo> jiaxiquanList = null;
	private Context context;
	private LayoutInflater layoutInflater;
	private OnJXQItemClickListener clickListener;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Date sysNowTime ;
	
	public MyJXQListAdapter(Context context,OnJXQItemClickListener clickListener) {
		super(context, RESOURCE_ID);
		this.context = context;
		this.clickListener = clickListener;
		layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		jiaxiquanList = new ArrayList<JiaxiquanInfo>();
	}
	
	/**
	 * ���ⷽ������̬�ı�listview��item������ˢ��
	 * @param articleList
	 * @param nowTime ϵͳ��ǰʱ��
	 */
	public void setItems(List<JiaxiquanInfo> list,String nowTime){
		try {
			this.sysNowTime = sdf1.parse(nowTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.jiaxiquanList.clear();
		if(jiaxiquanList != null){
			this.jiaxiquanList.addAll(list);
		}
//		Collections.reverse(jiaxiquanList);
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return jiaxiquanList.size();
	}
	
	@Override
	public JiaxiquanInfo getItem(int position) {
		return jiaxiquanList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	int curPosition = 0;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		curPosition = position;
		JiaxiquanInfo info = jiaxiquanList.get(position);
		ViewHolder viewHolder = null;
		String startTime = "";
		String endTime = "";
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(RESOURCE_ID, null);
			viewHolder.jxText = (TextView) convertView.findViewById(R.id.myjxq_item_jiaxi);
			viewHolder.syfwText = (TextView) convertView.findViewById(R.id.myjxq_item_syfw);
			viewHolder.yxqText = (TextView) convertView.findViewById(R.id.myjxq_item_validity);
			viewHolder.syyqText = (TextView) convertView.findViewById(R.id.myjxq_item_limit);
			viewHolder.remark = (TextView) convertView.findViewById(R.id.myjxq_item_remark);
			viewHolder.useBtn = (Button) convertView.findViewById(R.id.myjxq_list_item_btn);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		try {
			Date startDate = sdf.parse(info.getEffective_start_time());
			Date endDate = sdf.parse(info.getEffective_end_time());
			startTime = sdf.format(startDate);
			endTime = sdf.format(endDate);
		} catch (Exception e) {
		}
		
		viewHolder.jxText.setText(info.getMoney()+"%");
		if("".equals(info.getBorrow_type()) || info.getBorrow_type() == null || "null".equals(info.getBorrow_type())
				|| "NULL".equals(info.getBorrow_type())){
			viewHolder.syfwText.setText("���÷�Χ������");
		}else{
			viewHolder.syfwText.setText("���÷�Χ��"+info.getBorrow_type());
		}
		
		viewHolder.yxqText.setText("��Ч�ڣ�"+startTime+"~"+endTime);
		viewHolder.syyqText.setText("ʹ��Ҫ��"+"����Ͷ�ʽ�����"+info.getMin_invest_money()+"Ԫ");
		if(info.getCoupon_from() == null || "".equals(info.getCoupon_from())){
			viewHolder.remark.setText("��ע�� �� ��");
		}else{
			viewHolder.remark.setText("��ע��"+info.getCoupon_from());
		}
		if("δʹ��".equals(info.getUse_status())){
			//
			Date endDate = null;
			Date startDate = null;
			try {
				endDate = sdf1.parse(info.getEffective_end_time());
				startDate = sdf1.parse(info.getEffective_start_time());
				if(sysNowTime.compareTo(startDate) == 1 && endDate.compareTo(sysNowTime) == 1){
					//˵����Ϣȯ�Ѿ���Ч
					viewHolder.useBtn.setEnabled(true);
					viewHolder.useBtn.setTextColor(context.getResources().getColor(R.color.common_topbar_bg_color));
					viewHolder.useBtn.setBackgroundResource(R.drawable.style_rect_fillet_blue);
				}else{
					viewHolder.useBtn.setEnabled(false);
					viewHolder.useBtn.setTextColor(context.getResources().getColor(R.color.gray));
					viewHolder.useBtn.setBackgroundResource(R.drawable.style_rect_fillet_gray);
				}
				//��ʾ��Ϣȯδ����
				if(endDate.compareTo(sysNowTime) == -1){
					//�ѹ���
					viewHolder.useBtn.setVisibility(View.GONE);
				}else{
					viewHolder.useBtn.setVisibility(View.VISIBLE);
				}
				
				viewHolder.useBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						clickListener.onClick(curPosition);
					}
				});
			} catch (Exception e) {
			}
		}else{
			viewHolder.useBtn.setVisibility(View.GONE);
		}
		return convertView;
	}

	/**
	 * �ڲ��࣬����item��Ԫ��
	 * @author Mr.liu
	 *
	 */
	class ViewHolder{
		TextView jxText;//��Ϣ
		TextView syfwText;//ʹ�÷�Χ
		TextView yxqText;//��Ч��
		TextView syyqText;//ʹ��Ҫ��
		TextView remark;//��ע
		Button useBtn;//����ʹ��
	}

	public interface OnJXQItemClickListener{
		void onClick(int position);
	}
}
