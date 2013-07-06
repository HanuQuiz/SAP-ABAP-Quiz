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
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class Main extends Activity implements Invoker,
		OnIabSetupFinishedListener, QueryInventoryFinishedListener {

	private Application app;
	private IabHelper billingHelper;
	
	private boolean appInitialized, billingInitialized;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		appInitialized = billingInitialized = false;
		
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
		
		// Instantiate billing helper class
		billingHelper = IabHelper.getInstance(this, Constants.getPublicKey());
		
		// Set up
		billingHelper.startSetup(this);
		
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
		
		appInitialized = true;
		
		if(billingInitialized){
			startApp();
		}		
		
	}

	private void startApp() {
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

	@Override
	public void onIabSetupFinished(IabResult result) {
		
		if (!result.isSuccess()) {
			
			// Log error ! Now I don't know what to do
			Log.w(Application.TAG, result.getMessage());
			
			billingInitialized = true;
			if(appInitialized){
				startApp();
			}
			
			
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
			
		} else {
			
			String productKey = Constants.getProductKey();
			
			Purchase item = inv.getPurchase(productKey);
			
			if (item != null) {
				// Has user purchased this premium service ???
				Constants.setPremiumVersion(inv.hasPurchase(productKey));
			}
			
			Constants.setProductTitle(inv.getSkuDetails(productKey).getTitle());
			Constants.setProductDescription(inv.getSkuDetails(productKey).getDescription());
			Constants.setProductPrice(inv.getSkuDetails(productKey).getPrice());
		}
		
		billingInitialized = true;
		if(appInitialized){
			startApp();
		}
		
	}

}