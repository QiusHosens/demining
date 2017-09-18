package demin.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import demin.constants.LayoutConstants;
import demin.window.DeminFrame;

public class ReloadActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		LayoutConstants.IS_RELOAD = true;
		DeminFrame frame = DeminFrame.getDeminFrame();
		frame.refresh();
	}

}
