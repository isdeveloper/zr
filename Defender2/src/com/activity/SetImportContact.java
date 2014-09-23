package com.activity;

import java.util.HashMap;
import java.util.List;

import com.example.defender2.R;
import com.listview.adapter.ImportAdapter;
import com.listview.consdata.ContactConstruct;
import com.listview.entity.Contact;
import com.sql.DBProcess;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

@SuppressLint("UseSparseArrays")
public class SetImportContact extends Activity {
	/**
	 * 读取通讯录，导入联系人到黑白名单中
	 */
	private ListView contactlist;//联系人列表
	private Button finish;//完成按钮
	private HashMap<Integer,Boolean> checkedMap = new HashMap<Integer, Boolean>();//选中的条目
	private DBProcess prodb = new DBProcess(this);//数据库操作
	private String source;//来源（黑白名单，作为数据库操作的表）
    private List<Contact> datalist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_import_contact);
		
		//处理上页参数
		Intent intent = getIntent();
		source = intent.getStringExtra("source");
		
		//获取控件
		contactlist = (ListView)findViewById(R.id.importcontact_list);
		finish = (Button)findViewById(R.id.importcontact_finish_button);
		
		//设置适配器
		contactlist.setItemsCanFocus(false);
		contactlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		ContactConstruct contact = new ContactConstruct(this);
		datalist = contact.getList();//构造list
		contactlist.setAdapter(new ImportAdapter(this,datalist,checkedMap));//list绑定到适配器，适配器绑定到listview

		//设置监听
		contactlist.setOnItemClickListener(new OnItemClickListener() {
			   @Override
			   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    //对于由position指定的项目，返回其是否被选中。
			    //只有当选择模式已被设置为CHOICE_MODE_SINGLE或CHOICE_MODE_MULTIPLE时 ，结果才有效。
			    checkedMap.put(position,contactlist.isItemChecked(position));
			   }
		});
		finish.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				addData();
				Toast.makeText(SetImportContact.this, "导入成功！", Toast.LENGTH_SHORT).show();
				
				Intent intent;
				if(source.equals("whitelist_table"))
					intent= new Intent(SetImportContact.this,SetWhiteList.class);
				else if(source.equals("blacklist_table"))
					intent= new Intent(SetImportContact.this,SetBlackList.class);
				else
					intent= new Intent(SetImportContact.this,Setting.class);
				startActivity(intent);
				prodb.close();
				finish();
			}});
	}
	
	//向数据库添加数据
	private void addData(){
		Cursor myCursor;//结果集
		myCursor = prodb.select(source);
		
		for(int posizion = 0; posizion <= datalist.size()-1; posizion++)
			if(checkedMap.containsKey(posizion))
				if(checkedMap.get(posizion) != null && true == checkedMap.get(posizion)){
					boolean flag = true;
					
					while(myCursor.moveToNext())
						if(myCursor.getString(1).equals(datalist.get(posizion).getName()))
							flag = false;
					
					if(flag)
						prodb.insert(datalist.get(posizion).getName(), datalist.get(posizion).getPhone(), source);
				}
	}
}
