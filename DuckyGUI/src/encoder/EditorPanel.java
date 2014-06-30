package encoder;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class EditorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextPane textArea = new JTextPane();
	private JScrollPane scrollPane = new JScrollPane(textArea);
	private File f;

	public EditorPanel() {
		setBorder(new TitledBorder(null, "Editor", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(null);
		scrollPane.setBounds(8, 24, 386, 166);
		add(scrollPane);

		textArea.setBounds(10, 26, 164, 138);

		CodeDocument doc = new CodeDocument();
		Map<String, Color> keywords = new HashMap<String, Color>();

		Color comment = new Color(63, 197, 95);
		Color javadoc = new Color(63, 95, 191);
		Color annotation = new Color(100, 100, 100);
		doc.setCommentColor(comment);
		doc.setJavadocColor(javadoc);
		doc.setAnnotationColor(annotation);

		Color defColor = Color.BLUE;
		keywords.put("rem", comment);
		keywords.put("ctrl", defColor);
		keywords.put("control", defColor);
		keywords.put("control", defColor);
		keywords.put("ctrl-alt", defColor);
		keywords.put("ctrl-shift", defColor);
		keywords.put("default_delay", defColor);
		keywords.put("defaultdelay", defColor);
		keywords.put("gui", defColor);
		keywords.put("windows", defColor);
		keywords.put("alt-shift", defColor);
		keywords.put("shift", defColor);
		keywords.put("string", defColor);
		keywords.put("repeat", defColor);
		keywords.put("space", defColor);
		keywords.put("home", defColor);
		keywords.put("end", defColor);
		keywords.put("alt", defColor);
		keywords.put("downarrow", defColor);
		keywords.put("leftarrow", defColor);
		keywords.put("rightarrow", defColor);
		keywords.put("uparrow", defColor);
		keywords.put("tab", defColor);
		keywords.put("escape", defColor);
		keywords.put("capslock", defColor);
		keywords.put("delay", defColor);
		keywords.put("enter", defColor);

		
		doc.setKeywords(keywords);
		textArea.setDocument(doc);
	}

	public void setFile(File f) {
		this.f = f;
	}
	
	public String getContent() {
		return textArea.getText();
	}

	public void outputFile() {
		BufferedReader reader = null;
		FileReader fileReader;
		String line;
		try {
			fileReader = new FileReader(f);
			reader = new BufferedReader(fileReader);
			while ((line = reader.readLine()) != null) {
				appendString(line + "\n");
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this,
					"File not found.", "File Not Found",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"Input/Output Error.", "IO Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (BadLocationException e) {
			JOptionPane.showMessageDialog(this,
					"Bad Location Error.", "Bad Location",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void appendString(String str) throws BadLocationException {
		StyledDocument document = (StyledDocument) textArea.getDocument();
		document.insertString(document.getLength(), str, null);
		// ^ or your style attribute
	}
}
