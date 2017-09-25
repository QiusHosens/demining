package demin.entity;

import java.util.List;

public class StrategyGroup {
	
	private List<Strategy> strategys;
	
	public StrategyGroup(List<Strategy> strategies){
		this.strategys = strategies;
	}

	public List<Strategy> getStrategys() {
		return strategys;
	}

	public void setStrategys(List<Strategy> strategys) {
		this.strategys = strategys;
	}
	
}
