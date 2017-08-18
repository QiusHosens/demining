package demin.listener;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class OKMouseListener implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		Button b = (Button) e.getSource();
		Dialog d = (Dialog) b.getParent();
		d.setVisible(false);
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

}
