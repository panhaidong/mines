package com.roiding.mines;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class MinesView extends View {

	// bitmap's dimension size
	private final int mTileSize = 20;
	private final String TAG = "MINES";
	private long clickTimeMSEL = 0;
	private long startTime;
	private int screenOrientation;
	private MinesPlay minesPlay;

	public void setMinesPlay(MinesPlay minesPlay) {
		this.minesPlay = minesPlay;
		screenOrientation = minesPlay.getResources().getConfiguration().orientation;
	}

	public MinesView(Context context, AttributeSet attrs)
			throws InterruptedException {
		super(context, attrs);
		initView();
	}

	private void initView() {

		setFocusable(true);
		mPaint.setTextSize(16);

		Resources r = this.getContext().getResources();
		loadTile(CellIcon.SUSPICION, r.getDrawable(R.drawable.g9));
		loadTile(CellIcon.BOMB, r.getDrawable(R.drawable.done));
		loadTile(CellIcon.UNKNOWN, r.getDrawable(R.drawable.g));
		loadTile(CellIcon.CURRENT, r.getDrawable(R.drawable.current));

	}

	public void startNewGame() {
		SCORE_SuspicationCount = 0;
		SCORE_ClickCount = 0;
		SCORE_Time = 0;

		SCENE_MinesCount = 60;

		if (minesPlay.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			init_land();
		else
			init_port();

		SCENE_UnknownCount = SCENE_MaxX * SCENE_MaxY;
		SCENE_Mode = READY;
		SCENE_CurrentX = SCENE_MaxX / 2;
		SCENE_CurrentY = SCENE_MaxY / 2;

		SCENE_Cell = new Cell[SCENE_MaxX][SCENE_MaxY];

		for (int x = 0; x < SCENE_Cell.length; x++) {
			for (int y = 0; y < SCENE_Cell[0].length; y++) {
				SCENE_Cell[x][y] = new Cell();
				SCENE_Cell[x][y].fact = CellIcon.EMPTY;
				SCENE_Cell[x][y].gloss = CellIcon.UNKNOWN;
			}
		}

		startTime = System.currentTimeMillis();

	}

	private void init_port() {
		SCENE_MaxX = 14;
		SCENE_MaxY = 20;
		SCENE_OffsetX = 20;
		SCENE_OffsetY = 50;

	}

	private void init_land() {
		SCENE_MaxX = 20;
		SCENE_MaxY = 14;
		SCENE_OffsetX = 70;
		SCENE_OffsetY = 7;
	}

	public void prepareContinueGame(HashMap<String, Object> map) {
		Log.i(TAG, "prepareContinueGame");

		if (minesPlay.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			init_land();
		else
			init_port();

		SCORE_SuspicationCount = (Integer) map.get("SCORE_SuspicationCount");
		SCORE_ClickCount = (Integer) map.get("SCORE_ClickCount");
		SCORE_Time = (Long) map.get("SCORE_Time");

		SCENE_CurrentX = (Integer) map.get("SCENE_CurrentX");
		SCENE_CurrentY = (Integer) map.get("SCENE_CurrentY");
		SCENE_UnknownCount = (Integer) map.get("SCENE_UnknownCount");
		SCENE_Cell = (Cell[][]) map.get("SCENE_Cell");

		int preScreenOrientation = (Integer) map.get("screenOrientation");
		if (preScreenOrientation != screenOrientation) {
			if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE)
				t1(SCENE_Cell);
			else
				t2(SCENE_Cell);
		}

	}

	public void continueGame() {
		SCENE_Mode = PLAYING;
		startTime = System.currentTimeMillis();
	}

	private void t1(Cell[][] c) {
		SCENE_Cell = new Cell[SCENE_MaxX][SCENE_MaxY];
		for (int x = 0; x < SCENE_Cell.length; x++) {
			for (int y = 0; y < SCENE_Cell[0].length; y++) {
				SCENE_Cell[x][SCENE_MaxY - y - 1] = c[y][x];
			}
		}
	}

	private void t2(Cell[][] c) {
		SCENE_Cell = new Cell[SCENE_MaxX][SCENE_MaxY];
		for (int x = 0; x < SCENE_Cell.length; x++) {
			for (int y = 0; y < SCENE_Cell[0].length; y++) {
				SCENE_Cell[SCENE_MaxX - x - 1][y] = c[y][x];
			}
		}
	}

	int SCENE_OffsetX;
	int SCENE_OffsetY;
	int SCENE_MaxX;
	int SCENE_MaxY;
	int SCENE_MinesCount;

	int SCORE_SuspicationCount;
	int SCORE_ClickCount;
	long SCORE_Time;

	int SCENE_CurrentX;
	int SCENE_CurrentY;
	int SCENE_UnknownCount;
	int SCENE_Mode;

	Cell[][] SCENE_Cell;

	public HashMap<String, Object> saveState() {

		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("SCORE_SuspicationCount", Integer
				.valueOf(SCORE_SuspicationCount));
		map.put("SCORE_ClickCount", Integer.valueOf(SCORE_ClickCount));
		map.put("SCORE_Time", Long.valueOf(SCORE_Time
				+ (System.currentTimeMillis() - startTime)));

		map.put("SCENE_CurrentX", Integer.valueOf(SCENE_CurrentX));
		map.put("SCENE_CurrentY", Integer.valueOf(SCENE_CurrentY));
		map.put("SCENE_UnknownCount", Integer.valueOf(SCENE_UnknownCount));
		map.put("screenOrientation", Integer.valueOf(screenOrientation));

		map.put("SCENE_Cell", SCENE_Cell);

		return map;
	}

	public static final int READY = 0;
	public static final int PLAYING = 1;
	public static final int DONE = 2;

	// all bitmap resource
	private HashMap<Integer, Bitmap> mTileBitmapMap = new HashMap<Integer, Bitmap>();

	private void loadTile(int key, Drawable tile) {
		Bitmap bitmap = Bitmap.createBitmap(mTileSize, mTileSize,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		tile.setBounds(0, 0, mTileSize, mTileSize);
		tile.draw(canvas);

		mTileBitmapMap.put(key, bitmap);
	}

	public long getScore() {
		if (SCENE_Mode == PLAYING)
			return SCORE_Time + (System.currentTimeMillis() - startTime);
		else
			return SCORE_Time;
	}

	@SuppressWarnings("unchecked")
	private void done() {
		SCORE_Time += System.currentTimeMillis() - startTime;

		// store the score
		if (SCORE_SuspicationCount == SCENE_MinesCount
				&& SCENE_UnknownCount == 0 && SCENE_Mode != DONE) {
			Log.i("###", "score");

			ArrayList<long[]> list = (ArrayList<long[]>) Persist.load(
					minesPlay, Core.SCORE_FILE);
			if (list == null)
				list = new ArrayList<long[]>();

			list.add(new long[] { System.currentTimeMillis(), getScore() });
			Persist.store(minesPlay, list, Core.SCORE_FILE);
		}

		SCENE_Mode = DONE;
		minesPlay.update();
	}

	private final Paint mPaint = new Paint();

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.i(TAG, "onDraw:"+canvas.toString());
		mPaint.setColor(0xFF000000);
		for (int x = 0; x < SCENE_MaxX; x++) {
			canvas.drawLine(x * mTileSize + SCENE_OffsetX, 0 + SCENE_OffsetY, x
					* mTileSize + SCENE_OffsetX, SCENE_MaxY * mTileSize
					+ SCENE_OffsetY, mPaint);
		}
		for (int y = 0; y < SCENE_MaxY; y++) {
			canvas.drawLine(0 + SCENE_OffsetX, y * mTileSize + SCENE_OffsetY,
					SCENE_MaxX * mTileSize + SCENE_OffsetX, y * mTileSize
							+ SCENE_OffsetY, mPaint);
		}
		for (int x = 0; x < SCENE_MaxX; x++) {
			for (int y = 0; y < SCENE_MaxY; y++) {
				int o = SCENE_Cell[x][y].gloss;
				if (o == 0) {
				} else if (o < 9) {
					mPaint.setColor(Core.FONT_COLOR[o - 1]);
					canvas.drawText(String.valueOf(o), x * mTileSize + 5
							+ SCENE_OffsetX, (y + 1) * mTileSize - 2
							+ SCENE_OffsetY, mPaint);
				} else {
					canvas.drawBitmap(mTileBitmapMap.get(o), x * mTileSize
							+ SCENE_OffsetX, y * mTileSize + SCENE_OffsetY,
							mPaint);
				}
			}
		}

		// draw current bar
		canvas.drawBitmap(mTileBitmapMap.get(CellIcon.CURRENT), SCENE_CurrentX
				* mTileSize + SCENE_OffsetX, SCENE_CurrentY * mTileSize
				+ SCENE_OffsetY, mPaint);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		Log.i(TAG, "onKeyDown:" + keyCode);
		if (SCENE_Mode == DONE)
			return false;
		if (keyCode == KeyEvent.KEYCODE_C) {
			minesPlay.switchView(FaceView.BE_CAREFUL);
		}
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent msg) {
		if (SCENE_Mode == DONE)
			return false;

		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			SCENE_CurrentY--;
			SCENE_CurrentY = Math.max(SCENE_CurrentY, 0);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			SCENE_CurrentY++;
			SCENE_CurrentY = Math.min(SCENE_CurrentY, SCENE_MaxY - 1);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			SCENE_CurrentX--;
			SCENE_CurrentX = Math.max(SCENE_CurrentX, 0);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			SCENE_CurrentX++;
			SCENE_CurrentX = Math.min(SCENE_CurrentX, SCENE_MaxX - 1);
		} else if (keyCode == KeyEvent.KEYCODE_C) {
			// click
			click(SCENE_CurrentX, SCENE_CurrentY);
			minesPlay.switchView(FaceView.NORMAL);
			SCORE_ClickCount++;
		} else if (keyCode == KeyEvent.KEYCODE_X) {
			// mark
			mark(SCENE_CurrentX, SCENE_CurrentY);
			SCORE_ClickCount++;
		}

		this.postInvalidate();
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (SCENE_Mode == DONE)
			return false;

		int eventaction = event.getAction();
		SCORE_ClickCount++;

		int x = (int) event.getX() - SCENE_OffsetX;
		int y = (int) event.getY() - SCENE_OffsetY;

		int tempX = (int) (x / mTileSize);
		int tempY = (int) (y / mTileSize);

		if (tempX < 0 || tempX >= SCENE_MaxX || tempY < 0
				|| tempY >= SCENE_MaxY) {
			return false;
		}

		switch (eventaction) {

		case MotionEvent.ACTION_DOWN:
			minesPlay.switchView(FaceView.BE_CAREFUL);
			break;

		case MotionEvent.ACTION_UP:
			// double click
			if (tempX == SCENE_CurrentX && tempY == SCENE_CurrentY
					&& System.currentTimeMillis() - clickTimeMSEL < 500) {
				if (SCENE_Cell[tempX][tempY].gloss == CellIcon.SUSPICION)
					SCORE_SuspicationCount--;

				click(tempX, tempY);
			} else {
				mark(tempX, tempY);
			}

			SCENE_CurrentX = tempX;
			SCENE_CurrentY = tempY;

			clickTimeMSEL = System.currentTimeMillis();
			minesPlay.switchView(FaceView.NORMAL);
			break;
		}
		this.postInvalidate();

		return true;
	}

	private void _click(int x, int y) {

		if (SCENE_Cell[x][y].gloss == CellIcon.UNKNOWN)
			SCENE_UnknownCount--;

		if (SCENE_Cell[x][y].fact == CellIcon.SUSPICION) {
			SCENE_Cell[x][y].gloss = SCENE_Cell[x][y].fact = CellIcon.BOMB;
			done();
		}

		SCENE_Cell[x][y].gloss = SCENE_Cell[x][y].fact;

		if (SCORE_SuspicationCount == SCENE_MinesCount
				&& SCENE_UnknownCount == 0) {
			done();
		}
	}

	private void fast(int x, int y) {
		if (SCENE_Cell[x][y].gloss == CellIcon.UNKNOWN)
			return;

		ArrayList<int[]> list = Core.isClear(SCENE_Cell, x, y);
		if (list != null) {
			for (int[] position : list) {
				if (SCENE_Cell[position[0]][position[1]].gloss != CellIcon.SUSPICION)
					_click(position[0], position[1]);
			}
		}
	}

	private void quickStart(int x, int y, HashMap<String, String> map) {
		map.put(x + "," + y, "");
		ArrayList<int[]> list = new ArrayList<int[]>();
		list.add(new int[] { x, y });

		int i = 0;
		while (i < list.size()) {
			int[] position = list.get(i);
			int cx = position[0];
			int cy = position[1];
			fast(cx, cy);

			for (int[] p : Core.getCorrelativeCell(SCENE_Cell, cx, cy)) {

				String key = p[0] + "," + p[1];
				if (map.get(key) != null)
					continue;

				if (SCENE_Cell[p[0]][p[1]].gloss != CellIcon.UNKNOWN) {
					list.add(p);
					map.put(key, "");
				}
			}
			i++;
		}
	}

	private void click(int x, int y) {
		Log.i(TAG, "click(" + x + "," + y + ")");

		if (SCENE_Mode == READY) {
			SCENE_Mode = PLAYING;
			Core.setup(SCENE_Cell, SCENE_MinesCount, x, y);

			_click(x, y);
			quickStart(x, y, new HashMap<String, String>());
		}
		_click(x, y);

		if (SCENE_Cell[x][y].gloss != CellIcon.UNKNOWN
				&& SCENE_Cell[x][y].gloss != CellIcon.SUSPICION)
			fast(x, y);
	}

	private void mark(int x, int y) {
		Log.i(TAG, "mark(" + x + "," + y + ")");

		if (SCENE_Cell[x][y].gloss == CellIcon.SUSPICION) {
			SCENE_Cell[x][y].gloss = CellIcon.UNKNOWN;
			SCORE_SuspicationCount--;
			SCENE_UnknownCount++;

		} else if (SCENE_Cell[x][y].gloss == CellIcon.UNKNOWN) {
			SCENE_Cell[x][y].gloss = CellIcon.SUSPICION;
			SCORE_SuspicationCount++;
			SCENE_UnknownCount--;
		}
		if (SCORE_SuspicationCount == SCENE_MinesCount
				&& SCENE_UnknownCount == 0) {
			done();
		}
	}
}