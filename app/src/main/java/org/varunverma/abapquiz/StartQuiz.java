/**
 * 
 */
package org.varunverma.abapquiz;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.varunverma.hanuquiz.Application;
import org.varunverma.hanuquiz.Quiz;
import org.varunverma.hanuquiz.QuizManager;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Varun
 *
 */
public class StartQuiz extends FragmentActivity implements IF_QuizUI{

	private ViewPager viewPager;
	private PagerAdapter pagerAdapter;
	private int quizId;
	private Quiz quiz;
	private HashMap<Integer,QuestionFragment> fragments;
	private Chronometer timer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		fragments = new HashMap<Integer,QuestionFragment>();
		
		setContentView(R.layout.quiz);
		
		timer = (Chronometer) findViewById(R.id.chronometer);
		timer.start();
		
		quizId = getIntent().getIntExtra("QuizId", -1);
		if(quizId == -1){
			finish();
		}
		
		// Show Ad.
		if (!Constants.isPremiumVersion()) {

			AdRequest adRequest = new AdRequest.Builder()
					.addTestDevice(com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR)
					.addTestDevice("9F11CAC92EB404500CAA3F8B0BBA5277").build();

			AdView adView = (AdView) findViewById(R.id.adView);

			// Start loading the ad in the background.
			adView.loadAd(adRequest);

		}
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		
		pagerAdapter = new QuizPagerAdapter(getSupportFragmentManager(), quizId);
		viewPager.setAdapter(pagerAdapter);
		
		quiz = QuizManager.getInstance().getQuizById(quizId);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(quiz.getDescription());
		actionBar.setDisplayHomeAsUpEnabled(true);

        // Show swipe help ?
        String swipeHelp = Application.getApplicationInstance().getSettings().get("SwipeHelp");
        final LinearLayout swipeHelpLayout = (LinearLayout) findViewById(R.id.swipe_help);
        
        if(swipeHelp != null && swipeHelp.contentEquals("Skip")){
        	// Skip the swipe help
        	swipeHelpLayout.setVisibility(View.GONE);
        }
        else{
        	
        	final CheckBox showHelpAgain = (CheckBox) swipeHelpLayout.findViewById(R.id.show_again);
        	
        	Button dismissHelp = (Button) swipeHelpLayout.findViewById(R.id.dismiss_help);
        	dismissHelp.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// Hide the swipe help
					swipeHelpLayout.setVisibility(View.GONE);
					
					if(showHelpAgain.isChecked()){
						Application.getApplicationInstance().addParameter("SwipeHelp", "Skip");
					}
				}
			});
        	
        }
		
	}
	
	@Override
	protected void onStart() {
		
		timer.start();
		super.onStart();
			
	}
	
	@Override
	public void onStop() {
		
		timer.stop();
		
		super.onStop();

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
			case android.R.id.home:
				if(quiz.getStatus() != Quiz.QuizStatus.Completed){
					quiz.pause();
				}
				finish();
				return true;
				
			default:
	            return false;
		}
		
	}
	
	@Override
    public void onBackPressed() {
		
		if(quiz.getStatus() != Quiz.QuizStatus.Completed){
			quiz.pause();
		}
		
		super.onBackPressed();

	}

	@Override
	public void evaluateQuiz() {
		
		timer.stop();
		
		Iterator<QuestionFragment> i = fragments.values().iterator();
		while(i.hasNext()){
			i.next().saveAnswers();
		}
		
		quiz.evaluateQuiz();	
		
	}

	@Override
	public void finishQuiz() {
		
		if(quiz.getStatus() != Quiz.QuizStatus.Completed){
			quiz.pause();
		}
		
		finish();		
	}

	@Override
	public void nextQuestion() {
		// move to next page
		int id = viewPager.getCurrentItem();
		viewPager.setCurrentItem(id+1, true);
		
	}

	@Override
	public void attachFragment(int pos, QuestionFragment f) {
		fragments.put(pos, f);
	}

	@Override
	public void removeFragment(int pos) {
		fragments.remove(pos);
	}
	
}