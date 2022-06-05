package com.example.wordlepromax;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

	private String letter;
	private String answer;
	private String word = "";
	private DictionaryBST dict;
	private boolean firstBoot = true;
	private ArrayList<String> words;
	private TreeSet<String> usedWords;
	private ConstraintLayout layout;
	private int letterCount = 0;
	private int attempt = 1;
	private int wordLength = 0;
	private ArrayList<ConstraintLayout> layouts;
	
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
			letter = savedInstanceState.getString("view_Letter");
			firstBoot = savedInstanceState.getBoolean("FIRST_BOOT");
			dict = savedInstanceState.getParcelable("dict");
			word = savedInstanceState.getString("word");
			words = savedInstanceState.getStringArrayList("words");
			letterCount = savedInstanceState.getInt("letterCount");
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
		/*DataHandler db = new DataHandler(MainActivity.this);
		dict = db.readDict();
		Random random = new Random();
		words = db.readWords();
		System.out.println(dict.size());
		usedWords = db.readUsedWords();
		do {
			word = words.get(random.nextInt(words.size()));
		} while(usedWords.contains(word));
		db.addUsedWord(word);*/
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
		answer = words.get(new Random().nextInt(words.size())).toUpperCase();
	}

	public void set(View view) {
		if (letterCount < 5) {
			Button button = (Button) view;
			switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
				case Configuration.UI_MODE_NIGHT_YES:
					setContentView(R.layout.activity_main_dark);
					break;
				case Configuration.UI_MODE_NIGHT_NO:
					setContentView(R.layout.activity_main);
					break;
			}
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
			wordLength = word.length() - 1;
			for (int i = 0; i < letterCount; i++) {
				TextView currView = (TextView) layout.getChildAt(i);
				currView.setText(String.valueOf(word.charAt(i)));
			}
		}
	}

	public void validate(View view) {
		if (word.length() < 5) {
			Toast.makeText(this, "Not enough letters", Toast.LENGTH_SHORT).show();
		}
		else if (!dict.isWord(word)) {
			Toast.makeText(this, "Not in word list", Toast.LENGTH_SHORT).show();
		}
		else if (dict.isWord(word)) {
			ArrayList<String> comparison = compareWords(word, answer);
			for (int i = 0; i < 5; i++) {
				TextView currView = (TextView) layout.getChildAt(i);
				if (comparison.get(i).equals("yes")) {
					currView.setBackgroundColor(Color.GREEN);
				}
				if (comparison.get(i).equals("inString")) {
					currView.setBackgroundColor(Color.YELLOW);
				}
			}
		}
		if (attempt < 6) {
			attempt++;
		}
		letterCount = 0;
		word = "";
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
			letterCount--;
		}
	}

	private ArrayList<String> compareWords(String word1, String word2) {
		ArrayList<String> output = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			output.add("no");
		}
		for (int i = 0; i < word1.length(); i++) {
			if (word1.charAt(i) == word2.charAt(i)) {
				output.set(i, "yes");
			}
			else {
				for (int j = 0; j < word1.length(); j++) {
					if (word1.charAt(i) == word2.charAt(j)) {
						output.set(i, "inString");
					}
				}
			}
		}
		return output;
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
		super.onSaveInstanceState(outState);
	}

}