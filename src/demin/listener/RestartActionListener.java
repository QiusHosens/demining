package demin.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import demin.window.DeminFrame;

public class RestartActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		DeminFrame frame = DeminFrame.getDeminFrame();
		frame.refresh();
		frame.refreshFrame();
	}

}
