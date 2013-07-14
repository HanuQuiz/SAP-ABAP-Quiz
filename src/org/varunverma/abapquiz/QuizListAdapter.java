package org.varunverma.abapquiz;

import java.util.List;

import org.varunverma.hanuquiz.Quiz;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
		
		// Set Icon
		ImageView quizIcon = (ImageView) rowView.findViewById(R.id.status);
		if(quiz.getStatus() == Quiz.QuizStatus.Completed){
			quizIcon.setImageResource(R.drawable.completed);
		}
		else if(quiz.getStatus() == Quiz.QuizStatus.Paused){
			quizIcon.setImageResource(R.drawable.pause);
		}
		else{
			quizIcon.setImageResource(R.drawable.new_quiz);
		}
		
		TextView title = (TextView) rowView.findViewById(R.id.title);
		title.setText(quiz.getDescription());
		
		//TODO - Also show the last played time
		TextView desc = (TextView) rowView.findViewById(R.id.desc);
		desc.setText(quiz.getCount() + " questions");
		
		TextView score = (TextView) rowView.findViewById(R.id.score);
		if(quiz.getStatus() == Quiz.QuizStatus.Completed){
			// If completed, then only show the score
			String quizScore = quiz.getScore() + "/" + quiz.getCount();
			score.setText(quizScore);
			
			float perct = 100 * quiz.getScore() / quiz.getCount();
			if(perct >= 70){
				score.setTextColor(Color.GREEN);
			}
			else if(perct < 50){
				score.setTextColor(Color.RED);
			}
			else{
				score.setTextColor(Color.MAGENTA);
			}
			
		}
		else{
			score.setText("");
		}
		
		return rowView;
		
	}

}
