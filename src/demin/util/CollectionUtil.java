package demin.util;

import java.util.ArrayList;
import java.util.List;

import demin.entity.MyGrid;

public class CollectionUtil {

	/**
	 * 补集,即{x|x in source && x not in target}
	 * @param source
	 * @param target
	 * @return
	 */
	public static List<MyGrid> exclude(List<MyGrid> source, List<MyGrid> target){
		List<MyGrid> sourceClone = new ArrayList<MyGrid>();
		sourceClone.addAll(source);
		
		sourceClone.removeIf(grid -> target.contains(grid));
		return sourceClone;
	}
	
	/**
	 * 交集,即{x|x in source && x in target}
	 * @param source
	 * @param target
	 * @return
	 */
	public static List<MyGrid> intersection(List<MyGrid> source, List<MyGrid> target){
		List<MyGrid> sourceClone = new ArrayList<MyGrid>();
		sourceClone.addAll(source);
		
		sourceClone.removeIf(grid -> !target.contains(grid));
		return sourceClone;
	}
	
	/**
	 * 并集,即{x|x in source || x in target}
	 * @param source
	 * @param target
	 * @return
	 */
	public static List<MyGrid> union(List<MyGrid> source, List<MyGrid> target){
		List<MyGrid> sourceClone = new ArrayList<MyGrid>();
		sourceClone.addAll(source);
		
		sourceClone.addAll(exclude(target, source));
		return sourceClone;
	}
	
}
