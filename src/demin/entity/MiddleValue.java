package demin.entity;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MiddleValue {
	
	private Integer currPos;
	
	private StringBuilder gridPos;
	
	private int closeGridNum;
	
	private int regionMineNum;
	
	private Map<String, Integer> commonRegion;
	
	private Map<String, Integer> unCommonRegion;
	
	private CountDownLatch latch;
	
	private BigDecimal possible;
	
	public MiddleValue(Integer currPos, StringBuilder gridPos, int closeGridNum, int regionMineNum, Map<String, Integer> region, Map<String, Integer> unCommonRegion){
		this.currPos = currPos;
		this.gridPos = gridPos;
		this.closeGridNum = closeGridNum;
		this.regionMineNum = regionMineNum;
		this.commonRegion = region;
		this.unCommonRegion = unCommonRegion;
	}
	
	public MiddleValue(Integer currPos, StringBuilder gridPos, int closeGridNum, int regionMineNum, Map<String, Integer> region, Map<String, Integer> unCommonRegion, CountDownLatch latch){
		this.currPos = currPos;
		this.gridPos = gridPos;
		this.closeGridNum = closeGridNum;
		this.regionMineNum = regionMineNum;
		this.commonRegion = region;
		this.unCommonRegion = unCommonRegion;
		this.latch = latch;
	}
	
	public MiddleValue(Integer currPos, StringBuilder gridPos, int closeGridNum, int regionMineNum, Map<String, Integer> region, Map<String, Integer> unCommonRegion, BigDecimal possible, CountDownLatch latch){
		this.currPos = currPos;
		this.gridPos = gridPos;
		this.closeGridNum = closeGridNum;
		this.regionMineNum = regionMineNum;
		this.commonRegion = region;
		this.unCommonRegion = unCommonRegion;
		this.possible = possible;
		this.latch = latch;
	}
	
	public MiddleValue(StringBuilder gridPos, BigDecimal possible, int closeGridNum, int regionMineNum, Map<String, Integer> region){
		this.gridPos = gridPos;
		this.possible = possible;
		this.closeGridNum = closeGridNum;
		this.regionMineNum = regionMineNum;
		this.commonRegion = region;
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
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

	public Map<String, Integer> getCommonRegion() {
		return commonRegion;
	}

	public void setCommonRegion(Map<String, Integer> commonRegion) {
		this.commonRegion = commonRegion;
	}

	public Map<String, Integer> getUnCommonRegion() {
		return unCommonRegion;
	}

	public void setUnCommonRegion(Map<String, Integer> unCommonRegion) {
		this.unCommonRegion = unCommonRegion;
	}

	public BigDecimal getPossible() {
		return possible;
	}

	public void setPossible(BigDecimal possible) {
		this.possible = possible;
	}
	
}
