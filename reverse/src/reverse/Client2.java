//�������
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
	private boolean isMyTurn = false;// �Ƿ����Լ��غ�

	private Client2() {
		while (true) {
			try {
				String ip = JOptionPane.showInputDialog("�����������IP��");
				int port = Integer.parseInt(JOptionPane.showInputDialog("������������˿ںţ�"));
				socket = new Socket("192.168.0.117", 8888);

				jp1 = new JPanel();
				jtf = new JTextField(30);
				jtf.setFont(new Font("����", Font.BOLD, 20));
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
				JOptionPane.showMessageDialog(null, "�������Ӵ������������롣");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "���Ӵ���");
			}
		}
	}

	public static Client2 getClient2() {
		if (client2 == null) {
			client2 = new Client2();
		}
		return client2;
	}

	private void launch() {// ����
		try {
			pw = new PrintWriter(socket.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			jtf.setText("���Ű��壬���ߡ��ȴ��������ӡ���");
			String strIn = "";
			strIn = br.readLine();
			checkerboard = stringToCheckerboard(strIn);
			setBoard();
			jtf.setText("���������Ļغϣ������ӣ�");
			isMyTurn = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setBoard() {// ��������
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

	private String checkerboardToString(int[][] cb) {// ����ת�ַ���
		String str = "";
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				str += cb[i][j];
			}
		}
		return str;
	}

	private int[][] stringToCheckerboard(String str) {// �ַ���ת����
		int[][] cb = new int[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				cb[i][j] = (int) (str.charAt(i * 8 + j) - 48);
			}
		}
		return cb;
	}

	public String reverse(int r, int c) {// �߼��ж�
		oldCheckerboard = checkerboardToString(checkerboard);// ���浱ǰ���̣��Ա���hasChoice����Ƿ��޴����Ӻ�ԭ����
		boolean legal = false;// �Ϸ�����
		int myc = 2;// �ҵ���ɫ
		int opc = 1;// ������ɫ

		checkerboard[r][c] = myc;

		int i;
		int j;

		try {
			i = r - 1;
			if (checkerboard[i][c] == opc) {// �Ϸ�Ϊ�Է����ӣ�Ϊ�߽��˳��жϣ�
				for (;; i--) {
					if (checkerboard[i - 1][c] == 0) {// ���Ϸ�Ϊ��
						break;
					} else if (checkerboard[i - 1][c] == myc) {// ���Ϸ�Ϊ�ҷ�����
						for (; i < r; i++) {
							checkerboard[i][c] = myc;// ��ת����������������֮��ĶԷ�����
						}
						legal = true;// ȷ��Ϊ��������
						break;
					} // ���Ϸ�Ϊ�Է����ӣ������жϣ�ֱ���߽���ҷ����ӻ��
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
			i = r - 1;// ȡ���Ϸ�����
			j = c - 1;
			if (checkerboard[i][j] == opc) {// Ϊ�Է����ӣ�Ϊ�߽��˳��жϣ�����Ҳ��һ����
				for (;; i--, j--) {
					if (checkerboard[i - 1][j - 1] == 0) {// �����Ϸ�Ϊ��
						break;
					} else if (checkerboard[i - 1][j - 1] == myc) {// �����Ϸ�Ϊ�ҷ�����
						for (; i < r; i++, j++) {// �к���ֻ���ж�����һ��
							checkerboard[i][j] = myc;// ��ת����������������֮��ĶԷ�����
						}
						legal = true;// ȷ��Ϊ��������
						break;
					} // �����Ϸ�Ϊ�Է����ӣ������жϣ�ֱ���߽���ҷ����ӻ��
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

	public String ifEnd() {// �Ƿ���Ϸ����
		if (checkerboardToString(checkerboard).replace('0', '3').equals(checkerboardToString(checkerboard))) {// �����Ƿ�����
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
			if (isMyTurn == false) {// �����Լ��Ļغϲ�����Ӧ
				return;
			}
			isMyTurn = false;// ��ֹ���ٵ������bug
			String strOut = null;
			strOut = reverse(((MyJLabel) (e.getSource())).getRow(), ((MyJLabel) (e.getSource())).getColumn());

			switch (strOut) {
			case "win":
				break;
			case "lose":
				break;
			case "nope":
				jtf.setText("���ﲻ�����ӡ���������ѡ��");
				isMyTurn = true;
				break;
			default:
				RefreshAndWaitsNew r = getClient2().new RefreshAndWaitsNew(strOut);
				Thread t = new Thread(r);
				t.start();// ֮�����¿��̣߳�����ΪMyJLabel�����listener��Ӧʱ����ֱ����removeAll()������ö���
				break;// Ӧ���и���ݵİ취����ʹ���̣߳���������һ���̻߳��ԵñȽ�����
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
				checkerboard = stringToCheckerboard(strOut);// ��������
				setBoard();
				switch (ifEnd()) {
				case "continue":
					break;
				case "win":
					pw.println("lose");
					jtf.setText("��Ӯ�ˣ�");
					break;
				case "lose":
					pw.println("win");
					jtf.setText("�����ˣ�");
					break;
				case "draw":
					pw.println("draw");
					jtf.setText("ƽ�֣�");
					break;
				default:
					pw.println(strOut);// ���������
					jtf.setText("�Է��غϣ���ȴ�����");
					boolean exit = true;
					while (exit) {// �����޴�����ʱѭ��
						String strIn = null;
						strIn = br.readLine();// �ȴ��Է�����
						switch (strIn) {
						case "lose":
							jtf.setText("�����ˣ�");
							exit = false;
							break;
						case "win":
							jtf.setText("��Ӯ�ˣ�");
							exit = false;
							break;
						case "draw":
							jtf.setText("ƽ�֣�");
							exit = false;
							break;
						default:
							checkerboard = stringToCheckerboard(strIn);
							setBoard();
							switch (ifEnd()) {
							case "continue":
								break;
							case "win":
								jtf.setText("��Ӯ�ˣ�");
								break;
							case "lose":
								jtf.setText("�����ˣ�");
								break;
							case "draw":
								jtf.setText("ƽ�֣�");
								break;
							default:
								break;
							}
							if (!hasChoice()) {
								jtf.setText("û�п������ӵĸ��ӣ��ȴ����ּ������ӡ���");
								pw.println(strIn);
							} else {
								isMyTurn = true;
								jtf.setText("���������Ļغϣ������ӣ�");
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
