package demin.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import demin.entity.MyGrid;
import demin.util.CollectionUtil;

public class MineRegionCache {
	
	private static Map<String, Integer> mineRegions = new ConcurrentHashMap<>();
	
	private static Map<String, Integer> newMineRegions = new ConcurrentHashMap<>();
	
	public static boolean canClearNewRegion;
	
	public static void putNewRegion(List<MyGrid> grids, Integer mineNum){
		if(grids != null && !grids.isEmpty()){
			grids.sort((g1, g2) -> g1.getPos() > g2.getPos() ? 1 : -1);
			StringBuilder sb = new StringBuilder();
			List<String> currPosList = new ArrayList<>();
			int index = 0;
			for (MyGrid myGrid : grids) {
				currPosList.add(String.valueOf(myGrid.getPos()));
				if(index == 0)
					sb.append(myGrid.getPos());
				else
					sb.append(",").append(myGrid.getPos());
				index ++;
			}
			if(!newMineRegions.containsKey(sb.toString())){
				//查找是否有与之存在包含关系的区
				List<String> removeKeys = new ArrayList<String>();
				Map<String, Integer> addRegions = new HashMap<String, Integer>();
				for(Entry<String, Integer> entry : newMineRegions.entrySet()){
					String poss = entry.getKey();
					List<String> posList = new ArrayList<>(Arrays.asList(poss.split(",")));
					
					List<String> selfExcludeOther = CollectionUtil.exclude(currPosList, posList);
					List<String> otherExcludeSelf = CollectionUtil.exclude(posList, currPosList);
					if(selfExcludeOther.isEmpty()){//如果自身包含在其他的里面,则移除其他的,在加上其他对自身的补集
						removeKeys.add(poss);
						addRegions.put(CollectionUtil.listToString(otherExcludeSelf, ","), entry.getValue() - mineNum);
					}
					else if(otherExcludeSelf.isEmpty()){
						addRegions.put(CollectionUtil.listToString(selfExcludeOther, ","), mineNum - entry.getValue());
					}
				}
				
				if(!addRegions.isEmpty()){
					for (String key : removeKeys)
						newMineRegions.remove(key);
					newMineRegions.putAll(addRegions);
				}
				else
					newMineRegions.put(sb.toString(), mineNum);
			}
		}
	}
	
	public static void putAllRegion(List<MyGrid> grids, Integer mineNum){
		if(grids != null && !grids.isEmpty()){
			grids.sort((g1, g2) -> g1.getPos() > g2.getPos() ? 1 : -1);
			StringBuilder sb = new StringBuilder();
			List<String> currPosList = new ArrayList<>();
			int index = 0;
			for (MyGrid myGrid : grids) {
				currPosList.add(String.valueOf(myGrid.getPos()));
				if(index == 0)
					sb.append(myGrid.getPos());
				else
					sb.append(",").append(myGrid.getPos());
				index ++;
			}
			if(!mineRegions.containsKey(sb.toString())){
				//查找是否有与之存在包含关系的区
				List<String> removeKeys = new ArrayList<String>();
				Map<String, Integer> addRegions = new HashMap<String, Integer>();
				for(Entry<String, Integer> entry : mineRegions.entrySet()){
					String poss = entry.getKey();
					List<String> posList = new ArrayList<>(Arrays.asList(poss.split(",")));
					
					List<String> selfExcludeOther = CollectionUtil.exclude(currPosList, posList);
					List<String> otherExcludeSelf = CollectionUtil.exclude(posList, currPosList);
					if(selfExcludeOther.isEmpty()){//如果自身包含在其他的里面,则移除其他的,在加上其他对自身的补集
						removeKeys.add(poss);
						addRegions.put(CollectionUtil.listToString(otherExcludeSelf, ","), entry.getValue() - mineNum);
					}
					else if(otherExcludeSelf.isEmpty()){
						addRegions.put(CollectionUtil.listToString(selfExcludeOther, ","), mineNum - entry.getValue());
					}
				}
				
				if(!addRegions.isEmpty()){
					for (String key : removeKeys)
						mineRegions.remove(key);
					mineRegions.putAll(addRegions);
				}
				else
					mineRegions.put(sb.toString(), mineNum);
			}
		}
	}
	
