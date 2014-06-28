package encoder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.JComboBox;

public class PathPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private JTextField inputField;
	private JTextField outputField;
	private JFileChooser fileChooser = new JFileChooser();
	private JButton inputBrowse = new JButton("Browse");
	private JButton outputBrowse = new JButton("Browse");
	private JButton layoutBrowse = new JButton("Browse");
	private File[] files = new File[3];
	private GUIFrame frame;
	private String[] languages = {"be", "ca", "ch", "de", "dk", "es", "fr", "gb", "it", "no", "pt", "ru", "se", "sv", "uk", "us"};
	private JComboBox<String> comboBox = new JComboBox<String>(languages);

	/**
	 * Create the panel.
	 */
	public PathPanel(GUIFrame frame) {
		setBorder(new TitledBorder(null, "Paths", TitledBorder.LEFT,
				TitledBorder.TOP, null, null));
		setLayout(null);

		this.frame = frame;
		JLabel label = new JLabel("Input file:");
		label.setBounds(10, 25, 72, 14);
		add(label);

		inputField = new JTextField();
		inputField.setToolTipText("Enter path of the input file");
		inputField.setColumns(10);
		inputField.setBounds(76, 22, 220, 20);
		add(inputField);

		JLabel label_1 = new JLabel("Output file:");
		label_1.setBounds(10, 81, 72, 14);
		add(label_1);

		outputField = new JTextField();
		outputField.setColumns(10);
		outputField.setBounds(76, 78, 220, 20);
		add(outputField);

		JLabel label_2 = new JLabel("Layout file:");
		label_2.setBounds(10, 53, 72, 14);
		add(label_2);

		inputBrowse.setBounds(323, 21, 72, 23);
		inputBrowse.addActionListener(this);
		add(inputBrowse);

		layoutBrowse.setBounds(323, 49, 72, 23);
		layoutBrowse.addActionListener(this);
		add(layoutBrowse);

		outputBrowse.setBounds(323, 77, 72, 23);
		outputBrowse.addActionListener(this);
		add(outputBrowse);

		comboBox.setBounds(76, 50, 220, 20);
		comboBox.setEditable(true);
		comboBox.addActionListener(this);

		add(comboBox);
	}

	public String[] getPaths() {
		String[] paths = new String[3];
		paths[0] = inputField.getText();
		paths[1] = (String) comboBox.getSelectedItem();
		paths[2] = outputField.getText();

		return paths;
	}

	private File openFileChooser(final String ext) {
		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Choose a file");
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory()
						|| f.getName().toLowerCase().endsWith(ext);
			}

			@Override
			public String getDescription() {
				if (ext.equals(".txt")) {
					return "Text Documents (*.txt)";
				} else if (ext.equals(".properties")) {
					return "Properties (*.properties)";
				} else if (ext.equals(".bin")) {
					return ("Binary Files (*.bin)");
				} else {
					return "All Files (*.*)";
				}
			}
		});
		int state = fileChooser.showOpenDialog(null);
		if (state == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			return file;
		} else {
			System.out.println("Cancel");
			return null;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == inputBrowse) {
			File f = openFileChooser(".txt");
			if (f != null) {
				files[0] = f;
				inputField.setText(f.getAbsolutePath());
				frame.openInEditor(f);
			}
		} else if (e.getSource() == layoutBrowse) {
			File f = openFileChooser(".properties");
			if (f != null) {
				files[1] = f;
				comboBox.setSelectedItem(f.getAbsolutePath());
			}
		} else if (e.getSource() == outputBrowse) {
			File f = openFileChooser(".bin");
			if (f != null) {
				files[2] = f;
				outputField.setText(f.getAbsolutePath());
			}
		} 
	}
}
