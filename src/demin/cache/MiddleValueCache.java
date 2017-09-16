package demin.cache;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import demin.entity.MiddleValue;

public class MiddleValueCache {

	private static Queue<Stack<MiddleValue>> middleValueCache = new ConcurrentLinkedQueue<>();
	
	public static void add(Stack<MiddleValue> middleValue){
		middleValueCache.add(middleValue);
	}
	
	public static Stack<MiddleValue> poll(){
		return middleValueCache.poll();
	}
	
	public static int size(){
		return middleValueCache.size();
	}
	
	public static boolean isEmpty(){
		return middleValueCache.isEmpty();
	}
	
}
