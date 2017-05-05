package com.ylfcf.ppp.adapter;

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
import com.ylfcf.ppp.entity.RedBagInfo;

/**
 * ���������
 * 
 * @author Administrator
 */
public class RedBagAdapter extends ArrayAdapter<RedBagInfo> {
	private static final int RESOURCE_ID = R.layout.redbag_item;
	private Context context;
	private LayoutInflater layoutInflater;
	private List<RedBagInfo> redbagList;
	private OnHBListItemClickListener onItemClickListener;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public RedBagAdapter(Context context, OnHBListItemClickListener listener) {
		super(context, RESOURCE_ID);
		this.context = context;
		this.onItemClickListener = listener;
		redbagList = new ArrayList<RedBagInfo>();
		layoutInflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * ���ⷽ������̬�ı�listview��item������ˢ��
	 * @param list
	 */
	public void setItems(List<RedBagInfo> list) {
		this.redbagList.clear();
		if (list != null) {
			this.redbagList.addAll(list);
		}
		Collections.reverse(redbagList);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return redbagList.size();
	}

	@Override
	public RedBagInfo getItem(int position) {
		return redbagList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	int curPosition = 0;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		curPosition = position;
		RedBagInfo info = redbagList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(RESOURCE_ID, null);
			viewHolder.eduText = (TextView) convertView
					.findViewById(R.id.redbag_item_edu);
			viewHolder.validityText = (TextView) convertView
					.findViewById(R.id.regbag_item_validity);
			viewHolder.useLimitText = (TextView) convertView
					.findViewById(R.id.regbag_item_use_limit);
			viewHolder.btn = (Button) convertView
					.findViewById(R.id.redbag_list_item_btn);
			viewHolder.remark = (TextView) convertView
					.findViewById(R.id.regbag_item_remark);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		long endTime = 0l;
		long nowTime = new Date().getTime();
		try {
			endTime = sdf.parse(info.getEnd_time()).getTime();
		} catch (Exception e) {
		}

		if ("δʹ��".equals(info.getUse_status()) && endTime > nowTime) {
			viewHolder.btn.setVisibility(View.VISIBLE);
		} else {
			viewHolder.btn.setVisibility(View.GONE);
		}

		viewHolder.btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onItemClickListener != null) {
					onItemClickListener.onItemClick(v, curPosition);
				}
			}
		});
		if(info.getRemark() != null && !"".equals(info.getRemark())){
			viewHolder.remark.setText("��ע��"+info.getRemark());
		}else{
			viewHolder.remark.setText("��ע�� �� ��");
		}
		viewHolder.eduText.setText(info.getMoney());
		if("��ʹ��".equals(info.getUse_status())){
			viewHolder.validityText.setText("Ͷ�ʱ�ģ�" + info.getBorrow_name());
			viewHolder.useLimitText.setText("ʹ��ʱ�䣺" + info.getUse_time());
		}else{
			viewHolder.validityText.setText("��Ч�ڣ���" + info.getEnd_time());
			viewHolder.useLimitText.setText("ʹ�����ƣ�Ͷ�ʽ�����"
					+ info.getNeed_invest_money() + "Ԫ");
		}
		return convertView;
	}

	/**
	 * �ڲ��࣬����Item������
	 * @author Mr.liu
	 *
	 */
	class ViewHolder {
		TextView eduText;// ���
		TextView validityText;// ��Ч��
		TextView useLimitText;// ʹ������
		TextView remark;//��ע
		Button btn;
	}

	public interface OnHBListItemClickListener {
		void onItemClick(View v, int position);
	}
}
