package org.varunverma.abapquiz;

public interface IF_QuizUI {
	
	public void evaluateQuiz();
	public void finishQuiz();
	public void nextQuestion();
	
	public void attachFragment(int pos, QuestionFragment f);
	public void removeFragment(int pos);

}