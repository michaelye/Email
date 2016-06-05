package com.michael.email.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库生成类
 * 
 * @author Michael
 * */
public class DBHelper extends SQLiteOpenHelper
{

	private static final String DATABASE_NAME = "email.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME_EMAIL = "email";
//    public static final String FIELD_EMAIL_ID = "id";
    public static final String FIELD_EMAIL_RECEIVER = "receiver";
    public static final String FIELD_EMAIL_SENDER = "sender";
    public static final String FIELD_EMAIL_SUBJECT = "subject";
    public static final String FIELD_EMAIL_CONTENT = "content";
    public static final String FIELD_EMAIL_ATTACH_PATHS = "attachPaths";
    public static final String FIELD_EMAIL_IS_STAR = "isStar";
    public static final String FIELD_EMAIL_STATE = "state";
    public static final String FIELD_EMAIL_SEND_TIME = "send_time";

	public static final String TABLE_NAME_CONTACT = "contact";
//	public static final String FIELD_CONTACT_ID = "id";
	public static final String FIELD_CONTACT = "contactName";

	public DBHelper(Context context)
	{
		// CursorFactory设置为null,使用默认值
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// 数据库第一次被创建时onCreate会被调用
	@Override
	public void onCreate(SQLiteDatabase db)
	{
        StringBuilder emailSql = new StringBuilder();
		emailSql.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME_EMAIL)
                .append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT, ")
//                .append(FIELD_EMAIL_ID).append(" INTEGER, ")
                .append(FIELD_EMAIL_RECEIVER).append(" TEXT, ")
                .append(FIELD_EMAIL_SENDER).append(" TEXT, ")
                .append(FIELD_EMAIL_SUBJECT).append(" TEXT, ")
                .append(FIELD_EMAIL_CONTENT).append(" TEXT, ")
                .append(FIELD_EMAIL_ATTACH_PATHS).append(" TEXT, ")
                .append(FIELD_EMAIL_IS_STAR).append(" INTEGER, ")
                .append(FIELD_EMAIL_STATE).append(" INTEGER, ")
                .append(FIELD_EMAIL_SEND_TIME).append(" LONG, ")
				.append(" TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
		db.execSQL(emailSql.toString());

		StringBuilder contactSql = new StringBuilder();
		contactSql.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME_CONTACT)
				.append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT, ")
//				.append(FIELD_CONTACT_ID).append(" INTEGER, ")
				.append(FIELD_CONTACT).append(" TEXT, ")
				.append(" TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
		db.execSQL(contactSql.toString());
	}

	// 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}
}
