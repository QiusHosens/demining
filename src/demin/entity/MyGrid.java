package demin.entity;

import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import demin.cache.MineRegionCache;
import demin.constants.Constants;
import demin.constants.GridStateConstants;
import demin.constants.LayoutConstants;
import demin.window.DeminFrame;

public class MyGrid extends Button {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5167891564356011040L;
	
	/**
	 * 位置
	 */
	private Integer pos;
	
	/**
	 * 是否是雷
	 */
	private boolean isMine = false;
	
	/**
	 * 周围雷的数量
	 */
	private Integer mineNum = 0;
	
	/**
	 * 状态</p>
	 * 0
	 */
	private int state = GridStateConstants.GRID_STATE_CLOSE;
	
	/**
	 * 是否全部展开
	 */
	private boolean isFullOpen = false;
	
	/**
	 * 格子的图片
	 */
	private Image image;
	
	/**
	 * 西北方格子
	 */
	private MyGrid wnGrid;
	
	/**
	 * 北方格子
	 */
	private MyGrid nGrid;
	
	/**
	 * 东北方格子
	 */
	private MyGrid enGrid;
	
	/**
	 * 东方格子
	 */
	private MyGrid eGrid;
	
	/**
	 * 东南方格子
	 */
	private MyGrid esGrid;
	
	/**
	 * 南方格子
	 */
	private MyGrid sGrid;
	
	/**
	 * 西南方格子
	 */
	private MyGrid wsGrid;
	
	/**
	 * 西方格子
	 */
	private MyGrid wGrid;
	
	public Integer getPos() {
		return pos;
	}

	public void setPos(Integer pos) {
		this.pos = pos;
	}

