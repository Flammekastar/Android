package com.example.hangman;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameActivity extends Activity {


	private String[] words;
	private Random rand;
	private String currWord;
	private LinearLayout wordLayout;
	private TextView[] charViews;
	private GridView letters;
	private LetterAdapter ltrAdapt;
	private ImageView[] bodyParts;
	private int numParts=6;
	private int currPart;
	private int numChars;
	private int numCorr;
	private int wins, losses;
	private AlertDialog helpAlert;
	public static final String PREFS_NAME = "MyPrefsFile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		Resources res = getResources();
		words = res.getStringArray(R.array.words);

		rand = new Random();
		currWord="";

		wordLayout = (LinearLayout)findViewById(R.id.word);

		letters = (GridView)findViewById(R.id.letters);

		bodyParts = new ImageView[numParts];
		bodyParts[0] = (ImageView)findViewById(R.id.head);
		bodyParts[1] = (ImageView)findViewById(R.id.body);
		bodyParts[2] = (ImageView)findViewById(R.id.arm1);
		bodyParts[3] = (ImageView)findViewById(R.id.arm2);
		bodyParts[4] = (ImageView)findViewById(R.id.leg1);
		bodyParts[5] = (ImageView)findViewById(R.id.leg2);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//Henter spilleren sin vinn/tap statistikk
	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    wins = settings.getInt("wins", 0);
	    losses = settings.getInt("losses", 0);
		//Starter spillet
		playGame();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_help:
			showHelp();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//play a new game
	private void playGame(){

		String newWord = words[rand.nextInt(words.length)];
		//Sørger for at samme ord ikke velges to ganger på rad.
		while(newWord.equals(currWord)) newWord = words[rand.nextInt(words.length)];
		currWord = newWord;

		charViews = new TextView[currWord.length()];

		wordLayout.removeAllViews();

		for(int c=0; c<currWord.length(); c++){
			charViews[c] = new TextView(this);
			charViews[c].setText(""+currWord.charAt(c));
			charViews[c].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
					LayoutParams.WRAP_CONTENT));
			charViews[c].setGravity(Gravity.CENTER);
			charViews[c].setTextColor(Color.WHITE);
			charViews[c].setBackgroundResource(R.drawable.letter_bg);

			wordLayout.addView(charViews[c]);
		}


		ltrAdapt=new LetterAdapter(this);
		letters.setAdapter(ltrAdapt);
		currPart=0;
		numChars=currWord.length();
		numCorr=0;

		for(int p=0; p<numParts; p++){
			bodyParts[p].setVisibility(View.INVISIBLE);
		}
	}


	public void letterPressed(View view){

		String ltr=((TextView)view).getText().toString();
		char letterChar = ltr.charAt(0);

		view.setEnabled(false);
		view.setBackgroundResource(R.drawable.letter_down);

		boolean correct=false;
		for(int k=0; k<currWord.length(); k++){ 
			if(currWord.charAt(k)==letterChar){
				correct=true;
				numCorr++;
				charViews[k].setTextColor(Color.BLACK);
			}
		}

		if(correct){
			if(numCorr==numChars){

				disableBtns();
				//Oppdatere score
				wins += 1;
			    SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putInt("wins", wins);
			    editor.commit();
			    //Vise spilleren at han/hun har vunnet og gi videre valg
				AlertDialog.Builder winBuild = new AlertDialog.Builder(this);
				winBuild.setTitle(getString(R.string.congratz));
				winBuild.setMessage(getString(R.string.win) + "\n\n" + getString(R.string.answer) + "\n\n" +currWord);
				winBuild.setPositiveButton(getString(R.string.playagain), 
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						GameActivity.this.playGame();
					}});
				winBuild.setNegativeButton(getString(R.string.exit), 
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						GameActivity.this.finish();
					}});
				winBuild.show();
			}
		}
		else if(currPart<numParts){
			bodyParts[currPart].setVisibility(View.VISIBLE);
			currPart++;
		}
		else{
			//spilleren har tapt
			disableBtns();
			//Oppdatere score
			losses += 1;
		    SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putInt("losses", losses);
		    editor.commit();
			//spill igjen eller exit meny
			AlertDialog.Builder loseBuild = new AlertDialog.Builder(this);
			loseBuild.setTitle(getString(R.string.sorry));
			loseBuild.setMessage(getString(R.string.lose) + "\n\n" + getString(R.string.theanswer) + "\n\n" + currWord);
			loseBuild.setPositiveButton(getString(R.string.playagain), 
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					GameActivity.this.playGame();
				}});
			loseBuild.setNegativeButton(getString(R.string.exit), 
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					GameActivity.this.finish();
				}});
			loseBuild.show();
		}
	}

	public void disableBtns(){	
		int numLetters = letters.getChildCount();
		for(int l=0; l<numLetters; l++){
			letters.getChildAt(l).setEnabled(false);
		}
	}

	//Viser hjelp/regler
	public void showHelp(){
		AlertDialog.Builder helpBuild = new AlertDialog.Builder(this);
		helpBuild.setTitle(getString(R.string.help));
		helpBuild.setMessage(getString(R.string.helptext) + "\n\n"
				+ getString(R.string.helptext2));
		helpBuild.setPositiveButton(getString(R.string.ok), 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				helpAlert.dismiss();
			}});
		helpAlert = helpBuild.create();
		helpBuild.show();
	}

}
