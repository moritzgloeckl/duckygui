package ducky.gui.encoder;

import ducky.gui.GUIFrame;
import ducky.gui.Settings;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathPanel extends JPanel implements ActionListener {

	/**
     *
	 */
	private static final long serialVersionUID = 1L;
	/**
     *
	 */
	private JTextField inputField;
	private JTextField outputField;
	private JFileChooser fileChooser = new JFileChooser();
	private JButton inputBrowse = new JButton("Browse");
	private JButton outputBrowse = new JButton("Browse");
	private GUIFrame frame;
	private String[] languages = {"be", "ca", "ch", "de", "dk", "es", "fr", "gb", "it", "no", "pt", "ru", "se", "sv", "uk", "us"};
	private JComboBox<String> comboBox = new JComboBox<String>(languages);
    private Settings settings;

	/**
	 * Create the panel.
	 */
    public PathPanel(GUIFrame frame, Settings settings) {
		setBorder(new TitledBorder(null, "Paths", TitledBorder.LEFT,
				TitledBorder.TOP, null, null));
		setLayout(null);

        this.settings = settings;
		this.frame = frame;
		JLabel label = new JLabel("Input file:");
		label.setBounds(10, 25, 72, 14);
		add(label);

        inputField = getTextField(22);
		inputField.setToolTipText("Enter path of the input file");
		add(inputField);

		JLabel label_1 = new JLabel("Output file:");
		label_1.setBounds(10, 81, 72, 14);
		add(label_1);

        outputField = getTextField(78);
		add(outputField);

		JLabel label_2 = new JLabel("Layout file:");
		label_2.setBounds(10, 53, 72, 14);
		add(label_2);

		inputBrowse.setBounds(323, 21, 72, 23);
		inputBrowse.addActionListener(this);
		add(inputBrowse);

		outputBrowse.setBounds(323, 77, 72, 23);
		outputBrowse.addActionListener(this);
		add(outputBrowse);

		comboBox.setBounds(76, 50, 220, 20);
		comboBox.addActionListener(this);
		add(comboBox);
	}

    /**
     * get paths from user settings
     */
    public void syncSettings() {
        String inputFilePath = settings.getInputFilePath();
        inputField.setText(inputFilePath);
        Path path = Paths.get(inputFilePath);
        if (Files.exists(path)) {
            frame.openInEditor(path.toFile());
        }

        outputField.setText(settings.getOutputFilePath());
        comboBox.setSelectedItem(settings.getLayout());
    }

    private JTextField getTextField(int y) {
        JTextField textField = new JTextField();
        textField.setEditable(false);
        textField.setColumns(10);
        textField.setBounds(76, y, 220, 20);
        return textField;
    }

    private File openFileChooser(String ext, String defaultPath) {
        fileChooser = new JFileChooser(defaultPath);
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
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    private void setInputField(String path) {
        inputField.setText(path);
        settings.setInputFilePath(path);
    }

    private void setOutputField(String path) {
        outputField.setText(path);
        settings.setOutputFilePath(path);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == inputBrowse) {
            File f = openFileChooser(".txt", settings.getInputFilePath());
			if (f != null) {
                setInputField(f.getAbsolutePath());
				frame.openInEditor(f);
			}
		} else if (e.getSource() == outputBrowse) {
            File f = openFileChooser(".bin", settings.getOutputFilePath());
			if (f != null) {
                setOutputField(f.getAbsolutePath());
            }
        } else if (e.getSource() == comboBox) {
            settings.setLayout((String) comboBox.getSelectedItem());
		}
	}
}
