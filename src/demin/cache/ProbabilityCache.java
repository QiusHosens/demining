package demin.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import demin.entity.Probability;

public class ProbabilityCache {

	private static List<Probability> probabilitys = new ArrayList<>();
	
	public static void add(Probability probability){
		boolean isFind = false;
		int pos = probability.getPos();
		int index = 0;
		for(int len = probabilitys.size(); index < len; index ++){
			Probability p1 = probabilitys.get(index);
			if(p1.getPos() == pos){
				isFind = true;
				break;
			}
		}
		if(isFind)
			probabilitys.remove(index);
		probabilitys.add(probability);
	}
	
	public static void addAll(Collection<Probability> ps){
		for (Probability probability : ps) {
			add(probability);
		}
	}
	
	public static List<Probability> get(){
		return probabilitys;
	}
	
	public static void clear(){
		probabilitys.clear();
	}
	
}
