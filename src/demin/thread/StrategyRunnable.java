package demin.thread;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.Map.Entry;

import demin.cache.LatchCache;
import demin.cache.MiddleValueCache;
import demin.cache.MineRegionCache;
import demin.cache.StrategyDeduceCache;
import demin.constants.LayoutConstants;
import demin.entity.MiddleValue;
import demin.entity.Strategy;
import demin.util.CollectionUtil;

public class StrategyRunnable implements Runnable {

	@Override
	public void run() {
		while(!MiddleValueCache.isEmpty()){
			MiddleValue useValueStack = MiddleValueCache.poll();
			if(useValueStack != null)
				generateStrategy(useValueStack);
		}
		
		Map<String, CountDownLatch> latchs = LatchCache.getCurrLatch();
		CountDownLatch currLatch = latchs.get("currLatch");
		CountDownLatch preLatch = latchs.get("preLatch");
		System.out.println(latchs);
		if(preLatch == null)
			currLatch.countDown();
		else{
			try {
				preLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			currLatch.countDown();
		}
	}
	
	private void generateStrategy(MiddleValue useValue){
		Map<String, Integer> region = useValue.getRegion();
		StringBuilder poss = useValue.getGridPos();
		int closeGridNum = useValue.getCloseGridNum();
		int regionMineNum = useValue.getRegionMineNum();
		int pos = useValue.getCurrPos();
		while(pos != -1){
			region = MineRegionCache.removeMarkGridPosFromRegion(region, String.valueOf(pos));
			poss.append(",").append(pos);
			closeGridNum --;
			regionMineNum ++;
			if(regionMineNum > LayoutConstants.LEFT_MINE)//如果区域内假设雷数量多于剩余雷数,则策略失败
				break;
			
			if(!region.isEmpty()){
				Set<Integer> canOpenGridPos = new HashSet<Integer>();
				//去除不是雷的块
				for(Entry<String, Integer> entry1 : region.entrySet()){
					String gridsPoss = entry1.getKey();
					Integer mineNum = entry1.getValue();
					String[] gridsArr = gridsPoss.split(",");
					List<String> gridsPosList = Arrays.asList(gridsArr);
					if(mineNum == 0){
						for(String gridPos1 : gridsPosList){
							Integer gridPosInt = Integer.parseInt(gridPos1);
							canOpenGridPos.add(gridPosInt);
						}
					}
				}
			
				for(Integer gridPos1 : canOpenGridPos){
					region = MineRegionCache.removeOpenGridPosFromRegion(region, String.valueOf(gridPos1));
					closeGridNum --;
				}
			}
			
			if(region.isEmpty()){
				Strategy strategy = new Strategy(poss.substring(1).toString(), CollectionUtil.combination(closeGridNum, LayoutConstants.LEFT_MINE - regionMineNum), regionMineNum);
				StrategyDeduceCache.add(strategy);
				break;
			}
			
			//找确定是雷的块
			boolean isFind = false;
			for(Entry<String, Integer> entry1 : region.entrySet()){
				String gridsPoss = entry1.getKey();
				Integer mineNum = entry1.getValue();
				String[] gridsArr = gridsPoss.split(",");
				List<String> gridsPosList = Arrays.asList(gridsArr);
				if(mineNum == gridsPosList.size()){
					isFind = true;
					for(String gridPos1 : gridsPosList){
						Integer gridPosInt = Integer.parseInt(gridPos1);
						pos = gridPosInt;
						isFind = true;
					}
				}
			}
			if(!isFind){
				break;
			}
		}
		if(region != null && !region.isEmpty()){
			for(Entry<String, Integer> entry1 : region.entrySet()){
				String gridPoss = entry1.getKey();
				String[] gridPossList = gridPoss.split(",");
				Map<String, Integer> cloneRegion = new HashMap<String, Integer>(region);
				for(String gridPos1 : gridPossList){
					Integer gridPosInt = Integer.parseInt(gridPos1);
					MiddleValue setValue = new MiddleValue(gridPosInt, new StringBuilder(poss), closeGridNum, regionMineNum, new HashMap<>(cloneRegion));
					MiddleValueCache.add(setValue);
					//当前假定地雷在同一区域内,下次不能再假定为地雷,会生成重复策略
					cloneRegion = MineRegionCache.removeOpenGridPosFromRegion(cloneRegion, gridPos1);
				}
				break;
			}
			//检查是否需要增加线程
			StrategyGenerator.getStrategyGenerator().check();
		}
	}
	
}
