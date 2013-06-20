/**
 * 
 */
package org.varunverma.abapquiz;

import org.varunverma.hanuquiz.Question;
import org.varunverma.hanuquiz.QuizManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * @author varun
 *
 */
public class QuestionFragment extends Fragment {

	private int questionId;
	private int quizId;
	
	public static QuestionFragment create(int quizId, int questionId){
		
		QuestionFragment fragment = new QuestionFragment();
		Bundle args = new Bundle();
		
		/*
		 * Note that this is not the real question Id.
		 * This is the position of the question in the question list of the quiz.
		 */
        args.putInt("QuestionId", questionId);
        args.putInt("QuizId", quizId);
        fragment.setArguments(args);
		
		return fragment;
	}
	
	public QuestionFragment(){
		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        
        questionId = getArguments().getInt("QuestionId");
        quizId = getArguments().getInt("QuizId");
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.question, container, false);
		
		WebView wv = (WebView) rootView.findViewById(R.id.webview);
		
		/*
		 * TODO - Varun to populate the question
		 */
		Question question = QuizManager.getInstance().getQuizById(quizId).getQuestion(questionId);
		
		String html = question.getHTML();
		
		wv.loadDataWithBaseURL("fake://not/needed", html, "text/html", "UTF-8", "");
		
		return rootView;
		
	}

}