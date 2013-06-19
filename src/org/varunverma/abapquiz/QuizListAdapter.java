package org.varunverma.abapquiz;

import java.util.List;

import org.varunverma.hanuquiz.Quiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class QuizListAdapter extends ArrayAdapter<Quiz> {

	private List<Quiz> quizList;
	
	public QuizListAdapter(Context context, int resId, List<Quiz> objects) {
		
		// Super constructor
		super(context, resId, objects);
		
		quizList = objects;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View rowView;
		
		Quiz quiz = quizList.get(position);
		
		if (convertView == null) {
			rowView = LayoutInflater.from(getContext()).inflate(R.layout.quiz_list_row, parent, false);
			rowView.setTag(quiz.toString());
		}

		else {
			if(convertView.getTag().toString().contentEquals(quiz.toString())){
				rowView = convertView;
			}
			else{
				rowView = LayoutInflater.from(getContext()).inflate(R.layout.quiz_list_row, parent, false);
				rowView.setTag(quiz.toString());
			}
		}
		
		//TODO - Populate UI from the Quiz Attributes
		
		return rowView;
		
	}

}
