/**
 * 
 */
package org.varunverma.abapquiz;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ayansh.hanuquiz.QuizManager;

/**
 * @author varun
 *
 */
public class QuizPagerAdapter extends FragmentStatePagerAdapter {

	private int questionCount;
	private int quizId;
	
	/**
	 * @param fm
	 */
	public QuizPagerAdapter(FragmentManager fm, int quizId) {
		
		super(fm);
		
		this.quizId = quizId;
		questionCount = QuizManager.getInstance().getQuizById(quizId).getCount();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentStatePagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int pos) {
		
		if(pos < questionCount){
			return QuestionFragment.create(quizId, pos);
		}
		else{
			return QuizResultFragment.create(quizId);
		}
		
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return questionCount + 1;
	}

}
