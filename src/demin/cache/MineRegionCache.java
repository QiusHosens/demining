package demin.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import demin.entity.MyGrid;
import demin.util.CollectionUtil;

public class MineRegionCache {
	
	private static Map<String, Integer> mineRegions = new HashMap<>();
	
	public static void putRegion(List<MyGrid> grids, Integer mineNum){
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
	
	public static void clear(){
		mineRegions.clear();
	}
	
	public static Map<String, Integer> getRegions(){
		return mineRegions;
	}
	
}
