package encoder;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;

public class GUIFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Encoder encoder = new Encoder();
	private PathPanel pathPanel;
	private EditorPanel editorPanel = new EditorPanel();
	private JButton btnSaveFile;
	private JButton export = new JButton("Export bin");

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Unknown Error.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIFrame frame = new GUIFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
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

		pathPanel = new PathPanel(this);

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
		String[] paths = pathPanel.getPaths();
		boolean error = false;
		if (paths[0].equals("")) {
			if (getContent().length() != 0) {
				encoder.setInputText(getContent());
			} else {
				error = true;
				JOptionPane.showMessageDialog(this, "Please specify an input path or write some code in the editor.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			encoder.setInput(paths[0]);
		}
		
		if (paths[2].equals("")) {
			error = true;
			JOptionPane.showMessageDialog(this, "Please specify an output path.", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			encoder.setOutput(paths[2]);
		}
		encoder.setLayout(paths[1]);
		if (!error) {
			encoder.encode();
		}
	}
	
	private String getContent() {
		return editorPanel.getContent();
	}

	private void saveFile() {
		JFileChooser fileChooser = new JFileChooser();
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
