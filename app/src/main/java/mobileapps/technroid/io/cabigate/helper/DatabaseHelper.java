package mobileapps.technroid.io.cabigate.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import mobileapps.technroid.io.cabigate.models.ChatMessage;

public class DatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION =14;

	// Database Name
	private static final String DATABASE_NAME = "cabigate";

	// Table Names
	private static final String TABLE_CHAT = "chat_tb";



	private static final String KEY_CREATED_AT = "created_at";
	private static final String KEY_UPDATETED_AT = "updated_at";

	private static final String KEY_CAT_ID = "cat_id";
	private static final String KEY_NAME = "name";
	private static final String KEY_IMAGEPATH = "imagePath";
	private static final String KEY_ID = "id";

	private static final String KEY_ISMINE="isMine";
	private static final String KEY_SENDER_ID="sender_id";
	private static final String KEY_CONTENT="content";






	private static final String CREATE_TABLE_CHAT = "CREATE TABLE "
			+ TABLE_CHAT + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			 + KEY_SENDER_ID+ " TEXT,"
			+ KEY_IMAGEPATH + " TEXT,"
			+ KEY_ISMINE + " TEXT,"
			+ KEY_CONTENT + " TEXT,"
			+ KEY_NAME
	+ " TEXT," + KEY_CREATED_AT + " DATETIME)";




	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables

		db.execSQL(CREATE_TABLE_CHAT);


	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
		onCreate(db);
	}

	/**/

	public long createCatagory(ChatMessage chatMessage) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_CONTENT, chatMessage.getContent());
		values.put(KEY_NAME, chatMessage.getName());
		values.put(KEY_ISMINE, chatMessage.isMine()+"");
		values.put(KEY_CREATED_AT, getDateTime());

		long todo_id = db.insert(TABLE_CHAT, null, values);

		return todo_id;
	}

























	public ArrayList<ChatMessage> getAllChat()  {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<ChatMessage> productlist = new ArrayList<ChatMessage>();
		String selectQuery = "SELECT * FROM " + TABLE_CHAT +" ORDER BY "
				+ KEY_CREATED_AT + " ASC";
		;
		Gson gson = new Gson();
		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			if (c.moveToFirst()) {
				do {
                 Log.e("chaha",c.getString((c.getColumnIndex(KEY_ISMINE))));

					ChatMessage chatMessage =new ChatMessage(false,
							Boolean.parseBoolean(c.getString((c.getColumnIndex(KEY_ISMINE)))),
							c.getString((c.getColumnIndex(KEY_NAME))),
							c.getString((c.getColumnIndex(KEY_CONTENT))),
							c.getString((c.getColumnIndex(KEY_SENDER_ID))),c.getString((c.getColumnIndex(KEY_CREATED_AT))));

					productlist.add(chatMessage);



				} while (c.moveToNext());
			}
		c.close();
		closeDB();
		return productlist;
	}







	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	public void deleteAllHIatorY() {
		SQLiteDatabase db = this.getWritableDatabase();
		// db.delete(TABLE_NAME,null,null);
		db.execSQL("delete from " + TABLE_CHAT);

		db.close();
	}






	public String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

}
