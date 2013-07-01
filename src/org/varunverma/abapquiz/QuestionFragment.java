/**
 * 
 */
package org.varunverma.abapquiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.varunverma.hanuquiz.Question;
import org.varunverma.hanuquiz.QuizManager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * @author varun
 *
 */
public class QuestionFragment extends Fragment {

	private int questionId;
	private int quizId;
	private Question question;
	private IF_QuizUI activity;
	private List<CompoundButton> optionButtonList;
	
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
		
		question = QuizManager.getInstance().getQuizById(quizId).getQuestion(questionId);
		
		String html = question.getHTML();
		
		wv.loadDataWithBaseURL("fake://not/needed", html, "text/html", "UTF-8", "");
		
		RadioGroup rg = (RadioGroup) rootView.findViewById(R.id.single_choice);
		LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.multiple_choice);
		
		optionButtonList = new ArrayList<CompoundButton>();
		
		if(question.getChoiceType() == 1){
			// Single Choice
			ll.setVisibility(View.GONE);
			rg.setVisibility(View.VISIBLE);
		}
		else{
			// Multiple Choice
			rg.setVisibility(View.GONE);
			ll.setVisibility(View.VISIBLE);
		}
		
		HashMap<Integer,String> options = question.getOptions();
		
		Iterator<Integer> i = options.keySet().iterator();
		while(i.hasNext()){
			
			int optionId = i.next();
			String optionValue = options.get(optionId);
			
			if(question.getChoiceType() == 1){
				// Single Choice
				RadioButton rb = new RadioButton(getActivity());
				rb.setTag(String.valueOf(optionId));
				rb.setText(optionValue);
				rg.addView(rb);
				optionButtonList.add(rb);
			}
			else{
				// Single Choice
				CheckBox cb = new CheckBox(getActivity());
				cb.setTag(String.valueOf(optionId));
				cb.setText(optionValue);
				ll.addView(cb);
				optionButtonList.add(cb);
			}
			
		}
		
		Button reset = (Button) rootView.findViewById(R.id.reset);
		Button ok = (Button) rootView.findViewById(R.id.ok);
		
		reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Iterator<CompoundButton> i = optionButtonList.iterator();
				while(i.hasNext()){
					i.next().setChecked(false);
				}
				
			}
		});
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String answer = "";
				
				Iterator<CompoundButton> i = optionButtonList.iterator();
				while(i.hasNext()){
					
					CompoundButton cb = i.next();
					if(cb.isChecked()){
						answer = answer + "," + cb.getTag();
					}
					
				}
				
				if(answer.length() > 1){
					answer = answer.substring(1, answer.length());
				}
				
				question.updateMyAnswer(answer);
				activity.nextQuestion();
				
			}
		});
		
		return rootView;
		
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