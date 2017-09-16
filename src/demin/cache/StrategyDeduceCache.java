package demin.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import demin.entity.Strategy;
import demin.util.CollectionUtil;

public class StrategyDeduceCache {
	
	public static Queue<Strategy> strategyDeduce = new ConcurrentLinkedQueue<>();
	
	public static void add(Strategy strategy){
		String gridPos = strategy.getGrids();
		List<String> gridPosList = new ArrayList<String>(Arrays.asList(gridPos.split(",")));
		boolean isExist = false;
		for(Strategy strat : strategyDeduce){
			List<String> gridPosList1 = new ArrayList<String>(Arrays.asList(strat.getGrids().split(",")));
			if(CollectionUtil.exclude(gridPosList, gridPosList1).isEmpty() && CollectionUtil.exclude(gridPosList1, gridPosList).isEmpty()){
				isExist = true;
				break;
			}
		}
		if(!isExist)
			strategyDeduce.add(strategy);
	}
	
	public static Queue<Strategy> get(){
		return strategyDeduce;
	}
	
	public static void clear(){
		strategyDeduce.clear();
	}
	
}
