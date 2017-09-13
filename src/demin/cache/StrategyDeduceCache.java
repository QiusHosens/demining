package demin.cache;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import demin.entity.MyGrid;

public class StrategyDeduceCache {
	
	public static Queue<Map<String, List<MyGrid>>> strategyDeduce = new ConcurrentLinkedQueue<>();
	
	public static void add(Map<String, List<MyGrid>> strategy){
		strategyDeduce.add(strategy);
	}
	
	public static Queue<Map<String, List<MyGrid>>> get(){
		return strategyDeduce;
	}
	
	public static void clear(){
		strategyDeduce.clear();
	}
	
}
