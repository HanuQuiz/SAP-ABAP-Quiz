package org.varunverma.abapquiz;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Main extends Activity implements Invoker, OnNavigationListener {
	
	private Application app;
	private ProgressDialog dialog;
	private boolean firstUse;
	private boolean appClosing;
	private ActionBar actionBar;
	private ListView listView;
	private ArrayAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		// Create application instance.
		app = Application.getApplicationInstance();
		
		// Set application context.
		app.setContext(this);
		
		listView = (ListView) findViewById(R.id.myList);
		adapter = new ArrayAdapter<Quiz>(this,R.layout.quiz_list_row);
		listView.setAdapter(adapter);
		
		// Get Action Bar
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	}
	
	@Override
	protected void onStart() {
		
		super.onStart();
		
		// Accept my Terms
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
        app.registerAppForGCM();
		
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
		// TODO - Varun to design
		
		// Set the actionbar items
		 ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.DifficultyLevel, android.R.layout.simple_spinner_item);
	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     
	     actionBar.setListNavigationCallbacks(adapter, this);
	     
	     loadQuizListByLevel(1);	// 1 => Easy
	     
	}

	private void loadQuizListByLevel(int level) {
		// Load Quiz List by Level
		List<Quiz> quizList = app.getQuizListByLevel(level);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
		// TODO Auto-generated method stub
		return true;
	}

}
