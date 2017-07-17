package com.ylfcf.ppp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ylfcf.ppp.R;
import com.ylfcf.ppp.entity.JiaxiquanInfo;

import java.util.ArrayList;
import java.util.List;
/**
 * Ͷ��ҳ�� ��Ϣȯ���б�
 * @author Mr.liu
 *
 */
public class JXQInvestAdapter extends ArrayAdapter<JiaxiquanInfo>{
	private static final int RESOURCE_ID = R.layout.experience_listview_item;  
	private Context context;
	private List<JiaxiquanInfo> jiaxiList = null;
	private LayoutInflater layoutInflater = null;
	
	public JXQInvestAdapter(Context context){
		super(context, RESOURCE_ID);
		this.context = context;
		jiaxiList = new ArrayList<JiaxiquanInfo>();
		layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
	}
	
	/**
	 * ���ⷽ������̬�ı�listview��item������ˢ��
	 * @param list
	 */
	public void setItems(List<JiaxiquanInfo> list){
		jiaxiList.clear();
		if(list != null){
			jiaxiList.addAll(list);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return jiaxiList.size();
	}

	@Override
	public JiaxiquanInfo getItem(int position) {
		return jiaxiList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		JiaxiquanInfo info = jiaxiList.get(position);
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(RESOURCE_ID, null);
			viewHolder.text = (TextView)convertView.findViewById(R.id.experience_listview_item_text);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		double needInvestMoneyD = 0;
		try {
			needInvestMoneyD = Double.parseDouble(info.getMin_invest_money());
		} catch (Exception e) {
		}
		if(needInvestMoneyD <= 0){
			viewHolder.text.setText(info.getMoney()+"%�ļ�Ϣȯ");
		}else{
			if(needInvestMoneyD >= 10000){
				viewHolder.text.setText(info.getMoney()+"%�ļ�Ϣȯ��"+"��Ͷ��"+needInvestMoneyD/10000+"��Ԫ�����Ͽ���");
			}else{
				viewHolder.text.setText(info.getMoney()+"%�ļ�Ϣȯ��"+"��Ͷ��"+info.getMin_invest_money()+"Ԫ�����Ͽ���");
			}
		}
		return convertView;
	}
	
	/**
	 * �ڲ��࣬����Item��Ԫ��
	 * @author Mr.liu
	 *
	 */
	class ViewHolder{
		TextView text;
	}
}
