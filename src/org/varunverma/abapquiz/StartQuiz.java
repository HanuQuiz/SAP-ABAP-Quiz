/**
 * 
 */
package org.varunverma.abapquiz;

import java.util.HashMap;
import java.util.Iterator;

import org.varunverma.hanuquiz.Quiz;
import org.varunverma.hanuquiz.QuizManager;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		fragments = new HashMap<Integer,QuestionFragment>();
		
		setContentView(R.layout.quiz);
		
		quizId = getIntent().getIntExtra("QuizId", -1);
		if(quizId == -1){
			finish();
		}
		
		// Show Ad.
		if (!Constants.isPremiumVersion()) {

			AdRequest adRequest = new AdRequest();
			adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
			adRequest.addTestDevice("E16F3DE5DF824FE222EDDA27A63E2F8A"); // My S2 mobile
			adRequest.addTestDevice("06FE63303C3DA13C859515930A396C91");	// Pramodh's mobile
			AdView adView = (AdView) findViewById(R.id.adView);

			adView.loadAd(adRequest);

		}
		
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
		
		if(quiz.getStatus() != Quiz.QuizStatus.Completed){
			quiz.pause();
		}
		
		super.onBackPressed();

	}

	@Override
	public void evaluateQuiz() {
		
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