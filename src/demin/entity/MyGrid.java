package demin.entity;

import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

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
		this.state = state;
		switch (state) {
		case GridStateConstants.GRID_STATE_CLOSE_MARK_MINE:
			if(image == null){
				image = GridStateConstants.GRID_IMAGE_MARK_MINE;
//				image.flush();
				this.prepareImage(image, Constants.SINGLE_WIDTH - 6, Constants.SINGLE_HEIGHT - 6, null);
			}else{
				image = GridStateConstants.GRID_IMAGE_MARK_MINE;
//				image.flush();
				this.imageUpdate(GridStateConstants.GRID_IMAGE_MARK_MINE, 16, 3, 3, Constants.SINGLE_WIDTH - 6, Constants.SINGLE_HEIGHT - 6);
			}
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
			DeminFrame.getDeminFrame().gameOver();
		}else{
			this.setState(GridStateConstants.GRID_STATE_OPEN_ISNOT_MINE);
			this.setLabel(mineNum.toString());
			this.setBackground(new Color(255, 255, 255));
			
			if(LayoutConstants.MODEL_AUTO == Constants.MODEL_SEMI_AUTO_TEXT || LayoutConstants.MODEL_AUTO == Constants.MODEL_AUTO_TEXT){
				int markCount = getMarkCount();
				
				if(LayoutConstants.MODEL_AUTO == Constants.MODEL_SEMI_AUTO_TEXT && (mineNum == 0 || markCount == mineNum)){
					mutiOpen();
				}
				else if(LayoutConstants.MODEL_AUTO == Constants.MODEL_AUTO_TEXT){
					List<MyGrid> unOpenGrids = getUnOpenNotMarkGrids();
					if(mineNum == 0 || markCount + unOpenGrids.size() == mineNum)
						autoMarkOpen();
				}
			}
		}
	}
	
	/**
	 * 打开多个,即自动打开可推测不是雷的块
	 */
	public void mutiOpen(){
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
	}
	
	/**
	 * 自动标记打开
	 */
	public void autoMarkOpen(){
		List<MyGrid> unOpenGrids = getUnOpenNotMarkGrids();
		int markCount = getMarkCount();
		
		if(markCount + unOpenGrids.size() == mineNum){
			for(MyGrid grid : unOpenGrids){
				grid.setState(GridStateConstants.GRID_STATE_CLOSE_MARK_MINE);
			}
		}
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
	}
	
	public int getMarkCount(){
		int markCount = 0;//标记数量
		if(this.getWnGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == this.getWnGrid().getState())
			markCount ++;
		if(this.getnGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == this.getnGrid().getState())
			markCount ++;
		if(this.getEnGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == this.getEnGrid().getState())
			markCount ++;
		if(this.geteGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == this.geteGrid().getState())
			markCount ++;
		if(this.getEsGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == this.getEsGrid().getState())
			markCount ++;
		if(this.getsGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == this.getsGrid().getState())
			markCount ++;
		if(this.getWsGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == this.getWsGrid().getState())
			markCount ++;
		if(this.getwGrid() != null && GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == this.getwGrid().getState())
			markCount ++;
		return markCount;
	}
	
	public List<MyGrid> getUnOpenNotMarkGrids(){
		List<MyGrid> unOpenGrids = new ArrayList<MyGrid>();
		if(this.getWnGrid() != null && GridStateConstants.GRID_STATE_CLOSE == this.getWnGrid().getState())
			unOpenGrids.add(this.getWnGrid());
		if(this.getnGrid() != null && GridStateConstants.GRID_STATE_CLOSE == this.getnGrid().getState())
			unOpenGrids.add(this.getnGrid());
		if(this.getEnGrid() != null && GridStateConstants.GRID_STATE_CLOSE == this.getEnGrid().getState())
			unOpenGrids.add(this.getEnGrid());
		if(this.geteGrid() != null && GridStateConstants.GRID_STATE_CLOSE == this.geteGrid().getState())
			unOpenGrids.add(this.geteGrid());
		if(this.getEsGrid() != null && GridStateConstants.GRID_STATE_CLOSE == this.getEsGrid().getState())
			unOpenGrids.add(this.getEsGrid());
		if(this.getsGrid() != null && GridStateConstants.GRID_STATE_CLOSE == this.getsGrid().getState())
			unOpenGrids.add(this.getsGrid());
		if(this.getWsGrid() != null && GridStateConstants.GRID_STATE_CLOSE == this.getWsGrid().getState())
			unOpenGrids.add(this.getWsGrid());
		if(this.getwGrid() != null && GridStateConstants.GRID_STATE_CLOSE == this.getwGrid().getState())
			unOpenGrids.add(this.getwGrid());
		return unOpenGrids;
	}
	
	public boolean canFullOpen(){
		List<MyGrid> unOpenGrids = getUnOpenNotMarkGrids();
		int markCount = getMarkCount();
		
		if(markCount + unOpenGrids.size() == mineNum || mineNum == 0)
			return true;
		return false;
	}
	
	public void paint(Graphics g){
		super.paint(g);
		g.drawImage(image, 3, 3, this.getWidth() - 6, this.getHeight() - 6, null);
	}
	
	private void refresh(){
		validate();
		repaint();
	}
	
}
