/**
 * 
 */
package org.varunverma.abapquiz;

import org.varunverma.abapquiz.billingutil.IabHelper;
import org.varunverma.abapquiz.billingutil.IabHelper.OnIabPurchaseFinishedListener;
import org.varunverma.abapquiz.billingutil.IabResult;
import org.varunverma.abapquiz.billingutil.Purchase;
import org.varunverma.hanuquiz.Application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author varun
 *
 */
public class ActivatePremiumFeatures extends Activity implements
		OnClickListener, OnIabPurchaseFinishedListener {

	private TextView prodName, prodDesc, prodHelp;
	private Button buy, cancel;
	private IabHelper billingHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.premium_features);
		
		prodName = (TextView) findViewById(R.id.product_name);
		prodDesc = (TextView) findViewById(R.id.product_desc);
		prodHelp = (TextView) findViewById(R.id.product_help);
		
		buy = (Button) findViewById(R.id.buy);
		buy.setOnClickListener(this);
		
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		
	}
	
	@Override
	protected void onStart() {
		
		super.onStart();
		
		billingHelper = IabHelper.getInstance();
		
		if(billingHelper == null){
			finish();
		}
		
		prodName.setText(Constants.getProductTitle());
		prodDesc.setText(Constants.getProductDescription());
		buy.setText(Constants.getProductPrice());
		
		showProductHelp();
		
	}
	
	private void showProductHelp() {
		
		String help = "By purchasing this product, you will have access to " +
				"unlimitied number of Quizzes of all levels.\n" +
				"This is a one time purchase only.";
		
		prodHelp.setText(help);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.buy:
			buy();
			break;

		case R.id.cancel:
			finish();
			break;
		}
		
	}

	private void buy() {
		
		String devPayLoad = ""; //Application.get_instance().getEmail();
		billingHelper.launchPurchaseFlow(this, Constants.getProductKey(), 22, this, devPayLoad);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (billingHelper.handleActivityResult(requestCode, resultCode, data)) {
			// Nothing
		} else {
			// Handle
		}

	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase info) {
		
		if (result.isFailure()) {
			Log.d(Application.TAG, "Error purchasing: " + result);
			return;
		}
		
		if(info.getSku().contentEquals(Constants.getProductKey())){
			// Purchase was success
			Constants.setPremiumVersion(true);
		}
		
	}
}