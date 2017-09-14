package demin.entity;

public class Strategy {
	
	private String grids;
	
	private Double probability;

	public Strategy(String grids, Double probability){
		this.grids = grids;
		this.probability = probability;
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
	
}
