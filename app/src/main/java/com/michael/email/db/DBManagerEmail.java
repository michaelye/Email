package com.michael.email.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.michael.email.EmailApp;
import com.michael.email.model.Email;
import com.michael.email.util.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 数据库管理类，将对数据库的各种操作封装为方法
 * 
 * @author Michael
 * 
 * */
public class DBManagerEmail
{
	private static final String TAG = "DBManagerEmail";

	private static DBHelper helper;

	private static DBManagerEmail uniqueInstance;

	/**
	 * 单例
	 * */
	public static DBManagerEmail getInstance()
	{
		if (uniqueInstance == null)
		{
			uniqueInstance = new DBManagerEmail();
		}
		return uniqueInstance;
	}

	private DBManagerEmail()
	{
		helper = new DBHelper(EmailApp.applicationContext);
	}

	/**
	 * 插入缓存,如果存在就删除
	 * */
	public void insertEmail(Email email)
	{
		if (email != null)
		{
			String sql = new StringBuilder("INSERT INTO ")
					.append(DBHelper.TABLE_NAME_EMAIL)
					.append(" (")
					.append(DBHelper.FIELD_EMAIL_RECEIVER).append(",")
					.append(DBHelper.FIELD_EMAIL_SENDER).append(",")
					.append(DBHelper.FIELD_EMAIL_SUBJECT).append(",")
					.append(DBHelper.FIELD_EMAIL_CONTENT).append(",")
					.append(DBHelper.FIELD_EMAIL_ATTACH_PATHS).append(",")
					.append(DBHelper.FIELD_EMAIL_IS_STAR).append(",")
					.append(DBHelper.FIELD_EMAIL_STATE).append(",")
					.append(DBHelper.FIELD_EMAIL_SEND_TIME).append(")")
					.append("VALUES(?,?,?,?,?,?,?,?)").toString();
			SQLiteDatabase db = helper.getWritableDatabase();
			SQLiteStatement insertStmt = db.compileStatement(sql);
			insertStmt.clearBindings();
			insertStmt.bindString(1, email.receiver);
			insertStmt.bindString(2, email.sender);
			insertStmt.bindString(3, email.subject);
			insertStmt.bindString(4, email.content);
			insertStmt.bindString(5, convertAttachPathToString(email.attachPaths));
			insertStmt.bindLong(6, email.isStar == true ? 1 : 0);
			insertStmt.bindLong(7, email.state);
			insertStmt.bindLong(8, email.sendTime);
			insertStmt.executeInsert();
			db.close();
			L.e(TAG, "插入缓存");
		}
	}

	/**
	 * http://stackoverflow.com/a/7779427/1727934
	 * */
	private String convertAttachPathToString(List<String> paths)
	{
		String pathString = "";
		if(paths != null && !paths.isEmpty())
		{
			pathString = TextUtils.join(",", paths);
		}
		return pathString.toString();
	}

	private List<String> convertStringToList(String attachs)
	{
		if(attachs == null || attachs.isEmpty())
		{
			return new ArrayList<String>();
		}
		return Arrays.asList(attachs.split(","));
	}

	/**
	 * 更新缓存
	 * */
	public void updateEmail(String _id, Email email)
	{
		if (email != null)
		{
			SQLiteDatabase db = helper.getWritableDatabase();
			ContentValues dataToInsert = new ContentValues();
			dataToInsert.put(DBHelper.FIELD_EMAIL_RECEIVER, email.receiver);
			dataToInsert.put(DBHelper.FIELD_EMAIL_SENDER, email.sender);
			dataToInsert.put(DBHelper.FIELD_EMAIL_SUBJECT, email.subject);
			dataToInsert.put(DBHelper.FIELD_EMAIL_CONTENT, email.content);
			dataToInsert.put(DBHelper.FIELD_EMAIL_ATTACH_PATHS, convertAttachPathToString(email.attachPaths));
			dataToInsert.put(DBHelper.FIELD_EMAIL_IS_STAR, email.isStar == true ? 1 : 0);
			dataToInsert.put(DBHelper.FIELD_EMAIL_STATE, email.state);
			dataToInsert.put(DBHelper.FIELD_EMAIL_SEND_TIME, email.sendTime);
			String where = "_id" + " = " + "\"" + _id + "\"";
			db.update(DBHelper.TABLE_NAME_EMAIL, dataToInsert, where, null);
			db.close();
			L.e(TAG, "更新缓存");
		}
	}

	/**
	 * 获取已发送
	 * */
	public List<Email> getEmailSend()
	{
		return getCacheByType(TYPE_SEND);
	}

