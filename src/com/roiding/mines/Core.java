package com.roiding.mines;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Core {
	public static final String PREVIOUS_GAME_FILE = "MINES-PREVIOUS_GAME";
	public static final String SCORE_FILE = "MINES-TOPTEN";
	public static HashMap<String, Object> PREVIOUS_GAME_MAP = null;
	public static final int[] FONT_COLOR = new int[] { 0xFF0000FF, 0xFF00FF00,
			0xFFFF0000, 0xFFFFFF00, 0xFFFF00FF, 0xFF00FFFF, 0xFF0FF0,
			0xFFF0F0F0 };

	public static void setup(Cell[][] field, int count, int currentX,
			int currentY) {
		if (field.length == 0 || field[0].length == 0)
			return;

		int cellCount = field.length * field[0].length;
		if (cellCount <= count)
			return;

		int needFill = 0;
		boolean judge1 = (currentX == 0) || (currentX == field.length);
		boolean judge2 = (currentY == 0) || (currentY == field[0].length);
		if (judge1 && judge2) {
			needFill = 4;
		} else if (judge1 || judge2) {
			needFill = 6;
		} else {
			needFill = 9;
		}

		ArrayList<Integer> list = new ArrayList<Integer>();

		for (int i = 0; i < cellCount - count - needFill; i++) {
			list.add(CellIcon.EMPTY);
		}

		for (int i = 0; i < count; i++) {
			list.add(Integer.MIN_VALUE);
		}

		Collections.shuffle(list);

		for (int i = 0; i < needFill; i++) {
			list.add(CellIcon.EMPTY);
		}

		ArrayList<int[]> positionList = getCorrelativeCell(field.length,
				field[0].length, currentX, currentY);

		positionList.add(new int[] { currentX, currentY });

		int p = list.size() - 1;
		for (int[] position : positionList) {
			int x = position[0];
			int y = position[1];

			int index1 = y * field.length + x;

			int index2 = p;
			Collections.swap(list, index1, index2);
			p--;
		}

		for (int x = 0; x < field.length; x++) {
			for (int y = 0; y < field[0].length; y++) {

				int c = list.get(y * field.length + x);

				if (c < 0) {
					field[x][y].fact = c;
					stat(field, getCorrelativeCell(field, x, y));
				}
			}
		}
		for (int x = 0; x < field.length; x++) {
			for (int y = 0; y < field[0].length; y++) {
				if (field[x][y].fact < 0)
					field[x][y].fact = CellIcon.SUSPICION;
			}
		}
	}

	public static ArrayList<int[]> getCorrelativeCell(int arrayX, int arrayY,
			int currentX, int currentY) {
		Object[][] array = new Object[arrayX][arrayY];
		return getCorrelativeCell(array, currentX, currentY);
	}

	private static int[] wasai(int x, int y) {
		int[] a = new int[2];
		a[0] = x;
		a[1] = y;
		return a;
	}

	public static ArrayList<int[]> getCorrelativeCell(Object[][] array, int x,
			int y) {
		ArrayList<int[]> list = new ArrayList<int[]>();
		if ((x - 1 >= 0) && y - 1 >= 0)
			list.add(wasai(x - 1, y - 1));

		if ((y - 1 >= 0))
			list.add(wasai(x, y - 1));

		if ((x + 1 <= array.length - 1) && (y - 1 >= 0))
			list.add(wasai(x + 1, y - 1));

		if ((x - 1 >= 0))
			list.add(wasai(x - 1, y));

		if ((x + 1 <= array.length - 1))
			list.add(wasai(x + 1, y));

		if ((x - 1 >= 0) && (y + 1 <= array[0].length - 1))
			list.add(wasai(x - 1, y + 1));

		if ((y + 1 <= array[0].length - 1))
			list.add(wasai(x, y + 1));

		if ((x + 1 <= array.length - 1) && (y + 1 <= array[0].length - 1))
			list.add(wasai(x + 1, y + 1));

		return list;
	}

	private static void stat(Cell[][] field, ArrayList<int[]> list) {
		for (int[] position : list)
			field[position[0]][position[1]].fact++;
	}

	public static ArrayList<int[]> isClear(Cell[][] array, int x, int y) {
		ArrayList<int[]> list = getCorrelativeCell(array, x, y);
		int count = array[x][y].gloss;
		int checkCount = 0;
		for (int[] position : list) {
			if (array[position[0]][position[1]].gloss == CellIcon.SUSPICION)
				checkCount++;
		}
		if (checkCount == count)
			return list;
		else
			return null;
	}
}

@SuppressWarnings("serial")
class Cell implements Serializable {
	public int gloss;
	public int fact;
}

class CellIcon {
	public static final int EMPTY = 0;
	public static final int ONE = 1;
	public static final int TWO = 2;
	public static final int THREE = 3;
	public static final int FOUR = 4;
	public static final int FIVE = 5;
	public static final int SIX = 6;
	public static final int SEVEN = 7;
	public static final int EIGHT = 8;
	public static final int SUSPICION = 9;
	public static final int CURRENT = 10;
	public static final int UNKNOWN = 11;
	public static final int BOMB = 12;
}

class FaceView {
	public static final int NORMAL = 0;
	public static final int BE_CAREFUL = 1;
}
