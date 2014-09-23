package com.listview.consdata;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

import com.listview.entity.Contact;

public class ContactConstruct {
	/**
	 * ���쵼����ϵ�����ݵ�������list��
	 */
	private Context c;
	private List<Contact> list = new ArrayList<Contact>();
	
	//Ϊ���������������
	public ContactConstruct(Context c){
		this.c = c;
		// ��ȡͨѶ¼,����list
		ContentResolver resolver=this.c.getContentResolver();
		
		String[] columns = new String[] {Phone.DISPLAY_NAME,Phone.NUMBER,Phone.PHOTO_ID,Phone.CONTACT_ID};
		Cursor cursor = resolver.query(Phone.CONTENT_URI, columns, null,null, null);
		while (cursor.moveToNext()) {
			String name=cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
			String number=cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
			if(TextUtils.isEmpty(number))
				continue;//�жϺ����Ƿ�Ϊ��
			
			Contact contact = new Contact();
			contact.setName(name);
			contact.setPhone(number);
			list.add(contact);
		}
		cursor.close();
	}
	
	public List<Contact> getList() {
		return list;
	}
}
