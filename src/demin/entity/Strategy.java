package demin.entity;

public class Strategy {
	
	private String grids;
	
	private Double probability;
	
	private Integer mineNum;

	public Strategy(String grids, Double probability, Integer mineNum){
		this.grids = grids;
		this.probability = probability;
		this.mineNum = mineNum;
	}
	
	public String getGrids() {
		return grids;
	}

	public void setGrids(String grids) {
		this.grids = grids;
	}

	public Double getProbability() {
		return probability;
	}

	public void setProbability(Double probability) {
		this.probability = probability;
	}

	public Integer getMineNum() {
		return mineNum;
	}

	public void setMineNum(Integer mineNum) {
		this.mineNum = mineNum;
	}
	
}
