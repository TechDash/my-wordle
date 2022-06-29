package com.example.wordlepromax;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

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
	private AlertDialog builderC;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pickTheme();
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		db = new DataHandler(this);

		layout = findViewById(R.id.attempt1);

		if (savedInstanceState != null) {
			firstBoot = savedInstanceState.getBoolean("FIRST_BOOT");
			dict = savedInstanceState.getParcelable("dict");
			words = savedInstanceState.getStringArrayList("words");
			//answer = words.get(new Random().nextInt(words.size())).toUpperCase();
			answer = "ASSET";
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

	private void pickTheme() {
		switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
			case Configuration.UI_MODE_NIGHT_YES:
				setContentView(R.layout.activity_main_dark);
				break;
			case Configuration.UI_MODE_NIGHT_NO:
				setContentView(R.layout.activity_main);
				break;
		}
	}

	public void getData() throws IOException {
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
		//answer = words.get(new Random().nextInt(words.size())).toUpperCase();
		answer = "ASSET";
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
			word += button.getHint().toString();
			letterCount++;
			for (int i = 0; i < letterCount; i++) {
				TextView currView = (TextView) layout.getChildAt(i);
				currView.setText(String.valueOf(word.charAt(i)));
			}
		}
	}

	public void validate(View view) throws InterruptedException {
		attempt++;
		if (attempt > 7) {
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
					for (int j = 1; j <= 360; j++) {
						currView.setRotationX(j);
					}
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
				db.updateGameData(false, attempt - 1);
				popUp();
			}
			if (word.equals(answer)) {
				notFound = false;
				db.updateGameData(true, attempt - 1);
				popUp();
			}
			buttons.clear();
			letterCount = 0;
			word = "";
		}
	}

	private void popUp() {
		HashMap<String, Integer> data = db.getData();
		float percentage = ((float)data.get("won") / (float) data.get("played")) * 100;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final View popUp = getLayoutInflater().inflate(R.layout.play_again, null);

		TextView played = popUp.findViewById(R.id.playedNum);
		TextView winPercent = popUp.findViewById(R.id.winPercentageNum);
		TextView maxStreak = popUp.findViewById(R.id.maxStreakNum);
		TextView currStreak = popUp.findViewById(R.id.currStreakNum);
		TextView playedTitle = popUp.findViewById(R.id.played);
		TextView winPercentTitle = popUp.findViewById(R.id.winPercentage);
		TextView maxStreakTitle = popUp.findViewById(R.id.maxStreak);
		TextView currStreakTitle = popUp.findViewById(R.id.currStreak);
		TextView stat = popUp.findViewById(R.id.stat);
		TextView guess = popUp.findViewById(R.id.guess);

		switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
			case Configuration.UI_MODE_NIGHT_YES:
				played.setTextColor(Color.WHITE);
				winPercent.setTextColor(Color.WHITE);
				maxStreak.setTextColor(Color.WHITE);
				currStreak.setTextColor(Color.WHITE);
				playedTitle.setTextColor(Color.WHITE);
				winPercentTitle.setTextColor(Color.WHITE);
				maxStreakTitle.setTextColor(Color.WHITE);
				currStreakTitle.setTextColor(Color.WHITE);
				stat.setTextColor(Color.WHITE);
				guess.setTextColor(Color.WHITE);
				break;
			case Configuration.UI_MODE_NIGHT_NO:
				played.setTextColor(Color.BLACK);
				winPercent.setTextColor(Color.BLACK);
				maxStreak.setTextColor(Color.BLACK);
				currStreak.setTextColor(Color.BLACK);
				playedTitle.setTextColor(Color.BLACK);
				winPercentTitle.setTextColor(Color.BLACK);
				maxStreakTitle.setTextColor(Color.BLACK);
				currStreakTitle.setTextColor(Color.BLACK);
				stat.setTextColor(Color.BLACK);
				guess.setTextColor(Color.BLACK);
				break;
		}

		played.setText(String.valueOf(data.get("played")));
		winPercent.setText(String.valueOf((int)percentage));
		maxStreak.setText(String.valueOf(data.get("maxStreak")));
		currStreak.setText(String.valueOf(data.get("currStreak")));

		HorizontalBarChart chart = popUp.findViewById(R.id.idBarChart);
		BarDataSet dataSet = new BarDataSet(getBarEntries(data), null);
		BarData barData = new BarData(dataSet);
		chart.setData(barData);
		dataSet.setColors(ContextCompat.getColor(this, R.color.app_green));

		// setting text color.
		dataSet.setValueTextColor(Color.BLACK);

		ValueFormatter vf = new ValueFormatter() {
			@Override
			public String getFormattedValue(float value) {
				return String.valueOf((int) value);
			}
		};
		dataSet.setValueFormatter(vf);
		dataSet.setValueTextSize(15);

		chart.getXAxis().setTextSize(15);
		chart.getDescription().setEnabled(false);
		chart.getXAxis().setDrawGridLines(false);
		chart.getAxisLeft().setDrawGridLines(false);
		chart.getAxisRight().setDrawGridLines(false);
		chart.getAxisRight().setEnabled(false);
		chart.getAxisLeft().setDrawLabels(false);
		chart.getLegend().setEnabled(false);

		builder.setView(popUp);
		builderC = builder.create();
		builderC.show();
	}

	private ArrayList<BarEntry> getBarEntries(HashMap<String, Integer> map) {
		ArrayList<BarEntry> entries = new ArrayList<>();
		int one = map.get("one");
		int two = map.get("two");
		int three = map.get("three");
		int four = map.get("four");
		int five = map.get("five");
		int six = map.get("six");
		entries.add(new BarEntry(1, one));
		entries.add(new BarEntry(2, two));
		entries.add(new BarEntry(3, three));
		entries.add(new BarEntry(4, four));
		entries.add(new BarEntry(5, five));
		entries.add(new BarEntry(6, six));
		return entries;
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
		ArrayList<String> seenOnce = new ArrayList<>();
		HashMap<String, ArrayList<Integer>> occ = occurrences(word1, word2);
		for (int i = 0; i < 5; i++) {
			output.add("no");
		}

		for (int i = 0; i < word1.length(); i++) {
			String currLetter = String.valueOf(word1.charAt(i));
			ArrayList<Integer> letterOcc = occ.get(currLetter);
			if (!seenOnce.contains(currLetter)) {
				if (letterOcc != null) {
					if (letterOcc.size() < 2) {
						seenOnce.add(currLetter);
					}
					for (int j = 0; j < letterOcc.size(); j++) {
						int pos = letterOcc.get(j);
						if (i == pos) {
							output.set(i, "yes");
							letterOcc.remove(j);
							occ.put(currLetter, letterOcc);
							//break;
						}
						else if (output.get(i).equals("no")){
							output.set(i, "inString");
							letterOcc.remove(j);
							occ.put(currLetter, letterOcc);
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
		if (!notFound) {
			builderC.cancel();
			recreate();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// call superclass to save any view hierarchy
		outState.putString("word", word);
		outState.putParcelable("dict", dict);
		outState.putBoolean("FIRST_BOOT", firstBoot);
		outState.putStringArrayList("words", words);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.howtoPlay) {
			Intent intent = new Intent(this, HowToPlay.class);
			startActivity(intent);
		}
		if (item.getItemId() == R.id.stats) {
			popUp();
		}
		return super.onOptionsItemSelected(item);
	}
}