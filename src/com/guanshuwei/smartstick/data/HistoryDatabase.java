package com.guanshuwei.smartstick.data;

import java.util.Calendar;
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

import com.guanshuwei.smartstick.instance.HistoryModule;
import com.guanshuwei.smartstick.instance.HistoryModule.LogType;
import com.guanshuwei.smartstick.util.Utilities;

public class HistoryDatabase {
	private static class Data {
		private static class Tables {
			public static class HistoryTable implements BaseColumns {
				public static final String TABLE_NAME = "HistoryDatabase";
				public static final String NAME = "_NAME";
				public static final String PHONEMUNBER = "_PHONENUMBER";
				public static final String LOGTYPE = "_LOGTYPE";
				public static final String LANGTITUDE = "_LANGTITUDE";
				public static final String LONGITUDE = "_LONGITUDE";
				public static final String DATE = "_DATE";
				public static final String UNIQUE = "_UNIQUE";
				public static final String CREATE_QUERY = "CREATE TABLE "
						+ TABLE_NAME + " (" + _ID
						+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME
						+ " TEXT NOT NULL, " + PHONEMUNBER + " TEXT NOT NULL,"
						+ LOGTYPE + " TEXT NOT NULL," + DATE
						+ " TEXT NOT NULL," + LANGTITUDE + " TEXT NOT NULL,"
						+ LONGITUDE + " TEXT NOT NULL);";
				public static final String CREATE_UNIQUE = "CREATE UNIQUE INDEX "
						+ UNIQUE + " ON " + TABLE_NAME + " (" + DATE + ");";
				public static final String DROP_QUERY = "DROP TABLE IF EXISTS "
						+ TABLE_NAME + ";";
			}
		}

		private static boolean AnyMatch = false;

		private final Context applicationContext;

		private static class DataHelper extends SQLiteOpenHelper {
			public static final int USERINFO_DATABASE_VERSION = 2;
			public static final String USERINFO_DATABASE_NAME = "HistoryDatabase.db";

			public DataHelper(Context context) {
				super(context, USERINFO_DATABASE_NAME, null,
						USERINFO_DATABASE_VERSION);
			}

