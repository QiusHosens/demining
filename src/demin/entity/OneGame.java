package demin.entity;

import java.util.List;

public class OneGame {
	
	private List<Integer> mines;
	
	private String step;

	public OneGame(List<Integer> mines, String step){
		this.mines = mines;
		this.step = step;
	}
	
	public List<Integer> getMines() {
		return mines;
	}

	public void setMines(List<Integer> mines) {
		this.mines = mines;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}
	
}
