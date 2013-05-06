package com.roiding.mines;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class MinesPlay extends Activity {

	private TextView mScoreText;
	private MinesView minesView;
	private ImageView faceView_1;
	private ImageView faceView_2;

	private final int DIALOG_FAIL = 1;
	private final int DIALOG_SUCCESS = 2;
	private final int DIALOG_CONTINUE = 3;

	ImageView[] faceView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.play);

		mScoreText = (TextView) findViewById(R.id.score);
		minesView = (MinesView) findViewById(R.id.mines);
		faceView_1 = (ImageView) findViewById(R.id.img_face_1);
		faceView_2 = (ImageView) findViewById(R.id.img_face_2);

		faceView = new ImageView[] { faceView_1, faceView_2 };

		minesView.setMinesPlay(this);

		if (Core.PREVIOUS_GAME_MAP == null
				|| Core.PREVIOUS_GAME_MAP.size() == 0) {
			minesView.startNewGame();
		} else {
			minesView.prepareContinueGame(Core.PREVIOUS_GAME_MAP);
			showDialog(DIALOG_CONTINUE);
		}
		update();
	}

	public void switchView(int view) {
		for (ImageView iv : faceView) {
			iv.setVisibility(View.INVISIBLE);
		}
		faceView[view].setVisibility(View.VISIBLE);
	}

	private RefreshHandler mHandler = new RefreshHandler();

	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			update();
			minesView.invalidate();
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	}

	public void update() {

		String text = "Flags:" + minesView.SCORE_SuspicationCount + "/"
				+ minesView.SCENE_MinesCount + "   Time:"
				+ minesView.getScore() / 1000 + " s";

		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			text = "Flags\n" + minesView.SCORE_SuspicationCount + "/"
					+ minesView.SCENE_MinesCount + "\n\nTime\n"
					+ minesView.getScore() / 1000 + " s";

		mScoreText.setText(text);

		if (minesView.SCENE_Mode == MinesView.DONE) {
			if (minesView.SCORE_SuspicationCount == minesView.SCENE_MinesCount)
				showDialog(DIALOG_SUCCESS);
			else
				showDialog(DIALOG_FAIL);
		} else {
			mHandler.sleep(1000);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		int messageId = 0;
		if (id == DIALOG_CONTINUE) {
			return new AlertDialog.Builder(MinesPlay.this).setTitle("")
					.setMessage(R.string.continue_message).setPositiveButton(
							R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									minesView.continueGame();
								}
							}).create();
		} else {
			switch (id) {
			case DIALOG_FAIL:
				messageId = R.string.fail_message;
				break;
			case DIALOG_SUCCESS:
				messageId = R.string.success_message;
				break;
			}

			return new AlertDialog.Builder(MinesPlay.this).setTitle("")
					.setMessage(messageId).setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									minesView.startNewGame();
									minesView.invalidate();
									update();
								}
							}).setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									MinesPlay.this.finish();
								}
							}).create();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (minesView.SCENE_Mode == MinesView.PLAYING)
			Core.PREVIOUS_GAME_MAP = minesView.saveState();
		else
			Core.PREVIOUS_GAME_MAP = new HashMap<String, Object>();
	}
}