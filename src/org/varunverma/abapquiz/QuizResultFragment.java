/**
 * 
 */
package org.varunverma.abapquiz;

import org.varunverma.hanuquiz.Quiz;
import org.varunverma.hanuquiz.QuizManager;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author varun
 *
 */
public class QuizResultFragment extends Fragment {
	
	private int quizId;
	private IF_QuizUI activity;
	private TextView quizResultView;
	
	public static Fragment create(int quizId) {
		
		QuizResultFragment fragment = new QuizResultFragment();
		Bundle args = new Bundle();
		
        args.putInt("QuizId", quizId);
        fragment.setArguments(args);
		
		return fragment;
	}
	
	public QuizResultFragment(){
		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
        
        quizId = getArguments().getInt("QuizId");
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.quiz_result, container, false);
		
		quizResultView = (TextView) rootView.findViewById(R.id.quiz_result);
		
		//showQuizScore();
		quizResultView.setTextSize(20);
		quizResultView.setText("Click on Evaluate to check your score");
		quizResultView.setTextColor(Color.GRAY); 
		
		Button evaluateQuiz = (Button) rootView.findViewById(R.id.evaluate_quiz);
		Button done = (Button) rootView.findViewById(R.id.done);
		
		evaluateQuiz.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activity.evaluateQuiz();
				showQuizScore();
			}
		});
		
		done.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activity.finishQuiz();
			}
		});
		
		return rootView;
		
	}
	
	private void showQuizScore() {
		
		Quiz quiz = QuizManager.getInstance().getQuizById(quizId);
		int score = quiz.getScore();
		int count = quiz.getCount();
		
		quizResultView.setTextSize(60); //Score Size
		
		String myScore = String.valueOf(score) + " / " + String.valueOf(count);
		quizResultView.setText(myScore);
		
		float perct = 100 * score / count;
		if(perct >= 70){
			quizResultView.setTextColor(Color.GREEN);
		}
		else if(perct < 50){
			quizResultView.setTextColor(Color.RED);
		}
		else{
			quizResultView.setTextColor(Color.MAGENTA);
		}
		
		 quizResultView.setTop(0);
		Animation animation=new TranslateAnimation(0,0,0,0,	Animation.ZORDER_BOTTOM, 300,Animation.ZORDER_TOP,0);
	    animation.setDuration(1000);
	    quizResultView.setAnimation(animation);
	    
		
	}

	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		this.activity = (IF_QuizUI) activity;
		
	}

	@Override
	public void onDetach() {
		
		super.onDetach();
		activity = null;
		
	}

}