package demin.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;

public class LatchCache {
	
	private static Stack<CountDownLatch> latchs = new Stack<>();
	
	private static CountDownLatch currLatch;
	
	private static CountDownLatch preLatch;
	
	private static int count = 0;
	
	public static CountDownLatch pop() {
		return latchs.pop();
	}

	public static void push(CountDownLatch latch) {
		latchs.push(latch);
	}
	
	public static synchronized Map<String, CountDownLatch> getCurrLatch() {
		if(currLatch == null && !latchs.isEmpty())
			currLatch = pop();
		Map<String, CountDownLatch> latchMap = new HashMap<>();
		latchMap.put("currLatch", currLatch);
		latchMap.put("preLatch", preLatch);
		if(count < currLatch.getCount()){
			count ++;
			return latchMap;
		}
		preLatch = currLatch;
		currLatch = null;
		count = 0;
		return null;
	}

	public static synchronized void setCurrLatch(CountDownLatch currLatch) {
		LatchCache.currLatch = currLatch;
	}
	
	public static void clear(){
		latchs.clear();
		currLatch = null;
		preLatch = null;
	}
	
}
