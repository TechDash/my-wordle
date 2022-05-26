package com.example.wordlepromax;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

	private String letter;
	private String word;
	private DictionaryBST dict;
	private boolean firstBoot = true;
	private ArrayList<String> words;
	private TreeSet<String> usedWords;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		if (savedInstanceState != null) {
			letter = savedInstanceState.getString("view_Letter");
			firstBoot = savedInstanceState.getBoolean("FIRST_BOOT");
			dict = savedInstanceState.getParcelable("dict");
			word = savedInstanceState.getString("word");
			words = savedInstanceState.getStringArrayList("words");
		}
		if (firstBoot) {
			firstBoot = false;
			System.out.println("mnor");
			try {
				getData();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			ConstraintLayout layout = findViewById(R.id.attempt1);
			System.out.println(word);
			for (int i = 0; i < 5; i++) {
				System.out.println(String.valueOf(word.charAt(i)));
				Letter currView = (Letter) layout.getChildAt(i);
				currView.setLetter(String.valueOf(word.charAt(i)));
				System.out.println(currView.getLetter());
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
		word = words.get(new Random().nextInt(words.size())).toUpperCase();

		try {
			File f1 = new File("data/data.com.example.wordlepromax/databases/usedWords.txt");

			FileWriter fileWritter = new FileWriter(f1.getName(),true);
			BufferedWriter bw = new BufferedWriter(fileWritter);
			bw.write(word);
			bw.close();
			System.out.println("Done");
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	// invoked when the activity may be temporarily destroyed, save the instance state here
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// call superclass to save any view hierarchy
		outState.putString("view_Letter", letter);
		outState.putString("word", word);
		outState.putParcelable("dict", dict);
		outState.putBoolean("FIRST_BOOT", firstBoot);
		outState.putStringArrayList("words", words);
		super.onSaveInstanceState(outState);
	}

	public void testSet(View view) {
		recreate();
	}

}