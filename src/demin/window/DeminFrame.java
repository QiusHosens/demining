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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import javax.swing.JLabel;

import demin.cache.GameCache;
import demin.cache.LatchCache;
import demin.cache.MiddleValueCache;
import demin.cache.MineRegionCache;
import demin.cache.ProbabilityCache;
import demin.cache.StrategyDeduceCache;
import demin.constants.Constants;
import demin.constants.GridStateConstants;
import demin.constants.LayoutConstants;
import demin.entity.GridPossible;
import demin.entity.GroupStrategy;
import demin.entity.MiddleValue;
import demin.entity.MyGrid;
import demin.entity.OneGame;
import demin.entity.Probability;
import demin.entity.Strategy;
import demin.listener.MainWindowListener;
import demin.listener.MineMouseListener;
import demin.listener.ModelMouseListener;
import demin.listener.OKMouseListener;
import demin.listener.ReloadActionListener;
import demin.listener.RestartActionListener;
import demin.thread.StrategyGenerator;
import demin.thread.StrategyRunnable;
import demin.util.CollectionUtil;

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
	
	private Dialog tip;
	
	private JLabel tipLabel;
	
	private Label stepCount;
	
	private Label leftMines;
	
	private Integer closeCount;
	
	private Label closeCountLabel;
	
	private Map<Integer, MyGrid> closeGrids;
	
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
		MineRegionCache.clearAllRegion();
		MineRegionCache.clearNewRegion();
		StrategyDeduceCache.clear();
		ProbabilityCache.clear();
		removeAll();
		if(tip != null && tipLabel != null)
			tipLabel.setText("");
		
		LayoutConstants.FRAME_WIDTH = (LayoutConstants.MODEL_ROW + 1) * Constants.SINGLE_WIDTH + 20;
		LayoutConstants.FRAME_HEIGHT = (LayoutConstants.MODEL_COLUMN + 1) * Constants.SINGLE_HEIGHT + 120;
		
		setLayout(new GridBagLayout());
		
		if(frame == null){
			setLocation((LayoutConstants.SCREEN_WIDTH - LayoutConstants.FRAME_WIDTH) / 2, 
					(LayoutConstants.SCREEN_HEIGHT - LayoutConstants.FRAME_HEIGHT) / 2);
		}
		
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
		if(MineRegionCache.isCanClearNewRegion()){
			MineRegionCache.clearNewRegion();
		}
		
		while(allGrids != null && !allGrids.isEmpty()){
			//先移除已经完全打开的块和已标识为雷的块
			allGrids.removeIf(grid -> (GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == grid.getState() || grid.isFullOpen()));
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
					System.out.println("regions:");
					for(Entry<String, Integer> entry : MineRegionCache.getAllRegions().entrySet()){
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
						else if(mineNum == 0){
							for (String pos : gridPoss) {
								MyGrid grid = getGridByPos(Integer.parseInt(pos));
								grid.autoMarkOpen();
							}
							needReFind = true;
						}
						else if(mineNum < 0){
							System.out.println("异常");
							return;
						}
					}
				}
				
				if(!needReFind){
					this.refreshFrame();
					if(this.strategyDeduce()){
						needReFind = true;
					}
					MineRegionCache.setCanClearNewRegion(true);
				}
				
				if(!needReFind){
					this.refreshFrame();
					
					if(LayoutConstants.AUTO_RANDOM_OPEN){//是否随机开启
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
	
	public boolean strategyDeduce(){
		StrategyDeduceCache.clear();
		LatchCache.clear();
		Map<String, Integer> allRegions = MineRegionCache.getAllRegions();
//		Map<String, Integer> newRegions = MineRegionCache.getNewRegions();
//		Map<String, Integer> regions = MineRegionCache.getIntersectRegion(allRegions, newRegions);
		if(allRegions.isEmpty())
			return false;
		Map<String, Integer> cloneRegions = new HashMap<String, Integer>(allRegions);

		boolean isChange = false;
		
		
		
		Set<String> posList = new HashSet<>();
		Stack<MiddleValue> stack = new Stack<>();
		//找出所有区域的可能是雷的情况
		System.out.println("new region: ");
		for(Entry<String, Integer> entry : cloneRegions.entrySet()){
			String grids = entry.getKey();
			String[] gridArr = grids.split(",");
			System.out.println(grids + " " + entry.getValue());
			for (String gridPos : gridArr) {
				posList.add(gridPos);
			}
		}
		
		StringBuilder allPoss = new StringBuilder();
		int allCloseGridNum = this.closeCount;
		int allRegionMineNum = 0;
		MiddleValue value = new MiddleValue(-1, new StringBuilder(allPoss), allCloseGridNum, allRegionMineNum, new HashMap<>(cloneRegions), new HashMap<>());
		MiddleValueCache.add(value);
		stack.push(value);
		
		//多线程
		long strategy2Start = System.currentTimeMillis();
		threadGenerateStrategy(stack);
		long strategy2End = System.currentTimeMillis();
		
		long strategyStart = System.currentTimeMillis();
		int count = generateStrategy(stack);
		long strategyEnd = System.currentTimeMillis();
		
		System.out.println("difficult: " + StrategyDeduceCache.compare12());
		
		Queue<Strategy> strategyList = StrategyDeduceCache.get();
		Queue<Strategy> strategyList2 = StrategyDeduceCache.get2();
		System.out.println("strategys: " + strategyList.size() + " strategys2: " + strategyList2.size() + " strategy count: " + count);
		System.out.println("strategys use time: " + (strategyEnd - strategyStart) + " strategys count: " + count);
		System.out.println("strategys2 use time: " + (strategy2End - strategy2Start) + " strategys2 count: " + StrategyRunnable.count + " thread num: " + LayoutConstants.CURRENT_THREAD_COUNT);
//		for (Strategy strategy : strategyList) {
//			System.out.println(strategy.getGrids() + " " + strategy.getMineNum() + " " + strategy.getLeftCloseNum());
//		}
		
		//计算各点是雷的概率
		List<Probability> probabilityList = new ArrayList<>();
		//总的可能情况数量
		if(!strategyList.isEmpty()){
			BigDecimal totalPosibleNum = new BigDecimal(0);
			int minPosibleMinNum = 0;//最小可能雷数
			for (Strategy strategy : strategyList) {
				if(minPosibleMinNum == 0 || minPosibleMinNum > strategy.getMineNum())
					minPosibleMinNum = strategy.getMineNum();
			}
			
			for (Strategy strategy : strategyList) {
				BigDecimal possible = CollectionUtil.combinateDivide(strategy.getLeftCloseNum(), LayoutConstants.LEFT_MINE - minPosibleMinNum, LayoutConstants.LEFT_MINE - strategy.getMineNum());
				strategy.setPossible(possible);
				totalPosibleNum = totalPosibleNum.add(possible);
			}
			
			if(minPosibleMinNum == LayoutConstants.LEFT_MINE){//如果策略中最小的可能雷数等于剩余的雷数,则雷肯定在雷区中,打开不在雷区中的块
				for(Entry<Integer, MyGrid> entry : this.getCloseGrids().entrySet()){
					if(!MineRegionCache.hasGrid(cloneRegions, entry.getValue())){
						entry.getValue().autoMarkOpen();
						isChange = true;
					}
				}
			}
			
			//所有区域中的块
			Set<String> allPos = new HashSet<>();
			//公共块
			Set<String> commonPos = new HashSet<>();
			for(Entry<String, Integer> entry : allRegions.entrySet()){
				String gridPos = entry.getKey();
				List<String> gridPosList = Arrays.asList(gridPos.split(","));
				for (String pos : gridPosList) {
					if(!allPos.contains(pos))
						allPos.add(pos);
					else if(!commonPos.contains(pos))
						commonPos.add(pos);
				}
			}
			
			for(String pos : posList){
				BigDecimal gridPosibleNum = new BigDecimal(0);
				for (Strategy strategy : strategyList) {
					String gridPos = strategy.getGrids();
					List<String> gridPosList = Arrays.asList(gridPos.split(","));
					if(gridPosList.contains(pos))
						gridPosibleNum = gridPosibleNum.add(strategy.getPossible());
				}
				Probability gridProbability = new Probability(Integer.parseInt(pos), gridPosibleNum.divide(totalPosibleNum, Constants.PROBABILITY_SCALE, 0));
				probabilityList.add(gridProbability);
			}
			
//			ProbabilityCache.addAll(probabilityList);
//			probabilityList = ProbabilityCache.get();
			probabilityList.sort((p1, p2) -> p1.getProbability().compareTo(p2.getProbability()));
			System.out.println("probabilityList:");
			for (Probability probability : probabilityList) {
				System.out.println(probability.getPos() + " " + probability.getProbability());
			}
			
			StringBuilder sb = new StringBuilder();
			int pos, row, column;
			sb.append("<html>");
			for (Probability probability : probabilityList) {
				pos = probability.getPos();
				if(probability.getProbability().compareTo(BigDecimal.ZERO) == 0){
					this.grids.get(pos).autoMarkOpen();
					isChange = true;
					break;
				}
				if(probability.getProbability().compareTo(BigDecimal.ONE) == 0){
					this.grids.get(pos).setState(GridStateConstants.GRID_STATE_CLOSE_MARK_MINE);
					isChange = true;
					break;
				}
				row = pos / LayoutConstants.MODEL_ROW + 1;
				column = pos % LayoutConstants.MODEL_ROW + 1;
				sb.append("pos: ").append(String.valueOf(pos)).append(" row: ").append(String.valueOf(row)).append(" column: ").append(String.valueOf(column)).append(" probability: ").append(probability.getProbability()).append("<br/>");
			}
			sb.append("</html>");
			
			tip = getTipDialog(probabilityList.size());
			tip.setVisible(true);
			
			tipLabel.setText(sb.toString());
		}
		
		return isChange;
	}
	
	public int generateStrategy(Stack<MiddleValue> stack){
		int count = 0;
		while(!stack.isEmpty()){
			MiddleValue useValue = stack.pop();
			Map<String, Integer> region = useValue.getCommonRegion();
			Map<String, Integer> unCommonRegion = useValue.getUnCommonRegion();
			StringBuilder poss = useValue.getGridPos();
			int closeGridNum = useValue.getCloseGridNum();
			int regionMineNum = useValue.getRegionMineNum();
			int pos = useValue.getCurrPos();
			while(pos != -1){
				count ++;
				region = MineRegionCache.removeMarkGridPosFromRegion(region, String.valueOf(pos));
				poss.append(",").append(pos);
				closeGridNum --;
				regionMineNum ++;
				if(regionMineNum > LayoutConstants.LEFT_MINE)//如果区域内假设雷数量多于剩余雷数,则策略失败
					break;
				
				if(!region.isEmpty()){
					Set<Integer> canOpenGridPos = new HashSet<Integer>();
					//去除不是雷的块
					for(Entry<String, Integer> entry1 : region.entrySet()){
						String gridsPoss = entry1.getKey();
						Integer mineNum = entry1.getValue();
						String[] gridsArr = gridsPoss.split(",");
						List<String> gridsPosList = Arrays.asList(gridsArr);
						if(mineNum == 0){
							for(String gridPos1 : gridsPosList){
								Integer gridPosInt = Integer.parseInt(gridPos1);
								canOpenGridPos.add(gridPosInt);
							}
						}
					}
				
					for(Integer gridPos1 : canOpenGridPos){
						region = MineRegionCache.removeOpenGridPosFromRegion(region, String.valueOf(gridPos1));
						closeGridNum --;
					}
				}
				
				if(region.isEmpty()){
					if(LayoutConstants.LEFT_MINE - regionMineNum > closeGridNum)
						break;
					Strategy strategy = new Strategy(poss.substring(1).toString(), closeGridNum, regionMineNum);
//					Strategy strategy = new Strategy(poss.substring(1).toString(), CollectionUtil.combination(LayoutConstants.LEFT_MINE, LayoutConstants.LEFT_MINE - regionMineNum), regionMineNum);
					StrategyDeduceCache.add(strategy);
					break;
				}
				
				//找确定是雷的块
				boolean isFind = false;
				for(Entry<String, Integer> entry1 : region.entrySet()){
					String gridsPoss = entry1.getKey();
					Integer mineNum = entry1.getValue();
					String[] gridsArr = gridsPoss.split(",");
					List<String> gridsPosList = Arrays.asList(gridsArr);
					if(mineNum == gridsPosList.size()){
						isFind = true;
						for(String gridPos1 : gridsPosList){
							Integer gridPosInt = Integer.parseInt(gridPos1);
							pos = gridPosInt;
							isFind = true;
							break;
						}
					}
					if(isFind)
						break;
				}
				if(!isFind){
					break;
				}
			}
			
//			Map<String, Integer> commonRegion = MineRegionCache.getCommonRegion(region);
//			Map<String, Integer> newUnCommonRegion = MineRegionCache.exclude(region, commonRegion);
//			regionMineNum += MineRegionCache.getUnCommonRegionMineNum(newUnCommonRegion);
//			closeGridNum -= MineRegionCache.getUnCommonRegionGridNum(newUnCommonRegion);
//			unCommonRegion.putAll(newUnCommonRegion);
//			region = commonRegion;
			
			if(region != null && !region.isEmpty()){
				for(Entry<String, Integer> entry1 : region.entrySet()){
					String gridPoss = entry1.getKey();
					String[] gridPossList = gridPoss.split(",");
					Map<String, Integer> cloneRegion = new HashMap<String, Integer>(region);
					for(String gridPos1 : gridPossList){
						Integer gridPosInt = Integer.parseInt(gridPos1);
						if(!MineRegionCache.validate(cloneRegion))
							break;
						MiddleValue setValue = new MiddleValue(gridPosInt, new StringBuilder(poss), closeGridNum, regionMineNum, new HashMap<>(cloneRegion), new HashMap<>(unCommonRegion));
						stack.push(setValue);
						//当前假定地雷在同一区域内,下次不能再假定为地雷,会生成重复策略
						cloneRegion = MineRegionCache.removeOpenGridPosFromRegion(cloneRegion, gridPos1);
						closeGridNum --;
					}
					break;
				}
			}
//			else{
//				GroupStrategy group = MineRegionCache.getGroupStrategy(unCommonRegion);
//				for(String pos1 : group.getPos()){
//					StringBuilder poss1 = new StringBuilder(poss);
//					poss1.append(",").append(pos1);
//					Strategy strategy = new Strategy(poss1.substring(1).toString(), 
//							group.getPossible(), closeGridNum, regionMineNum);
//					StrategyDeduceCache.add(strategy);
//				}
//			}
		}
		return count;
	}
	
	public void threadGenerateStrategy(Stack<MiddleValue> stack){
		LayoutConstants.CURRENT_THREAD_COUNT = Constants.INIT_THREAD_COUNT;
		StrategyRunnable.count = 0;
		CountDownLatch latch = new CountDownLatch(Constants.INIT_THREAD_COUNT);
		System.out.println("init latch: " + latch);
		LatchCache.push(latch);
		StrategyGenerator.getStrategyGenerator().execute();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//策略2每个块的概率
		Queue<Strategy> strategyList2 = StrategyDeduceCache.get2();
		BigDecimal totalPosibleNum2 = new BigDecimal(0);
		int minPosibleMinNum2 = 0;//最小可能雷数
		for(Strategy strategy : strategyList2){
			if(minPosibleMinNum2 == 0 || minPosibleMinNum2 > strategy.getMineNum())
				minPosibleMinNum2 = strategy.getMineNum();
		}
		
		for(Strategy strategy : strategyList2){
			BigDecimal possible = strategy.getPossible().multiply(CollectionUtil.combinateDivide(strategy.getLeftCloseNum(), LayoutConstants.LEFT_MINE - minPosibleMinNum2, LayoutConstants.LEFT_MINE - strategy.getMineNum()));
			strategy.setPossible(possible);
			totalPosibleNum2 = totalPosibleNum2.add(possible);
		}
		
		Set<String> posList = new HashSet<>();
		
		int index = 0;
		System.out.println("strategy2:");
		for (Strategy strategy : strategyList2){
			String gridPos = strategy.getGrids();
			System.out.print("child strategy" + index + ": ");
			System.out.print(" common pos: " + gridPos);
			if(gridPos != null && !"".equals(gridPos)){
				List<String> gridPosList = Arrays.asList(gridPos.split(","));
				for (String pos : gridPosList) {
					posList.add(pos);
				}
			}
			
			System.out.print(" uncommon pos: ");
			for(GridPossible gridPossible : strategy.getGridPossibles()){
				int pos = gridPossible.getPos();
				System.out.print(pos + ",");
				posList.add(String.valueOf(pos));
			}
			index ++;
			System.out.print(" close grid:" + strategy.getLeftCloseNum() + " mine num:" + strategy.getMineNum());
			System.out.println();
		}
		System.out.println();
		
		List<Probability> probabilityList2 = new ArrayList<>();
		for(String pos2 : posList){
			BigDecimal gridPosibleNum = new BigDecimal(0);
			for (Strategy strategy : strategyList2) {
				String gridPos = strategy.getGrids();
				List<String> gridPosList = Arrays.asList(gridPos.split(","));
				if(gridPosList.contains(pos2))
					gridPosibleNum = gridPosibleNum.add(strategy.getPossible());
				
				//修改
				for(GridPossible gridPossible : strategy.getGridPossibles()){
					if(pos2.equals(String.valueOf(gridPossible.getPos()))){
						gridPosibleNum = gridPosibleNum.add(gridPossible.getIsMinePossible());
						break;
					}
				}
			}
			Probability gridProbability = new Probability(Integer.parseInt(pos2), gridPosibleNum.divide(totalPosibleNum2, Constants.PROBABILITY_SCALE, 0));
			probabilityList2.add(gridProbability);
		}
		probabilityList2.sort((p1, p2) -> p1.getProbability().compareTo(p2.getProbability()));
		System.out.println("probabilityList2:");
		for (Probability probability : probabilityList2) {
			System.out.println(probability.getPos() + " " + probability.getProbability());
		}
	}
	
	public <T> List<T> calculateProbability(Map<String, Integer> region){
		List<T> list = new ArrayList<>();
		Map<String, Integer> cloneRegion = new HashMap<>(region);
		while(!cloneRegion.isEmpty()){
			Map<String, Integer> cloneRegion1 = new HashMap<>(cloneRegion);
			for(Entry<String, Integer> entry : cloneRegion1.entrySet()){
				String poss = entry.getKey();
				int mineNum = entry.getValue();
				List<String> posList = new ArrayList<>(Arrays.asList(poss.split(",")));
				cloneRegion.remove(poss);
				Set<String> commonPoss = MineRegionCache.getCommonPos(posList, cloneRegion);
				
				//计算非公有域的可能性
				posList.removeIf(p -> commonPoss.contains(p));
				int uncommonSize = posList.size();
				int uncommonMin = mineNum < uncommonSize ? mineNum : uncommonSize;
				for(int i = 0; i < uncommonMin; i ++){
					BigDecimal possible = CollectionUtil.combination(uncommonSize, i);
					//计算公有域的可能性
					int commonMin = mineNum - i;
					
				}
				break;
			}
		}
		return list;
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
		
		MenuItem resLoadMenuItem = new MenuItem();
		resLoadMenuItem.setLabel("ReLoad");
		resLoadMenuItem.addActionListener(new ReloadActionListener());
		mainMenu.add(resLoadMenuItem);
		
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
		p.setBounds(10, 120, LayoutConstants.FRAME_WIDTH, (LayoutConstants.MODEL_COLUMN + 1) * Constants.SINGLE_HEIGHT);
		
		if(LayoutConstants.IS_RELOAD){
			OneGame lastGame = GameCache.removeLastGame();
			if(lastGame != null){
				mines = lastGame.getMines();
				System.out.println("step: " + lastGame.getStep());
			}
		}
		else{
			mines = generateMines(LayoutConstants.MODEL_ROW * LayoutConstants.MODEL_COLUMN, LayoutConstants.MODEL_MINE);
		}
		GameCache.setMines(mines);
		
		GridBagConstraints gbc = null;
		System.out.println(mines);
		if(grids == null || grids.isEmpty())
			grids = new HashMap<Integer, MyGrid>();
		if(closeGrids == null || closeGrids.isEmpty())
			closeGrids = new ConcurrentHashMap<Integer, MyGrid>();
		
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
		closeGrids.putAll(grids);
		
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
		for(int x = 0; x <= LayoutConstants.MODEL_ROW; x ++){
			for (int y = 0; y <= LayoutConstants.MODEL_COLUMN; y ++){
				if(x == 0 || y == 0){
					Button button = new Button();
					button.setPreferredSize(new Dimension(Constants.SINGLE_WIDTH, Constants.SINGLE_HEIGHT));
					button.setBounds(x * Constants.SINGLE_WIDTH + 10, y * Constants.SINGLE_HEIGHT, Constants.SINGLE_WIDTH, Constants.SINGLE_HEIGHT);
					button.setEnabled(false);
					if(x == 0 && y == 0){
						button.setLabel("c\\r");
					}else if(x == 0){
						button.setLabel(String.valueOf(y));
					}else if(y == 0){
						button.setLabel(String.valueOf(x));
					}
					gbc = new GridBagConstraints(x * Constants.SINGLE_WIDTH + 10, y * Constants.SINGLE_HEIGHT, Constants.SINGLE_WIDTH, Constants.SINGLE_HEIGHT, 0, 0, GridBagConstraints.CENTER, 
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
					button.setVisible(true);
					p.add(button, gbc);
				}
				else{
					Integer pos = (y - 1) * LayoutConstants.MODEL_ROW + x - 1;
					MyGrid grid = grids.get(pos);
					gbc = new GridBagConstraints(x * Constants.SINGLE_WIDTH + 10, y * Constants.SINGLE_HEIGHT, Constants.SINGLE_WIDTH, Constants.SINGLE_HEIGHT, 0, 0, GridBagConstraints.CENTER, 
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
					grid.setVisible(true);
					p.add(grid, gbc);
				}
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
	
	public Dialog getTipDialog(int size){
		if(tip == null){
			tip = new Dialog(this);
			tip.setLocation((LayoutConstants.SCREEN_WIDTH + LayoutConstants.FRAME_WIDTH) / 2, (LayoutConstants.SCREEN_HEIGHT - LayoutConstants.FRAME_HEIGHT) / 2);
		}
		tip.setLayout(new GridBagLayout());
		int dWidth = 300 + 10 * Constants.PROBABILITY_SCALE;
		int dHeight;
		if(size < 5)
			dHeight = 50 + 20 * size;
		else
			dHeight = 30 + 18 * size;
		tip.setSize(dWidth, dHeight);
		
		if(tipLabel == null){
			tipLabel = new JLabel();
			tipLabel.setName("probability");
			tip.add(tipLabel);
		}
		tipLabel.setBounds(10, 5, dWidth - 5 * 2, dHeight - 10 * 2);
		return tip;
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
		LayoutConstants.LEFT_MINE --;
		leftMines.setText(LayoutConstants.LEFT_MINE.toString());
	}
	
	public void increaseStepCount(){
		LayoutConstants.STEP_COUNT ++;
		stepCount.setText(LayoutConstants.STEP_COUNT.toString());
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
	
	public void removeGridFromClose(Integer pos){
		this.closeGrids.remove(pos);
	}
	
	public Map<Integer, MyGrid> getCloseGrids(){
		return this.closeGrids;
	}

}
