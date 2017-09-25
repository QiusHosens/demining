package demin.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import demin.constants.Constants;

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
		if(m == 0)
			return new BigDecimal(1);
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
	
	/**
	 * 计算同底组合之间相差的倍数
	 * @param n 底数
	 * @param m1 组合的选择数
	 * @param m2 
	 * @return
	 */
	public static BigDecimal combinateDivide(int n, int m1, int m2){
		if(m1 < m2)
			return new BigDecimal(1).divide(combinateDivide(n, m2, m1), Constants.PROBABILITY_SCALE, 0);
		int len = m1 - m2;
		BigDecimal denom = new BigDecimal(1);//分母
		BigDecimal mole = new BigDecimal(1);//分子
		for(int i = 0; i < len; i ++){
			mole = mole.multiply(new BigDecimal(n - m1 + 1 + i));
			denom = denom.multiply(new BigDecimal(m1 - i));
		}
		if(denom.equals(0))
			System.out.println("错误:n:" + n + " m1:" + m1 + " m2:" + m2 + " denom:" + denom + " mole:" + mole);
		return mole.divide(denom, Constants.PROBABILITY_SCALE, 0);
	}
	
	public static <T> String listToString(Collection<T> list, String separator){
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
	
	public static <T> boolean listEquals(Collection<T> list1, Collection<T> list2){
		for (T t : list2)
			if(!list1.contains(t))
				return false;
		
		for (T t : list1)
			if(!list2.contains(t))
				return false;
		return true;
	}
	
	public static void main(String[] args){
		System.out.println(combinateDivide(10, 2, 3));
	}
	
}
