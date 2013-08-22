package org.varunverma.abapquiz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.varunverma.CommandExecuter.ResultObject;
import org.varunverma.abapquiz.billingutil.IabHelper;
import org.varunverma.abapquiz.billingutil.IabHelper.OnIabSetupFinishedListener;
import org.varunverma.abapquiz.billingutil.IabHelper.QueryInventoryFinishedListener;
import org.varunverma.abapquiz.billingutil.IabResult;
import org.varunverma.abapquiz.billingutil.Inventory;
import org.varunverma.abapquiz.billingutil.Purchase;
import org.varunverma.hanuquiz.Application;
import org.varunverma.hanuquiz.HanuGCMIntentService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GCMIntentService extends HanuGCMIntentService {
	
	@Override
	protected void onMessage(Context context, Intent intent) {
		
		/*
		 * We must first initialize billing helper.
		 * So that we know if this is a free user or paid user !
		 */
		
		// Initialize the application
		Application app = Application.getApplicationInstance();
		app.setContext(context);
				
		// Instantiate billing helper class
		IabHelper billingHelper = IabHelper.getInstance(context, Constants.getPublicKey());

		// Set up
		try {
			
			billingHelper.startSetup(new OnIabSetupFinishedListener(){

				@Override
				public void onIabSetupFinished(IabResult result) {
					
					if (!result.isSuccess()) {
						
						// Log error ! Now I don't know what to do
						Log.w(Application.TAG, result.getMessage());
						Application.getApplicationInstance().setSyncCategory("Free");					
						
					} else {
						
						// Check if the user has purchased premium service			
						// Query for Product Details
						Log.i(Application.TAG, "IAB Initialize Done");
						List<String> productList = new ArrayList<String>();
						productList.add(Constants.getProductKey());
						
						try{
							
							IabHelper.getInstance().queryInventoryAsync(true, productList, new QueryInventoryFinishedListener(){

								@Override
								public void onQueryInventoryFinished(IabResult result, Inventory inv) {
									
									if (result.isFailure()) {
										
										// Log error ! Now I don't know what to do
										Log.w(Application.TAG, result.getMessage());
										Application.getApplicationInstance().setSyncCategory("Free");
										
									} else {
										
										Log.i(Application.TAG, "IAB Inventory Query Done");
										String productKey = Constants.getProductKey();
										
										Purchase item = inv.getPurchase(productKey);
										
										if (item != null) {
											// Has user purchased this premium service ???
											Constants.setPremiumVersion(inv.hasPurchase(productKey));
											
											if(Constants.isPremiumVersion()){
												Application.getApplicationInstance().setSyncCategory("Premium");
											}
											else{
												Application.getApplicationInstance().setSyncCategory("Free");
											}
										}
										else{
											Application.getApplicationInstance().setSyncCategory("Free");
										}
										
									}
									
								}});
							
						}
						catch(Exception e){
							Log.w(Application.TAG, e.getMessage(), e);
							Application.getApplicationInstance().setSyncCategory("Free");
						}
						
					}
					
				}});
			
		} catch (Exception e) {
			Log.w(Application.TAG, e.getMessage(), e);
			Application.getApplicationInstance().setSyncCategory("Free");
		}
		
		IABInitializeDone(intent);
		
	}
	
	private void IABInitializeDone(Intent intent){
		
		String message = intent.getExtras().getString("message");

		if (message.contentEquals("InfoMessage")) {
			// Show Info Message to the User
			showInfoMessage(intent);

		} else {

			ResultObject result = processMessage(intent);

			if (result.getData().getBoolean("ShowNotification")) {
				createNotification(result);
			}
		}
		
	}

	private void showInfoMessage(Intent intent) {
		// Show Info Message
		String subject = intent.getExtras().getString("subject");
		String content = intent.getExtras().getString("content");
		String mid = intent.getExtras().getString("message_id");
		if(mid == null || mid.contentEquals("")){
			mid = "0";
		}
		int id = Integer.valueOf(mid);

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// Create Intent and Set Extras
		Intent notificationIntent = new Intent(this, DisplayFile.class);

		notificationIntent.putExtra("Title", "Info:");
		notificationIntent.putExtra("Subject", subject);
		notificationIntent.putExtra("Content", content);
		notificationIntent.addCategory(subject);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		Notification notification = new NotificationCompat.Builder(this)
										.setContentTitle(subject)
										.setContentText(content)
										.setSmallIcon(R.drawable.ic_launcher)
										.setContentIntent(pendingIntent).build();

		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = subject;
		notification.when = System.currentTimeMillis();

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;

		nm.notify(id, notification);
	}

	private void createNotification(ResultObject result) {
		
		ArrayList<String> quizDescList = result.getData().getStringArrayList("QuizDesc");
		
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		String title;

		int quizzesDownloaded = result.getData().getInt("QuizzesDownloaded");
		if (quizzesDownloaded == 0) {
			title = "New quizzes downloaded.";
		} else {
			title = quizzesDownloaded + " new quizzes have been downloaded";
		}

		Intent notificationIntent = new Intent(this, Main.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
														.setContentTitle(title)
														.setContentInfo(String.valueOf(quizzesDownloaded))
														.setSmallIcon(R.drawable.ic_launcher)
														.setContentIntent(pendingIntent);

		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

		inboxStyle.setBigContentTitle(title);
		inboxStyle.setSummaryText("New quizzes downloaded");

		// Add joke titles
		Iterator<String> i = quizDescList.listIterator();
		while (i.hasNext()) {
			inboxStyle.addLine(i.next());
		}

		// Moves the big view style object into the notification object.
		notifBuilder.setStyle(inboxStyle);

		Notification notification = notifBuilder.build();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = title;
		notification.when = System.currentTimeMillis();

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;

		nm.notify(1, notification);

	}
}