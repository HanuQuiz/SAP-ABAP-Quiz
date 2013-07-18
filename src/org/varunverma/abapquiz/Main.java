package org.varunverma.abapquiz;

import java.util.ArrayList;
import java.util.List;

import org.varunverma.CommandExecuter.Invoker;
import org.varunverma.CommandExecuter.ProgressInfo;
import org.varunverma.CommandExecuter.ResultObject;
import org.varunverma.abapquiz.billingutil.IabHelper;
import org.varunverma.abapquiz.billingutil.IabHelper.OnIabSetupFinishedListener;
import org.varunverma.abapquiz.billingutil.IabHelper.QueryInventoryFinishedListener;
import org.varunverma.abapquiz.billingutil.IabResult;
import org.varunverma.abapquiz.billingutil.Inventory;
import org.varunverma.abapquiz.billingutil.Purchase;
import org.varunverma.hanuquiz.Application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

public class Main extends Activity implements Invoker,
		OnIabSetupFinishedListener, QueryInventoryFinishedListener {

	private Application app;
	private IabHelper billingHelper;
	private TextView statusView;
	private boolean appStarted = false;
	private boolean firstUse = false;
	private boolean wait = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		statusView = (TextView) findViewById(R.id.status);
			
		// Create application instance.
		app = Application.getApplicationInstance();
		
		// Set application context.
		app.setContext(this);
		
		// Tracking.
        EasyTracker.getInstance().activityStart(this);
        
		// Accept my Terms
		if (!app.isEULAAccepted()) {
			
			// Show EULA.
			Intent eula = new Intent(Main.this, Eula.class);
			Main.this.startActivityForResult(eula, Application.EULA);
			
		} else {
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
        app.registerAppForGCM();
		
		// Instantiate billing helper class
		billingHelper = IabHelper.getInstance(this, Constants.getPublicKey());
		
		// Set up
		billingHelper.startSetup(this);

	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {

		case Application.EULA:
			if (!app.isEULAAccepted()) {
				finish();
			} else {
				// Start Main Activity
				startMainActivity();
			}
			break;
			
		case 900:
			
			startApp();
			
			// Kill this activity.
			Log.i(Application.TAG, "Kill Main Activity");
			Main.this.finish();
			
			break;
		}
	}
	
	private void initializeApp(){
		
		// Initialize app...
		if (app.isThisFirstUse()) {
			// This is the first run !
			
			firstUse = true;
			wait = true;
			
			statusView.setText("Initializing app for first use.\nPlease wait, this may take a while");
			app.initializeAppForFirstUse(this);		

		} else {
			
			// Regular use. Initialize App
			statusView.setText("Initializing application...");
			app.initialize(this);
			
			// Check if this is upgrade
			int oldFrameworkVersion = app.getOldFrameworkVersion();
			int newFrameworkVersion = app.getNewFrameworkVersion();
			
			int oldAppVersion = app.getOldAppVersion();
			int newAppVersion;
			try {
				newAppVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			} catch (NameNotFoundException e) {
				newAppVersion = 0;
				Log.e(Application.TAG, e.getMessage(), e);
			}
			
			if(newAppVersion > oldAppVersion ||	newFrameworkVersion > oldFrameworkVersion){
				
				app.updateVersion();
				wait = true;
			}
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
		
		if(!wait){
			
			startApp();
			
			// Kill this activity.
			Log.i(Application.TAG, "Kill Main Activity");
			Main.this.finish();
			
		}
		else{
			
			if(firstUse){
				showHelp();
			}
			else{
				showWhatsNew();
			}
		}
		
	}

	private void showHelp() {
		
		Intent help = new Intent(Main.this, DisplayFile.class);
		help.putExtra("File", "help.html");
		help.putExtra("Title", "Help: ");
		Main.this.startActivityForResult(help, 900);
		
	}

	private void showWhatsNew() {
		
		Intent newFeatures = new Intent(Main.this, DisplayFile.class);
		newFeatures.putExtra("File", "NewFeatures.html");
		newFeatures.putExtra("Title", "New Features: ");
		Main.this.startActivityForResult(newFeatures, 900);
	}

	private void startApp() {
		
		if(appStarted){
			return;
		}
		
		appStarted = true;
		
		// Start the Quiz List
		Log.i(Application.TAG, "Start Quiz List");
		Intent start = new Intent(Main.this, QuizList.class);
		Main.this.startActivity(start);

	}

	@Override
	public void ProgressUpdate(ProgressInfo progress) {
		
		String message = progress.getProgressMessage();
		if(message != null && !message.contentEquals("")){
			
			if(message.contentEquals("Show UI")){
				if(!wait){
					startApp();
				}
			}
			
		}
		
	}

	@Override
	public void onIabSetupFinished(IabResult result) {
		
		if (!result.isSuccess()) {
			
			// Log error ! Now I don't know what to do
			Log.w(Application.TAG, result.getMessage());
			
			app.setSyncCategory("Free");
			
			// Initialize the app
			initializeApp();
			
			
		} else {
			
			// Check if the user has purchased premium service			
			// Query for Product Details
			
			List<String> productList = new ArrayList<String>();
			productList.add(Constants.getProductKey());
			billingHelper.queryInventoryAsync(true, productList, this);
		}
		
	}

	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inv) {
		
		if (result.isFailure()) {
			
			// Log error ! Now I don't know what to do
			Log.w(Application.TAG, result.getMessage());
			
			app.setSyncCategory("Free");
			
		} else {
			
			String productKey = Constants.getProductKey();
			
			Purchase item = inv.getPurchase(productKey);
			
			if (item != null) {
				// Has user purchased this premium service ???
				Constants.setPremiumVersion(inv.hasPurchase(productKey));
				
				if(Constants.isPremiumVersion()){
					app.setSyncCategory("Premium");
				}
				else{
					app.setSyncCategory("Free");
				}
			}
			else{
				app.setSyncCategory("Free");
			}
			
			Constants.setProductTitle(inv.getSkuDetails(productKey).getTitle());
			Constants.setProductDescription(inv.getSkuDetails(productKey).getDescription());
			Constants.setProductPrice(inv.getSkuDetails(productKey).getPrice());
		}
		
		// Initialize the app
		initializeApp();
		
	}
	
	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(this);
	}
	
	@Override
	protected void onDestroy(){
		
		if(Constants.isPremiumVersion()){
			billingHelper.dispose();
		}
		super.onDestroy();
	}

}