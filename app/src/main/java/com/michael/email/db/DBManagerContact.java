package com.michael.email.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.michael.email.EmailApp;
import com.michael.email.model.Contact;
import com.michael.email.util.L;

import java.util.ArrayList;
import java.util.List;


/**
 * 数据库管理类，将对数据库的各种操作封装为方法
 * 
 * @author Michael
 * 
 * */
public class DBManagerContact
{
	private static final String TAG = "DBManagerContact";

	private static DBHelper helper;

	private static DBManagerContact uniqueInstance;

	/**
	 * 单例
	 * */
	public static DBManagerContact getInstance()
	{
		if (uniqueInstance == null)
		{
			uniqueInstance = new DBManagerContact();
		}
		return uniqueInstance;
	}

	private DBManagerContact()
	{
		helper = new DBHelper(EmailApp.applicationContext);
	}

	/**
	 * 插入缓存,如果存在就删除
	 * */
	public void insertContact(Contact contact)
	{
		if (contact != null)
		{
			String sql = new StringBuilder("INSERT INTO ")
					.append(DBHelper.TABLE_NAME_CONTACT)
					.append(" (")
					.append(DBHelper.FIELD_CONTACT).append(")")
					.append("VALUES(?)").toString();
			SQLiteDatabase db = helper.getWritableDatabase();
			SQLiteStatement insertStmt = db.compileStatement(sql);
			insertStmt.clearBindings();
			insertStmt.bindString(1, contact.emailAddress);
			insertStmt.executeInsert();
			db.close();
			L.e(TAG, "插入缓存");
		}
	}

	/**
     * 获取缓存
     * */
    public List<Contact> getContacts()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = new StringBuilder("SELECT * FROM ").append(DBHelper.TABLE_NAME_CONTACT).append(" order by ").append("TIMESTAMP DESC").toString();
        Cursor cursor = db.rawQuery(sql, new String[] {});
        List<Contact> list = new ArrayList<Contact>();
        if (cursor.getCount() > 0)
        {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
				Contact contact = new Contact();
				contact.id = cursor.getString(cursor.getColumnIndex("_id"));
				contact.emailAddress = cursor.getString(cursor.getColumnIndex(DBHelper.FIELD_CONTACT));
                list.add(contact);
            }
        }
        if (cursor != null && !cursor.isClosed())
        {
            cursor.close();
        }
        db.close();
        if (cursor.getCount() == 0)
        {
            L.e(TAG, "当前缓存不存在，返回null");
        }
        else
        {
            L.e(TAG, "当前缓存存在, 成功返回");
        }
        return list;
    }

	/**
	 * 缓存是否存在
	 * */
	public boolean isContactExist(String emailAddress)
	{
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "SELECT * FROM " + DBHelper.TABLE_NAME_CONTACT + " WHERE " + DBHelper.FIELD_CONTACT + " = '"+ emailAddress + "'";
		Cursor cursor = db.rawQuery(sql, new String[] {});
		int count = cursor.getCount();
		db.close();
		if (count > 0)
		{
			L.e(TAG, "缓存存在");
			return true;
		}
		else
		{
			L.e(TAG, "缓存不存在");
			return false;
		}
	}

	/**
	 * 删除缓存
	 * */
	public void deleteContact(String emailAddress)
	{
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "DELETE FROM " + DBHelper.TABLE_NAME_CONTACT + " WHERE " + DBHelper.FIELD_CONTACT + " = '"+ emailAddress + "'";
		db.execSQL(sql);
		db.close();
		L.e(TAG, "删除缓存");
	}

	/**
	 * 清空缓存
	 * */
	public void clearContact()
	{
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "DELETE FROM "+DBHelper.TABLE_NAME_CONTACT;
		db.execSQL(sql);
		db.close();
		L.e(TAG, "清空所有缓存");
	}
}
