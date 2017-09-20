package demin.entity;

import java.math.BigDecimal;

public class GridPossible {

	private Integer pos;
	
	private BigDecimal isMinePossible;
	
	public GridPossible(Integer pos, BigDecimal isMinePossible){
		this.pos = pos;
		this.isMinePossible = isMinePossible;
	}

	public Integer getPos() {
		return pos;
	}

	public void setPos(Integer pos) {
		this.pos = pos;
	}

	public BigDecimal getIsMinePossible() {
		return isMinePossible;
	}

	public void setIsMinePossible(BigDecimal isMinePossible) {
		this.isMinePossible = isMinePossible;
	}
	
}
