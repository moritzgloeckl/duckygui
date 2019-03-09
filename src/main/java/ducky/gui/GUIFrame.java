package ducky.gui;

import ducky.gui.encoder.EditorPanel;
import ducky.gui.encoder.Encoder;
import ducky.gui.encoder.PathPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GUIFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Encoder encoder = new Encoder();
	private PathPanel pathPanel;
    private EditorPanel editorPanel;
	private JButton btnSaveFile;
	private JButton export = new JButton("Export bin");
    private Settings settings;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Unknown Error.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		EventQueue.invokeLater(() -> {
			try {
				GUIFrame frame = new GUIFrame();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUIFrame() {
		setResizable(false);
		setTitle("Ducky Encoder");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 430, 410);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

        settings = new Settings();
        pathPanel = new PathPanel(this, settings);
        editorPanel = new EditorPanel();

		pathPanel.setBounds(10, 11, 406, 108);
		contentPane.add(pathPanel);

		editorPanel.setBounds(10, 130, 406, 206);
		contentPane.add(editorPanel);

		export.setBounds(10, 347, 200, 23);
		export.addActionListener(this);
		contentPane.add(export);

		btnSaveFile = new JButton("Save file");
		btnSaveFile.addActionListener(this);
		btnSaveFile.setBounds(216, 347, 200, 23);
		contentPane.add(btnSaveFile);

        pathPanel.syncSettings(); // put here to not get Nullpointer - because will call openInEditor() callback
	}

	public void openInEditor(File f) {
		editorPanel.setFile(f);
		editorPanel.outputFile();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == export) {
			encode();
		} else if (e.getSource() == btnSaveFile) {
			saveFile();
		}
	}

	private void encode() {
        String inputPath = settings.getInputFilePath();
		boolean error = false;
        if (inputPath.equals("")) {
			if (getContent().length() != 0) {
				encoder.setInputText(getContent());
			} else {
				error = true;
				JOptionPane.showMessageDialog(this, "Please specify an input path or write some code in the editor.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
            encoder.setInput(inputPath);
        }

        String outputPath = settings.getOutputFilePath();
        if (outputPath.equals("")) {
			error = true;
			JOptionPane.showMessageDialog(this, "Please specify an output path.", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else {
            encoder.setOutput(outputPath);
        }
        encoder.setLayout(settings.getLayout());
		if (!error) {
			encoder.encode();
		}
	}
	
	private String getContent() {
		return editorPanel.getContent();
	}

	private void saveFile() {
        JFileChooser fileChooser = new JFileChooser(settings.getInputFilePath());
		fileChooser.setDialogTitle("Specify a file to save");

		int userSelection = fileChooser.showSaveDialog(this);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(fileToSave));
				writer.write(getContent());

			} catch (IOException e) {
				JOptionPane.showMessageDialog(this,
						"Input/Output Error.", "IO Error",
						JOptionPane.ERROR_MESSAGE);
			} finally {
				try {
					if (writer != null)
						writer.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this,
							"Input/Output Error.", "IO Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
