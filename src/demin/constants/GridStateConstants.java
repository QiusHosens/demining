package demin.constants;

import java.awt.Image;
import java.awt.Toolkit;

public class GridStateConstants {
	
	/**
	 * 最初关闭状态
	 */
	public static final int GRID_STATE_CLOSE = 0;
	
	/**
	 * 关闭标记为雷
	 */
	public static final int GRID_STATE_CLOSE_MARK_MINE = 1;
	
	/**
	 * 打开不是雷
	 */
	public static final int GRID_STATE_OPEN_ISNOT_MINE = 2;
	
	/**
	 * 打开是雷
	 */
	public static final int GRID_STATE_OPEN_IS_MINE = 3;
	
	/**
	 * 模拟标记为雷
	 */
	public static final int GRID_STATE_VIRTUAL_MARK_MINE = 4;
	
	/**
	 * 模拟打开不是雷
	 */
	public static final int GRID_STATE_VIRTUAL_OPEN_ISNOT_MINE = 5;
	
	/**
	 * 标记雷图标
	 */
	public static final Image GRID_IMAGE_MARK_MINE = Toolkit.getDefaultToolkit().getImage(GridStateConstants.class.getResource("hongqi.png"));
	
	/**
	 * 标记雷图标
	 */
	public static final Image GRID_IMAGE_IS_MINE = Toolkit.getDefaultToolkit().getImage(GridStateConstants.class.getResource("dilei.png"));
	
}
