package demin.entity;

import java.math.BigDecimal;
import java.util.List;

public class GroupStrategy {
	
	private List<String> pos;
	
	private BigDecimal possible;
	
	public GroupStrategy(List<String> pos, BigDecimal possible){
		this.pos = pos;
		this.possible = possible;
	}

	public List<String> getPos() {
		return pos;
	}

	public void setPos(List<String> pos) {
		this.pos = pos;
	}

	public BigDecimal getPossible() {
		return possible;
	}

	public void setPossible(BigDecimal possible) {
		this.possible = possible;
	}
	
}
