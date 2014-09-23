package com.listview.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.defender2.R;
import com.listview.entity.Log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LogAdapter extends BaseAdapter {
	/**
	 * 导入防护日志的listview适配器日志
	 */
	private List<Log> list = new ArrayList<Log>();
	private LayoutInflater li = null;
	
	public LogAdapter(Context c, List<Log> list) {
		this.list = list;
		li = LayoutInflater.from(c);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if(convertView == null) {
			convertView = li.inflate(R.layout.log_item, null);
			viewHolder = new ViewHolder();
			viewHolder.num = (TextView)convertView.findViewById(R.id.log_number);
			viewHolder.time = (TextView)convertView.findViewById(R.id.log_time);
			viewHolder.process = (TextView)convertView.findViewById(R.id.log_process);
			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder)convertView.getTag();
		
		Log logEntity = list.get(position);
		viewHolder.num.setText(logEntity.getPhoneNO());
		viewHolder.time.setText(logEntity.getTime());
		viewHolder.process.setText(logEntity.getProcess());
		return convertView;
	}
	
	class ViewHolder {
		public TextView num;
		public TextView time;
		public TextView	process;
	}
}
