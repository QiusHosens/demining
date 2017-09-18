package demin.cache;

import java.util.ArrayList;
import java.util.List;
import demin.entity.OneGame;

public class GameCache {
	
	private static List<OneGame> gameCache = new ArrayList<>();

	public static OneGame getGame(int index) {
		return gameCache.get(index);
	}
	
	public static OneGame getLastGame(){
		if(gameCache.size() < 1)
			return null;
		return getGame(gameCache.size() - 1);
	}
	
	public static OneGame removeLastGame(){
		return gameCache.remove(gameCache.size() - 1);
	}

	public static void setMines(List<Integer> mines) {
		OneGame one = new OneGame(mines, null);
		gameCache.add(one);
	}
	
	public static void setStep(String pos){
		OneGame one = getLastGame();
		String step = one.getStep();
		if(step == null || "".equals(step)){
			step = pos;
		}
		else{
			step += "," + pos;
		}
		one.setStep(step);
	}
	
	public static int size(){
		return gameCache.size();
	}
	
}
