package demin.entity;

public class Strategy {
	
	private String grids;
	
	private Integer possible;
	
	private Integer mineNum;

	public Strategy(String grids, Integer possible, Integer mineNum){
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

	public Integer getProbability() {
		return possible;
	}

	public void setProbability(Integer possible) {
		this.possible = possible;
	}

	public Integer getMineNum() {
		return mineNum;
	}

	public void setMineNum(Integer mineNum) {
		this.mineNum = mineNum;
	}
	
}
