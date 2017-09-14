package demin.entity;

public class Probability {

	private Integer pos;
	
	private Double probability;
	
	public Probability(Integer pos, Double probability){
		this.pos = pos;
		this.probability = probability;
	}

	public Integer getPos() {
		return pos;
	}

	public void setPos(Integer pos) {
		this.pos = pos;
	}

	public Double getProbability() {
		return probability;
	}

	public void setProbability(Double probability) {
		this.probability = probability;
	}
	
}
