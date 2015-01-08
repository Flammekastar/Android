package com.example.hangman;

import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;


public class MainActivity extends Activity implements OnClickListener {
	
	private Locale myLocale;
	private AlertDialog helpAlert;
	public static final String PREFS_NAME = "MyPrefsFile";
	private int wins, losses;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button playBtn = (Button)findViewById(R.id.playBtn);
        playBtn.setOnClickListener(this);
        
        Button settingsBtn = (Button)findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(this);
        
       ImageButton norbutton = (ImageButton)findViewById(R.id.norbutton);
       norbutton.setOnClickListener(this);
       
       ImageButton ukbutton = (ImageButton)findViewById(R.id.ukbutton);
       ukbutton.setOnClickListener(this);
       
       updateScore();
    }
    
    
    public void onClick(View view) {
      //handle clicks
    	if (view.getId() == R.id.playBtn) {
    		  Intent playIntent = new Intent(this, GameActivity.class);
    		  this.startActivity(playIntent);
    		}
    	if (view.getId() == R.id.settingsBtn) {
    	      updateScore();
    		  showHelp();
    		}
    	if (view.getId() == R.id.norbutton) {
            setLocale("no");
  		}
    	if (view.getId() == R.id.ukbutton) {
            setLocale("en");
  		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void setLocale(String lang) { 
    	myLocale = new Locale(lang); 
    	Resources res = getResources(); 
    	DisplayMetrics dm = res.getDisplayMetrics(); 
    	Configuration conf = res.getConfiguration(); 
    	conf.locale = myLocale; 
    	res.updateConfiguration(conf, dm); 
    	Intent refresh = new Intent(this, MainActivity.class); 
    	startActivity(refresh); 
    	} 
    
	//Viser hjelp/regler
	public void showHelp(){
		AlertDialog.Builder helpBuild = new AlertDialog.Builder(this);
		helpBuild.setTitle(getString(R.string.help));
		helpBuild.setMessage(getString(R.string.helptext) + "\n\n"
			+ getString(R.string.helptext2) + "\n\n" + getString(R.string.wins)+ " " + wins +  "\n\n" + getString(R.string.losses) + " "  + losses);
		helpBuild.setPositiveButton(getString(R.string.ok), 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				helpAlert.dismiss();
			}});
		helpAlert = helpBuild.create();
		helpBuild.show();
	}
	
	//Hjelpemetode for å huske brukerens score.
	public void updateScore() {
	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    wins = settings.getInt("wins", 0);
	    losses = settings.getInt("losses", 0);
	}
}
