package com.example.wordlepromax;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class Letter extends View {

	private boolean foundInString;
	private boolean foundPlace;
	private Paint paint;
	private String letter;

	public Letter(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();

	}

	public boolean isFoundInString() {
		return foundInString;
	}

	public boolean isFoundPlace() {
		return foundPlace;
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (foundInString) {
			paint.setARGB(255, 0, 255, 255);
		}
		paint.setARGB(255, 0, 0, 0);
		paint.setStyle(Paint.Style.STROKE);
		int height = getHeight();
		canvas.drawRect(new Rect(0, 0, height, height), paint);
		paint.setTextSize(120);
		paint.setARGB(255, 0, 0, 0);
		if (letter != null) {
			canvas.drawText(letter, height/2 - 50, height/2 + 40, paint);
		}
	}

	public void setLetter(String lett) {
		letter = lett;
	}

	public String getLetter() {
		return letter;
	}
}
