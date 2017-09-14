package demin.cache;

import java.util.ArrayList;
import java.util.List;
import demin.entity.Strategy;

public class StrategyDeduceCache {
	
	public static List<Strategy> strategyDeduce = new ArrayList<>();
	
	public static void add(Strategy strategy){
		strategyDeduce.add(strategy);
	}
	
	public static List<Strategy> get(){
		return strategyDeduce;
	}
	
	public static void clear(){
		strategyDeduce.clear();
	}
	
}
