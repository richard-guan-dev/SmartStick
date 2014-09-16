package com.guanshuwei.smartstick.data;

import java.util.Locale;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.guanshuwei.smartstick.instance.UserModule;
import com.guanshuwei.smartstick.util.Utilities;

public class UserInfoDatabase {
	private static class Data {
		private static class Tables {
			public static class UserInfoTable implements BaseColumns {
				public static final String TABLE_NAME = "UserInfoDatabase";
				public static final String NAME = "_NAME";
				public static final String PHONEMUNBER = "_PHONENUMBER";
				public static final String UNIQUE = "_UNIQUE";
				public static final String CREATE_QUERY = "CREATE TABLE "
						+ TABLE_NAME + " (" + _ID
						+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME
						+ " TEXT NOT NULL, " + PHONEMUNBER + " TEXT NOT NULL);";
				public static final String CREATE_UNIQUE = "CREATE UNIQUE INDEX "
						+ UNIQUE + " ON " + TABLE_NAME + " (" + NAME + ");";
				public static final String DROP_QUERY = "DROP TABLE IF EXISTS "
						+ TABLE_NAME + ";";
			}
		}

		private static boolean AnyMatch = false;

		private final Context applicationContext;

		private static class DataHelper extends SQLiteOpenHelper {
			public static final int USERINFO_DATABASE_VERSION = 1;
			public static final String USERINFO_DATABASE_NAME = "UserInfoDatabase.db";

			public DataHelper(Context context) {
				super(context, USERINFO_DATABASE_NAME, null,
						USERINFO_DATABASE_VERSION);
			}