	public static Integer getRegionMineNum(List<MyGrid> grids){
		if(grids != null && !grids.isEmpty()){
			grids.sort((g1, g2) -> g1.getPos() > g2.getPos() ? 1 : -1);
			StringBuilder sb = new StringBuilder();
			int index = 0;
			for (MyGrid myGrid : grids) {
				if(index == 0)
					sb.append(myGrid.getPos());
				else
					sb.append(",").append(myGrid.getPos());
				index ++;
			}
			return mineRegions.get(sb.toString());
		}
		return -1;
	}
	
	/**
	 * 移除已标记为地雷的块
	 * @param grid
	 */
	public static Map<String, Integer> removeMarkGridPosFromRegion(Map<String, Integer> region, MyGrid grid){
		String pos = String.valueOf(grid.getPos());
		List<String> removeKeys = new ArrayList<String>();
		Map<String, Integer> addRegions = new HashMap<String, Integer>();
		for (Entry<String, Integer> entry : region.entrySet()) {
			String poss = entry.getKey();
			Integer mineNum = entry.getValue();
			List<String> posList = new ArrayList<String>();
			posList.addAll(Arrays.asList(poss.split(",")));
			if(posList.contains(pos)){
				removeKeys.add(poss);
				posList.remove(pos);
				int index = 0;
				poss = "";
				for (String string : posList) {
					if(index == 0)
						poss += string;
					else
						poss += "," + string;
					index ++;
				}
				
				if(!"".equals(poss)){
					addRegions.put(poss, --mineNum);
				}
			}
		}
		
		for(String key : removeKeys)
			region.remove(key);
		
		region.putAll(addRegions);
		return region;
	}
	
	/**
	 * 移除已标记为地雷的块
	 * @param grid
	 */
	public static void removeMarkGridPos(MyGrid grid){
		String pos = String.valueOf(grid.getPos());
		List<String> removeKeys = new ArrayList<String>();
		Map<String, Integer> addRegions = new HashMap<String, Integer>();
		for (Entry<String, Integer> entry : mineRegions.entrySet()) {
			String poss = entry.getKey();
			Integer mineNum = entry.getValue();
			List<String> posList = new ArrayList<String>();
			posList.addAll(Arrays.asList(poss.split(",")));
			if(posList.contains(pos)){
				removeKeys.add(poss);
				posList.remove(pos);
				int index = 0;
				poss = "";
				for (String string : posList) {
					if(index == 0)
						poss += string;
					else
						poss += "," + string;
					index ++;
				}
				
				if(!"".equals(poss)){
					addRegions.put(poss, --mineNum);
				}
			}
		}
		
		for(String key : removeKeys)
			mineRegions.remove(key);
		
		mineRegions.putAll(addRegions);
	}
	
	/**
	 * 移除已标记为打开的块
	 * @param grid
	 */
	public static Map<String, Integer> removeOpenGridPosFromRegion(Map<String, Integer> region, MyGrid grid){
		String pos = String.valueOf(grid.getPos());
		List<String> removeKeys = new ArrayList<String>();
		Map<String, Integer> addRegions = new HashMap<String, Integer>();
		for (Entry<String, Integer> entry : region.entrySet()) {
			String poss = entry.getKey();
			Integer mineNum = entry.getValue();
			List<String> posList = new ArrayList<String>();
			posList.addAll(Arrays.asList(poss.split(",")));
			if(posList.contains(pos)){
				removeKeys.add(poss);
				posList.remove(pos);
				int index = 0;
				poss = "";
				for (String string : posList) {
					if(index == 0)
						poss += string;
					else
						poss += "," + string;
					index ++;
				}
				
				if(!"".equals(poss)){
					addRegions.put(poss, mineNum);
				}
			}
		}
		
		for(String key : removeKeys)
			region.remove(key);
		
		region.putAll(addRegions);
		return region;
	}
	
	/**
	 * 移除已标记为打开的块
	 * @param grid
	 */
	public static void removeOpenGridPos(MyGrid grid){
		String pos = String.valueOf(grid.getPos());
		List<String> removeKeys = new ArrayList<String>();
		Map<String, Integer> addRegions = new HashMap<String, Integer>();
		for (Entry<String, Integer> entry : mineRegions.entrySet()) {
			String poss = entry.getKey();
			Integer mineNum = entry.getValue();
			List<String> posList = new ArrayList<String>();
			posList.addAll(Arrays.asList(poss.split(",")));
			if(posList.contains(pos)){
				removeKeys.add(poss);
				posList.remove(pos);
				int index = 0;
				poss = "";
				for (String string : posList) {
					if(index == 0)
						poss += string;
					else
						poss += "," + string;
					index ++;
				}
				
				if(!"".equals(poss)){
					addRegions.put(poss, mineNum);
				}
			}
		}
		
		for(String key : removeKeys)
			mineRegions.remove(key);
		
		mineRegions.putAll(addRegions);
	}
	
