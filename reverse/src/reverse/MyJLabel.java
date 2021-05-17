package reverse;

import javax.swing.Icon;
import javax.swing.JLabel;

public class MyJLabel extends JLabel {
	
	private int row;
	private int column;
	
	public MyJLabel(Icon image) {
		super(image);
	}
	
	public void setRowAndColumn(int r, int c) {
		row = r;
		column = c;
	}
	
	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
	
}
