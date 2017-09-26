package demin.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class RegionMineMiddle {
	
	private StringBuilder gridPos;
	
	private List<List<String>> regionGroup;
	
	private BigDecimal possible;
	
	private Map<String, Integer> region;
	
	private Integer mineNum;
	
	public RegionMineMiddle(StringBuilder gridPos, List<List<String>> regionGroup, BigDecimal possible, Map<String, Integer> region, Integer mineNum){
		this.gridPos = gridPos;
		this.regionGroup = regionGroup;
		this.possible = possible;
		this.region = region;
		this.mineNum = mineNum;
	}

	public StringBuilder getGridPos() {
		return gridPos;
	}

	public void setGridPos(StringBuilder gridPos) {
		this.gridPos = gridPos;
	}

	public List<List<String>> getRegionGroup() {
		return regionGroup;
	}

	public void setRegionGroup(List<List<String>> regionGroup) {
		this.regionGroup = regionGroup;
	}

	public BigDecimal getPossible() {
		return possible;
	}

	public void setPossible(BigDecimal possible) {
		this.possible = possible;
	}

	public Map<String, Integer> getRegion() {
		return region;
	}

	public void setRegion(Map<String, Integer> region) {
		this.region = region;
	}

	public Integer getMineNum() {
		return mineNum;
	}

	public void setMineNum(Integer mineNum) {
		this.mineNum = mineNum;
	}
	
}
