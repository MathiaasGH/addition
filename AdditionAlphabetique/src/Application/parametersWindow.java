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
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextField;

public class parametersWindow {

	private JFrame frame;
	public static appli mainAppli;
	public static parametersWindow instance = new parametersWindow();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JPanel panelSouth;
	private JLabel labelError;

	private double dd=1.2;
	private double ed=0;
	private double wi=1.5;
	private int ip=0;
	private int incp=1;
	private double is=1.5;
	private double incs=2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					parametersWindow window = new parametersWindow();
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
	private parametersWindow() {
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

		JLabel lblNewLabel = new JLabel("Settings");
		lblNewLabel.setFont(new Font("Sitka Text", Font.BOLD, 15));
		panel.add(lblNewLabel);

		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new GridLayout(7, 1, 0, 0));

		JPanel panel_7 = new JPanel();
		panel_1.add(panel_7);

		JLabel lblNewLabel_1 = new JLabel("decision determinism : ");
		panel_7.add(lblNewLabel_1);

		textField = new JTextField();
		textField.setText(Double.toString(dd));
		panel_7.add(textField);
		textField.setColumns(10);

		JPanel panel_8 = new JPanel();
		panel_1.add(panel_8);

		JLabel lblNewLabel_2 = new JLabel("error discount");
		panel_8.add(lblNewLabel_2);

		textField_1 = new JTextField();
		textField_1.setText(Double.toString(ed));
		panel_8.add(textField_1);
		textField_1.setColumns(10);

		JPanel panel_6 = new JPanel();
		panel_1.add(panel_6);

		JLabel lblNewLabel_3 = new JLabel("weight increase");
		panel_6.add(lblNewLabel_3);

		textField_2 = new JTextField();
		textField_2.setText(Double.toString(wi));
		panel_6.add(textField_2);
		textField_2.setColumns(10);

		JPanel panel_10 = new JPanel();
		panel_1.add(panel_10);

		JLabel lblNewLabel_4 = new JLabel("initial practice");
		panel_10.add(lblNewLabel_4);

		textField_3 = new JTextField();
		textField_3.setText(Integer.toString(ip));
		panel_10.add(textField_3);
		textField_3.setColumns(10);

		JPanel panel_11 = new JPanel();
		panel_1.add(panel_11);

		JLabel lblNewLabel_5 = new JLabel("increase practice");
		panel_11.add(lblNewLabel_5);

		textField_4 = new JTextField();
		textField_4.setText(Integer.toString(incp));
		panel_11.add(textField_4);
		textField_4.setColumns(10);

		JPanel panel_12 = new JPanel();
		panel_1.add(panel_12);

		JLabel lblNewLabel_6 = new JLabel("initial strength");
		panel_12.add(lblNewLabel_6);

		textField_5 = new JTextField();
		textField_5.setText(Double.toString(is));
		panel_12.add(textField_5);
		textField_5.setColumns(10);

		JPanel panel_9 = new JPanel();
		panel_1.add(panel_9);

		JLabel lblNewLabel_7 = new JLabel("increase strength");
		panel_9.add(lblNewLabel_7);

		textField_6 = new JTextField();
		textField_6.setText(Double.toString(incs));
		panel_9.add(textField_6);
		textField_6.setColumns(10);

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
				textField.setText(Double.toString(dd));
				textField_1.setText(Double.toString(ed));
				textField_2.setText(Double.toString(wi));
				textField_3.setText(Integer.toString(ip));
				textField_4.setText(Integer.toString(incp));
				textField_5.setText(Double.toString(is));
				textField_6.setText(Double.toString(incs));

			}
		});
		panel_3.add(btnNewButton);
		panel_2.add(panel_4);

		JButton btnNewButton_1 = new JButton("OK");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkValue()) {
					System.out.println(wi);
					mainAppli.setParam(dd,ed,wi,ip,incp,is,incs);
					frame.dispose();
				}
			}
		});
		panel_4.add(btnNewButton_1);
		panel_2.add(panel_5);

		JButton btnNewButton_2 = new JButton("Reset");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dd=1.2;
				ed=0;
				wi=1.5;
				ip=0;
				incp=1;
				is=1.5;
				incs=2;
				textField.setText(Double.toString(dd));
				textField_1.setText(Double.toString(ed));
				textField_2.setText(Double.toString(wi));
				textField_3.setText(Integer.toString(ip));
				textField_4.setText(Integer.toString(incp));
				textField_5.setText(Double.toString(is));
				textField_6.setText(Double.toString(incs));	
				mainAppli.setParam(dd,ed,wi,ip,incp,is,incs);
			}
		});
		panel_5.add(btnNewButton_2);
	}

	public static parametersWindow getInstance() {
		return instance;
	}

	public void show() {
		this.frame.setVisible(true);
	}

	public boolean checkValue() {
		double dd;
		double ed;
		double wi;
		int ip;
		int incp;
		double is;
		double incs;
		try {
			dd = Double.parseDouble(textField.getText());
		} catch (NumberFormatException e) {
			labelError.setText("double determinism must be a double.");
			return false;
		}
		try {
			ed = Double.parseDouble(textField_1.getText());
		} catch (NumberFormatException e) {
			labelError.setText("error discount must be a double.");
			return false;
		}
		try {
			wi = Double.parseDouble(textField_2.getText());
		} catch (NumberFormatException e) {
			labelError.setText("weight increase must be a double.");
			return false;
		}
		try {
			ip = Integer.parseInt(textField_3.getText());
		} catch (NumberFormatException e) {
			labelError.setText("initial practice must be an integer.");
			return false;
		}
		try {
			incp = Integer.parseInt(textField_4.getText());
		} catch (NumberFormatException e) {
			labelError.setText("increase practice must be an integer.");
			return false;
		}
		try {
			is = Double.parseDouble(textField_5.getText());
		} catch (NumberFormatException e) {
			labelError.setText("initial strength must be a double.");
			return false;
		}
		try {
			incs = Double.parseDouble(textField_6.getText());
		} catch (NumberFormatException e) {
			labelError.setText("increase strength must be a double.");
			return false;
		}
		this.dd=dd;
		this.ed=ed;
		this.wi=wi;
		this.ip=ip;
		this.incp=incp;
		this.is=is;
		this.incs=incs;
		labelError.setText("");
		return true;
	}
	
	public void setAppli(appli appli) {
		mainAppli=appli;
		mainAppli.setParam(dd,ed,wi,ip,incp,is,incs);
	}

}