	public static boolean hasGrid(Map<String, Integer> region, MyGrid grid){
		String pos = String.valueOf(grid.getPos());
		for(Entry<String, Integer> entry : region.entrySet()){
			String poss = entry.getKey();
			List<String> posList = new ArrayList<String>();
			posList.addAll(Arrays.asList(poss.split(",")));
			if(posList.contains(pos)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasGridPos(Map<String, Integer> region, String pos){
		for(Entry<String, Integer> entry : region.entrySet()){
			String poss = entry.getKey();
			List<String> posList = new ArrayList<String>();
			posList.addAll(Arrays.asList(poss.split(",")));
			if(posList.contains(pos)){
				return true;
			}
		}
		return false;
	}
	
	public static Map<String, Integer> mergeRegion(Map<String, Integer> region1, Map<String, Integer> region2){
		for(Entry<String, Integer> entry2 : region2.entrySet()){
			String poss2 = entry2.getKey();
			List<String> posList2 = new ArrayList<>(Arrays.asList(poss2.split(",")));
			int mineNum2 = entry2.getValue();
			if(!region1.containsKey(poss2)){
				//查找是否有与之存在包含关系的区
				List<String> removeKeys = new ArrayList<String>();
				Map<String, Integer> addRegions = new HashMap<String, Integer>();
				for(Entry<String, Integer> entry : region1.entrySet()){
					String poss = entry.getKey();
					List<String> posList = new ArrayList<>(Arrays.asList(poss.split(",")));
					
					List<String> selfExcludeOther = CollectionUtil.exclude(posList2, posList);
					List<String> otherExcludeSelf = CollectionUtil.exclude(posList, posList2);
					if(selfExcludeOther.isEmpty()){//如果自身包含在其他的里面,则移除其他的,在加上其他对自身的补集
						removeKeys.add(poss);
						addRegions.put(CollectionUtil.listToString(otherExcludeSelf, ","), entry.getValue() - mineNum2);
					}
					else if(otherExcludeSelf.isEmpty()){
						addRegions.put(CollectionUtil.listToString(selfExcludeOther, ","), mineNum2 - entry.getValue());
					}
				}
				
				if(!addRegions.isEmpty()){
					for (String key : removeKeys)
						region1.remove(key);
					region1.putAll(addRegions);
				}
				else
					region1.put(poss2, mineNum2);
			}
		}
		return region1;
	}
	
	public static Map<String, Integer> getIntersectRegion(Map<String, Integer> region1, Map<String, Integer> region2){
		Map<String, Integer> intersection = new HashMap<String, Integer>();
		intersection.putAll(region2);
		if(!region2.isEmpty() && !region1.isEmpty()){
			for(Entry<String, Integer> entry2 : region2.entrySet()){
				String poss2 = entry2.getKey();
				List<String> posList2 = new ArrayList<>(Arrays.asList(poss2.split(",")));
				for(String pos : posList2){
					for(Entry<String, Integer> entry1 : region1.entrySet()){
						String poss1 = entry1.getKey();
						if(!intersection.containsKey(poss1)){
							List<String> posList1 = new ArrayList<>(Arrays.asList(poss1.split(",")));
							if(posList1.contains(pos)){
								intersection.put(poss1, entry1.getValue());
								continue;
							}
						}
					}
				}
			}
		}
		return intersection;
	}
	
	public static void clearNewRegion(){
		newMineRegions.clear();
		setCanClearNewRegion(false);
	}
	
	public static void clearAllRegion(){
		mineRegions.clear();
	}
	
	public static void setAllRegions(Map<String, Integer> regions){
		MineRegionCache.mineRegions = regions;
	}
	
	public static Map<String, Integer> getNewRegions(){
		return newMineRegions;
	}
	
	public static Map<String, Integer> getAllRegions(){
		return mineRegions;
	}

	public static boolean isCanClearNewRegion() {
		return canClearNewRegion;
	}

	public static void setCanClearNewRegion(boolean canClearNewRegion) {
		MineRegionCache.canClearNewRegion = canClearNewRegion;
	}
	
}
