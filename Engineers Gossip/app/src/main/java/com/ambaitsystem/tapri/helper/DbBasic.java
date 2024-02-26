package com.ambaitsystem.tapri.helper;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//class extends SQLiteOpenHelper
public class DbBasic extends SQLiteOpenHelper {
	
	
	private static final String DATABASE_NAME = "db";
	public static final String TITLE = "title";
	public static final String VALUE = "value";

	//constructor called
	public DbBasic(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	//oncreate method
	@Override
	public void onCreate(SQLiteDatabase db) {}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		android.util.Log.w("Constants","Upgrading database, which will destroy all old data");
		
		//execution of data
		db.execSQL("DROP TABLE IF EXISTS contact");
		onCreate(db);
	}
}