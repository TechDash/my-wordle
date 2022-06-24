package com.example.wordlepromax;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class DataHandler extends SQLiteOpenHelper {
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	private static final String DATABASE_NAME = "game_data.sqlite";
	public final static String DATABASE_PATH = "/data/data/com.example.wordlepromax/databases/";
	public static final int DATABASE_VERSION = 1;
	private static final String WORDS_TABLE = "Words";
	private static final String NAME_COL = "words";
	private static final String GAME_DAT = "Game_dat";

	public DataHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.myContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_GAME_DATA_TABLE = "CREATE TABLE Game_dat ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, played INTEGER DEFAULT 0 NOT NULL, " +
				"max_streak INTEGER DEFAULT 0 NOT NULL, curr_streak INTEGER DEFAULT 0 NOT NULL, won INTEGER DEFAULT 0 NOT NULL );";

		String CREATE_WIN_ATTEMPT_TABLE = "CREATE TABLE win_attempt ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, one INTEGER DEFAULT 0 NOT NULL, two INTEGER DEFAULT 0 NOT NULL, " +
				"three INTEGER DEFAULT 0 NOT NULL, four INTEGER DEFAULT 0 NOT NULL, five INTEGER DEFAULT 0 NOT NULL, six INTEGER DEFAULT 0 NOT NULL );";

		db.execSQL(CREATE_WIN_ATTEMPT_TABLE);
		db.execSQL(CREATE_GAME_DATA_TABLE);

		db.execSQL("INSERT OR IGNORE INTO Game_dat(played) VALUES(0)");
		db.execSQL("INSERT OR IGNORE INTO win_attempt(one) VALUES(0)");
	}

	public void updateGameData(boolean won, int attempt) {
		Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM Game_dat", null);
		cursor.moveToFirst();
		int played = cursor.getInt(1) + 1;
		int wonNum = cursor.getInt(4) + 1;
		int currStreak = cursor.getInt(3) + 1;
		int maxStreak = cursor.getInt(2) + 1;
		ContentValues values = new ContentValues();
		values.put("played", played);
		SQLiteDatabase db = this.getWritableDatabase();
		if (won) {
			values.put("won", wonNum);
			values.put("curr_streak", currStreak);
			if (currStreak >= maxStreak) {
				values.put("max_streak", currStreak);
			}
		}
		else {
			values.put("curr_streak", 0);
		}
		db.update(GAME_DAT, values, "id = ?", new String[]{"1"});
		cursor.close();
		Cursor cursor2 = getReadableDatabase().rawQuery("SELECT * FROM win_attempt", null);
		cursor2.moveToFirst();
		String column = getColumn(attempt);
		if (won) {
			int wins = cursor2.getInt(attempt) + 1;
			ContentValues cv = new ContentValues();
			cv.put(column, wins);
			db.update("win_attempt", cv, "id = ?", new String[] {"1"});
		}
		cursor2.close();
	}

	private String getColumn(int attempt) {
		if (attempt == 1) {
			return "one";
		}
		if (attempt == 2) {
			return "two";
		}
		if (attempt == 3) {
			return "three";
		}
		if (attempt == 4) {
			return "four";
		}
		if (attempt == 5) {
			return "five";
		}
		if (attempt == 6) {
			return "six";
		}
		return "";
	}

	public HashMap<String, Integer> getData() {
		HashMap<String, Integer> data = new HashMap<>();
		Cursor cursorGameDat = getReadableDatabase().rawQuery("SELECT * FROM Game_dat", null);
		cursorGameDat.moveToFirst();
		data.put("played", cursorGameDat.getInt(1));
		data.put("maxStreak", cursorGameDat.getInt(2));
		data.put("currStreak", cursorGameDat.getInt(3));
		data.put("won", cursorGameDat.getInt(4));
		cursorGameDat.close();
		Cursor wins = getReadableDatabase().rawQuery("SELECT * FROM win_attempt", null);
		wins.moveToFirst();
		data.put("one", wins.getInt(1));
		data.put("two", wins.getInt(2));
		data.put("three", wins.getInt(3));
		data.put("four", wins.getInt(4));
		data.put("five", wins.getInt(5));
		data.put("six", wins.getInt(6));
		wins.close();
		return data;
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + GAME_DAT);
		db.execSQL("DROP TABLE IF EXISTS win_attempt");
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
		ContentValues values = new ContentValues();
		int lines = 0;
		while (st != null) {
			lines++;
			System.out.println(lines);
			System.out.println(st);
			values.put(NAME_COL, st);
			st = reader.readLine();
			db.insert(WORDS_TABLE, null, values);
		}
	}

}