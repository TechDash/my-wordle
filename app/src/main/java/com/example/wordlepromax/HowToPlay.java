package com.example.wordlepromax;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HowToPlay extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
			case Configuration.UI_MODE_NIGHT_YES:
				setContentView(R.layout.how_to_play_dark);
				break;
			case Configuration.UI_MODE_NIGHT_NO:
				setContentView(R.layout.how_to_play);
				break;
		}
		Toolbar toolBar = findViewById(R.id.my_toolbar);
		setSupportActionBar(toolBar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
}
