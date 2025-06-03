	package Application;
	
	import java.awt.EventQueue;
	
	import javax.swing.JFrame;
	import java.awt.BorderLayout;
	import javax.swing.JPanel;
	import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
	import javax.swing.JList;
	import javax.swing.AbstractListModel;
	import javax.swing.JComboBox;
	import javax.swing.JButton;
	import javax.swing.border.BevelBorder;
	
	import Exceptions.ProblemException;
	import Simulation.Answer_Memory;
	import Simulation.Problem;
	import Simulation.Procedure_Memory;
	
	import javax.swing.JSlider;
	import java.awt.event.ActionListener;
	import java.awt.event.ActionEvent;
	import javax.swing.DefaultComboBoxModel;
	import java.awt.event.MouseAdapter;
	import java.awt.event.MouseEvent;
	import java.util.Random;
	import java.awt.Font;
	import javax.swing.JRadioButton;
	import javax.swing.JScrollPane;
	import javax.swing.JTextArea;
	import java.awt.GridLayout;
	import javax.swing.JCheckBox;
	
	public class appli {
	
		private JFrame frame;
		private mainPanel mainPanel = new mainPanel() ;
		private parametersWindow paramWindow = parametersWindow.getInstance();
		private JRadioButton csc;
		private JRadioButton ncsc;
		private JLabel strSelected;
		private JTextArea history;
		private JTextArea infos;
		
		private Structure currentStr;
		private boolean randomProblem;
		
		private JComboBox augends;
		private JComboBox addends;
		private JButton autoB;
		private boolean auto
		;
	
		/**
		 * Launch the application.
		 */
		public static void main(String[] args) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						appli window = new appli();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	
		/**
		 * Create the application.
		 */
		public appli() {
			mainPanel.setAppli(this);
			paramWindow.setAppli(this);
			initialize();
		}
	
		/**
		 * Initialize the contents of the frame.
		 */
		private void initialize() {
			frame = new JFrame();
			frame.getContentPane().setLayout(new BorderLayout(0, 0));
			frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
	
			JPanel northPanel = new JPanel();
			northPanel.setBackground(new Color(255, 0, 0));
			frame.getContentPane().add(northPanel, BorderLayout.NORTH);
			northPanel.setLayout(new BorderLayout(0, 0));
	
			JPanel panel = new JPanel();
			panel.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0)));
			panel.setBackground(new Color(255, 255, 255));
			northPanel.add(panel, BorderLayout.WEST);
			panel.setLayout(new BorderLayout(0,0));
	
			JPanel panelN = new JPanel();
			JPanel panelS = new JPanel();
	
			panel.add(panelN, BorderLayout.NORTH);
			panel.add(panelS, BorderLayout.SOUTH);
			JLabel lblNewLabel_3 = new JLabel("Animation Speed");
			panelS.add(lblNewLabel_3);
			JSlider slider = new JSlider();
			slider.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					mainPanel.setVitesse(slider.getValue());
				}
			});
			slider.setValue(10);
			slider.setMaximum(50);
			slider.setMinimum(1);
			slider.setToolTipText("Set animation speed");
			slider.setSnapToTicks(true);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			panelS.add(slider);
	
	
	
			JLabel lblNewLabel_1 = new JLabel("Problem to solve : ");
			panelN.add(lblNewLabel_1);
	
			augends = new JComboBox();
			augends.setModel(new DefaultComboBoxModel(new String[] {"e", "f", "g", "h", "i", "j"}));
			panelN.add(augends);
	
			JLabel lblNewLabel_2 = new JLabel("+");
			panelN.add(lblNewLabel_2);
	
			addends = new JComboBox();
			panelN.add(addends);
			addends.addItem("2");
			addends.addItem("3");
			addends.addItem("4");
			addends.addItem("5");
	
			JButton startB = new JButton("Solve");
			startB.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainPanel.setVitesse(slider.getValue());
					mainPanel.start(augends.getSelectedItem() + "+" + addends.getSelectedItem(), csc.isSelected()?"CSC":"NCSC");
				}
			});
			panelN.add(startB);
	
			JPanel panel_6 = new JPanel();
			panel.add(panel_6, BorderLayout.CENTER);
			
			JCheckBox chckbxNewCheckBox = new JCheckBox("Random Problem");
			chckbxNewCheckBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					randomProblem=!randomProblem;
				}
			});
			panel_6.add(chckbxNewCheckBox);
			
			autoB = new JButton("Auto");
			autoB.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					auto=!auto;
					if(auto)
						autoB.setText("Stop");
					else
						autoB.setText("Auto");
					auto();
	
				}
			});
			panel_6.add(autoB);
	
			JPanel panel_2 = new JPanel();
			panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0)));
			panel_2.setBackground(new Color(192, 192, 192));
			northPanel.add(panel_2, BorderLayout.CENTER);
			panel_2.setLayout(new BorderLayout(0, 0));
	
			JPanel panel_3 = new JPanel();
			panel_3.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0)));
			panel_2.add(panel_3, BorderLayout.NORTH);
	
			JLabel lblNewLabel = new JLabel("Parameters and fast simulations");
			lblNewLabel.setFont(new Font("Rubik", Font.BOLD, 15));
			panel_3.add(lblNewLabel);
	
			JPanel panel_para = new JPanel();
			panel_para.setLayout(new BorderLayout());
	
			panel_2.add(panel_para);
	
			JPanel panel_4 = new JPanel();
			panel_para.add(panel_4, BorderLayout.CENTER);
	
	
			JButton btnNewButton_2 = new JButton("Generate 1 session");
			btnNewButton_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainPanel.session(csc.isSelected()?"CSC":"NCSC");
				}
			});
			panel_4.add(btnNewButton_2);
	
	
			JButton btnNewButton_1 = new JButton("Generate 10 sessions");
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainPanel.session10(csc.isSelected()?"CSC":"NCSC");
				}
			});
			panel_4.add(btnNewButton_1);
	
	
	
			JButton btnNewButton = new JButton("Parameters");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					paramWindow.show();
				}
			});
			panel_4.add(btnNewButton);
	
			JPanel panel_5 = new JPanel();
			panel_para.add(panel_5, BorderLayout.SOUTH);
	
			csc = new JRadioButton("CSC");
			csc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ncsc.setSelected(!ncsc.isSelected());
				}
			});
			csc.setSelected(true);
			panel_5.add(csc);
	
			ncsc = new JRadioButton("NCSC");
			ncsc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					csc.setSelected(!csc.isSelected());
					augends.setModel(new DefaultComboBoxModel(new String[] {"a", "c","e", "g", "i", "k"}));
				}
			});
			panel_5.add(ncsc);
	
			JPanel panel_1 = new JPanel();
			panel_1.setBackground(new Color(255, 128, 255));
			panel_1.setPreferredSize(new Dimension(225, 0));
			frame.getContentPane().add(panel_1, BorderLayout.EAST);
			panel_1.setLayout(new BorderLayout(0, 0));
	
			JPanel panel_7 = new JPanel();
			panel_7.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0)));
			panel_1.add(panel_7, BorderLayout.NORTH);
	
			JLabel lblNewLabel_4 = new JLabel("Informations");
			lblNewLabel_4.setFont(new Font("Rubik", Font.BOLD, 13));
			panel_7.add(lblNewLabel_4);
	
	
	
			JPanel panel_11 = new JPanel();
			panel_11.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0)));
			panel_1.add(panel_11, BorderLayout.CENTER);
			panel_11.setLayout(new GridLayout(0, 1, 0, 0));
	
			JPanel panel_12 = new JPanel();
			panel_11.add(panel_12);
			panel_12.setLayout(new BorderLayout(0, 0));
	
			infos = new JTextArea();
			infos.setEditable(false);
			JScrollPane scrollInfos = new JScrollPane(infos);
			scrollInfos.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollInfos.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			panel_12.add(scrollInfos, BorderLayout.CENTER);
	
	
			JPanel panel_13 = new JPanel();
			panel_12.add(panel_13, BorderLayout.NORTH);
	
			strSelected = new JLabel("Nothing Selected");
			panel_13.add(strSelected);
	
			JPanel panel_8 = new JPanel();
			panel_11.add(panel_8, BorderLayout.SOUTH);
			panel_8.setLayout(new BorderLayout(0, 0));
	
			JPanel panel_9 = new JPanel();
			panel_9.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0)));
			panel_8.add(panel_9, BorderLayout.NORTH);
	
			JLabel lblNewLabel_5 = new JLabel("History");
			lblNewLabel_5.setFont(new Font("Rubik", Font.BOLD, 13));
			panel_9.add(lblNewLabel_5);
	
			JPanel panel_10 = new JPanel();
			panel_8.add(panel_10, BorderLayout.CENTER);
			panel_10.setLayout(new BorderLayout(0, 0));
	
			history = new JTextArea() {
			    @Override
			    public boolean getScrollableTracksViewportWidth() {
			        return getPreferredSize().width < getParent().getWidth();
			    }
			};
			history.setLineWrap(true);
			history.setWrapStyleWord(true);

	
			JScrollPane scrollHistory = new JScrollPane(history);
			scrollHistory.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollHistory.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	
			panel_10.add(scrollHistory, BorderLayout.CENTER);
	
			
			//panel_10.add(history);
			
			frame.setBounds(100, 100, 883, 545);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	
		public void setParam(double dd, double ed, double wi, int ip, int incp, double is, double incs) {
			mainPanel.setParam(dd, ed, wi, ip, incp, is, incs);
		}
	
		public void select(Structure r) {
			currentStr = r;
			strSelected.setText(r.getName());
			if(r.getName().equals("Procedural Memory")){
				infos.setText(Procedure_Memory.getInstance().toString().replace(" / ", "\n"));
			}
			else if(r.getName().equals("Answer Memory")){
				infos.setText(Answer_Memory.getInstance().toString().replace(",", "\n"));	
			}
	
		}
	
		public void create(String pb) {
			history.setText("Problem created : " + pb);
			history.revalidate();
		    history.repaint();
		    frame.revalidate();
		    frame.repaint();
		}
	
		public void newRule(String rule) {
			history.setText(history.getText() + "\n" + "Rule chosen : " + rule);
			history.revalidate();
		    history.repaint();
		    frame.revalidate();
		    frame.repaint();
		}
	
		public void newChunk(char letter) {
			history.setText(history.getText() + "\n" + "New chunk created : " + letter + " + 1 = " + (char)(Integer.valueOf(letter)+1));
			history.revalidate();
		    history.repaint();
		    frame.revalidate();
		    frame.repaint();
		}
		
		public void ansRet(String str) {
			history.setText(history.getText() + "\n" + "The answer retrieved is : " + str);
			history.revalidate();
		    history.repaint();
		    frame.revalidate();
		    frame.repaint();
		}
	
		public void solution(char letter, String pb, boolean error) {
			history.setText(history.getText() + "\n" + "Solution : " + pb + " = " + letter + " " + (error?"❌":"✅"));
			updateInfos();
			history.revalidate();
		    history.repaint();
		    frame.revalidate();
		    frame.repaint();
		}
		
		public void updateInfos() {
			if(currentStr!=null) {
				if(currentStr.getName().equals("Procedural Memory")){
					infos.setText(Procedure_Memory.getInstance().toString().replace("/", "\n"));
				}
				else if(currentStr.getName().equals("Answer Memory")){
					infos.setText(Answer_Memory.getInstance().toString().replace(",", "\n"));	
				}
			}
		}
		
		public void auto() {
			if(auto)
			if(randomProblem){
				char[] letters = {'a', 'c', 'e', 'g', 'i', 'k'};
				int randomNumber = (int) ((Math.random() * (5 - 2 + 1)) + 2);
				int randomLetter;
				Problem problem;
				try {
				if(csc.isSelected()) {
					randomLetter = (int) ((Math.random() * (107 - 101)) + 101);
					problem = new Problem((char)(randomLetter) + "+" + randomNumber ,mainPanel.getModel(), "CSC","breaker");
					augends.setSelectedItem((char)(randomLetter) + "");
				}
				else {
					randomLetter = new Random().nextInt(letters.length);
					problem = new Problem(letters[randomLetter] + "+" + randomNumber ,mainPanel.getModel(), "NCSC","breaker");
					augends.setSelectedItem(letters[randomLetter] + "");
				}
				addends.setSelectedItem(randomNumber + "");
				mainPanel.start(problem.getName(), csc.isSelected()?"CSC":"NCSC");
				}
				catch(ProblemException e) {
					
				}
			}
			else {
				mainPanel.start(augends.getSelectedItem() + "+" + addends.getSelectedItem(), csc.isSelected()?"CSC":"NCSC");
			}
			
			
			
			
		}
	
	}
