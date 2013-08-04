package org.varunverma.abapquiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.varunverma.abapquiz.billingutil.IabHelper;
import org.varunverma.hanuquiz.Application;
import org.varunverma.hanuquiz.Quiz;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;

public class QuizList extends Activity implements OnNavigationListener, OnItemClickListener, Callback {
	
	private Application app;
	private ActionBar actionBar;
	private ListView listView;
	private QuizListAdapter adapter;
	private List<Quiz> quizList;
	private int level;
	private ActionMode actionMode;
	
	private OnClickListener clickHandler = new OnClickListener(){

		@Override
		public void onClick(View view) {
			
			if(view.getId() == R.id.status){
				// Show CAB
				if(actionMode == null){
					actionMode = startActionMode(QuizList.this);
				}
				
				int position = Integer.valueOf((String) view.getTag());
				boolean itemChecked = listView.getCheckedItemPositions().get(position);
				if(itemChecked){
					listView.setItemChecked(position, false);
				}
				else{
					listView.setItemChecked(position, true);
				}
				// TODO - Varun to fix the list selection issue
				/*
				 * TODO - Pramodh
				 * Pramodh to code the following
				 * a) Remember the positions that are selected
				 * b) Use them to reset the quiz - see other to do items
				 */
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.quiz_list);
		
		// Create application instance.
		app = Application.getApplicationInstance();
		
		// Set application context.
		app.setContext(this);
		
		listView = (ListView) findViewById(R.id.myList);
		listView.setItemsCanFocus(true);
		listView.setOnItemClickListener(this);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		// Get Action Bar
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		// Tracking.
        EasyTracker.getInstance().activityStart(this);

		// Start the UI
		showUI();

	}
	
	@Override
	public void onStop() {
		
		super.onStop();
		
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(this);
	}
	
	private void showUI() {
		
		// Show Ad.
		if(!Constants.isPremiumVersion()){
			
			AdRequest adRequest = new AdRequest();
			adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
			adRequest.addTestDevice("E16F3DE5DF824FE222EDDA27A63E2F8A");	// My S2 Mobile
			adRequest.addTestDevice("06FE63303C3DA13C859515930A396C91");	// Pramodh's mobile
			AdView adView = (AdView) findViewById(R.id.adView);
			
			adView.loadAd(adRequest);
			
		}
		
		// Set the action bar items
		 ArrayAdapter<CharSequence> ABAdapter = ArrayAdapter.createFromResource(this, R.array.DifficultyLevel, android.R.layout.simple_spinner_item);
		 ABAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     
	     actionBar.setListNavigationCallbacks(ABAdapter, this);
	     
	     quizList = new ArrayList<Quiz>();
	     level = 1;
	     loadQuizByLevel();
	     
	     // Adapter for the Quiz List
	     adapter = new QuizListAdapter(this, R.layout.quiz_list_row, quizList);
	     adapter.setViewClickListener(clickHandler);
	     listView.setAdapter(adapter);
	     
	}

	private void loadQuizByLevel() {
		
		quizList.clear();
		quizList.addAll(app.getQuizListByLevel(level));	// 1 => Easy
		
		// Remove the premium content if any
		if(!Constants.isPremiumVersion()){
			
			Iterator<Quiz> iterator = quizList.iterator();
			while(iterator.hasNext()){
				
				if(iterator.next().getSyncTags().contains("Free")){
					// Nothing to do
				}
				else{
					iterator.remove();
				}
				
			}
			
		}
		
	    Collections.sort(quizList, Quiz.SortByID);
		
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
			Intent help = new Intent(QuizList.this, DisplayFile.class);
			help.putExtra("File", "help.html");
			help.putExtra("Title", "Help: ");
			QuizList.this.startActivity(help);
			return true;
			
		case R.id.about:
			Intent info = new Intent(QuizList.this, DisplayFile.class);
			info.putExtra("File", "about.html");
			info.putExtra("Title", "About: ");
			QuizList.this.startActivity(info);
			return true;
			
		default:
			return false;
				
		}
	}
	
	@Override
	protected void onDestroy(){
		
		// Close billing helper
		try{
			IabHelper.getInstance().dispose();
		}
		catch(Exception e){
			Log.w(Application.TAG, e.getMessage(), e);
		}

		// Close app
		app.close();
		
		// Super destroy.
		super.onDestroy();
	}

	@Override
	public boolean onNavigationItemSelected(int pos, long id) {
		/* 
		 * On click of toolbar spinner item 
		 */
		
		if(pos == 2 && !Constants.isPremiumVersion()){
			/* 
			 * This is not a premium version 
			 * and user has selected to see the Difficult Quiz !
			 * How dare he. He must buy
			 */
			
			Intent buy = new Intent(QuizList.this, ActivatePremiumFeatures.class);
			QuizList.this.startActivityForResult(buy,999);
			
		}
		
		level = pos + 1;
		loadQuizByLevel();
		
		adapter.notifyDataSetChanged();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		/*
		 * Load Questions for this quiz
		 * Start new activity
		 */		
		Quiz quiz = quizList.get(pos);

		Intent startQuiz = new Intent(QuizList.this, StartQuiz.class);
		startQuiz.putExtra("QuizId", quiz.getQuizId());
		startActivityForResult(startQuiz, 998);

	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

		case 999:
			
			if(data.getBooleanExtra("RestartApp", false)){
				finish();
			}
			else{
				level = 1;
				loadQuizByLevel();
				adapter.notifyDataSetChanged();
			}
			break;
			
		case 998:
			// Reload the quiz list to update the UI
			loadQuizByLevel();
			adapter.notifyDataSetChanged();
			break;
		}
	}

	@Override
	public boolean onActionItemClicked(ActionMode am, MenuItem menuItem) {
		/*
		 * TODO - Pramodh
		 * Check if the reset quiz status is selected, then do the following
		 * a) Find out the items that are selected (See related to do item)
		 * b) For each quiz - reset its status. Call quiz.resetStatus();
		 */
		return false;
	}

	@Override
	public boolean onCreateActionMode(ActionMode am, Menu menu) {

		MenuInflater inflater = am.getMenuInflater();
        inflater.inflate(R.menu.quiz_context_menu, menu);
        return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode am) {
		
		actionMode = null;
		
	}

	@Override
	public boolean onPrepareActionMode(ActionMode am, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

}