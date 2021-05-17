//其次运行
package reverse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client2 extends JFrame {
	private static Client2 client2;
	private JPanel jp = null;
	private JPanel jp1 = null;
	private JPanel jp2 = null;
	private JTextField jtf = null;
	private ImageIcon black = new ImageIcon("img/black.png");
	private ImageIcon white = new ImageIcon("img/white.png");
	private ImageIcon none = new ImageIcon("img/none.png");

	private int[][] checkerboard = { { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 1, 2, 0, 0, 0 }, { 0, 0, 0, 2, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 } };
	private String oldCheckerboard = null;
	private Socket socket = null;
	private PrintWriter pw = null;
	private BufferedReader br = null;
	private boolean isMyTurn = false;// 是否是自己回合

	private Client2() {
		while (true) {
			try {
				String ip = JOptionPane.showInputDialog("请输入服务器IP：");
				int port = Integer.parseInt(JOptionPane.showInputDialog("请输入服务器端口号："));
				socket = new Socket("192.168.0.117", 8888);

				jp1 = new JPanel();
				jtf = new JTextField(30);
				jtf.setFont(new Font("楷体", Font.BOLD, 20));
				jtf.setEditable(false);
				jp1.add(jtf);
				jp2 = new JPanel(new GridLayout(8, 8, 2, 2));
				jp = new JPanel();
				jp.add(jp1, BorderLayout.NORTH);
				jp.add(jp2, BorderLayout.CENTER);
				setBoard();
				this.add(jp);
				this.setResizable(false);
				this.setBounds(200, 100, 550, 600);
				this.setBackground(Color.black);
				this.setDefaultCloseOperation(EXIT_ON_CLOSE);
				this.setVisible(true);
				break;
			} catch (UnknownHostException e) {
				JOptionPane.showMessageDialog(null, "网络连接错误。请重新输入。");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "连接错误。");
			}
		}
	}

	public static Client2 getClient2() {
		if (client2 == null) {
			client2 = new Client2();
		}
		return client2;
	}

	private void launch() {// 启动
		try {
			pw = new PrintWriter(socket.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			jtf.setText("您着白棋，后走。等待对手落子……");
			String strIn = "";
			strIn = br.readLine();
			checkerboard = stringToCheckerboard(strIn);
			setBoard();
			jtf.setText("现在是您的回合，请落子：");
			isMyTurn = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setBoard() {// 更新棋盘
		jp2.removeAll();
		jp2.setLayout(new GridLayout(8, 8, 2, 2));
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (checkerboard[i][j] == 1) {
					jp2.add(new JLabel(black));
				} else if (checkerboard[i][j] == 2) {
					jp2.add(new JLabel(white));
				} else {
					MyJLabel noneJLabel = new MyJLabel(none);
					noneJLabel.setRowAndColumn(i, j);
					noneJLabel.addMouseListener(new MyMouseListener());
					jp2.add(noneJLabel);
				}
			}
		}
		jp2.updateUI();
	}

	private String checkerboardToString(int[][] cb) {// 数组转字符串
		String str = "";
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				str += cb[i][j];
			}
		}
		return str;
	}

	private int[][] stringToCheckerboard(String str) {// 字符串转数组
		int[][] cb = new int[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				cb[i][j] = (int) (str.charAt(i * 8 + j) - 48);
			}
		}
		return cb;
	}

	public String reverse(int r, int c) {// 逻辑判断
		oldCheckerboard = checkerboardToString(checkerboard);// 保存当前棋盘，以便于hasChoice检测是否无处落子后还原棋盘
		boolean legal = false;// 合法落子
		int myc = 2;// 我的颜色
		int opc = 1;// 对手颜色

		checkerboard[r][c] = myc;

		int i;
		int j;

		try {
			i = r - 1;
			if (checkerboard[i][c] == opc) {// 上方为对方棋子（为边界退出判断）
				for (;; i--) {
					if (checkerboard[i - 1][c] == 0) {// 再上方为空
						break;
					} else if (checkerboard[i - 1][c] == myc) {// 再上方为我方棋子
						for (; i < r; i++) {
							checkerboard[i][c] = myc;// 翻转该再上棋子与落子之间的对方棋子
						}
						legal = true;// 确认为合理落子
						break;
					} // 再上方为对方棋子，继续判断，直到边界或我方棋子或空
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		try {
			i = r + 1;
			if (checkerboard[i][c] == opc) {
				for (;; i++) {
					if (checkerboard[i + 1][c] == 0) {
						break;
					} else if (checkerboard[i + 1][c] == myc) {
						for (; i > r; i--) {
							checkerboard[i][c] = myc;
						}
						legal = true;
						break;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		try {
			j = c + 1;
			if (checkerboard[r][j] == opc) {
				for (;; j++) {
					if (checkerboard[r][j + 1] == 0) {
						break;
					} else if (checkerboard[r][j + 1] == myc) {
						for (; j > c; j--) {
							checkerboard[r][j] = myc;
						}
						legal = true;
						break;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		try {
			j = c - 1;
			if (checkerboard[r][j] == opc) {
				for (;; j--) {
					if (checkerboard[r][j - 1] == 0) {
						break;
					} else if (checkerboard[r][j - 1] == myc) {
						for (; j < c; j++) {
							checkerboard[r][j] = myc;
						}
						legal = true;
						break;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		try {
			i = r - 1;// 取左上方棋子
			j = c - 1;
			if (checkerboard[i][j] == opc) {// 为对方棋子（为边界退出判断，后面也是一样）
				for (;; i--, j--) {
					if (checkerboard[i - 1][j - 1] == 0) {// 再左上方为空
						break;
					} else if (checkerboard[i - 1][j - 1] == myc) {// 再左上方为我方棋子
						for (; i < r; i++, j++) {// 行和列只需判断其中一个
							checkerboard[i][j] = myc;// 翻转该再上棋子与落子之间的对方棋子
						}
						legal = true;// 确认为合理落子
						break;
					} // 再左上方为对方棋子，继续判断，直到边界或我方棋子或空
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		try {
			i = r - 1;
			j = c + 1;
			if (checkerboard[i][j] == opc) {
				for (;; i--, j++) {
					if (checkerboard[i - 1][j + 1] == 0) {
						break;
					} else if (checkerboard[i - 1][j + 1] == myc) {
						for (; i < r; i++, j--) {
							checkerboard[i][j] = myc;
						}
						legal = true;
						break;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		try {
			i = r + 1;
			j = c - 1;
			if (checkerboard[i][j] == opc) {
				for (;; i++, j--) {
					if (checkerboard[i + 1][j - 1] == 0) {
						break;
					} else if (checkerboard[i + 1][j - 1] == myc) {
						for (; i > r; i--, j++) {
							checkerboard[i][j] = myc;
						}
						legal = true;
						break;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		try {
			i = r + 1;
			j = c + 1;
			if (checkerboard[i][j] == opc) {
				for (;; i++, j++) {
					if (checkerboard[i + 1][j + 1] == 0) {
						break;
					} else if (checkerboard[i + 1][j + 1] == myc) {
						for (; i > r; i--, j--) {
							checkerboard[i][j] = myc;
						}
						legal = true;
						break;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		if (legal == false) {
			checkerboard[r][c] = 0;
			return "nope";
		} else
			return checkerboardToString(checkerboard);
	}

	public boolean hasChoice() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (checkerboard[i][j] == 0) {
					if (!reverse(i, j).equals("nope")) {
						checkerboard = stringToCheckerboard(oldCheckerboard);
						return true;
					}
				}
			}
		}
		return false;
	}

	public String ifEnd() {// 是否游戏结束
		if (checkerboardToString(checkerboard).replace('0', '3').equals(checkerboardToString(checkerboard))) {// 棋盘是否已满
			int black = 0;
			int white = 0;
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (checkerboard[i][j] == 1) {
						black++;
					} else {
						white++;
					}
				}
			}
			if (black < white)
				return "win";
			else if (black > white)
				return "lose";
			else
				return "draw";
		}
		return "cotinue";
	}

	public static void main(String[] args) {
		getClient2().launch();
	}

	class MyMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			if (isMyTurn == false) {// 不是自己的回合不做反应
				return;
			}
			isMyTurn = false;// 防止快速点击出现bug
			String strOut = null;
			strOut = reverse(((MyJLabel) (e.getSource())).getRow(), ((MyJLabel) (e.getSource())).getColumn());

			switch (strOut) {
			case "win":
				break;
			case "lose":
				break;
			case "nope":
				jtf.setText("这里不能落子……请重新选择");
				isMyTurn = true;
				break;
			default:
				RefreshAndWaitsNew r = getClient2().new RefreshAndWaitsNew(strOut);
				Thread t = new Thread(r);
				t.start();// 之所以新开线程，是因为MyJLabel对象的listener响应时不能直接用removeAll()消灭掉该对象
				break;// 应该有更便捷的办法（不使用线程），不过用一下线程会显得比较厉害
			}

		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

	}

	class RefreshAndWaitsNew extends Thread {
		String strOut = null;

		public RefreshAndWaitsNew(String so) {
			strOut = so;
		}

		public void run() {
			try {
				checkerboard = stringToCheckerboard(strOut);// 更新棋盘
				setBoard();
				switch (ifEnd()) {
				case "continue":
					break;
				case "win":
					pw.println("lose");
					jtf.setText("您赢了！");
					break;
				case "lose":
					pw.println("win");
					jtf.setText("您输了！");
					break;
				case "draw":
					pw.println("draw");
					jtf.setText("平局！");
					break;
				default:
					pw.println(strOut);// 输出新棋盘
					jtf.setText("对方回合，请等待……");
					boolean exit = true;
					while (exit) {// 用于无处落子时循环
						String strIn = null;
						strIn = br.readLine();// 等待对方下棋
						switch (strIn) {
						case "lose":
							jtf.setText("您输了！");
							exit = false;
							break;
						case "win":
							jtf.setText("您赢了！");
							exit = false;
							break;
						case "draw":
							jtf.setText("平局！");
							exit = false;
							break;
						default:
							checkerboard = stringToCheckerboard(strIn);
							setBoard();
							switch (ifEnd()) {
							case "continue":
								break;
							case "win":
								jtf.setText("您赢了！");
								break;
							case "lose":
								jtf.setText("您输了！");
								break;
							case "draw":
								jtf.setText("平局！");
								break;
							default:
								break;
							}
							if (!hasChoice()) {
								jtf.setText("没有可以落子的格子！等待对手继续落子……");
								pw.println(strIn);
							} else {
								isMyTurn = true;
								jtf.setText("现在是您的回合，请落子：");
								exit = false;
							}
						}
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
