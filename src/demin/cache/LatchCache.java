package demin.cache;

import java.util.Stack;
import java.util.concurrent.CountDownLatch;

public class LatchCache {
	
	private static Stack<CountDownLatch> latchs = new Stack<>();
	
	private static CountDownLatch currLatch;
	
	public static CountDownLatch pop() {
		return latchs.pop();
	}

	public static void push(CountDownLatch latch) {
		latchs.push(latch);
	}

	public static CountDownLatch getCurrLatch() {
		return currLatch;
	}

	public static void setCurrLatch(CountDownLatch currLatch) {
		LatchCache.currLatch = currLatch;
	}
	
}