			@Override
			public void onCreate(SQLiteDatabase db) {
				db.beginTransaction();
				try {
					db.execSQL(Tables.HistoryTable.CREATE_QUERY);
					db.execSQL(Tables.HistoryTable.CREATE_UNIQUE);
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
					db.execSQL(Tables.HistoryTable.DROP_QUERY);
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

		public synchronized void insert(HistoryModule item) throws Exception {
			if (!this.adapterInitialized) {
				this.initialize();
			}

			SQLiteDatabase database = this.dataHelper.getWritableDatabase();
			database.beginTransaction();

			try {
				int y, m, d, h, mi, s;
				Calendar cal = Calendar.getInstance();
				y = cal.get(Calendar.YEAR);
				m = cal.get(Calendar.MONTH);
				d = cal.get(Calendar.DATE);
				h = cal.get(Calendar.HOUR_OF_DAY);
				mi = cal.get(Calendar.MINUTE);
				s = cal.get(Calendar.SECOND);

				ContentValues values = new ContentValues();
				values.put(Tables.HistoryTable.NAME, item.getUserName());
				values.put(Tables.HistoryTable.PHONEMUNBER,
						item.getUserPhoneNumber());
				values.put(Tables.HistoryTable.LOGTYPE, item.getLogType()
						.toString());
				values.put(Tables.HistoryTable.LONGITUDE,
						String.valueOf(item.getLongitude()));
				values.put(Tables.HistoryTable.LANGTITUDE,
						String.valueOf(item.getLatitude()));
				values.put(Tables.HistoryTable.DATE, y + "年" + m + "月" + d
						+ "日" + h + "时" + mi + "分" + s + "秒");
				long id = database.replace(Tables.HistoryTable.TABLE_NAME,
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
					Tables.HistoryTable.TABLE_NAME);

			return result;
		}

		public synchronized Vector<HistoryModule> getAll() {
			if (!this.adapterInitialized) {
				this.initialize();
			}

			Vector<HistoryModule> items = new Vector<HistoryModule>();

			// Obtain database.
			SQLiteDatabase database = this.dataHelper.getReadableDatabase();

			// Select all data, order depending or specified sort order.
			String where = Tables.HistoryTable._ID + " DESC";
			Cursor reader = database.query(Tables.HistoryTable.TABLE_NAME,
					null, null, null, null, null, where);
			if (reader == null || reader.getCount() == 0) {
				if (reader != null) {
					reader.close();
				}
				return items;
			}

			int columnIdIndex = reader.getColumnIndex(Tables.HistoryTable._ID);
			int columnNameIndex = reader
					.getColumnIndex(Tables.HistoryTable.NAME);
			int columnPhoneIndex = reader
					.getColumnIndex(Tables.HistoryTable.PHONEMUNBER);
			int columnTypeIndex = reader
					.getColumnIndex(Tables.HistoryTable.LOGTYPE);
			int columnLatIndex = reader
					.getColumnIndex(Tables.HistoryTable.LANGTITUDE);
			int columnLngIndex = reader
					.getColumnIndex(Tables.HistoryTable.LONGITUDE);
			int columnDateIndex = reader
					.getColumnIndex(Tables.HistoryTable.DATE);

			reader.moveToFirst();
			while (!reader.isAfterLast()) {
				HistoryModule item = new HistoryModule();
				item.setID(reader.getLong(columnIdIndex));
				item.setUserName(reader.getString(columnNameIndex));
				item.setUserPhoneNumber(reader.getString(columnPhoneIndex));
				item.setLongitude(Double.parseDouble(reader
						.getString(columnLngIndex)));
				item.setLatitude(Double.parseDouble(reader
						.getString(columnLatIndex)));
				LogType type;
				if (reader.getString(columnTypeIndex).equals(
						LogType.ALERT.toString())) {
					type = LogType.ALERT;
				} else {
					type = LogType.LOCATE;
				}
				item.setLogType(type);
				item.setmDate(reader.getString(columnDateIndex));
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
				database.delete(Tables.HistoryTable.TABLE_NAME, null, null);
				database.setTransactionSuccessful();
			} finally {
				database.endTransaction();
			}
		}

		public synchronized boolean exists(HistoryModule item) throws Exception {
			if (!this.adapterInitialized) {
				this.initialize();
			}

			// Get original item Id first.
			long recordId = this.getRecordId(item);
			return recordId > 0;
		}

		public synchronized void remove(HistoryModule item) throws Exception {
			if (!this.adapterInitialized) {
				this.initialize();
			}

			long recordId = this.getRecordId(item);

			SQLiteDatabase database = this.dataHelper.getReadableDatabase();
			database.beginTransaction();

			try {
				String deleteWhere = Tables.HistoryTable._ID + " = ?";
				String[] deleteArgs = new String[] { String.valueOf(recordId) };
				database.delete(Tables.HistoryTable.TABLE_NAME, deleteWhere,
						deleteArgs);
				database.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				database.endTransaction();
			}
		}

		public synchronized Vector<HistoryModule> getLatestRecords(int limit) {
			if (!this.adapterInitialized) {
				this.initialize();
			}

			Vector<HistoryModule> items = new Vector<HistoryModule>();

			try {
				// Obtain database.
				SQLiteDatabase database = this.dataHelper.getReadableDatabase();

				// Select recently data, order depending or specified sort
				// order.
				String where = Tables.HistoryTable.PHONEMUNBER + " DESC";
				Cursor reader = database.query(Tables.HistoryTable.TABLE_NAME,
						null, null, null, null, null, where,
						"0, " + String.valueOf(limit));
				if (reader == null || reader.getCount() == 0) {
					if (reader != null) {
						reader.close();
					}

					return items;
				}

				int columnIdIndex = reader
						.getColumnIndex(Tables.HistoryTable._ID);
				int columnNameIndex = reader
						.getColumnIndex(Tables.HistoryTable.NAME);
				int columnPhoneIndex = reader
						.getColumnIndex(Tables.HistoryTable.PHONEMUNBER);
				int columnTypeIndex = reader
						.getColumnIndex(Tables.HistoryTable.LOGTYPE);
				int columnLatIndex = reader
						.getColumnIndex(Tables.HistoryTable.LANGTITUDE);
				int columnLngIndex = reader
						.getColumnIndex(Tables.HistoryTable.LONGITUDE);
				int columnDateIndex = reader
						.getColumnIndex(Tables.HistoryTable.DATE);

				reader.moveToFirst();
				while (!reader.isAfterLast()) {
					HistoryModule item = new HistoryModule();
					item.setID(reader.getLong(columnIdIndex));
					item.setUserName(reader.getString(columnNameIndex));
					item.setUserPhoneNumber(reader.getString(columnPhoneIndex));
					item.setLongitude(Double.parseDouble(reader
							.getString(columnLngIndex)));
					item.setLatitude(Double.parseDouble(reader
							.getString(columnLatIndex)));
					LogType type;
					if (reader.getString(columnTypeIndex).equals(
							LogType.ALERT.toString())) {
						type = LogType.ALERT;
					} else {
						type = LogType.LOCATE;
					}
					item.setLogType(type);
					item.setmDate(reader.getString(columnDateIndex));
					items.add(item);
					reader.moveToNext();
				}

				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return items;
		}

		public synchronized Vector<HistoryModule> findMatches(String substring) {
			Vector<HistoryModule> items = new Vector<HistoryModule>();

			if (Utilities.isStringNullOrEmpty(substring)) {
				return items;
			}

			if (!this.adapterInitialized) {
				this.initialize();
			}

			SQLiteDatabase database = this.dataHelper.getReadableDatabase();

			String where = "LOWER(" + Tables.HistoryTable.NAME + ") LIKE ?";
			String[] args;
			if (Data.AnyMatch) {
				args = new String[] { "%" + substring.toLowerCase(Locale.US)
						+ "%" };
			} else {
				args = new String[] { substring.toLowerCase(Locale.US) + "%" };
			}

			Cursor reader = database.query(Tables.HistoryTable.TABLE_NAME,
					null, where, args, null, null,
					Tables.HistoryTable.PHONEMUNBER + " DESC");

			if (reader == null || reader.getCount() == 0) {
				if (reader != null) {
					reader.close();
				}
				return items;
			}

			int columnIdIndex = reader.getColumnIndex(Tables.HistoryTable._ID);
			int columnNameIndex = reader
					.getColumnIndex(Tables.HistoryTable.NAME);
			int columnPhoneIndex = reader
					.getColumnIndex(Tables.HistoryTable.PHONEMUNBER);
			int columnTypeIndex = reader
					.getColumnIndex(Tables.HistoryTable.LOGTYPE);
			int columnLatIndex = reader
					.getColumnIndex(Tables.HistoryTable.LANGTITUDE);
			int columnLngIndex = reader
					.getColumnIndex(Tables.HistoryTable.LONGITUDE);
			int columnDateIndex = reader
					.getColumnIndex(Tables.HistoryTable.DATE);

			reader.moveToFirst();
			while (!reader.isAfterLast()) {
				HistoryModule item = new HistoryModule();
				item.setID(reader.getLong(columnIdIndex));
				item.setUserName(reader.getString(columnNameIndex));
				item.setUserPhoneNumber(reader.getString(columnPhoneIndex));
				item.setLongitude(Double.parseDouble(reader
						.getString(columnLngIndex)));
				item.setLatitude(Double.parseDouble(reader
						.getString(columnLatIndex)));
				LogType type;
				if (reader.getString(columnTypeIndex).equals(
						LogType.ALERT.toString())) {
					type = LogType.ALERT;
				} else {
					type = LogType.LOCATE;
				}
				item.setLogType(type);
				item.setmDate(reader.getString(columnDateIndex));
				items.add(item);
				reader.moveToNext();
			}

			reader.close();
			return items;
		}

		private long getRecordId(HistoryModule item) throws Exception {
			// Obtain item id first.
			SQLiteDatabase database = this.dataHelper.getReadableDatabase();
			String[] columns = new String[] { Tables.HistoryTable._ID };

			// unique name match
			final String where = Tables.HistoryTable.NAME + " = ?";
			final String[] args = new String[] { String.valueOf(item
					.getUserName()) };

			// Select records.
			Cursor reader = database.query(Tables.HistoryTable.TABLE_NAME,
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
			int columnIndex = reader.getColumnIndex(Tables.HistoryTable._ID);
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

	public HistoryDatabase(Context context) {
		this.applicationContext = context;
	}

	public synchronized boolean addItem(HistoryModule item) {
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

	public synchronized Vector<HistoryModule> getAll() {
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

	public synchronized Vector<HistoryModule> getLatestRecords(int limit) {
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

	public synchronized boolean exists(HistoryModule item) {
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

	public synchronized void remove(HistoryModule item) {
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

	public synchronized Vector<HistoryModule> findMatches(String substring) {
		Data adapter = new Data(this.applicationContext);
		try {
			adapter.initialize();
			return adapter.findMatches(substring);
		} catch (Exception e) {
			e.printStackTrace();
			return new Vector<HistoryModule>();
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