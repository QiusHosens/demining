package demin.entity;

import java.math.BigDecimal;

public class Strategy {
	
	private String grids;
	
	private BigDecimal possible;
	
	private Integer mineNum;

	public Strategy(String grids, BigDecimal possible, Integer mineNum){
		this.grids = grids;
		this.possible = possible;
		this.mineNum = mineNum;
	}
	
	public String getGrids() {
		return grids;
	}

	public void setGrids(String grids) {
		this.grids = grids;
	}

	public BigDecimal getProbability() {
		return possible;
	}

	public void setProbability(BigDecimal possible) {
		this.possible = possible;
	}

	public Integer getMineNum() {
		return mineNum;
	}

	public void setMineNum(Integer mineNum) {
		this.mineNum = mineNum;
	}
	
	public boolean equals(Strategy s){
		if(this.grids.equals(s.getGrids()) && this.possible.equals(s.getGrids()) && this.mineNum.equals(s.getMineNum()))
			return true;
		return false;
	}
	
}
