package demin.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import demin.cache.MiddleValueCache;
import demin.constants.Constants;

public class StrategyGenerator {

	private static StrategyGenerator strategyGenerator = new StrategyGenerator();
	
	private ExecutorService strategyThreadPool = null;
	
	public static StrategyGenerator getStrategyGenerator(){
		return strategyGenerator;
	}
	
	public void execute(){
		if(MiddleValueCache.isEmpty())
			return;
		ExecutorService pool = this.getStrategyThreadPool();
		for(int index = 0; index < Constants.THREAD_COUNT; index ++){
			pool.execute(new StrategyRunnable());
		}
	}
	
	private ExecutorService getStrategyThreadPool(){
		if(strategyThreadPool == null){
			synchronized(this){
				if(strategyThreadPool == null)
					strategyThreadPool = Executors.newCachedThreadPool();
			}
		}
		return strategyThreadPool;
	}
	
}
