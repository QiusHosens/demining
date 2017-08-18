package demin.listener;

import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import demin.constants.Constants;
import demin.constants.LayoutConstants;
import demin.window.DeminFrame;

public class ModelMouseListener implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Panel p = null;
		Checkbox cb = null;
		Boolean state = null;
		if(e.getSource() instanceof Checkbox){
			cb = (Checkbox) e.getSource();
			p = (Panel) cb.getParent();
			cb.setState(!cb.getState());//状态设置反状态
		}
		else{
			if(e.getSource() instanceof Panel)
				p = (Panel) e.getSource();
			else 
				p = (Panel) ((Component)e.getSource()).getParent();
			Component[] c = p.getComponents();
			cb = (Checkbox) getChildComponentByType(c, Checkbox.class);
			state = cb.getState();
			if(!state){
				String name = p.getName();
				switch (name) {
				case Constants.MODEL_SIMPLE_TEXT://简单模式
				case Constants.MODEL_ORDINARY_TEXT://普通模式
				case Constants.MODEL_DIFFICULT_TEXT://困难模式
					DeminFrame.getDeminFrame().setSingleDifficultyEnable(cb);
					break;
				case Constants.MODEL_MANUAL_TEXT://全手动模式
				case Constants.MODEL_SEMI_AUTO_TEXT://半自动模式
				case Constants.MODEL_AUTO_TEXT://全自动模式
					DeminFrame.getDeminFrame().setSingleAutoEnable(cb);
					break;
				default:
					break;
				}
			}
			else
				cb.setState(false);
			
			state = !state;
		}
		
		state = cb.getState();
		if(state){
			String name = p.getName();
			switch (name) {
			case Constants.MODEL_SIMPLE_TEXT://简单模式
				DeminFrame.getDeminFrame().setSingleDifficultyEnable(cb);
				LayoutConstants.MODEL_TEXT = Constants.MODEL_SIMPLE_TEXT;
				LayoutConstants.MODEL_ROW = Constants.MODEL_SIMPLE_ROW;
				LayoutConstants.MODEL_COLUMN = Constants.MODEL_SIMPLE_COLUMN;
				LayoutConstants.MODEL_TOTAL = Constants.MODEL_SIMPLE_TOTAL;
				LayoutConstants.MODEL_MINE = Constants.MODEL_SIMPLE_MINE;
				DeminFrame.getDeminFrame().refresh();
				break;
			case Constants.MODEL_ORDINARY_TEXT://普通模式
				DeminFrame.getDeminFrame().setSingleDifficultyEnable(cb);
				LayoutConstants.MODEL_TEXT = Constants.MODEL_ORDINARY_TEXT;
				LayoutConstants.MODEL_ROW = Constants.MODEL_ORDINARY_ROW;
				LayoutConstants.MODEL_COLUMN = Constants.MODEL_ORDINARY_COLUMN;
				LayoutConstants.MODEL_TOTAL = Constants.MODEL_ORDINARY_TOTAL;
				LayoutConstants.MODEL_MINE = Constants.MODEL_ORDINARY_MINE;
				DeminFrame.getDeminFrame().refresh();
				break;
			case Constants.MODEL_DIFFICULT_TEXT://困难模式
				DeminFrame.getDeminFrame().setSingleDifficultyEnable(cb);
				LayoutConstants.MODEL_TEXT = Constants.MODEL_DIFFICULT_TEXT;
				LayoutConstants.MODEL_ROW = Constants.MODEL_DIFFICULT_ROW;
				LayoutConstants.MODEL_COLUMN = Constants.MODEL_DIFFICULT_COLUMN;
				LayoutConstants.MODEL_TOTAL = Constants.MODEL_DIFFICULT_TOTAL;
				LayoutConstants.MODEL_MINE = Constants.MODEL_DIFFICULT_MINE;
				DeminFrame.getDeminFrame().refresh();
				break;
			case Constants.MODEL_MANUAL_TEXT://全手动模式
				DeminFrame.getDeminFrame().setSingleAutoEnable(cb);
				LayoutConstants.MODEL_AUTO = Constants.MODEL_MANUAL_TEXT;
				break;
			case Constants.MODEL_SEMI_AUTO_TEXT://半自动模式
				DeminFrame.getDeminFrame().setSingleAutoEnable(cb);
				LayoutConstants.MODEL_AUTO = Constants.MODEL_SEMI_AUTO_TEXT;
				break;
			case Constants.MODEL_AUTO_TEXT://全自动模式
				DeminFrame.getDeminFrame().setSingleAutoEnable(cb);
				LayoutConstants.MODEL_AUTO = Constants.MODEL_AUTO_TEXT;
				DeminFrame.getDeminFrame().switchAutoModel();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	
	private <T> Component getChildComponentByType(Component[] c, Class<T> clazz){
		for (Component component : c) {
			if(component.getClass() == clazz)
				return component;
		}
		return null;
	}
	
}
