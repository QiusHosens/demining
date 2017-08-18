package demin.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

import demin.constants.Constants;
import demin.constants.GridStateConstants;
import demin.constants.LayoutConstants;
import demin.entity.MyGrid;
import demin.window.DeminFrame;

public class MineMouseListener implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		MyGrid grid = (MyGrid) e.getSource();
		Map<Integer, MyGrid> grids = DeminFrame.getDeminFrame().getGrids();
		grid = grids.get(grid.getPos());
		System.out.print(grid.getMineNum() + " ");
		int type = e.getButton();
		switch (type) {
		case MouseEvent.BUTTON1://左键
			int clickCount = e.getClickCount();
			switch (clickCount) {
			case 1://单击
				switch (LayoutConstants.MODEL_AUTO) {
				case Constants.MODEL_MANUAL_TEXT:
					if(GridStateConstants.GRID_STATE_CLOSE == grid.getState() || 
						GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == grid.getState())
						grid.open();
					break;
				case Constants.MODEL_SEMI_AUTO_TEXT:
					if(GridStateConstants.GRID_STATE_CLOSE == grid.getState() || 
						GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == grid.getState() ||
							GridStateConstants.GRID_STATE_OPEN_ISNOT_MINE == grid.getState())
						grid.mutiOpen();
					break;
				case Constants.MODEL_AUTO_TEXT:
					if(GridStateConstants.GRID_STATE_CLOSE == grid.getState() || 
						GridStateConstants.GRID_STATE_CLOSE_MARK_MINE == grid.getState() ||
							GridStateConstants.GRID_STATE_OPEN_ISNOT_MINE == grid.getState())
						grid.autoMarkOpen();
					break;
				default:
					break;
				}
				break;
			case 2://双击
				if(GridStateConstants.GRID_STATE_OPEN_ISNOT_MINE == grid.getState())
					grid.mutiOpen();
				break;

			default:
				break;
			}
			break;
		case MouseEvent.BUTTON2://中键
			
			break;
		case MouseEvent.BUTTON3://右键
			if(GridStateConstants.GRID_STATE_CLOSE == grid.getState())
				grid.setState(GridStateConstants.GRID_STATE_CLOSE_MARK_MINE);
			break;

		default:
			break;
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

}
