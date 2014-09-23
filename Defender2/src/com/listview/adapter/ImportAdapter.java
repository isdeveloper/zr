package com.listview.adapter;

import java.util.HashMap;
import java.util.List;

import com.example.defender2.R;
import com.listview.entity.Contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

public class ImportAdapter extends BaseAdapter{
	/**
	 * ������ϵ�˵�listview��������
	 */
	private LayoutInflater inflater ;
    private List<Contact> datalist;
    private HashMap<Integer,Boolean> checkedMap;
    
    public ImportAdapter(Context context , List<Contact> list, HashMap<Integer,Boolean> checkedMap){
    	this.datalist = list;
    	this.checkedMap = checkedMap;
    	inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
    	return datalist.size();
    }

    @Override
    public Object getItem(int position) {
    	return position;
    }

    @Override
    public long getItemId(int position) {
    	return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder = null;
    	if(convertView == null){
    		convertView = inflater.inflate(R.layout.setting_import_item, null);
    		holder = new ViewHolder();
    		holder.import_item = (CheckedTextView) convertView.findViewById(R.id.import_item);
    		convertView.setTag(holder);
    	}else
    		holder = (ViewHolder) convertView.getTag();
  
    	//��������
    	holder.import_item.setText(
    			"������"+datalist.get(position).getName().toString() + "\n"+
    			"�绰��"+datalist.get(position).getPhone().toString());
    	
    	//����checkMap��position��״̬�����Ƿ�ѡ��
    	if (checkedMap.get(position) != null && checkedMap.get(position) == true)
    		holder.import_item.setChecked(true);
    	else
    		holder.import_item.setChecked(false);
  
    	return convertView;
    }
    
    class ViewHolder {
    	CheckedTextView import_item;
    }
} 
