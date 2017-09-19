package demin.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LatchCache {
	
	private static Stack<CountDownLatch> latchs = new Stack<>();
	
	private static CountDownLatch currLatch;
	
	private static CountDownLatch preLatch;
	
	private static ReadWriteLock lock = new ReentrantReadWriteLock(false);
	
	private static int count = 0;
	
	public static CountDownLatch pop() {
		lock.readLock().lock();
		CountDownLatch latch = latchs.pop();
		lock.readLock().unlock();
		return latch;
	}

	public static void push(CountDownLatch latch) {
		lock.writeLock().lock();
		latchs.push(latch);
		lock.writeLock().unlock();
	}
	
	public static synchronized Map<String, CountDownLatch> getCurrLatch() {
		if(currLatch == null)
			currLatch = pop();
		if(count < currLatch.getCount()){
			count ++;
		} else {
			preLatch = currLatch;
			if(latchs.isEmpty()){
				System.out.println(currLatch);
			}
			currLatch = pop();
			count = 1;
		}
		Map<String, CountDownLatch> latchMap = new HashMap<>();
		latchMap.put("currLatch", currLatch);
		latchMap.put("preLatch", preLatch);
		return latchMap;
	}

	public static synchronized void setCurrLatch(CountDownLatch currLatch) {
		LatchCache.currLatch = currLatch;
	}
	
	public static void clear(){
		latchs.clear();
		currLatch = null;
		preLatch = null;
		count = 0;
	}
	
}
