package demin.thread;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import demin.constants.Constants;
import demin.constants.LayoutConstants;
import demin.entity.GridPossible;
import demin.entity.MiddleValue;
import demin.entity.Strategy;
import demin.util.CollectionUtil;

public class StrategyRunnable implements Runnable {
	
	public static int count;
	
	@Override
	public void run() {
		while(!MiddleValueCache.isEmpty()){
			MiddleValue useValueStack = MiddleValueCache.poll();
			if(useValueStack != null)
				generateStrategy(useValueStack);
		}
		
//		Map<String, CountDownLatch> latchs = LatchCache.getCurrLatch();
//		CountDownLatch currLatch = latchs.get("currLatch");
//		CountDownLatch preLatch = latchs.get("preLatch");
//		System.out.println(latchs);
//		if(preLatch == null)
//			currLatch.countDown();
//		else{
//			try {
//				preLatch.await();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			currLatch.countDown();
//		}
		CountDownLatch preLatch;
		if((preLatch = LatchCache.countDownAndGetPreLatch()) != null){
			try {
				preLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			
	}
	
	private void generateStrategy(MiddleValue useValue){
		Map<String, Integer> region = useValue.getCommonRegion();
		Map<String, Integer> unCommonRegion = useValue.getUnCommonRegion();
		StringBuilder poss = useValue.getGridPos();
		int closeGridNum = useValue.getCloseGridNum();
		int regionMineNum = useValue.getRegionMineNum();
		int pos = useValue.getCurrPos();
		while(pos != -1){
			count ++;
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
			
//			if(region.isEmpty()){
//				if(LayoutConstants.LEFT_MINE - regionMineNum > closeGridNum)
//					break;
//				Strategy strategy = new Strategy(poss.substring(1).toString(), closeGridNum, regionMineNum);
//				StrategyDeduceCache.add2(strategy);
//				break;
//			}
			
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
						break;
					}
				}
				if(isFind)
					break;
			}
			if(!isFind){
				break;
			}
		}
		
		Map<String, Integer> commonRegion = getCommonRegion(region);
		Map<String, Integer> newUnCommonRegion = exclude(region, commonRegion);
		regionMineNum += getUnCommonRegionMineNum(newUnCommonRegion);
		closeGridNum -= getUnCommonRegionGridNum(newUnCommonRegion);
		unCommonRegion.putAll(newUnCommonRegion);
		region = commonRegion;
		
		if(region != null && !region.isEmpty()){
			for(Entry<String, Integer> entry1 : region.entrySet()){
				String gridPoss = entry1.getKey();
				String[] gridPossList = gridPoss.split(",");
				Map<String, Integer> cloneRegion = new HashMap<String, Integer>(region);
				for(String gridPos1 : gridPossList){
					Integer gridPosInt = Integer.parseInt(gridPos1);
					if(!validate(cloneRegion))
						break;
					MiddleValue setValue = new MiddleValue(gridPosInt, new StringBuilder(poss), closeGridNum, regionMineNum, new HashMap<>(cloneRegion), new HashMap<>(unCommonRegion));
					MiddleValueCache.add(setValue);
					//当前假定地雷在同一区域内,下次不能再假定为地雷,会生成重复策略
					cloneRegion = MineRegionCache.removeOpenGridPosFromRegion(cloneRegion, gridPos1);
					closeGridNum --;
				}
				break;
			}
			//检查是否需要增加线程
			StrategyGenerator.getStrategyGenerator().check();
		}
		else{
			if(LayoutConstants.LEFT_MINE - regionMineNum > closeGridNum)
				return;
			GroupStrategy group = getGroupStrategy(unCommonRegion);
			if(!"".equals(poss.toString()))
				poss = new StringBuilder(poss.substring(1));
			Strategy strategy = new Strategy(poss.toString(), 
					group.getPossible(), group.getPos(), closeGridNum, regionMineNum);
			StrategyDeduceCache.add2(strategy);
		}
	}
	
	private Map<String, Integer> getCommonRegion(Map<String, Integer> region){
		Map<String, Integer> common = new HashMap<String, Integer>();
		Set<String> poss = new HashSet<String>();
		Set<String> repeatPos = new HashSet<String>();
		for(Entry<String, Integer> entry : region.entrySet()){
			String posstr = entry.getKey();
			String[] posList = posstr.split(",");
			for (String pos : posList) {
				if(poss.contains(pos)){
					repeatPos.add(pos);
					break;
				}else
					poss.add(pos);
			}
		}
		
		for(Entry<String, Integer> entry : region.entrySet()){
			String posstr = entry.getKey();
			String[] posList = posstr.split(",");
			for (String pos : posList) {
				if(repeatPos.contains(pos)){
					common.put(posstr, entry.getValue());
					break;
				}
			}
		}
		return common;
	}
	
	private Map<String, Integer> exclude(Map<String, Integer> region1, Map<String, Integer> region2){
		Map<String, Integer> region = new HashMap<String, Integer>();
		for(Entry<String, Integer> entry : region1.entrySet()){
			String poss = entry.getKey();
			if(!region2.containsKey(poss)){
				Integer mineNum = entry.getValue();
				region.put(poss, mineNum);
			}
		}
		return region;
	}
	
	private int getUnCommonRegionMineNum(Map<String, Integer> region){
		int mineNum = 0;
		for(Entry<String, Integer> entry : region.entrySet())
			mineNum += entry.getValue();
		return mineNum;
	}
	
	private int getUnCommonRegionGridNum(Map<String, Integer> region){
		int gridNum = 0;
		for(Entry<String, Integer> entry : region.entrySet())
			gridNum += entry.getKey().split(",").length;
		return gridNum;
	}
	
	private GroupStrategy getGroupStrategy(Map<String, Integer> region){
		List<GridPossible> allPosList = new ArrayList<>();
		BigDecimal allPossible = new BigDecimal(1);
		
		for(Entry<String, Integer> entry : region.entrySet()){
			String poss = entry.getKey();
			String[] posList = poss.split(",");
			int mineNum = entry.getValue();
			allPossible = allPossible.multiply(CollectionUtil.combination(posList.length, mineNum));
		}
		
		for(Entry<String, Integer> entry : region.entrySet()){
			String poss = entry.getKey();
			String[] posList = poss.split(",");
			int mineNum = entry.getValue();
			BigDecimal isMinePossible = allPossible.multiply(CollectionUtil.combination(posList.length - 1, mineNum - 1)).divide(CollectionUtil.combination(posList.length, mineNum), Constants.PROBABILITY_SCALE, 0);
			for(String pos : posList)
				allPosList.add(new GridPossible(Integer.parseInt(pos), isMinePossible));
		}
		return new GroupStrategy(allPosList, allPossible);
	}
	
	public boolean validate(Map<String, Integer> region){
		for(Entry<String, Integer> entry : region.entrySet()){
			if(entry.getKey().split(",").length < entry.getValue())
				return false;
		}
		return true;
	}
	
	class GroupStrategy{
		
		private List<GridPossible> pos;
		
		private BigDecimal possible;
		
		public GroupStrategy(List<GridPossible> pos, BigDecimal possible){
			this.pos = pos;
			this.possible = possible;
		}

		public List<GridPossible> getPos() {
			return pos;
		}

		public void setPos(List<GridPossible> pos) {
			this.pos = pos;
		}

		public BigDecimal getPossible() {
			return possible;
		}

		public void setPossible(BigDecimal possible) {
			this.possible = possible;
		}
		
	}
	
}
