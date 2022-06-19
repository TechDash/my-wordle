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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.TreeSet;

public class DataHandler extends SQLiteOpenHelper {
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	private static final String DATABASE_NAME = "game_data.sqlite";
	public final static String DATABASE_PATH = "/data/data/com.example.wordlepromax/databases/";
	public static final int DATABASE_VERSION = 1;
	private static final String DB_NAME = "gma_data.sqlite";
	private static final String WORDS_TABLE = "Words";
	private static final String NAME_COL = "words";
	private static final String GAME_DAT = "Game_dat";

	public DataHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.myContext = context;

		//onCreate(getReadableDatabase());
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_WORDS_TABLE = "CREATE TABLE " + WORDS_TABLE + "( " + NAME_COL + " TEXT)";
		String CREATE_GAME_DATA_TABLE = "CREATE TABLE Game_dat ( played INTEGER DEFAULT 0 , max_streak INTEGER DEFAULT 0, curr_streak INTEGER DEFAULT 0, won INTEGER DEFAULT 0 )";
		String CREATE_WIN_ATTEMPT_TABLE = "CREATE TABLE win_attempt ( one INTEGER DEFAULT 0, two INTEGER DEFAULT 0, three INTEGER DEFAULT 0, four INTEGER DEFAULT 0, five INTEGER DEFAULT 0, six INTEGER DEFAULT 0 )";
		ContentValues values = new ContentValues();
		values.put("played", 0);


		db.execSQL(CREATE_WIN_ATTEMPT_TABLE);
		//db.execSQL(CREATE_WORDS_TABLE);
		db.execSQL(CREATE_GAME_DATA_TABLE);
		//db.insert(GAME_DAT, null, values);
	}

	public void updateGameData(boolean won) {
		Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM Game_dat", null);
		cursor.moveToFirst();
		int played = cursor.getInt(0);
		ContentValues values = new ContentValues();
		values.put("played", played++);
		SQLiteDatabase db = getWritableDatabase();
		db.update(GAME_DAT, values, "played", null);
	}

	public int getDat() {
		Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM Game_dat", null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + WORDS_TABLE);

		// Create tables again
		onCreate(db);
	}

	public DictionaryBST readDict() {
		DictionaryBST dict = new DictionaryBST();
		Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + WORDS_TABLE, null);
		if (cursor.moveToFirst()) {
			do {
				dict.addWord(cursor.getString(0));
			}while (cursor.moveToNext());
		}
		cursor.close();
		return dict;
	}

	public ArrayList<String> readWords() {
		ArrayList<String> dict = new ArrayList<String>();
		Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + WORDS_TABLE, null);
		cursor.moveToFirst();
		System.out.println("Before if");
		//if (cursor.moveToFirst()) {
		//System.out.println("If block");
		do {
			System.out.println(cursor.getString(0));
			dict.add(cursor.getString(0));
		}while (cursor.moveToNext());
		//}
		cursor.close();
		return dict;
	}

	private void addWords(SQLiteDatabase db) throws IOException {
		AssetManager am = myContext.getAssets();
		InputStream im = am.open("words.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(im));
		String st = reader.readLine();
		while (st != null) {
			System.out.println("while");
			ContentValues values = new ContentValues();
			values.put(NAME_COL, st);
			db.insert(WORDS_TABLE, null, values);
		}
	}

}