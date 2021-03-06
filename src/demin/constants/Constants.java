package demin.constants;

public class Constants {
	
	/**
	 * 简单模式
	 */
	public static final String MODEL_SIMPLE_TEXT = "Simple";
	
	/**
	 * 简单模式总行数
	 */
	public static final Integer MODEL_SIMPLE_ROW = 20;
	
	/**
	 * 简单模式总列数
	 */
	public static final Integer MODEL_SIMPLE_COLUMN = 10;
	
	/**
	 * 简单模式总数
	 */
	public static final Integer MODEL_SIMPLE_TOTAL = MODEL_SIMPLE_ROW * MODEL_SIMPLE_COLUMN;
	
	/**
	 * 简单模式总雷数
	 */
	public static final Integer MODEL_SIMPLE_MINE = 40;
	
	/**
	 * 系数
	 */
	public static final Integer COEFFICIENT = 2;
	
	/**
	 * 普通模式
	 */
	public static final String MODEL_ORDINARY_TEXT = "Ordinary";
	
	/**
	 * 普通模式行数
	 */
	public static final Integer MODEL_ORDINARY_ROW = MODEL_SIMPLE_ROW * COEFFICIENT;
	
	/**
	 * 普通模式列数
	 */
	public static final Integer MODEL_ORDINARY_COLUMN = MODEL_SIMPLE_COLUMN * COEFFICIENT;
	
	/**
	 * 普通模式总数
	 */
	public static final Integer MODEL_ORDINARY_TOTAL = MODEL_ORDINARY_ROW * MODEL_ORDINARY_COLUMN;
	
	/**
	 * 普通模式雷数
	 */
	public static final Integer MODEL_ORDINARY_MINE = 200;
	
	/**
	 * 困难模式
	 */
	public static final String MODEL_DIFFICULT_TEXT = "Difficult";
	
	/**
	 * 困难模式行数
	 */
	public static final Integer MODEL_DIFFICULT_ROW = MODEL_ORDINARY_ROW * COEFFICIENT;
	
	/**
	 * 困难模式列数
	 */
	public static final Integer MODEL_DIFFICULT_COLUMN = MODEL_ORDINARY_COLUMN * COEFFICIENT;
	
	/**
	 * 困难模式总数
	 */
	public static final Integer MODEL_DIFFICULT_TOTAL = MODEL_DIFFICULT_ROW * MODEL_DIFFICULT_COLUMN;
	
	/**
	 * 困难模式雷数
	 */
	public static final Integer MODEL_DIFFICULT_MINE = 1200;
	
	/**
	 * 全手动模式
	 */
	public static final String MODEL_MANUAL_TEXT = "Manual";
	
	/**
	 * 半自动模式
	 */
	public static final String MODEL_SEMI_AUTO_TEXT = "Semi-auto";
	
	/**
	 * 自动模式
	 */
	public static final String MODEL_AUTO_TEXT = "Auto";
	
	/**
	 * 单个块宽度
	 */
	public static final Integer SINGLE_WIDTH = 20;
	
	/**
	 * 单个块高度
	 */
	public static final Integer SINGLE_HEIGHT = 20;
	
	/**
	 * 策略生成器线程数量
	 */
	public static final Integer INIT_THREAD_COUNT = 1;
	
	/**
	 * 增加线程条件
	 */
	public static final Integer ADD_THREAD_PER_NUM = 100;
	
	/**
	 * 最大线程数量
	 */
	public static final Integer MAX_THREAD_COUNT = 1000;
	
	/**
	 * 使用策略的条件--剩余雷数量
	 */
	public static final Integer USE_STRATEGY_LEFT_MINE_NUM = 100;
	
	/**
	 * 使用策略的条件--剩余关闭块数量
	 */
	public static final Integer USE_STRATEGY_CLOSE_GRID_NUM = 300;
	
	/**
	 * 概率保留小数点位数
	 */
	public static final Integer PROBABILITY_SCALE = 20;
	
}
