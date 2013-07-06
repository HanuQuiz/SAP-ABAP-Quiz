package org.varunverma.abapquiz;

import org.varunverma.CommandExecuter.Invoker;
import org.varunverma.CommandExecuter.ProgressInfo;
import org.varunverma.CommandExecuter.ResultObject;
import org.varunverma.hanuquiz.Application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class Main extends Activity implements Invoker{
	
	private Application app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		// Create application instance.
		app = Application.getApplicationInstance();
		
		// Set application context.
		app.setContext(this);
		
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	
    		return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
	
	private void startMainActivity() {
		
		// Register application.
        //app.registerAppForGCM();
		
		// Initialize app...
		if (app.isThisFirstUse()) {
			// This is the first run !
			app.initializeAppForFirstUse(this);
			
		} else {
			app.initialize(this);
		}
	}

	@Override
	public void NotifyCommandExecuted(ResultObject result) {
		
		Log.i(Application.TAG, "Command Execution completed");
		
		if (!result.isCommandExecutionSuccess()) {

			if (result.getResultCode() == 420) {
				// Application is not registered.
				String message = "This application is not registered with Hanu-Quiz.\n"
						+ "Please inform the developer about this error.";
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
				
				alertDialogBuilder
					.setTitle("Application not registered !")
					.setMessage(message).setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Main.this.finish();
						}
					}).
					create().
					show();
			}
		}
		
		// Start the Quiz List
		Log.i(Application.TAG, "Start Quiz List");
		Intent start = new Intent(Main.this, QuizList.class);
		Main.this.startActivity(start);
		
		// Kill this activity.
		Log.i(Application.TAG, "Kill Main Activity");
		Main.this.finish();
		
	}

	@Override
	public void ProgressUpdate(ProgressInfo progress) {
		
	}

}