package Application;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class conditionWindow {

	private JFrame frame;
	public static appli mainAppli;
	private JTextField textField;
	private JTextField textField_6;
	private JPanel panelSouth;
	private JLabel labelError;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					conditionWindow window = new conditionWindow(mainAppli);
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
	public conditionWindow(appli appli) {
		mainAppli=appli;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0)));
		frame.getContentPane().add(panel, BorderLayout.NORTH);

		JLabel lblNewLabel = new JLabel("Create new condition");
		lblNewLabel.setFont(new Font("Sitka Text", Font.BOLD, 15));
		panel.add(lblNewLabel);

		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));

		JPanel panel_7 = new JPanel();
		panel_1.add(panel_7, BorderLayout.NORTH);

		JLabel lblNewLabel_1 = new JLabel("title");
		panel_7.add(lblNewLabel_1);

		textField = new JTextField();
		panel_7.add(textField);
		textField.setColumns(10);

		JPanel panel_9 = new JPanel();
		panel_1.add(panel_9);
		panel_9.setLayout(new BorderLayout(0, 0));

		textField_6 = new JTextField();
		panel_9.add(textField_6);
		textField_6.setColumns(10);

		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_9.add(panel_6, BorderLayout.NORTH);

		JLabel lblNewLabel_7 = new JLabel("create your own alphabet (separate letters with semicolons)");
		panel_6.add(lblNewLabel_7);

		panelSouth = new JPanel();
		panelSouth.setLayout(new BorderLayout());

		JPanel panel_2 = new JPanel();
		frame.getContentPane().add(panelSouth, BorderLayout.SOUTH);
		panel_2.setLayout(new GridLayout(0, 3, 0, 0));

		JPanel panel_3 = new JPanel();
		JPanel panel_4 = new JPanel();
		JPanel panel_5 = new JPanel();

		panelSouth.add(panel_2, BorderLayout.CENTER);

		JPanel panelError = new JPanel();

		panelSouth.add(panelError,BorderLayout.NORTH);

		labelError = new JLabel("");
		labelError.setForeground(new Color(255, 0, 0));
		panelError.add(labelError);

		panel_2.add(panel_3);

		JButton btnNewButton = new JButton("Cancel");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		panel_3.add(btnNewButton);
		panel_2.add(panel_4);

		JButton btnNewButton_1 = new JButton("OK");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<Character> charList = checkValue();
				if(charList != null) {
					frame.dispose();
					setAppli(mainAppli, textField.getText(), charList);
					panelError.remove(labelError);
				}
				else {
					labelError = new JLabel("Title is missing or wrong alphabet (only capital letters)");
					labelError.setForeground(Color.RED);
					panelError.add(labelError);
					frame.revalidate();
					frame.repaint();
				}
				
			}
		});
		panel_5.add(btnNewButton_1);
		panel_2.add(panel_5);
	}

	public void show() {
		this.frame.setVisible(true);
	}

	public List<Character> checkValue() {
	    String title = textField.getText();
	    String input = textField_6.getText();

	    if (title != null && !title.isEmpty()) {
	        if (input.matches("[a-z](;[a-z])*")) {
	            List<Character> lettres = new ArrayList<>();
	            for (String s : input.split(";")) {
	                char c = s.charAt(0);
	                if (!lettres.contains(c)) {
	                    lettres.add(c);
	                } else {
	                    return null; // lettre en double → rejet
	                }
	            }
	            return lettres;
	        }
	    }
	    return null; // soit mauvais format, soit titre vide
	}


	public void setAppli(appli appli, String title, List<Character> charList) {
		mainAppli=appli;
		mainAppli.newCondition(title, charList);
	}

}
