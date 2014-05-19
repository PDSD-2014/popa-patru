package com.patrupopa.wordscocktail;




import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.os.Build;

public class FinalScore extends ActionBarActivity {

	private int _wordCount;
	private int _score;
	private Bundle _bun;
	private String _words;
	private String _badWords;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState == null )
		{
			Intent intent = getIntent();
			_bun = intent.getExtras();
			_words = _bun.getString("words");
			_badWords = _bun.getString("badWords");
			_score =_bun.getInt("score");
			_wordCount = _bun.getInt("wordCount");
		}
		setContentView(R.layout.activity_final_score);
		
		Button b = (Button) findViewById(R.id.close_score);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		TextView t = (TextView) findViewById(R.id.score_points);
		t.setText( "" + _score );

		t = (TextView) findViewById(R.id.score_words);
		t.setText("" + _wordCount );
		
		//and now the wordlist
		ViewGroup foundVG = initializeScrollView(R.id.word_list);
		drawWords(foundVG);
		
	
	}

	private ViewGroup initializeScrollView(int wordList) {
		// TODO Auto-generated method stub
		ScrollView sv = (ScrollView) findViewById(wordList);
		sv.setScrollBarStyle(sv.SCROLLBARS_OUTSIDE_INSET);

		ViewGroup.LayoutParams llLp = new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);

		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(llLp);
		ll.setOrientation(LinearLayout.VERTICAL);
		sv.addView(ll);

		return ll;
	}
	private void drawWords( ViewGroup vg ) {
			LinearLayout.LayoutParams text1Lp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
			TextView tv1 = new TextView(this);
			tv1.setGravity(Gravity.LEFT);
			tv1.setLayoutParams(text1Lp);
			tv1.setTextSize(16);
			tv1.setTextColor(0xff00ffff);
			tv1.setText(_words);

			LinearLayout.LayoutParams text2Lp = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					(float) 1.0);
			TextView tv2 = new TextView(this);
			tv2.setGravity(Gravity.RIGHT);
			tv2.setLayoutParams(text2Lp);
			tv2.setTextSize(16);
			tv2.setTextColor(0xffff0000);
			tv2.setText(_badWords);
		
			LinearLayout ll = new LinearLayout(this);
			ll.setOrientation(LinearLayout.HORIZONTAL);

			ll.addView(tv1);
			ll.addView(tv2);
			
			vg.addView(ll, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.final_score, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_final_score,
					container, false);
			return rootView;
		}
	}

}