	/**
	 * 获取已加星
	 * */
    public List<Email> getEmailStar()
    {
        return getCacheByType(TYPE_STAR);
    }

	/**
	 * 获取定时发送
	 * */
	public List<Email> getEmailPending()
	{
		return getCacheByType(TYPE_PENDING);
	}

    private static final int TYPE_SEND = 0;
    private static final int TYPE_STAR = 1;
    private static final int TYPE_PENDING = 2;


    /**
     * 获取缓存
     * */
    private List<Email> getCacheByType(int type)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "";
        switch (type)
        {
            case TYPE_SEND:
                sql = new StringBuilder("SELECT * FROM ").append(DBHelper.TABLE_NAME_EMAIL).append(" WHERE ").append(DBHelper.FIELD_EMAIL_STATE).append(" = 1").append(" order by ").append("TIMESTAMP DESC").toString();
                break;
            case TYPE_STAR:
				sql = new StringBuilder("SELECT * FROM ").append(DBHelper.TABLE_NAME_EMAIL).append(" WHERE ").append(DBHelper.FIELD_EMAIL_IS_STAR).append(" = 1").append(" order by ").append("TIMESTAMP DESC").toString();
                break;
            case TYPE_PENDING:
				sql = new StringBuilder("SELECT * FROM ").append(DBHelper.TABLE_NAME_EMAIL).append(" WHERE ").append(DBHelper.FIELD_EMAIL_STATE).append(" = 1").append(" order by ").append("TIMESTAMP DESC").toString();
                break;
        }
        Cursor cursor = db.rawQuery(sql, new String[] {});
        List<Email> list = new ArrayList<Email>();
        if (cursor.getCount() > 0)
        {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                list.add(getEmailByCursor(cursor));
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

	private Email getEmailByCursor(Cursor cursor)
	{
		Email email = new Email();
		email.id = cursor.getString(cursor.getColumnIndex("_id"));
		email.receiver = cursor.getString(cursor.getColumnIndex(DBHelper.FIELD_EMAIL_RECEIVER));
		email.sender = cursor.getString(cursor.getColumnIndex(DBHelper.FIELD_EMAIL_SENDER));
		email.subject = cursor.getString(cursor.getColumnIndex(DBHelper.FIELD_EMAIL_SUBJECT));
		email.content = cursor.getString(cursor.getColumnIndex(DBHelper.FIELD_EMAIL_CONTENT));
		email.attachPaths = convertStringToList(cursor.getString(cursor.getColumnIndex(DBHelper.FIELD_EMAIL_ATTACH_PATHS)));
		email.isStar = cursor.getInt(cursor.getColumnIndex(DBHelper.FIELD_EMAIL_IS_STAR)) == 0 ? false : true;
		email.state = cursor.getInt(cursor.getColumnIndex(DBHelper.FIELD_EMAIL_STATE));
		email.sendTime = cursor.getLong(cursor.getColumnIndex(DBHelper.FIELD_EMAIL_SEND_TIME));
		return email;
	}

	/**
	 * 获取缓存
	 * */
	public Email getEmail(String _id)
	{
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "SELECT * FROM " + DBHelper.TABLE_NAME_EMAIL + " WHERE " + "_id" + " = " + _id;
		Cursor cursor = db.rawQuery(sql, new String[] {});
		Email email = null;
		if (cursor.moveToFirst())
		{
			email = getEmailByCursor(cursor);
		}
		if (cursor != null && !cursor.isClosed())
		{
			cursor.close();
		}
		db.close();
		if (cursor.getCount() == 0)
		{
			L.e(TAG, "当前缓存不存在，返回null");
			return null;
		}
		else
		{
			L.e(TAG, "当前缓存存在, 成功返回");
			return email;
		}
	}

	/**
	 * 缓存是否存在
	 * */
	public boolean isEmailExist(int _id)
	{
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "SELECT * FROM " + DBHelper.TABLE_NAME_EMAIL + " WHERE " + "_id" + " = "+ _id;
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
	public void deleteEmail(String _id)
	{
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "DELETE FROM " + DBHelper.TABLE_NAME_EMAIL + " WHERE " + "_id" + " = "+ _id;
		db.execSQL(sql);
		db.close();
		L.e(TAG, "删除缓存");
	}

	/**
	 * 清空缓存
	 * */
	public void clearEmail()
	{
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "DELETE FROM "+DBHelper.TABLE_NAME_EMAIL;
		db.execSQL(sql);
		db.close();
		L.e(TAG, "清空所有缓存");
	}
}
