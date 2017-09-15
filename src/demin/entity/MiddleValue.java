package demin.entity;

import java.util.Map;

public class MiddleValue {
	
	private Integer currPos;
	
	private StringBuilder gridPos;
	
	private int closeGridNum;
	
	private int regionMineNum;
	
	private Map<String, Integer> region;
	
	public MiddleValue(Integer currPos, StringBuilder gridPos, int closeGridNum, int regionMineNum, Map<String, Integer> region){
		this.currPos = currPos;
		this.gridPos = gridPos;
		this.closeGridNum = closeGridNum;
		this.regionMineNum = regionMineNum;
		this.region = region;
	}

	public Integer getCurrPos() {
		return currPos;
	}

	public void setCurrPos(Integer currPos) {
		this.currPos = currPos;
	}

	public StringBuilder getGridPos() {
		return gridPos;
	}

	public void setGridPos(StringBuilder gridPos) {
		this.gridPos = gridPos;
	}

	public int getCloseGridNum() {
		return closeGridNum;
	}

	public void setCloseGridNum(int closeGridNum) {
		this.closeGridNum = closeGridNum;
	}

	public int getRegionMineNum() {
		return regionMineNum;
	}

	public void setRegionMineNum(int regionMineNum) {
		this.regionMineNum = regionMineNum;
	}

	public Map<String, Integer> getRegion() {
		return region;
	}

	public void setRegion(Map<String, Integer> region) {
		this.region = region;
	}
	
}
