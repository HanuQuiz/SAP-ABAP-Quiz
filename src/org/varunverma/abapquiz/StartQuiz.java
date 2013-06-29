/**
 * 
 */
package org.varunverma.abapquiz;

import org.varunverma.hanuquiz.Quiz;
import org.varunverma.hanuquiz.QuizManager;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.quiz);
		
		quizId = getIntent().getIntExtra("QuizId", -1);
		if(quizId == -1){
			finish();
		}
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		
		pagerAdapter = new QuizPagerAdapter(getSupportFragmentManager(), quizId);
		viewPager.setAdapter(pagerAdapter);
		
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
		
		super.onBackPressed();

	}

	@Override
	public void evaluateQuiz() {
		
		Quiz quiz = QuizManager.getInstance().getQuizById(quizId);
		quiz.evaluateQuiz();
		
	}

	@Override
	public void finishQuiz() {
		finish();		
	}
	
}