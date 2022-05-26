package com.example.wordlepromax;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.TreeSet;

public class DataHandler extends SQLiteOpenHelper {
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	private static final String DATABASE_NAME = "words";
	public final static String DATABASE_PATH = "/data/data/com.example.wordlepromax/databases/";
	public static final int DATABASE_VERSION = 1;
	private static final String DB_NAME = "words.sqlite";
	private static final String WORDS_TABLE = "Words";
	private static final String NAME_COL = "words";
	private static final String ID_COL = "id";
	private static final String USED_WORDS_TABLE = "UsedWords";
	private static final String UW_COL_ID = "id";
	private static final String UW_WORDS_COL = "word";

	public DataHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.myContext = context;
		openDatabase();
	}
	//Create a empty database on the system
	public void createDatabase() throws IOException
	{

		boolean dbExist = checkDataBase();

		if(dbExist)
		{
			Log.v("DB Exists", "db exists");
			// By calling this method here onUpgrade will be called on a
			// writeable database, but only if the version number has been
			// bumped
			//onUpgrade(myDataBase, DATABASE_VERSION_old, DATABASE_VERSION);
		}

		boolean dbExist1 = checkDataBase();
		if(!dbExist1)
		{
			this.getReadableDatabase();
			try
			{
				this.close();
				copyDataBase();
			}
			catch (IOException e)
			{
				throw new Error("Error copying database");
			}
		}

	}
	//Check database already exist or not
	private boolean checkDataBase()
	{
		boolean checkDB = false;
		try
		{
			String myPath = DATABASE_PATH + DATABASE_NAME;
			File dbfile = new File(myPath);
			checkDB = dbfile.exists();
		}
		catch(SQLiteException ignored)
		{
		}
		return checkDB;
	}
	//Copies your database from your local assets-folder to the just created empty database in the system folder
	private void copyDataBase() throws IOException
	{

		InputStream mInput = myContext.getAssets().open(DATABASE_NAME);
		String outFileName = DATABASE_PATH + DATABASE_NAME;
		OutputStream mOutput = new FileOutputStream(outFileName);
		byte[] mBuffer = new byte[2024];
		int mLength;
		while ((mLength = mInput.read(mBuffer)) > 0) {
			mOutput.write(mBuffer, 0, mLength);
		}
		mOutput.flush();
		mOutput.close();
		mInput.close();
	}
	//delete database
	public void db_delete() {
		File file = new File(DATABASE_PATH + DATABASE_NAME);
		if(file.exists())
		{
			file.delete();
			System.out.println("delete database file.");
		}
	}
	//Open database
	public void openDatabase() throws SQLException {
		String myPath = DATABASE_PATH + DATABASE_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
	}

	public synchronized void closeDataBase()throws SQLException {
		if(myDataBase != null)
			myDataBase.close();
		super.close();
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			Log.v("Database Upgrade", "Database version higher than old.");
			db_delete();
		}

	}

	public DictionaryBST readDict() {
		DictionaryBST dict = new DictionaryBST();
		Cursor cursor = myDataBase.rawQuery("SELECT * FROM " + WORDS_TABLE, null);
		if (cursor.moveToFirst()) {
			do {

				dict.addWord(cursor.getString(1));
			}while (cursor.moveToNext());
		}
		cursor.close();
		return dict;
	}

	public ArrayList<String> readWords() {
		ArrayList<String> dict = new ArrayList<String>();
		Cursor cursor = myDataBase.rawQuery("SELECT * FROM " + WORDS_TABLE, null);
		cursor.moveToFirst();
		System.out.println("Before if");
		//if (cursor.moveToFirst()) {
		//System.out.println("If block");
		do {
			System.out.println(cursor.getString(1));
			dict.add(cursor.getString(1));
		}while (cursor.moveToNext());
		//}
		cursor.close();
		return dict;
	}

	public TreeSet<String> readUsedWords() {
		TreeSet<String> dict = new TreeSet<String>();
		Cursor cursor = myDataBase.rawQuery("SELECT * FROM " + USED_WORDS_TABLE, null);
		if (cursor.moveToFirst()) {
			do {
				dict.add(cursor.getString(1));
			}while (cursor.moveToNext());
		}
		cursor.close();
		return dict;
	}

	public void addUsedWord(String word) {
		ContentValues values = new ContentValues();
		values.put(USED_WORDS_TABLE, word);
		myDataBase.insert(USED_WORDS_TABLE, null, values);
	}

}