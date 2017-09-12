package demin.window;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import demin.cache.MineRegionCache;
import demin.constants.Constants;
import demin.constants.GridStateConstants;
import demin.constants.LayoutConstants;
import demin.entity.MyGrid;
import demin.listener.MainWindowListener;
import demin.listener.MineMouseListener;
import demin.listener.ModelMouseListener;
import demin.listener.OKMouseListener;
import demin.listener.RestartActionListener;

public class DeminFrame extends Frame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static DeminFrame frame;
	
	private static Checkbox enableDifficultyBox;
	
	private static Checkbox enableAutoBox;
	
	private List<Integer> mines;
	
	private Map<Integer, MyGrid> grids;
	
	private Dialog dialog;
	
	private Label stepCount;
	
	private ReentrantReadWriteLock stepLock = new ReentrantReadWriteLock();
	
	private Label leftMines;
	
	private ReentrantReadWriteLock mineLock = new ReentrantReadWriteLock();
	
	private Integer closeCount;
	
	private Label closeCountLabel;
	
	public DeminFrame(){
		initParams();
		setMenuBar(getMenu());
		refresh();
		addWindowListener(new MainWindowListener());
	}
	
	private void initParams(){
		//获取屏幕高度、宽度
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		LayoutConstants.SCREEN_WIDTH = d.width;
		LayoutConstants.SCREEN_HEIGHT = d.height;
		LayoutConstants.MODEL_TEXT = Constants.MODEL_SIMPLE_TEXT;
		LayoutConstants.MODEL_ROW = Constants.MODEL_SIMPLE_ROW;
		LayoutConstants.MODEL_COLUMN = Constants.MODEL_SIMPLE_COLUMN;
		LayoutConstants.MODEL_TOTAL = Constants.MODEL_SIMPLE_TOTAL;
		LayoutConstants.MODEL_MINE = Constants.MODEL_SIMPLE_MINE;
		LayoutConstants.MODEL_AUTO = Constants.MODEL_MANUAL_TEXT;
		LayoutConstants.LEFT_MINE = LayoutConstants.MODEL_MINE;
		LayoutConstants.STEP_COUNT = 0;
	}
	
	public void refresh(){
		LayoutConstants.LEFT_MINE = LayoutConstants.MODEL_MINE;
		LayoutConstants.STEP_COUNT = 0;
		LayoutConstants.GAME_IS_OVER = false;
		this.closeCount = LayoutConstants.MODEL_TOTAL;
		MineRegionCache.clear();
		removeAll();
		
		LayoutConstants.FRAME_WIDTH = LayoutConstants.MODEL_ROW * Constants.SINGLE_WIDTH + 20;
		LayoutConstants.FRAME_HEIGHT = LayoutConstants.MODEL_COLUMN * Constants.SINGLE_HEIGHT + 120;
		
		setLayout(new GridBagLayout());
		setLocation((LayoutConstants.SCREEN_WIDTH - LayoutConstants.FRAME_WIDTH) / 2, 
				(LayoutConstants.SCREEN_HEIGHT - LayoutConstants.FRAME_HEIGHT) / 2);
		
		Panel selectPanel = getSelectPanel();
		selectPanel.setVisible(true);
		GridBagConstraints gbc = new GridBagConstraints(0, 0, LayoutConstants.FRAME_WIDTH, 120, 0, 0, GridBagConstraints.CENTER, 
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		add(selectPanel, gbc);
		
		Panel minePanel = getMinePanel();
		minePanel.setVisible(true);
		gbc = new GridBagConstraints(0, 120, LayoutConstants.FRAME_WIDTH, LayoutConstants.MODEL_COLUMN * Constants.SINGLE_HEIGHT, 0, 0, GridBagConstraints.CENTER, 
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		add(minePanel, gbc);
		
		Panel bottomPanel = getBottomPanel();
		bottomPanel.setVisible(true);
		int height = 120 + LayoutConstants.MODEL_COLUMN * Constants.SINGLE_HEIGHT + 5;
		gbc = new GridBagConstraints(0, height, LayoutConstants.FRAME_WIDTH, 30, 0, 0, GridBagConstraints.CENTER, 
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		add(bottomPanel, gbc);
		
		pack();
		setVisible(true);
		frame = this;
		
		if(Constants.MODEL_AUTO_TEXT.equals(LayoutConstants.MODEL_AUTO))
			switchAutoModel();
	}
	
	public void switchAutoModel(){
		if(!LayoutConstants.IS_AUTO_MODEL)
			LayoutConstants.IS_AUTO_MODEL = true;
		List<MyGrid> canFullOpenGrids = new ArrayList<MyGrid>();
		List<MyGrid> allGrids = new ArrayList<MyGrid>();
		allGrids.addAll(grids.values());
		
		while(allGrids != null && !allGrids.isEmpty()){
			//先移除已经完全打开的块和已标识为雷的块
			allGrids.removeIf(grid -> GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == grid.getState() || grid.isFullOpen());
			//找出可以完全打开的块
			for(int index = allGrids.size() - 1; index >= 0; index --){
				MyGrid grid = allGrids.get(index);
				if(grid.canFullOpen()){
					canFullOpenGrids.add(grid);
					allGrids.remove(grid);
				}
			}
			
			/**
			 * 如果没有可全部打开的块,则随机打开一个块
			 */
			if(canFullOpenGrids.isEmpty()){
				boolean needReFind = false;
				for (MyGrid myGrid : allGrids) {
					boolean haveMark = myGrid.markOrOpenGridDeduceByNearGrid();
					if(haveMark)
						needReFind = true;
				}
				
				if(!needReFind){
					//检查地雷区域,如果有区域大小等于区域中地雷数量的区域,则区域内都是地雷
					for(Entry<String, Integer> entry : MineRegionCache.getRegions().entrySet()){
						String region = entry.getKey();
						Integer mineNum = entry.getValue();
						System.out.println(region + "   " + mineNum);
						String[] gridPoss = region.split(",");
						if(gridPoss.length == mineNum){
							for (String pos : gridPoss) {
								MyGrid grid = getGridByPos(Integer.parseInt(pos));
								grid.setState(GridStateConstants.GRID_STATE_CLOSE_MARK_MINE);
							}
							needReFind = true;
						}
						else if(mineNum <= 0){
							for (String pos : gridPoss) {
								MyGrid grid = getGridByPos(Integer.parseInt(pos));
								grid.autoMarkOpen();
							}
							needReFind = true;
						}
					}
				}
				
				if(!needReFind){
					this.refreshFrame();
					
					if(LayoutConstants.AUTO_RANDOM_OPEN){
						int totalCloseGridCount = allGrids.size();
						int rand = (int) (Math.random() * totalCloseGridCount);
						MyGrid grid = allGrids.get(rand);
						grid.autoMarkOpen();
						this.increaseStepCount();
					}
					break;
				}
			}
			
			if(!canFullOpenGrids.isEmpty()){
				for(int index = canFullOpenGrids.size() - 1; index >= 0; index --){
					MyGrid grid = canFullOpenGrids.remove(index);
					grid.autoMarkOpen();
				}
			}
		}
		
		//成功
		if(allGrids.isEmpty()){
			int markCount = 0;
			List<MyGrid> allGridsClone = new ArrayList<MyGrid>(grids.values());
			for(MyGrid grid : allGridsClone){
				if(GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == grid.getState())
					markCount ++;
			}
			if(markCount == mines.size())
				gameOver(true);
		}
		
		if(LayoutConstants.LEFT_MINE == 0){
			for (MyGrid myGrid : allGrids) {
				myGrid.open();
			}
		}
		
		LayoutConstants.IS_AUTO_MODEL = false;
	}
	
	public void refreshFrame(){
		validate();
		repaint();
	}
	
	public static DeminFrame getDeminFrame(){
		if(frame == null){
			synchronized(frame){
				if(frame == null)
					frame = new DeminFrame();
			}
		}
		return frame;
	}
	
	private MenuBar getMenu(){
		MenuBar menuBar = new MenuBar();
		
		Menu mainMenu = new Menu();
		mainMenu.setLabel("Main");
		
		MenuItem restartMenuItem = new MenuItem();
		restartMenuItem.setLabel("Restart");
		restartMenuItem.addActionListener(new RestartActionListener());
		mainMenu.add(restartMenuItem);
		
		Menu setMenu = new Menu();
		setMenu.setLabel("Setting");
		
		MenuItem panelSetMenuItem = new MenuItem();
		panelSetMenuItem.setLabel("Set Params");
		setMenu.add(panelSetMenuItem);
		
		menuBar.add(mainMenu);
		menuBar.add(setMenu);
		
		return menuBar;
	}
	
	private Panel getSelectPanel(){
		Panel p = new Panel();
		p.setLayout(new GridBagLayout());
		p.setBounds(0, 0, LayoutConstants.FRAME_WIDTH, 110);
		
		int per_panel_width = (LayoutConstants.FRAME_WIDTH - 20) / 3;
		//简单模式
		Panel p_simple = new Panel();
		p_simple.setName(Constants.MODEL_SIMPLE_TEXT);
		p_simple.setBounds(10, 10, per_panel_width, 50);
		Checkbox cb1 = new Checkbox();
		cb1.addMouseListener(new ModelMouseListener());
		Label l1 = new Label(Constants.MODEL_SIMPLE_TEXT);
		l1.addMouseListener(new ModelMouseListener());
		p_simple.add(cb1);
		p_simple.add(l1);
		p_simple.addMouseListener(new ModelMouseListener());
		//普通模式
		Panel p_ordinary = new Panel();
		p_ordinary.setName(Constants.MODEL_ORDINARY_TEXT);
		p_ordinary.setBounds(110, 10, per_panel_width, 50);
		Checkbox cb2 = new Checkbox();
		cb2.addMouseListener(new ModelMouseListener());
		Label l2 = new Label(Constants.MODEL_ORDINARY_TEXT);
		l2.addMouseListener(new ModelMouseListener());
		p_ordinary.add(cb2);
		p_ordinary.add(l2);
		//困难模式
		Panel p_difficult = new Panel();
		p_difficult.setName(Constants.MODEL_DIFFICULT_TEXT);
		p_difficult.setBounds(210, 10, per_panel_width, 50);
		Checkbox cb3 = new Checkbox();
		cb3.addMouseListener(new ModelMouseListener());
		Label l3 = new Label(Constants.MODEL_DIFFICULT_TEXT);
		l3.addMouseListener(new ModelMouseListener());
		p_difficult.add(cb3);
		p_difficult.add(l3);
		
		//全手动模式
		Panel p_manual = new Panel();
		p_manual.setName(Constants.MODEL_MANUAL_TEXT);
		p_manual.setBounds(10, 60, per_panel_width, 50);
		Checkbox cb4 = new Checkbox();
		cb4.addMouseListener(new ModelMouseListener());
		Label l4 = new Label(Constants.MODEL_MANUAL_TEXT);
		l4.addMouseListener(new ModelMouseListener());
		p_manual.add(cb4);
		p_manual.add(l4);
		//半自动模式
		Panel p_semiauto = new Panel();
		p_semiauto.setName(Constants.MODEL_SEMI_AUTO_TEXT);
		p_semiauto.setBounds(110, 60, per_panel_width, 50);
		Checkbox cb5 = new Checkbox();
		cb5.addMouseListener(new ModelMouseListener());
		Label l5 = new Label(Constants.MODEL_SEMI_AUTO_TEXT);
		l5.addMouseListener(new ModelMouseListener());
		p_semiauto.add(cb5);
		p_semiauto.add(l5);
		//全自动模式
		Panel p_auto = new Panel();
		p_auto.setName(Constants.MODEL_AUTO_TEXT);
		p_auto.setBounds(210, 60, per_panel_width, 50);
		Checkbox cb6 = new Checkbox();
		cb6.addMouseListener(new ModelMouseListener());
		Label l6 = new Label(Constants.MODEL_AUTO_TEXT);
		l6.addMouseListener(new ModelMouseListener());
		p_auto.add(cb6);
		p_auto.add(l6);
		
		GridBagConstraints gbc = new GridBagConstraints(10, 10, per_panel_width, 50, 0, 0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		p.add(p_simple, gbc);
		gbc = new GridBagConstraints(per_panel_width + 10, 10, per_panel_width, 50, 0, 0, GridBagConstraints.CENTER, 
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		p.add(p_ordinary, gbc);
		gbc = new GridBagConstraints(2 * per_panel_width + 10, 10, per_panel_width, 50, 0, 0, GridBagConstraints.CENTER, 
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		p.add(p_difficult, gbc);
		gbc = new GridBagConstraints(10, 60, per_panel_width, 50, 0, 0, GridBagConstraints.CENTER, 
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		p.add(p_manual, gbc);
		gbc = new GridBagConstraints(per_panel_width + 10, 60, per_panel_width, 50, 0, 0, GridBagConstraints.CENTER, 
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		p.add(p_semiauto, gbc);
		gbc = new GridBagConstraints(2 * per_panel_width + 10, 60, per_panel_width, 50, 0, 0, GridBagConstraints.CENTER, 
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		p.add(p_auto, gbc);
		
		//默认简单、全手动模式
		switch (LayoutConstants.MODEL_TEXT) {
		case Constants.MODEL_SIMPLE_TEXT:
			setSingleDifficultyEnable(cb1);
			break;
		case Constants.MODEL_ORDINARY_TEXT:
			setSingleDifficultyEnable(cb2);
			break;
		case Constants.MODEL_DIFFICULT_TEXT:
			setSingleDifficultyEnable(cb3);
			break;
		default:
			break;
		}
		
		switch (LayoutConstants.MODEL_AUTO) {
		case Constants.MODEL_MANUAL_TEXT:
			setSingleAutoEnable(cb4);
			break;
		case Constants.MODEL_SEMI_AUTO_TEXT:
			setSingleAutoEnable(cb5);
			break;
		case Constants.MODEL_AUTO_TEXT:
			setSingleAutoEnable(cb6);
			break;
		default:
			break;
		}
		
		return p;
	}
	
	private Panel getMinePanel(){
		Panel p = new Panel();
		p.setLayout(new GridBagLayout());
		p.setBounds(10, 120, LayoutConstants.FRAME_WIDTH, LayoutConstants.MODEL_COLUMN * Constants.SINGLE_HEIGHT);
		
		mines = generateMines(LayoutConstants.MODEL_ROW * LayoutConstants.MODEL_COLUMN, LayoutConstants.MODEL_MINE);
		GridBagConstraints gbc = null;
		System.out.println(mines);
		if(grids == null || grids.isEmpty())
			grids = new HashMap<Integer, MyGrid>();
		for(int x = 0; x < LayoutConstants.MODEL_ROW; x ++){
			for (int y = 0; y < LayoutConstants.MODEL_COLUMN; y ++){
				MyGrid grid = new MyGrid();
				int curr = y * LayoutConstants.MODEL_ROW + x;
				grid.setPos(curr);
				
				Integer wnPos = curr - LayoutConstants.MODEL_ROW - 1;//西北方
				Integer nPos = curr - LayoutConstants.MODEL_ROW;//北方
				Integer enPos = curr - LayoutConstants.MODEL_ROW + 1;//东北方
				Integer ePos = curr + 1;//东方
				Integer esPos = curr + LayoutConstants.MODEL_ROW + 1;//东南方
				Integer sPos = curr + LayoutConstants.MODEL_ROW;//南方
				Integer wsPos = curr + LayoutConstants.MODEL_ROW - 1;//西南方
				Integer wPos = curr - 1;//西方
				//是否是雷
				if(mines.contains(curr)){
					grid.setMine(true);
//					grid.setLabel("M");
				}
				else{
					grid.setMine(false);
					int mineNum = 0;
					//计算周围八宫格中雷的数量,并且绑定格子
					if(y > 0 && x > 0 && mines.contains(wnPos)){
						mineNum ++;
					}
					if(y > 0 && mines.contains(nPos)){
						mineNum ++;
					}
					if(y > 0 && x < LayoutConstants.MODEL_ROW - 1 && mines.contains(enPos)){
						mineNum ++;
					}
					if(x < LayoutConstants.MODEL_ROW - 1 && mines.contains(ePos)){
						mineNum ++;
					}
					if(y < LayoutConstants.MODEL_COLUMN - 1 && x < LayoutConstants.MODEL_ROW - 1 && mines.contains(esPos)){
						mineNum ++;
					}
					if(y < LayoutConstants.MODEL_COLUMN - 1 && mines.contains(sPos)){
						mineNum ++;
					}
					if(y < LayoutConstants.MODEL_COLUMN - 1 && x > 0 && mines.contains(wsPos)){
						mineNum ++;
					}
					if(x > 0 && mines.contains(wPos)){
						mineNum ++;
					}
					grid.setMineNum(mineNum);
//					grid.setLabel(String.valueOf(mineNum));
				}
				
//				grid.setLabel(String.valueOf(curr));
				grid.setBackground(new Color(0, 191, 255));
				grid.setPreferredSize(new Dimension(Constants.SINGLE_WIDTH, Constants.SINGLE_HEIGHT));
				grid.setBounds(x * Constants.SINGLE_WIDTH + 10, y * Constants.SINGLE_HEIGHT, Constants.SINGLE_WIDTH, Constants.SINGLE_HEIGHT);
				grid.addMouseListener(new MineMouseListener());
				grids.put(curr, grid);
			}
		}
		
		//绑定周围格子
		for(int x = 0; x < LayoutConstants.MODEL_ROW; x ++){
			for (int y = 0; y < LayoutConstants.MODEL_COLUMN; y ++){
				int curr = y * LayoutConstants.MODEL_ROW + x;
				MyGrid grid = grids.get(curr);
				
				Integer wnPos = curr - LayoutConstants.MODEL_ROW - 1;//西北方
				Integer nPos = curr - LayoutConstants.MODEL_ROW;//北方
				Integer enPos = curr - LayoutConstants.MODEL_ROW + 1;//东北方
				Integer ePos = curr + 1;//东方
				Integer esPos = curr + LayoutConstants.MODEL_ROW + 1;//东南方
				Integer sPos = curr + LayoutConstants.MODEL_ROW;//南方
				Integer wsPos = curr + LayoutConstants.MODEL_ROW - 1;//西南方
				Integer wPos = curr - 1;//西方
				
				if(y > 0 && x > 0)
					grid.setWnGrid(grids.get(wnPos));
				if(y > 0)
					grid.setnGrid(grids.get(nPos));
				if(y > 0 && x < LayoutConstants.MODEL_ROW - 1)
					grid.setEnGrid(grids.get(enPos));
				if(x < LayoutConstants.MODEL_ROW - 1)
					grid.seteGrid(grids.get(ePos));
				if(y < LayoutConstants.MODEL_COLUMN - 1 && x < LayoutConstants.MODEL_ROW - 1)
					grid.setEsGrid(grids.get(esPos));
				if(y < LayoutConstants.MODEL_COLUMN - 1)
					grid.setsGrid(grids.get(sPos));
				if(y < LayoutConstants.MODEL_COLUMN - 1 && x > 0)
					grid.setWsGrid(grids.get(wsPos));
				if(x > 0)
					grid.setwGrid(grids.get(wPos));
			}
		}
		
		//将格子放到面板上
		for(int x = 0; x < LayoutConstants.MODEL_ROW; x ++){
			for (int y = 0; y < LayoutConstants.MODEL_COLUMN; y ++){
				Integer pos = y * LayoutConstants.MODEL_ROW + x;
				MyGrid grid = grids.get(pos);
				gbc = new GridBagConstraints(x * Constants.SINGLE_WIDTH + 10, y * Constants.SINGLE_HEIGHT, Constants.SINGLE_WIDTH, Constants.SINGLE_HEIGHT, 0, 0, GridBagConstraints.CENTER, 
						GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
				grid.setVisible(true);
				p.add(grid, gbc);
			}
		}
		
		return p;
	}
	
	public Panel getBottomPanel(){
		Panel p = new Panel();
		p.setLayout(new GridBagLayout());
		int height = 120 + LayoutConstants.MODEL_COLUMN * Constants.SINGLE_HEIGHT + 5;
		p.setBounds(10, height, LayoutConstants.FRAME_WIDTH, 40);
		
		int per_panel_width = (LayoutConstants.FRAME_WIDTH - 20) / 6;
		//close count
		Label closeText = new Label("close count:");
		closeText.setSize(per_panel_width, 30);
		
		closeCountLabel = new Label(this.closeCount.toString());
		closeCountLabel.setSize(per_panel_width, 30);
		//step count
		Label stepText = new Label("step count:");
		stepText.setSize(per_panel_width, 30);
		
		stepCount = new Label(LayoutConstants.STEP_COUNT.toString());
		stepCount.setSize(per_panel_width, 30);
		//mine count
		Label mineText = new Label("mine count:");
		mineText.setSize(per_panel_width, 30);
		
		leftMines = new Label(LayoutConstants.LEFT_MINE.toString());
		leftMines.setSize(per_panel_width, 30);
		
		GridBagConstraints gbc = new GridBagConstraints(10, 10, per_panel_width, 30, 0, 0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		p.add(closeText, gbc);
		
		gbc = new GridBagConstraints(10 + per_panel_width, 10, per_panel_width, 30, 0, 0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		p.add(closeCountLabel, gbc);
		
		gbc = new GridBagConstraints(10 + per_panel_width * 2, 10, per_panel_width, 30, 0, 0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		p.add(stepText, gbc);
		
		gbc = new GridBagConstraints(10 + per_panel_width * 3, 10, per_panel_width, 30, 0, 0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		p.add(stepCount, gbc);
		
		gbc = new GridBagConstraints(10 + per_panel_width * 4, 10, per_panel_width, 30, 0, 0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		p.add(mineText, gbc);
		
		gbc = new GridBagConstraints(10 + per_panel_width * 5, 10, per_panel_width, 30, 0, 0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		p.add(leftMines, gbc);
		return p;
	}
	
	public void setSingleDifficultyEnable(Checkbox cb){
		if(cb.equals(enableDifficultyBox))
			return;
		if(enableDifficultyBox != null)
			enableDifficultyBox.setState(false);
		cb.setState(true);
		enableDifficultyBox = cb;
	}
	
	public void setSingleAutoEnable(Checkbox cb){
		if(cb.equals(enableAutoBox))
			return;
		if(enableAutoBox != null)
			enableAutoBox.setState(false);
		cb.setState(true);
		enableAutoBox = cb;
	}
	
	public void decreaseMineNum(){
		mineLock.writeLock().lock();
		LayoutConstants.LEFT_MINE --;
		leftMines.setText(LayoutConstants.LEFT_MINE.toString());
		mineLock.writeLock().unlock();
	}
	
	public void increaseStepCount(){
		stepLock.writeLock().lock();
		LayoutConstants.STEP_COUNT ++;
		stepCount.setText(LayoutConstants.STEP_COUNT.toString());
		stepLock.writeLock().unlock();
	}
	
	private List<Integer> generateMines(int totalNum, int mineNum){
		List<Integer> mines = new ArrayList<Integer>();
		while(mines.size() < mineNum){
			Integer generateNum = (int) (Math.random() * totalNum);
			if(!mines.contains(generateNum))
				mines.add(generateNum);
		}
		mines.sort((Integer m1, Integer m2) -> m1 > m2 ? 1 : -1);
		return mines;
	}
	
	public void gameOver(boolean isSuccess){
		LayoutConstants.GAME_IS_OVER = true;
		//暴露全部地雷
		if(!isSuccess)
			for(MyGrid grid : grids.values()){
				if(mines.contains(grid.getPos()))
					grid.setState(GridStateConstants.GRID_STATE_OPEN_IS_MINE);
			}
		
		refreshFrame();
		
		if(dialog == null){
			dialog = new Dialog(this);
			dialog.setLayout(new GridBagLayout());
			dialog.addWindowListener(new MainWindowListener());
			int dWidth = 100;
			int dHeight = 90;
			dialog.setSize(dWidth, dHeight);
			dialog.setBounds((LayoutConstants.SCREEN_WIDTH - dWidth) / 2, (LayoutConstants.SCREEN_HEIGHT - dHeight) / 2, dWidth, dHeight);
			
			Label label = new Label();
			label.setName("msg");
			label.setBounds(10, 5, dWidth, dHeight / 2 - 5);
			
			Button b = new Button();
			b.setBounds(10, dHeight / 2, dWidth, dHeight / 2);
			b.setLabel("OK");
			b.addMouseListener(new OKMouseListener());
			
			GridBagConstraints gbc = new GridBagConstraints(10, 5, dWidth, dHeight / 2 - 5, 0, 0, GridBagConstraints.CENTER, 
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
			dialog.add(label, gbc);
			gbc = new GridBagConstraints(10, dHeight / 2, dWidth, dHeight / 2, 0, 0, GridBagConstraints.CENTER, 
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
			dialog.add(b, gbc);
		}
		Component[] cs = dialog.getComponents();
		Label msgLabel = null;
		for (Component component : cs) {
			if("msg".equals(component.getName()))
				msgLabel = (Label) component;
		}
		if(msgLabel != null){
			if(isSuccess)
				msgLabel.setText("Success");
			else
				msgLabel.setText("Game Over");
		}
		
		dialog.setVisible(true);
	}
	
	public List<Integer> getMines(){
		return mines;
	}
	
	public Map<Integer, MyGrid> getGrids(){
		return this.grids;
	}
	
	public Integer getCloseGridCount(){
		return this.closeCount;
	}
	
	public void decreseCloseCount(){
		this.closeCount --;
		this.closeCountLabel.setText(this.closeCount.toString());
	}
	
	public MyGrid getGridByPos(Integer pos){
		return this.grids.get(pos);
	}

}
