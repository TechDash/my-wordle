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
	public String letter;

	public Letter(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.Letter,
				0, 0);

		try {
			foundInString = a.getBoolean(R.styleable.Letter_foundInString, false);
			foundPlace = a.getBoolean(R.styleable.Letter_foundPlace, false);
		} finally {
			a.recycle();
		}

	}

	public boolean isFoundInString() {
		return foundInString;
	}

	public boolean isFoundPlace() {
		return foundPlace;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		Paint black = new Paint();
		black.setARGB(255, 0, 0, 0);
		if (foundInString) {
			paint.setARGB(255, 0, 255, 255);
		}
		paint.setARGB(255, 122, 122, 122);
		paint.setStyle(Paint.Style.STROKE);
		int height = canvas.getHeight();
		float coordinate = new Float(height);
		canvas.drawRect(new Rect(0, 0, height, height), paint);
		black.setTextSize(120);
		canvas.drawText("m", coordinate/2 - 50, coordinate/2 + 40, black);
	}
}
