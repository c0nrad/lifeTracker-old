/** DatabaseManager:
 *  Author: Stuart Larsen
 * 
 * 		DatabaseManager encapsulates all of the database IO. To use, create an instance of this class, then use the open() function, to open the connection.
 * 	from there you can create new tables. All tables have the same following columns:
 * 
 * 	ID - Primary key for the database
 *  DATE - The time date stamp of the entry
 *  TYPEID - The type of the input type, such as EditText, followed by it's unique number. Ex: Spinner0
 *  DATA - The data being stored for the input type. For example if the input type was storing "weight", data would be "180".
 *  
 *  Adding and removing elements is pretty simple, just use addEntry(tableName, date, typeId, data), and the entry is added. To get all the entries from 
 *  the database use the getAllEntries(tableName).
 */

package my.sandbox;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager {
	
	public static final String COLUMN_DATE = "_date";
	public static final String COLUMN_DATA = "_data";
	public static final String COLUMN_TYPEID = "_typeid";
	public static final String COLUMN_ID = "_id";

	public static final String DATABASE_NAME = "entries.db";
	private static final int DATABASE_VERSION = 1;

	private SQLiteDatabase database;
	private MySQLiteHelper mHelper;
	private String[] allColumns = { COLUMN_ID, COLUMN_DATE, COLUMN_TYPEID, COLUMN_DATA };

	public DatabaseManager(Context context) {
		mHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = mHelper.getWritableDatabase();
	}

	public void close() {
		mHelper.close();
	}
	
	public void createTable(String tableName) {
		String DATABASE_CREATE = "create table "
				+ tableName + "(" 
				+ COLUMN_ID + " integer primary key autoincrement, " 
				+ COLUMN_DATE + " text not null, "
				+ COLUMN_TYPEID + " text not null, "
				+ COLUMN_DATA + " text not null);";
		database.execSQL(DATABASE_CREATE);
	}

	public DatabaseEntry addEntry(String tableName, String date, String id, String data) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_DATE, date);
		values.put(COLUMN_TYPEID, id);
		values.put(COLUMN_DATA, data);
		
		long insertId = database.insert(tableName, null,
				values);
		Cursor cursor = database.query(tableName,
				allColumns, COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		DatabaseEntry newEntry = cursorToEntry(cursor);
		cursor.close();
		return newEntry;
	}

	public void deleteEntry(String tableName, DatabaseEntry entry) {
		long id = entry.mId;
		System.out.println("Entry deleted with id: " + id);
		database.delete(tableName, COLUMN_ID
				+ " = " + id, null);
	}

	public List<DatabaseEntry> getAllEntries(String tableName) {
		List<DatabaseEntry> entries = new ArrayList<DatabaseEntry>();

		Cursor cursor = database.query(tableName,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DatabaseEntry entry = cursorToEntry(cursor);
			entries.add(entry);
			cursor.moveToNext();
		}

		cursor.close();
		return entries;
	}

	private DatabaseEntry cursorToEntry(Cursor cursor) {
		DatabaseEntry entry = new DatabaseEntry(cursor.getString(1), cursor.getString(2), cursor.getString(3));
		entry.mId = (int) cursor.getLong(0);
		return entry;
	}
	
	/* MySQLiteHelper
	 * To be honest, I don't really understand how the whole database thing works. For some reason android requires this, so I used it. Wah-lah.
	 */
	public class MySQLiteHelper extends SQLiteOpenHelper {

		private static final String DATABASE_CREATE = "create table "
				+ "emptyTable" + "(" + "_id"
				+ " integer primary key autoincrement)";

		public MySQLiteHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(MySQLiteHelper.class.getName(),
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + "emptyTable");
			onCreate(db);
		}
	}
}
