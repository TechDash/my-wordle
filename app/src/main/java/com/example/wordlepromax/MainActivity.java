package com.example.wordlepromax;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

	private String letter;
	private String answer;
	private String word = "";
	private DictionaryBST dict;
	private boolean firstBoot = true;
	private ArrayList<String> words;
	private ConstraintLayout layout;
	private int letterCount = 0;
	private int attempt = 1;
	private ArrayList<Button> buttons = new ArrayList<>();
	private boolean notFound = true;
	private DataHandler db;
	boolean click = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
			case Configuration.UI_MODE_NIGHT_YES:
        		setContentView(R.layout.activity_main_dark);
				break;
			case Configuration.UI_MODE_NIGHT_NO:
				setContentView(R.layout.activity_main);
				break;
		}

		layout = findViewById(R.id.attempt1);

		if (savedInstanceState != null) {
			//letter = savedInstanceState.getString("view_Letter");
			firstBoot = savedInstanceState.getBoolean("FIRST_BOOT");
			dict = savedInstanceState.getParcelable("dict");
			//word = savedInstanceState.getString("word");
			words = savedInstanceState.getStringArrayList("words");
			//letterCount = savedInstanceState.getInt("letterCount");
			//answer = words.get(new Random().nextInt(words.size())).toUpperCase();
			answer = "WALLS";
		}
		if (firstBoot) {
			firstBoot = false;
			try {
				getData();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void getData() throws IOException {
		/*DataHandler db = new DataHandler(this);
		dict = db.readDict();
		Random random = new Random();
		words = db.readWords();
		System.out.println(dict.size());
		answer = words.get(random.nextInt(words.size()));*/

		Context context = this;
		AssetManager am = context.getAssets();
		InputStream im = am.open("words.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(im));
		String st = reader.readLine();
		dict = new DictionaryBST();
		words = new ArrayList<>();
		while (st != null) {
			dict.addWord(st);
			words.add(st);
			st = reader.readLine();
		}
		db = new DataHandler(this);
		db.updateGameData(true);
		System.out.println(db.getDat());
		//answer = words.get(new Random().nextInt(words.size())).toUpperCase();
		answer = "WALLS";
		}

	public void set(View view) {
		if (letterCount < 5 && attempt <= 6 && notFound) {
			Button button = (Button) view;
			buttons.add(button);
			if (attempt == 1) {
				layout = findViewById(R.id.attempt1);
			}
			if (attempt == 2) {
				layout = findViewById(R.id.attempt2);
			}
			if (attempt == 3) {
				layout = findViewById(R.id.attempt3);
			}
			if (attempt == 4) {
				layout = findViewById(R.id.attempt4);
			}
			if (attempt == 5) {
				layout = findViewById(R.id.attempt5);
			}
			if (attempt == 6) {
				layout = findViewById(R.id.attempt6);
			}
			letter = button.getHint().toString();
			word += letter;
			letterCount++;
			for (int i = 0; i < letterCount; i++) {
				TextView currView = (TextView) layout.getChildAt(i);
				currView.setText(String.valueOf(word.charAt(i)));
			}
		}
	}

	public void validate(View view) {
		attempt++;
		if (attempt > 6) {
			return;
		}
		else if (word.length() < 5 && notFound) {
			Toast.makeText(this, "Not enough letters", Toast.LENGTH_SHORT).show();
			attempt--;
		}
		else if (!dict.isWord(word)) {
			Toast.makeText(this, "Not in word list", Toast.LENGTH_SHORT).show();
			attempt--;
		}
		else if (dict.isWord(word)) {
			ArrayList<String> comparison = compareWords(word, answer);
			for (int i = 0; i < 5; i++) {
				TextView currView = (TextView) layout.getChildAt(i);
				Button key = buttons.get(i);

				if (comparison.get(i).equals("yes")) {
					currView.setBackgroundColor(Color.parseColor("#3EBF44"));
					key.setBackgroundResource(R.drawable.in_place);
					currView.setTextColor(Color.WHITE);
					key.setHintTextColor(Color.WHITE);
					key.setTag("true");
				}
				if (comparison.get(i).equals("inString")) {
					currView.setBackgroundColor(Color.parseColor("#E1CB07"));
					currView.setTextColor(Color.WHITE);
					if (!Boolean.parseBoolean((String) key.getTag())) {
						key.setBackgroundResource(R.drawable.in_word);
						key.setHintTextColor(Color.WHITE);
						key.setTag("yellow");
					}
				}
				if (comparison.get(i).equals("no")) {
					currView.setBackgroundColor(Color.parseColor("#474747"));
					currView.setTextColor(Color.WHITE);
					String tag = (String) key.getTag();
					if (!Boolean.parseBoolean(tag) && !tag.equals("yellow")) {
						key.setBackgroundResource(R.drawable.not_in_word);
						key.setHintTextColor(Color.WHITE);
					}
				}
			}
			if (!word.equals(answer) && attempt == 7) {
				Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
			}
			if (word.equals(answer)) {
				notFound = false;
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				final View popUp = getLayoutInflater().inflate(R.layout.play_again, null);
				builder.setView(popUp);
				AlertDialog builderC = builder.create();
				builderC.show();
			}
			buttons.clear();
			letterCount = 0;
			word = "";
		}
	}

	public void delete(View view) {
		if (attempt == 1) {
			layout = findViewById(R.id.attempt1);
		}
		if (attempt == 2) {
			layout = findViewById(R.id.attempt2);
		}
		if (attempt == 3) {
			layout = findViewById(R.id.attempt3);
		}
		if (attempt == 4) {
			layout = findViewById(R.id.attempt4);
		}
		if (attempt == 5) {
			layout = findViewById(R.id.attempt5);
		}
		if (attempt == 6) {
			layout = findViewById(R.id.attempt6);
		}
		if (word.length() - 1 >= 0) {
			TextView currView = (TextView) layout.getChildAt(word.length() - 1);
			currView.setText("");
			word = word.substring(0, word.length() - 1);
			buttons.remove(buttons.size()-1);
			letterCount--;
		}
	}

	private ArrayList<String> compareWords(String word1, String word2) {
		ArrayList<String> output = new ArrayList<>();
		ArrayList<String> seen = new ArrayList<>();
		HashMap<String, ArrayList<Integer>> occ = occurrences(word1, word2);
		for (int i = 0; i < 5; i++) {
			output.add("no");
		}

		for (int i = 0; i < word1.length(); i++) {
			String currLetter = String.valueOf(word1.charAt(i));
			ArrayList<Integer> letterOcc = occ.get(currLetter);
			if (!seen.contains(currLetter)) {
				if (letterOcc != null) {
					if (letterOcc.size() < 2) {
						seen.add(currLetter);
					}
					for (Integer pos : letterOcc) {
						if (i == pos) {
							output.set(i, "yes");
							break;
						} else {
							output.set(i, "inString");
						}
					}
				}
			}
		}
		return output;
	}

	private HashMap<String, ArrayList<Integer>> occurrences(String word1, String word2) {
		HashMap<String, ArrayList<Integer>> output = new HashMap<>();
		for (int i = 0; i < word1.length(); i++) {
			for (int j = 0; j < word1.length(); j++) {
				if (word1.charAt(i) == word2.charAt(j)) {
					if (output.containsKey(String.valueOf(word1.charAt(i)))) {
						ArrayList<Integer> occ = output.get(String.valueOf(word1.charAt(i)));
						assert occ != null;
						if (occ.get(occ.size()-1) < j) {
							occ.add(j);
							output.put(String.valueOf(word1.charAt(i)), occ);
						}
					}
					else {
						ArrayList<Integer> occ = new ArrayList<>();
						occ.add(j);
						output.put(String.valueOf(word1.charAt(i)), occ);
					}
				}
			}

		}
		return output;
	}

	public void playAgain(View view) {
		recreate();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// call superclass to save any view hierarchy
		outState.putString("view_Letter", letter);
		outState.putString("word", word);
		outState.putParcelable("dict", dict);
		outState.putBoolean("FIRST_BOOT", firstBoot);
		outState.putStringArrayList("words", words);
		outState.putInt("letterCount", letterCount);
		outState.p
		super.onSaveInstanceState(outState);
	}

}