	public boolean isMine() {
		return isMine;
	}

	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}

	public Integer getMineNum() {
		return mineNum;
	}

	public void setMineNum(Integer mineNum) {
		this.mineNum = mineNum;
	}

	public MyGrid getWnGrid() {
		return wnGrid;
	}

	public void setWnGrid(MyGrid wnGrid) {
		this.wnGrid = wnGrid;
	}

	public MyGrid getnGrid() {
		return nGrid;
	}

	public void setnGrid(MyGrid nGrid) {
		this.nGrid = nGrid;
	}

	public MyGrid getEnGrid() {
		return enGrid;
	}

	public void setEnGrid(MyGrid enGrid) {
		this.enGrid = enGrid;
	}

	public MyGrid geteGrid() {
		return eGrid;
	}

	public void seteGrid(MyGrid eGrid) {
		this.eGrid = eGrid;
	}

	public MyGrid getEsGrid() {
		return esGrid;
	}

	public void setEsGrid(MyGrid esGrid) {
		this.esGrid = esGrid;
	}

	public MyGrid getsGrid() {
		return sGrid;
	}

	public void setsGrid(MyGrid sGrid) {
		this.sGrid = sGrid;
	}

	public MyGrid getWsGrid() {
		return wsGrid;
	}

	public void setWsGrid(MyGrid wsGrid) {
		this.wsGrid = wsGrid;
	}

	public MyGrid getwGrid() {
		return wGrid;
	}

	public void setwGrid(MyGrid wGrid) {
		this.wGrid = wGrid;
	}

	public boolean isFullOpen() {
		return isFullOpen;
	}

	public void setFullOpen(boolean isFullOpen) {
		this.isFullOpen = isFullOpen;
	}

	public int getState() {
		return state;
	}
	
	public void setState(int state) {
		if(this.state == state)
			return;
		this.state = state;
		DeminFrame.getDeminFrame().decreseCloseCount();
		switch (state) {
		case GridStateConstants.GRID_STATE_CLOSE_MARK_MINE:
			if(image == null){
				image = GridStateConstants.GRID_IMAGE_MARK_MINE;
				image.flush();
				this.prepareImage(image, Constants.SINGLE_WIDTH - 6, Constants.SINGLE_HEIGHT - 6, null);
			}else{
				image = GridStateConstants.GRID_IMAGE_MARK_MINE;
				image.flush();
				this.imageUpdate(GridStateConstants.GRID_IMAGE_MARK_MINE, 16, 3, 3, Constants.SINGLE_WIDTH - 6, Constants.SINGLE_HEIGHT - 6);
			}
			DeminFrame.getDeminFrame().decreaseMineNum();
			MineRegionCache.removeMarkGridPos(this);
			break;
		case GridStateConstants.GRID_STATE_OPEN_IS_MINE:
			if(image == null){
				image = GridStateConstants.GRID_IMAGE_IS_MINE;
//				image.flush();
				this.prepareImage(image, Constants.SINGLE_WIDTH - 6, Constants.SINGLE_HEIGHT - 6, null);
			}else{
				image = GridStateConstants.GRID_IMAGE_IS_MINE;
//				image.flush();
				this.imageUpdate(GridStateConstants.GRID_IMAGE_IS_MINE, 32, 3, 3, Constants.SINGLE_WIDTH - 6, Constants.SINGLE_HEIGHT - 6);
			}
			break;
		default:
			if(image != null){
				image = null;
				this.imageUpdate(null, 1, 0, 0, 0, 0);
			}
			MineRegionCache.removeMarkGridPos(this);
			break;
		}
		refresh();
	}
	
	/**
	 * 打开当前块
	 */
	public void open(){
		if(LayoutConstants.GAME_IS_OVER)
			return;
		if(isMine){
			this.setState(GridStateConstants.GRID_STATE_OPEN_IS_MINE);
			DeminFrame.getDeminFrame().gameOver(false);
		}else{
			this.setState(GridStateConstants.GRID_STATE_OPEN_ISNOT_MINE);
			this.setLabel(mineNum.toString());
			this.setBackground(new Color(255, 255, 255));
			
			Integer closeCount = DeminFrame.getDeminFrame().getCloseGridCount();
			if(closeCount == 0)
				DeminFrame.getDeminFrame().gameOver(true);
			
			if(LayoutConstants.MODEL_AUTO == Constants.MODEL_SEMI_AUTO_TEXT || LayoutConstants.MODEL_AUTO == Constants.MODEL_AUTO_TEXT){
				int markCount = getMarkCount();
				
				if(LayoutConstants.MODEL_AUTO == Constants.MODEL_SEMI_AUTO_TEXT && (mineNum == 0 || markCount == mineNum)){
					mutiOpen();
				}
				else if(LayoutConstants.MODEL_AUTO == Constants.MODEL_AUTO_TEXT){
					List<MyGrid> unOpenGrids = getUnOpenNotMarkGrids();
					if(mineNum == 0 || markCount + unOpenGrids.size() == mineNum)
						autoMarkOpen();
					else if(markCount == mineNum)
						mutiOpen();
					else
						DeminFrame.getDeminFrame().switchAutoModel();
				}
			}
		}
	}
	
	/**
	 * 打开多个,即自动打开可推测不是雷的块
	 */
	public void mutiOpen(){
		if(GridStateConstants.GRID_STATE_CLOSE == this.getState())
			this.open();
		
		int markCount = getMarkCount();
		
		if(markCount == mineNum){
			if(this.getWnGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.getWnGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.getWnGrid().getState()){
				this.getWnGrid().open();
			}
			if(this.getnGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.getnGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.getnGrid().getState()){
				this.getnGrid().open();
			}
			if(this.getEnGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.getEnGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.getEnGrid().getState()){
				this.getEnGrid().open();
			}
			if(this.geteGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.geteGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.geteGrid().getState()){
				this.geteGrid().open();
			}
			if(this.getEsGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.getEsGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.getEsGrid().getState()){
				this.getEsGrid().open();
			}
			if(this.getsGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.getsGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.getsGrid().getState()){
				this.getsGrid().open();
			}
			if(this.getWsGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.getWsGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.getWsGrid().getState()){
				this.getWsGrid().open();
			}
			if(this.getwGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.getwGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.getwGrid().getState()){
				this.getwGrid().open();
			}
			this.setFullOpen(true);//已经全部打开
		}
		else{
			this.open();
		}
	}
	
	/**
	 * 自动标记打开
	 */
	public void autoMarkOpen(){
		if(GridStateConstants.GRID_STATE_CLOSE == this.getState())
			this.open();
		
		List<MyGrid> unOpenGrids = getUnOpenNotMarkGrids();
		int markCount = getMarkCount();
		
		if(markCount + unOpenGrids.size() == mineNum){
			for(MyGrid grid : unOpenGrids){
				grid.setState(GridStateConstants.GRID_STATE_CLOSE_MARK_MINE);
			}
		}
		else if(markCount == mineNum)
			mutiOpen();
		else if(mineNum == 0){
			if(this.getWnGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.getWnGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.getWnGrid().getState()){
				this.getWnGrid().open();
			}
			if(this.getnGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.getnGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.getnGrid().getState()){
				this.getnGrid().open();
			}
			if(this.getEnGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.getEnGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.getEnGrid().getState()){
				this.getEnGrid().open();
			}
			if(this.geteGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.geteGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.geteGrid().getState()){
				this.geteGrid().open();
			}
			if(this.getEsGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.getEsGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.getEsGrid().getState()){
				this.getEsGrid().open();
			}
			if(this.getsGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.getsGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.getsGrid().getState()){
				this.getsGrid().open();
			}
			if(this.getWsGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.getWsGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.getWsGrid().getState()){
				this.getWsGrid().open();
			}
			if(this.getwGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE != this.getwGrid().getState() && GridStateConstants.GRID_STATE_CLOSE == this.getwGrid().getState()){
				this.getwGrid().open();
			}
			this.setFullOpen(true);
		}
		else{
			this.open();
		}
	}
	
	/**
	 * 获取标记为雷的格子
	 * @return
	 */
	public int getMarkCount(){
		int markCount = getGridByStateInReflect(GridStateConstants.GRID_STATE_CLOSE_MARK_MINE).size();//标记数量
		return markCount;
	}
	
	/**
	 * 获取初始状态(即没打开也没标记为雷)的格子
	 * @return
	 */
	public List<MyGrid> getUnOpenNotMarkGrids(){
		List<MyGrid> unOpenGrids = getGridByStateInReflect(GridStateConstants.GRID_STATE_CLOSE);
		return unOpenGrids;
	}
	
	public List<MyGrid> getOpenIsNotMineGrids(){
		List<MyGrid> openGrids = getGridByStateInReflect(GridStateConstants.GRID_STATE_OPEN_ISNOT_MINE);
		return openGrids;
	}
	
	public List<MyGrid> getOpenIsNotMineAndNotFullOpenGrids(){
		List<MyGrid> openGrids = getOpenIsNotMineGrids();
		List<MyGrid> notFullOpenGrids = new ArrayList<MyGrid>();
		for (MyGrid myGrid : openGrids) {
			if(!myGrid.isFullOpen)
				notFullOpenGrids.add(myGrid);
		}
		return notFullOpenGrids;
	}
	
	/**
	 * 根据状态获取周围八宫格的格子,state为-1时,获取所有格子
	 * @param state
	 * @return
	 */
	private List<MyGrid> getGridByStateInReflect(int state){
		List<MyGrid> grids = new ArrayList<MyGrid>();
		Method[] methods = this.getClass().getMethods();
		try {
			for (Method method : methods) {
				if(MyGrid.class.equals(method.getReturnType()) && method.getName().endsWith("Grid")
						&& method.getName().startsWith("get")){
					MyGrid grid = (MyGrid) method.invoke(this, new Object[]{});
					if(grid != null){
						if(-1 == state)
							grids.add(grid);
						else if(state == grid.getState())
							grids.add(grid);
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return grids;
	}
	
	/**
	 * 获取本格子是否能将周围八宫格格子全部打开
	 * @return
	 */
	public boolean canFullOpen(){
		//未打开不可知
		if(GridStateConstants.GRID_STATE_CLOSE == state)
			return false;
		
		List<MyGrid> unOpenGrids = getUnOpenNotMarkGrids();
		int markCount = getMarkCount();
		
		if(mineNum == 0 || markCount == mineNum || markCount + unOpenGrids.size() == mineNum)
			return true;
		return false;
	}
	
	/**
	 * 根据相邻块推导并标记或打开块
	 */
	public boolean markOrOpenGridDeduceByNearGrid(){
		if(GridStateConstants.GRID_STATE_CLOSE == state || GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == state)
			return false;
		
		//与格子周围打开不是雷的块中比较,标记雷或打开不是雷的格子
		//找相邻打开格子
		List<MyGrid> grids = getOpenIsNotMineAndNotFullOpenGrids();
		//找相邻格子的相邻打开格子
		//所有格子
		List<MyGrid> allGrids = getGridByStateInReflect(-1);
		for (MyGrid myGrid : allGrids) {
			List<MyGrid> oneGrids = myGrid.getOpenIsNotMineAndNotFullOpenGrids();
			oneGrids.removeIf(grid -> grid.equals(this) || grids.contains(grid));
			grids.addAll(oneGrids);
		}
		
		if(grids.isEmpty())
			return false;
		
		boolean isChange = false;
		for (MyGrid grid : grids) {
			int selfMarkCount = getMarkCount();//标记为雷的格子数量
			int selfUnsureCount = mineNum - selfMarkCount;//周围不确定地雷的数量
			List<MyGrid> selfCloseGrids = getUnOpenNotMarkGrids();
			
			int markCount = grid.getMarkCount();
			int unsureCount = grid.getMineNum() - markCount;
			List<MyGrid> closeGrids = grid.getUnOpenNotMarkGrids();
			List<MyGrid> selfExcludeOtherGrids = exclude(selfCloseGrids, closeGrids);
			List<MyGrid> otherExcludeSelfGrids = exclude(closeGrids, selfCloseGrids);
			List<MyGrid> intersectionGrids = intersection(selfCloseGrids, closeGrids);
			List<MyGrid> unionGrids = union(selfCloseGrids, closeGrids);
			
			//如果自身不确定地雷数量减去其他块不确定地雷数量大于等于自身排除其他后的块数量,则排除后的块都是地雷
			if(selfExcludeOtherGrids.size() > 0 && selfUnsureCount - unsureCount == selfExcludeOtherGrids.size() && selfUnsureCount - unsureCount == 1){
				for (MyGrid myGrid : selfExcludeOtherGrids) {
					myGrid.setState(GridStateConstants.GRID_STATE_CLOSE_MARK_MINE);
				}
				MineRegionCache.putRegion(intersectionGrids, selfUnsureCount - unsureCount);
				//如果不确定地雷差值等于排除后的地雷数,并且不确定地雷数为1,则其他块不是地雷
				for (MyGrid myGrid : otherExcludeSelfGrids) {
					myGrid.autoMarkOpen();
				}
				return true;
			}
			else if(otherExcludeSelfGrids.size() > 0 && unsureCount - selfUnsureCount == otherExcludeSelfGrids.size() && unsureCount - selfUnsureCount == 1){
				for (MyGrid myGrid : otherExcludeSelfGrids) {
					myGrid.setState(GridStateConstants.GRID_STATE_CLOSE_MARK_MINE);
				}
				MineRegionCache.putRegion(intersectionGrids, unsureCount - selfUnsureCount);
				
				for (MyGrid myGrid : selfExcludeOtherGrids) {
					myGrid.autoMarkOpen();
				}
				return true;
			}
			//如果其中一块只有交集没打开了,即确定雷在交集中
			if(!intersectionGrids.isEmpty() && (isValueEqual(selfCloseGrids, intersectionGrids) || isValueEqual(closeGrids, intersectionGrids))){
				if(isValueEqual(selfCloseGrids, intersectionGrids)){
					//如果没打开的数量大于等于没确定的数量,则地雷在这些块中;等于的时候,这些是雷
					if(intersectionGrids.size() == selfUnsureCount){
						for (MyGrid myGrid : intersectionGrids) {
							myGrid.setState(GridStateConstants.GRID_STATE_CLOSE_MARK_MINE);
						}
						isChange = true;
					}
					else if(selfUnsureCount > 0){//如果不等,则这块区域中selfUnsureCount个雷
						MineRegionCache.putRegion(intersectionGrids, selfUnsureCount);
					}
					
					//如果
					if(selfUnsureCount == unsureCount && !otherExcludeSelfGrids.isEmpty()){
						for (MyGrid myGrid : otherExcludeSelfGrids) {
							myGrid.autoMarkOpen();
						}
						isChange = true;
					}
				}
				if(isValueEqual(closeGrids, intersectionGrids)){
					if(intersectionGrids.size() == unsureCount){
						for (MyGrid myGrid : intersectionGrids) {
							myGrid.setState(GridStateConstants.GRID_STATE_CLOSE_MARK_MINE);
						}
						isChange = true;
					}
					else if(unsureCount > 0){//如果不等,则这块区域中unsureCount个雷
						MineRegionCache.putRegion(intersectionGrids, unsureCount);
					}
					
					if(unsureCount == selfUnsureCount && !selfExcludeOtherGrids.isEmpty()){
						for (MyGrid myGrid : selfExcludeOtherGrids) {
							myGrid.autoMarkOpen();
						}
						isChange = true;
					}
				}
			}
		}
		return isChange;
	}
	
	/**
	 * 补集,即{x|x in source && x not in target}
	 * @param source
	 * @param target
	 * @return
	 */
	public List<MyGrid> exclude(List<MyGrid> source, List<MyGrid> target){
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
	public List<MyGrid> intersection(List<MyGrid> source, List<MyGrid> target){
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
	public List<MyGrid> union(List<MyGrid> source, List<MyGrid> target){
		List<MyGrid> sourceClone = new ArrayList<MyGrid>();
		sourceClone.addAll(source);
		
		sourceClone.addAll(exclude(target, source));
		return sourceClone;
	}
	
	/**
	 * 判断list是否相同
	 * @param source
	 * @param target
	 * @return
	 */
	public boolean isValueEqual(List<MyGrid> source, List<MyGrid> target){
		if(source.size() != target.size())
			return false;
		for(int index = 0, len = source.size(); index < len; index ++)
			if(!source.get(index).equals(target.get(index)))
				return false;
		return true;
	}
	
	public void paint(Graphics g){
		super.paint(g);
		g.drawImage(image, 3, 3, this.getWidth() - 6, this.getHeight() - 6, null);
	}
	
	private void refresh(){
		validate();
		repaint();
	}
	
	public boolean equals(Object o){
		if(o instanceof MyGrid)
			if(pos == ((MyGrid)o).getPos())
				return true;
		return false;
	}
	
}
