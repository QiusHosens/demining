package demin.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CollectionUtil {

	/**
	 * 补集,即{x|x in source && x not in target}
	 * @param source
	 * @param target
	 * @return
	 */
	public static <T> List<T> exclude(List<T> source, List<T> target){
		List<T> sourceClone = new ArrayList<T>();
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
	public static <T> List<T> intersection(List<T> source, List<T> target){
		List<T> sourceClone = new ArrayList<T>();
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
	public static <T> List<T> union(List<T> source, List<T> target){
		List<T> sourceClone = new ArrayList<T>();
		sourceClone.addAll(source);
		
		sourceClone.addAll(exclude(target, source));
		return sourceClone;
	}
	
	/**
	 * 组合
	 * @param n
	 * @param m
	 */
	public static BigDecimal combination(int n, int m){
		if(m > n / 2)
			return combination(n, n - m);
		else{
			BigDecimal denom = new BigDecimal(1);//分母
			BigDecimal mole = new BigDecimal(1);//分子
			for(int i = 0; i < m; i ++){
				mole = mole.multiply(new BigDecimal(n - i)); 
				denom = denom.multiply(new BigDecimal(m - i));
			}
			if(denom.equals(0))
				System.out.println("错误:n:" + n + " m:" + m + " denom:" + denom + " mole:" + mole);
			return mole.divide(denom);
		}
	}
	
	public static <T> String listToString(List<T> list, String separator){
		int index = 0;
		StringBuilder sb = new StringBuilder();
		for (T t : list) {
			if(index == 0)
				sb.append(t.toString());
			else
				sb.append(",").append(t.toString());
			index ++;
		}
		return sb.toString();
	}
	
}