			@Override
			public void onCreate(SQLiteDatabase db) {
				db.beginTransaction();
				try {
					db.execSQL(Tables.UserInfoTable.CREATE_QUERY);
					db.execSQL(Tables.UserInfoTable.CREATE_UNIQUE);
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion,
					int newVersion) {
				if (newVersion <= oldVersion) {
					return;
				}

				db.beginTransaction();
				try {
					db.execSQL(Tables.UserInfoTable.DROP_QUERY);
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
				this.onCreate(db);
			}
		}

		private DataHelper dataHelper;
		private boolean adapterInitialized;

		public Data(Context context) {
			this.applicationContext = context;
		}

		public synchronized void initialize() {
			if (this.adapterInitialized) {
				return;
			}
			this.dataHelper = new DataHelper(this.applicationContext);
			this.adapterInitialized = true;
		}

		public synchronized void release() {
			if (this.dataHelper != null) {
				this.dataHelper.close();
				this.dataHelper = null;
			}
			this.adapterInitialized = false;
		}

		public synchronized void insert(UserModule item) throws Exception {
			if (!this.adapterInitialized) {
				this.initialize();
			}

			SQLiteDatabase database = this.dataHelper.getWritableDatabase();
			database.beginTransaction();

			try {
				ContentValues values = new ContentValues();
				values.put(Tables.UserInfoTable.NAME, item.getUserName());
				values.put(Tables.UserInfoTable.PHONEMUNBER,
						item.getUserPhoneNumber());
				long id = database.replace(Tables.UserInfoTable.TABLE_NAME,
						null, values);

				// Validate root id.
				if (id <= 0) {
					throw new Exception("Failed to save History");
				}
				database.setTransactionSuccessful();
			} finally {
				database.endTransaction();
			}
		}

		// private synchronized String getDateTime() {
		// SimpleDateFormat dateFormat = new SimpleDateFormat(
		// "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		// Date date = new Date();
		// return dateFormat.format(date);
		// }

		public synchronized long getCount() {
			if (!this.adapterInitialized) {
				this.initialize();
			}

			// Obtain total number of records in main table.
			SQLiteDatabase database = this.dataHelper.getReadableDatabase();
			long result = DatabaseUtils.queryNumEntries(database,
					Tables.UserInfoTable.TABLE_NAME);

			return result;
		}

		public synchronized Vector<UserModule> getAll() {
			if (!this.adapterInitialized) {
				this.initialize();
			}

			Vector<UserModule> items = new Vector<UserModule>();

			// Obtain database.
			SQLiteDatabase database = this.dataHelper.getReadableDatabase();

			// Select all data, order depending or specified sort order.
			String where = Tables.UserInfoTable._ID + " DESC";
			Cursor reader = database.query(Tables.UserInfoTable.TABLE_NAME,
					null, null, null, null, null, where);
			if (reader == null || reader.getCount() == 0) {
				if (reader != null) {
					reader.close();
				}
				return items;
			}

			int columnIdIndex = reader.getColumnIndex(Tables.UserInfoTable._ID);
			int columnNameIndex = reader
					.getColumnIndex(Tables.UserInfoTable.NAME);
			int columnPhoneIndex = reader
					.getColumnIndex(Tables.UserInfoTable.PHONEMUNBER);

			reader.moveToFirst();
			while (!reader.isAfterLast()) {
				UserModule item = new UserModule();
				item.setID(reader.getLong(columnIdIndex));
				item.setUserName(reader.getString(columnNameIndex));
				item.setUserPhoneNumber(reader.getString(columnPhoneIndex));
				items.add(item);
				reader.moveToNext();
			}

			reader.close();
			return items;
		}

		public synchronized void removeAll() {
			if (!this.adapterInitialized) {
				this.initialize();
			}

			SQLiteDatabase database = this.dataHelper.getWritableDatabase();

			database.beginTransaction();
			try {
				database.delete(Tables.UserInfoTable.TABLE_NAME, null, null);
				database.setTransactionSuccessful();
			} finally {
				database.endTransaction();
			}
		}

		public synchronized boolean exists(UserModule item) throws Exception {
			if (!this.adapterInitialized) {
				this.initialize();
			}

			// Get original item Id first.
			long recordId = this.getRecordId(item);
			return recordId > 0;
		}

		public synchronized void remove(UserModule item) throws Exception {
			if (!this.adapterInitialized) {
				this.initialize();
			}

			long recordId = this.getRecordId(item);

			SQLiteDatabase database = this.dataHelper.getReadableDatabase();
			database.beginTransaction();

			try {
				String deleteWhere = Tables.UserInfoTable._ID + " = ?";
				String[] deleteArgs = new String[] { String.valueOf(recordId) };
				database.delete(Tables.UserInfoTable.TABLE_NAME, deleteWhere,
						deleteArgs);
				database.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				database.endTransaction();
			}
		}

		public synchronized Vector<UserModule> getLatestRecords(int limit) {
			if (!this.adapterInitialized) {
				this.initialize();
			}

			Vector<UserModule> items = new Vector<UserModule>();

			try {
				// Obtain database.
				SQLiteDatabase database = this.dataHelper.getReadableDatabase();

				// Select recently data, order depending or specified sort
				// order.
				String where = Tables.UserInfoTable.PHONEMUNBER + " DESC";
				Cursor reader = database.query(Tables.UserInfoTable.TABLE_NAME,
						null, null, null, null, null, where,
						"0, " + String.valueOf(limit));
				if (reader == null || reader.getCount() == 0) {
					if (reader != null) {
						reader.close();
					}

					return items;
				}

				int columnIdIndex = reader
						.getColumnIndex(Tables.UserInfoTable._ID);
				int columnNameIndex = reader
						.getColumnIndex(Tables.UserInfoTable.NAME);
				int columnPhoneIndex = reader
						.getColumnIndex(Tables.UserInfoTable.PHONEMUNBER);

				reader.moveToFirst();
				while (!reader.isAfterLast()) {
					UserModule item = new UserModule();
					item.setID(reader.getLong(columnIdIndex));
					item.setUserName(reader.getString(columnNameIndex));
					item.setUserPhoneNumber(reader.getString(columnPhoneIndex));
					items.add(item);
					reader.moveToNext();
				}

				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return items;
		}

		public synchronized Vector<UserModule> findMatches(String substring) {
			Vector<UserModule> items = new Vector<UserModule>();

			if (Utilities.isStringNullOrEmpty(substring)) {
				return items;
			}

			if (!this.adapterInitialized) {
				this.initialize();
			}

			SQLiteDatabase database = this.dataHelper.getReadableDatabase();

			String where = "LOWER(" + Tables.UserInfoTable.NAME + ") LIKE ?";
			String[] args;
			if (Data.AnyMatch) {
				args = new String[] { "%" + substring.toLowerCase(Locale.US)
						+ "%" };
			} else {
				args = new String[] { substring.toLowerCase(Locale.US) + "%" };
			}

			Cursor reader = database.query(Tables.UserInfoTable.TABLE_NAME,
					null, where, args, null, null,
					Tables.UserInfoTable.PHONEMUNBER + " DESC");

			if (reader == null || reader.getCount() == 0) {
				if (reader != null) {
					reader.close();
				}
				return items;
			}

			int columnIdIndex = reader.getColumnIndex(Tables.UserInfoTable._ID);
			int columnNameIndex = reader
					.getColumnIndex(Tables.UserInfoTable.NAME);
			int columnPhoneIndex = reader
					.getColumnIndex(Tables.UserInfoTable.PHONEMUNBER);

			reader.moveToFirst();
			while (!reader.isAfterLast()) {
				UserModule item = new UserModule();

				item.setID(reader.getLong(columnIdIndex));
				item.setUserName(reader.getString(columnNameIndex));
				item.setUserPhoneNumber(reader.getString(columnPhoneIndex));
				items.add(item);
				reader.moveToNext();
			}

			reader.close();
			return items;
		}

		private long getRecordId(UserModule item) throws Exception {
			// Obtain item id first.
			SQLiteDatabase database = this.dataHelper.getReadableDatabase();
			String[] columns = new String[] { Tables.UserInfoTable._ID };

			// unique name match
			final String where = Tables.UserInfoTable.NAME + " = ?";
			final String[] args = new String[] { String.valueOf(item.getUserName()) };

			// Select records.
			Cursor reader = database.query(Tables.UserInfoTable.TABLE_NAME,
					columns, where, args, null, null, null);

			// Skip it, nothing to remove.
			if (reader == null || reader.getCount() == 0) {
				if (reader != null) {
					reader.close();
				}

				return 0;
			}

			// Check total number of records.
			if (reader.getCount() > 1) {
				reader.close();
				throw new Exception(
						"More than one record was found. Can't proceed.");
			}

			reader.moveToFirst();
			int columnIndex = reader.getColumnIndex(Tables.UserInfoTable._ID);
			long recordId = reader.getLong(columnIndex);
			reader.close();

			if (recordId <= 0) {
				throw new Exception("Record id is: " + recordId
						+ ". Can't proceed.");
			}

			return recordId;
		}
	}

	public static final String LOG_TAG = "AutoSuggestionHostory";
	private static final long HISTORY_LIMIT = 1000;
	private final Context applicationContext;

	public UserInfoDatabase(Context context) {
		this.applicationContext = context;
	}

	public synchronized boolean addItem(UserModule item) {
		Data adapter = new Data(this.applicationContext);
		try {
			adapter.initialize();
			if (!Utilities.isStringNullOrEmpty(item.getUserPhoneNumber())
					&& !Utilities
							.isStringNullOrEmpty(item.getUserPhoneNumber())) {
				adapter.insert(item);
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.release();
		}
	}

	public synchronized long getCount() {
		Data adapter = new Data(this.applicationContext);

		long result = 0;
		try {
			adapter.initialize();
			result = adapter.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			adapter.release();
		}

		return result;
	}

	public synchronized Vector<UserModule> getAll() {
		Data adapter = new Data(this.applicationContext);

		try {
			adapter.initialize();
			return adapter.getAll();
		} catch (SQLiteException sqlEx) {
			// TODO: Find another way to deal with database lock, maybe
			// semaphore with acquire/release.
			sqlEx.printStackTrace();
			return null;
		} finally {
			adapter.release();
		}
	}

	public synchronized Vector<UserModule> getLatestRecords(int limit) {
		Data adapter = new Data(this.applicationContext);

		try {
			adapter.initialize();
			return adapter.getLatestRecords(limit);
		} catch (SQLiteException sqlEx) {
			// TODO: Find another way to deal with database lock, maybe
			// semaphore with acquire/release.
			sqlEx.printStackTrace();
			return null;
		} finally {
			adapter.release();
		}
	}

	public synchronized void removeAll() {
		Data adapter = new Data(this.applicationContext);
		try {
			adapter.initialize();
			adapter.removeAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			adapter.release();
		}
	}

	public synchronized boolean exists(UserModule item) {
		Data adapter = new Data(this.applicationContext);
		try {
			adapter.initialize();
			return adapter.exists(item);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.release();
		}
	}

	public synchronized void remove(UserModule item) {
		Data adapter = new Data(this.applicationContext);
		try {
			adapter.initialize();
			adapter.remove(item);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			adapter.release();
		}
	}

	public synchronized Vector<UserModule> findMatches(String substring) {
		Data adapter = new Data(this.applicationContext);
		try {
			adapter.initialize();
			return adapter.findMatches(substring);
		} catch (Exception e) {
			e.printStackTrace();
			return new Vector<UserModule>();
		} finally {
			adapter.release();
		}
	}

	public synchronized boolean canSave() {
		Data adapter = new Data(this.applicationContext);
		try {
			adapter.initialize();
			return adapter.getCount() < HISTORY_LIMIT;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.release();
		}
	}
}