package org.varunverma.abapquiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.varunverma.CommandExecuter.Invoker;
import org.varunverma.CommandExecuter.ProgressInfo;
import org.varunverma.CommandExecuter.ResultObject;
import org.varunverma.hanuquiz.Application;
import org.varunverma.hanuquiz.Quiz;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class Main extends Activity implements Invoker, OnNavigationListener, OnItemClickListener {
	
	private Application app;
	private ProgressDialog dialog;
	private boolean firstUse;
	private boolean appClosing;
	private ActionBar actionBar;
	private ListView listView;
	private QuizListAdapter adapter;
	private List<Quiz> quizList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		// Create application instance.
		app = Application.getApplicationInstance();
		
		// Set application context.
		app.setContext(this);
		
		listView = (ListView) findViewById(R.id.myList);
		listView.setOnItemClickListener(this);
		
		// Get Action Bar
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	}
	
	@Override
	protected void onStart() {
		
		super.onStart();
		
		// Accept my Terms
		app.setEULAResult(true);	//TODO Remove in production version
        if(!app.isEULAAccepted()){
        	// Show EULA.
        	Intent eula = new Intent(Main.this, DisplayFile.class);
        	eula.putExtra("File", "eula.html");
			eula.putExtra("Title", "End User License Aggrement: ");
			Main.this.startActivityForResult(eula, Application.EULA);
        }
        else{
        	// Start the Main Activity
       		startMainActivity();
        }
        
	}
	
	private void startMainActivity() {
		
		// Register application.
        //app.registerAppForGCM();
		
		// Initialize app...
		if (app.isThisFirstUse()) {
			// This is the first run !

			String message = "Please wait while the application is initialized for first usage...";
			dialog = ProgressDialog.show(Main.this, "", message, true);
			app.initializeAppForFirstUse(this);
			firstUse = true;
			
		} else {
			firstUse = false;
			app.initialize(this);

			// Start Main Activity.
			startMainScreen();
		}
	}

	private void startMainScreen() {
		
		// Show Ad.
		AdRequest adRequest = new AdRequest();
		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		adRequest.addTestDevice("E16F3DE5DF824FE222EDDA27A63E2F8A");	// My S2 Mobile
		//TODO - Pramodh to find his mobile guid and add it here.
		AdView adView = (AdView) findViewById(R.id.adView);
		
		adView.loadAd(adRequest);
		
		// Set the action bar items
		 ArrayAdapter<CharSequence> ABAdapter = ArrayAdapter.createFromResource(this, R.array.DifficultyLevel, android.R.layout.simple_spinner_item);
		 ABAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     
	     actionBar.setListNavigationCallbacks(ABAdapter, this);
	     
	     quizList = new ArrayList<Quiz>();
	     quizList.addAll(app.getQuizListByLevel(1));	// 1 => Easy
	     Collections.sort(quizList, Quiz.SortByID);
	     
	     // Adapter for the Quiz List
	     adapter = new QuizListAdapter(this, R.layout.quiz_list_row, quizList);
	     listView.setAdapter(adapter);
	     
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()){
		
		case R.id.help:
			Intent help = new Intent(Main.this, DisplayFile.class);
			help.putExtra("File", "help.html");
			help.putExtra("Title", "Help: ");
			Main.this.startActivity(help);
			return true;
			
		case R.id.about:
			Intent info = new Intent(Main.this, DisplayFile.class);
			info.putExtra("File", "about.html");
			info.putExtra("Title", "About: ");
			Main.this.startActivity(info);
			return true;
			
		case R.id.settings:
			return true;
			
		default:
			return false;
				
		}
	}
	
	@Override
	protected void onDestroy(){
		appClosing = true;
		app.close();
		super.onDestroy();
	}

	@Override
	public void NotifyCommandExecuted(ResultObject result) {
		
		if(appClosing && result.getResultStatus() == ResultObject.ResultStatus.CANCELLED){
			app.close();
		}
		
		if(!result.isCommandExecutionSuccess()){
			
			if(result.getResultCode() == 420){
				// Application is not registered.
				String message = "This application is not registered with Hanu-Quiz.\n" +
						"Please inform the developer about this error.";
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
				alertDialogBuilder
					.setTitle("Application not registered !")
					.setMessage(message)
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog,int id) {
													Main.this.finish();	}})
					.create()
					.show();
			}
			
			//Toast.makeText(getApplicationContext(), result.getErrorMessage(), Toast.LENGTH_LONG).show();
		}
		
		if(firstUse){
			
			if(dialog.isShowing()){
				dialog.dismiss();
				startMainScreen();	// Start Main Activity.
			}
		}
		
	}

	@Override
	public void ProgressUpdate(ProgressInfo progress) {
		// Show UI.
		if (progress.getProgressMessage().contentEquals("Show UI")) {

			if (dialog.isShowing()) {

				dialog.dismiss();
				startMainScreen(); // Start Main Activity.
			}
		}

		// Update UI.
		if (progress.getProgressMessage().contentEquals("Update UI")) {
			// TODO - Varun to think how to do this
		}
		
	}

	@Override
	public boolean onNavigationItemSelected(int pos, long id) {
		/* TODO - Varun to implement
		 * On click of toolbar spinner item 
		 */
		
		quizList.clear();
		quizList.addAll(app.getQuizListByLevel(pos + 1));	// 1 => Easy, 2 => Medium, 3 => Difficult
		Collections.sort(quizList, Quiz.SortByID);
		adapter.notifyDataSetChanged();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		/*
		 * TODO - Varun to do
		 * Load Questions for this quiz
		 * Start new activity
		 */
		
		Quiz quiz = quizList.get(pos);
		
		Intent startQuiz = new Intent(Main.this, StartQuiz.class);
		startQuiz.putExtra("QuizId", quiz.getQuizId());
		startActivity(startQuiz);
		
	}

}