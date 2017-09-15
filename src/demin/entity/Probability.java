package demin.entity;

import java.math.BigDecimal;

public class Probability {

	private Integer pos;
	
	private BigDecimal probability;
	
	public Probability(Integer pos, BigDecimal probability){
		this.pos = pos;
		this.probability = probability;
	}

	public Integer getPos() {
		return pos;
	}

	public void setPos(Integer pos) {
		this.pos = pos;
	}

	public BigDecimal getProbability() {
		return probability;
	}

	public void setProbability(BigDecimal probability) {
		this.probability = probability;
	}
	
}
