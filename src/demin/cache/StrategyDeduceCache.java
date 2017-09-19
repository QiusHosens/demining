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
	
	public static Queue<Strategy> strategyDeduce2 = new ConcurrentLinkedQueue<>();
	
	public static Queue<Strategy> strategyDeduce3 = new ConcurrentLinkedQueue<>();
	
	public static void add2(Strategy strategy){
		strategyDeduce2.add(strategy);
	}
	
	public static void add3(Strategy strategy){
		strategyDeduce3.add(strategy);
	}
	
	public static void add(Strategy strategy){
//		if(!isExist(strategy))
			strategyDeduce.add(strategy);
	}
	
	public static boolean isExist(Strategy strategy){
		String gridPos = strategy.getGrids();
		List<String> gridPosList = new ArrayList<String>(Arrays.asList(gridPos.split(",")));
		for(Strategy strat : strategyDeduce){
			List<String> gridPosList1 = new ArrayList<String>(Arrays.asList(strat.getGrids().split(",")));
			if(CollectionUtil.exclude(gridPosList, gridPosList1).isEmpty() && CollectionUtil.exclude(gridPosList1, gridPosList).isEmpty()){
				return true;
			}
		}
		return false;
	}
	
	public static Queue<Strategy> get(){
		return strategyDeduce;
	}
	
	public static Queue<Strategy> get2(){
		return strategyDeduce2;
	}
	
	public static Queue<Strategy> get3(){
		return strategyDeduce3;
	}
	
	public static void clear(){
		strategyDeduce.clear();
		strategyDeduce2.clear();
		strategyDeduce3.clear();
	}
	
	public static List<Strategy> compare12(){
		Queue<Strategy> maxQueue = strategyDeduce.size() > strategyDeduce2.size() ? strategyDeduce : strategyDeduce2;
		Queue<Strategy> minQueue = strategyDeduce.equals(maxQueue) ? strategyDeduce2 : strategyDeduce;
		List<Strategy> strategys = new ArrayList<>();
		for(Strategy strategy : maxQueue){
			if(!minQueue.contains(strategy))
				strategys.add(strategy);
		}
		return strategys;
	}
	
}
