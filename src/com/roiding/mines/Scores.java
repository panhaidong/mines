package com.roiding.mines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Scores extends Activity {
	public final static int DELETE = 0;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score);

		ArrayList<long[]> list = (ArrayList<long[]>) Persist.load(this,
				Core.SCORE_FILE);
		ListView lv = (ListView) findViewById(R.id.listview_topten);

		if (list != null && list.size() > 0) {
			Collections.sort(list, new Comparator<long[]>() {
				public int compare(long[] object1, long[] object2) {
					return (int) (object1[1] - object2[1]);
				}
			});
			
			lv.setAdapter(new SimpleAdapter(this, getScore(list),
					android.R.layout.simple_list_item_2, new String[] {
							"score", "time", }, new int[] { android.R.id.text1,
							android.R.id.text2 }));
		}

	}

	private List<Map<String, String>> getScore(ArrayList<long[]> list) {
		ArrayList<Map<String, String>> scoreList = new ArrayList<Map<String, String>>();
		for (long[] l : list) {
			Date d = new Date(l[0]);
			Map<String, String> map = new HashMap<String, String>();
			map.put("time", d.toLocaleString());
			map.put("score", getScore(l[1]));
			scoreList.add(map);
		}
		return scoreList;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, DELETE, 0, R.string.menu_delete).setShortcut('1', 'd')
				.setIcon(android.R.drawable.ic_menu_delete);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE:
			this.deleteFile(Core.SCORE_FILE);
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private String getScore(long l) {
		int temp = (int) (l / 1000);
		int minitues = temp / 60;
		int seconds = temp % 60;

		String r = "";
		if (minitues <= 9)
			r = r + "0";
		r = r + minitues + ":";
		if (seconds <= 9)
			r = r + "0";
		r = r + seconds;

		return r;
	}
}