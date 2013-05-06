package com.roiding.mines;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Mines extends Activity {

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// load previous game
		if (Core.PREVIOUS_GAME_MAP == null)
			Core.PREVIOUS_GAME_MAP = (HashMap<String, Object>) Persist.load(
					this, Core.PREVIOUS_GAME_FILE);
	}

	@Override
	public void onStart() {
		super.onStart();
		((Button) findViewById(R.id.btn_startNewGame))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						// disable the previous game
						Core.PREVIOUS_GAME_MAP = new HashMap<String, Object>();
						Intent intent = new Intent();
						intent.setClass(Mines.this, MinesPlay.class);
						startActivity(intent);
					}

				});

		Button continueGameButton = (Button) findViewById(R.id.btn_continueGame);
		if (Core.PREVIOUS_GAME_MAP == null
				|| Core.PREVIOUS_GAME_MAP.size() == 0)
			continueGameButton.setEnabled(false);
		else
			continueGameButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(Mines.this, MinesPlay.class);
					startActivity(intent);
				}
			});

		((Button) findViewById(R.id.btn_scores))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(Mines.this, Scores.class);
						startActivity(intent);
					}

				});
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (Core.PREVIOUS_GAME_MAP == null
				|| Core.PREVIOUS_GAME_MAP.size() == 0)
			this.deleteFile(Core.PREVIOUS_GAME_FILE);
		else
			Persist
					.store(this, Core.PREVIOUS_GAME_MAP,
							Core.PREVIOUS_GAME_FILE);

	}
}