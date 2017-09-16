package demin.thread;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.Map.Entry;

import demin.cache.MiddleValueCache;
import demin.cache.MineRegionCache;
import demin.cache.StrategyDeduceCache;
import demin.constants.LayoutConstants;
import demin.entity.MiddleValue;
import demin.entity.Strategy;
import demin.util.CollectionUtil;
import demin.window.DeminFrame;

public class StrategyRunnable implements Runnable {

	@Override
	public void run() {
		while(!MiddleValueCache.isEmpty()){
			Stack<MiddleValue> useValueStack = MiddleValueCache.poll();
			if(useValueStack != null)
				generateStrategy(useValueStack);
		}
	}
	
	private void generateStrategy(Stack<MiddleValue> useValueStack){
		CountDownLatch latch = null;
		while(!useValueStack.isEmpty()){
			MiddleValue useValue = useValueStack.pop();
			Map<String, Integer> region = useValue.getRegion();
			StringBuilder poss = useValue.getGridPos();
			int closeGridNum = useValue.getCloseGridNum();
			int regionMineNum = useValue.getRegionMineNum();
			int pos = useValue.getCurrPos();
			CountDownLatch latch1 = useValue.getLatch();
			if(latch1 != null)
				latch = latch1;
			while(true){
				region = MineRegionCache.removeMarkGridPosFromRegion(region, DeminFrame.getDeminFrame().getGridByPos(pos));
				poss.append(",").append(pos);
				closeGridNum --;
				regionMineNum ++;
				if(regionMineNum > LayoutConstants.LEFT_MINE)//如果区域内假设雷数量多于剩余雷数,则策略失败
					break;
				
				Set<Integer> canOpenGridPos = new HashSet<Integer>();
				String[] gridsArr;
				List<String> gridsPosList;
				//去除不是雷的块
				for(Entry<String, Integer> entry1 : region.entrySet()){
					String gridsPoss = entry1.getKey();
					Integer mineNum = entry1.getValue();
					gridsArr = gridsPoss.split(",");
					gridsPosList = Arrays.asList(gridsArr);
					if(mineNum == 0){
						for(String gridPos1 : gridsPosList){
							Integer gridPosInt = Integer.parseInt(gridPos1);
							canOpenGridPos.add(gridPosInt);
						}
					}
				}
				
				for(Integer gridPos1 : canOpenGridPos){
					region = MineRegionCache.removeOpenGridPosFromRegion(region, DeminFrame.getDeminFrame().getGridByPos(gridPos1));
					closeGridNum --;
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
					gridsArr = gridsPoss.split(",");
					gridsPosList = Arrays.asList(gridsArr);
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
					for(String gridPos1 : gridPossList){
						Integer gridPosInt = Integer.parseInt(gridPos1);
						MiddleValue setValue = new MiddleValue(gridPosInt, new StringBuilder(poss), closeGridNum, regionMineNum, new HashMap<>(region), null);
						useValueStack.push(setValue);
					}
				}
			}
		}
		latch.countDown();
	}
	
}
