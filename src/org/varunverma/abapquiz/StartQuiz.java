/**
 * 
 */
package org.varunverma.abapquiz;

import org.varunverma.hanuquiz.Quiz;
import org.varunverma.hanuquiz.QuizManager;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * @author Varun
 *
 */
public class StartQuiz extends FragmentActivity implements IF_QuizUI{

	private ViewPager viewPager;
	private PagerAdapter pagerAdapter;
	private int quizId;
	private Quiz quiz;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.quiz);
		
		quizId = getIntent().getIntExtra("QuizId", -1);
		if(quizId == -1){
			finish();
		}
		
		// Show Ad.
		AdRequest adRequest = new AdRequest();
		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		adRequest.addTestDevice("E16F3DE5DF824FE222EDDA27A63E2F8A"); // My S2 Mobile
		// TODO - Pramodh to find his mobile guid and add it here.
		AdView adView = (AdView) findViewById(R.id.adView);

		adView.loadAd(adRequest);
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		
		pagerAdapter = new QuizPagerAdapter(getSupportFragmentManager(), quizId);
		viewPager.setAdapter(pagerAdapter);
		
		quiz = QuizManager.getInstance().getQuizById(quizId);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(quiz.getDescription());
		
	}
	
	@Override
	protected void onStart() {
		
		super.onStart();
			
	}
	
	@Override
    public void onBackPressed() {
		/*
		 * TODO - Varun Handle the back press. 
		 * While taking quiz, the user should not leave quiz
		 * Give a warning if he wants to leave.
		 */
		
		quiz.pause();
		
		super.onBackPressed();

	}

	@Override
	public void evaluateQuiz() {
		
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
	
}