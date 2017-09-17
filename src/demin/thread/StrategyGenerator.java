package demin.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import demin.cache.MiddleValueCache;
import demin.constants.Constants;
import demin.constants.LayoutConstants;

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
		for(int index = 0; index < Constants.INIT_THREAD_COUNT; index ++){
			pool.execute(new StrategyRunnable());
		}
		LayoutConstants.CURRENT_THREAD_COUNT = Constants.INIT_THREAD_COUNT;
	}
	
	public void addThread(int num){
		ExecutorService pool = this.getStrategyThreadPool();
		for(int index = 0; index < num; index ++){
			pool.execute(new StrategyRunnable());
		}
		LayoutConstants.CURRENT_THREAD_COUNT += num;
	}
	
	public void check(){
		int threadCount = 0;
		int size = MiddleValueCache.size();
		threadCount = size / Constants.ADD_THREAD_PER_NUM + 1;
		if(threadCount > Constants.MAX_THREAD_COUNT)
			threadCount = Constants.MAX_THREAD_COUNT;
		int addThreadCount = threadCount - LayoutConstants.CURRENT_THREAD_COUNT;
		if(addThreadCount > 0)
			addThread(addThreadCount);
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
