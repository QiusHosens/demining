package demin.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Strategy {
	
	private String grids;
	
	private BigDecimal possible;
	
	private Integer mineNum;
	
	private Integer leftCloseNum;
	
	private List<GridPossible> gridPossibles;
	
	private Map<List<String>, Integer> resultRegion;
	
	public Strategy(String grids, Integer mineNum){
		this.grids = grids;
		this.mineNum = mineNum;
	}

	public Strategy(String grids, BigDecimal possible, Integer mineNum){
		this.grids = grids;
		this.possible = possible;
		this.mineNum = mineNum;
	}
	
	public Strategy(String grids, Integer leftCloseNum, Integer mineNum){
		this.grids = grids;
		this.leftCloseNum = leftCloseNum;
		this.mineNum = mineNum;
	}
	
	public Strategy(String grids, BigDecimal possible, Integer leftCloseNum, Integer mineNum){
		this.grids = grids;
		this.possible = possible;
		this.leftCloseNum = leftCloseNum;
		this.mineNum = mineNum;
	}
	
	public Strategy(String grids, BigDecimal possible, Integer leftCloseNum, Integer mineNum, Map<List<String>, Integer> resultRegion){
		this.grids = grids;
		this.possible = possible;
		this.leftCloseNum = leftCloseNum;
		this.mineNum = mineNum;
		this.resultRegion = resultRegion;
	}
	
	public Strategy(String grids, BigDecimal possible, List<GridPossible> gridPossibles, Integer leftCloseNum, Integer mineNum){
		this.grids = grids;
		this.possible = possible;
		this.gridPossibles = gridPossibles;
		this.leftCloseNum = leftCloseNum;
		this.mineNum = mineNum;
	}
	
	public String getGrids() {
		return grids;
	}

	public void setGrids(String grids) {
		this.grids = grids;
	}

	public BigDecimal getPossible() {
		return possible;
	}

	public void setPossible(BigDecimal possible) {
		this.possible = possible;
	}

	public Integer getMineNum() {
		return mineNum;
	}

	public void setMineNum(Integer mineNum) {
		this.mineNum = mineNum;
	}
	
	public Integer getLeftCloseNum() {
		return leftCloseNum;
	}

	public void setLeftCloseNum(Integer leftCloseNum) {
		this.leftCloseNum = leftCloseNum;
	}

	public List<GridPossible> getGridPossibles() {
		return gridPossibles;
	}

	public void setGridPossibles(List<GridPossible> gridPossibles) {
		this.gridPossibles = gridPossibles;
	}

	public Map<List<String>, Integer> getResultRegion() {
		return resultRegion;
	}

	public void setResultRegion(Map<List<String>, Integer> resultRegion) {
		this.resultRegion = resultRegion;
	}

	public boolean equals(Strategy s){
		if(this.grids.equals(s.getGrids()) 
				&& this.mineNum.equals(s.getMineNum()) && this.leftCloseNum.equals(s.getLeftCloseNum()))
			return true;
		return false;
	}
	
}
