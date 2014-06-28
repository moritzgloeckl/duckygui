package encoder;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

public class Encoder {

	private static Properties keyboardProps = new Properties();
	private static Properties layoutProps = new Properties();
	private static String version = "2.6.3";
	private String inputFile;
	private String outputFile;
	private String layoutFile;
	private static boolean debug = false;
	private String inputText;

	public Encoder() {

	}

	public void setInputText(String in) {
		this.inputText = in;
	}

	public void setInput(String in) {
		this.inputFile = in;
	}

	public void setOutput(String in) {
		this.outputFile = in;
	}

	public void setLayout(String in) {
		this.layoutFile = in;
	}

	public void encode() {
		String scriptStr = null;

		if (inputFile == null) {
			if (inputText != null) {
				scriptStr = inputText;
			}
		} else {
			if (inputFile.contains(".rtf")) {
				try {
					FileInputStream stream = new FileInputStream(inputFile);
					RTFEditorKit kit = new RTFEditorKit();
					Document doc = kit.createDefaultDocument();
					kit.read(stream, doc, 0);

					scriptStr = doc.getText(0, doc.getLength());
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "File not found.",
							"Error", JOptionPane.ERROR_MESSAGE);
				} catch (BadLocationException e) {
					JOptionPane.showMessageDialog(null, "File not found.",
							"Error", JOptionPane.ERROR_MESSAGE);
				}

			} else {
				DataInputStream in = null;
				try {
					File f = new File(inputFile);
					byte[] buffer = new byte[(int) f.length()];
					in = new DataInputStream(new FileInputStream(f));
					in.readFully(buffer);
					scriptStr = new String(buffer);
					System.out.println("Loading File .....\t\t[ OK ]");
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "File not found.",
							"Error", JOptionPane.ERROR_MESSAGE);

				} finally {
					try {
						if (in != null) {
							in.close();
						}
					} catch (IOException e) { /* ignore it */
						JOptionPane.showMessageDialog(null,
								"Input/Output Error.", "IO Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}

		if (layoutFile.equals("") || layoutFile == null) {
			layoutFile = "us";
		}

		loadProperties(layoutFile);

		encodeToFile(scriptStr, (outputFile == null) ? "inject.bin"
				: outputFile);
	}

	private static void loadProperties(String lang) {
		System.out.println("LALA: " + lang);
		InputStream in;
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		try {
			in = loader.getResourceAsStream("keyboard.properties");
			if (in != null) {
				keyboardProps.load(in);
				in.close();
				System.out.println("Loading Keyboard File .....\t[ OK ]");
			} else {
				JOptionPane.showMessageDialog(null,
						"Error with keyboard.properties file.",
						"Properties Error", JOptionPane.ERROR_MESSAGE);
				System.out.println("Error with keyboard.properties!");
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error with .properties file.",
					"Properties Error", JOptionPane.ERROR_MESSAGE);
		}

		try {
			in = loader.getResourceAsStream(lang + ".properties");
			if (in != null) {
				layoutProps.load(in);
				in.close();
				System.out.println("Loading Language File .....\t[ OK ]");
			} else {
				if (new File(lang).isFile()) {
					layoutProps.load(new FileInputStream(lang));
					System.out.println("Loading Language File .....\t[ OK ]");
				} else {
					JOptionPane.showMessageDialog(null,
							"External layout.properties not found.",
							"Properties Error", JOptionPane.ERROR_MESSAGE);
					System.out.println("External layout.properties non found!");
				}
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error with .properties file.",
					"Properties Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static void encodeToFile(String inStr, String fileDest) {

		inStr = inStr.replaceAll("\\r", ""); // CRLF Fix
		String[] instructions = inStr.split("\n");
		String[] last_instruction = inStr.split("\n");
		List<Byte> file = new ArrayList<Byte>();
		int defaultDelay = 0;
		int loop = 0;
		boolean repeat = false;
		System.out.println("Loading DuckyScript .....\t[ OK ]");
		if (debug)
			System.out.println("\nParsing Commands:");
		for (int i = 0; i < instructions.length; i++) {
			try {
				boolean delayOverride = false;
				String commentCheck = instructions[i].substring(0, 2);
				if (commentCheck.equals("//"))
					continue;
				if (instructions[i].equals("\n"))
					continue;
				String[] instruction = instructions[i].split(" ", 2);

				if (i > 0) {
					last_instruction = instructions[i - 1].split(" ", 2);
					last_instruction[0].trim();
					if (last_instruction.length == 2) {
						last_instruction[1].trim();
					}
				} else {
					last_instruction = instructions[i].split(" ", 2);
					last_instruction[0].trim();
					if (last_instruction.length == 2) {
						last_instruction[1].trim();
					}
				}

				instruction[0].trim();

				if (instruction.length == 2) {
					instruction[1].trim();
				}

				if (instruction[0].equals("REM")) {
					continue;
				}
				if (instruction[0].equals("REPEAT")) {
					loop = Integer.parseInt(instruction[1].trim());
					repeat = true;
				} else {
					repeat = false;
					loop = 1;
				}
				while (loop > 0) {
					if (repeat) {
						instruction = last_instruction;
						// System.out.println(Integer.toString(instruction.length));
					}
					if (debug)
						System.out.println(instruction[0] + " "
								+ instruction[1]);
					if (instruction[0].equals("DEFAULT_DELAY")
							|| instruction[0].equals("DEFAULTDELAY")) {
						defaultDelay = Integer.parseInt(instruction[1].trim());
						delayOverride = true;
					} else if (instruction[0].equals("DELAY")) {
						int delay = Integer.parseInt(instruction[1].trim());
						while (delay > 0) {
							file.add((byte) 0x00);
							if (delay > 255) {
								file.add((byte) 0xFF);
								delay = delay - 255;
							} else {
								file.add((byte) delay);
								delay = 0;
							}
						}
						delayOverride = true;
					} else if (instruction[0].equals("STRING")) {
						for (int j = 0; j < instruction[1].length(); j++) {
							char c = instruction[1].charAt(j);
							addBytes(file, charToBytes(c));
						}
					} else if (instruction[0].equals("CONTROL")
							|| instruction[0].equals("CTRL")) {
						if (instruction.length != 1) {
							file.add(strInstrToByte(instruction[1]));
							file.add(strToByte(keyboardProps
									.getProperty("MODIFIERKEY_CTRL")));
						} else {
							file.add(strToByte(keyboardProps
									.getProperty("KEY_LEFT_CTRL")));
							file.add((byte) 0x00);
						}
					} else if (instruction[0].equals("ALT")) {
						if (instruction.length != 1) {
							file.add(strInstrToByte(instruction[1]));
							file.add(strToByte(keyboardProps
									.getProperty("MODIFIERKEY_ALT")));
						} else {
							file.add(strToByte(keyboardProps
									.getProperty("KEY_LEFT_ALT")));
							file.add((byte) 0x00);
						}
					} else if (instruction[0].equals("SHIFT")) {
						if (instruction.length != 1) {
							file.add(strInstrToByte(instruction[1]));
							file.add(strToByte(keyboardProps
									.getProperty("MODIFIERKEY_SHIFT")));
						} else {
							file.add(strToByte(keyboardProps
									.getProperty("KEY_LEFT_SHIFT")));
							file.add((byte) 0x00);
						}
					} else if (instruction[0].equals("CTRL-ALT")) {
						if (instruction.length != 1) {
							file.add(strInstrToByte(instruction[1]));
							file.add((byte) (strToByte(keyboardProps
									.getProperty("MODIFIERKEY_CTRL")) | strToByte(keyboardProps
									.getProperty("MODIFIERKEY_ALT"))));
						} else {
							continue;
						}
					} else if (instruction[0].equals("CTRL-SHIFT")) {
						if (instruction.length != 1) {
							file.add(strInstrToByte(instruction[1]));
							file.add((byte) (strToByte(keyboardProps
									.getProperty("MODIFIERKEY_CTRL")) | strToByte(keyboardProps
									.getProperty("MODIFIERKEY_SHIFT"))));
						} else {
							continue;
						}
					} else if (instruction[0].equals("COMMAND-OPTION")) {
						if (instruction.length != 1) {
							file.add(strInstrToByte(instruction[1]));
							file.add((byte) (strToByte(keyboardProps
									.getProperty("MODIFIERKEY_KEY_LEFT_GUI")) | strToByte(keyboardProps
									.getProperty("MODIFIERKEY_ALT"))));
						} else {
							continue;
						}
					} else if (instruction[0].equals("ALT-SHIFT")) {
						if (instruction.length != 1) {
							file.add(strInstrToByte(instruction[1]));
							file.add((byte) (strToByte(keyboardProps
									.getProperty("MODIFIERKEY_LEFT_ALT")) | strToByte(keyboardProps
									.getProperty("MODIFIERKEY_SHIFT"))));
						} else {
							file.add(strToByte(keyboardProps
									.getProperty("KEY_LEFT_ALT")));
							file.add((byte) (strToByte(keyboardProps
									.getProperty("MODIFIERKEY_LEFT_ALT")) | strToByte(keyboardProps
									.getProperty("MODIFIERKEY_SHIFT"))));
						}
					} else if (instruction[0].equals("ALT-TAB")) {
						if (instruction.length == 1) {
							file.add(strToByte(keyboardProps
									.getProperty("KEY_TAB")));
							file.add(strToByte(keyboardProps
									.getProperty("MODIFIERKEY_LEFT_ALT")));
						} else {
							// do something?
						}
					} else if (instruction[0].equals("REM")) {
						/* no default delay for the comments */
						delayOverride = true;
						continue;
					} else if (instruction[0].equals("WINDOWS")
							|| instruction[0].equals("GUI")) {
						if (instruction.length == 1) {
							file.add(strToByte(keyboardProps
									.getProperty("MODIFIERKEY_LEFT_GUI")));
							file.add((byte) 0x00);
						} else {
							file.add(strInstrToByte(instruction[1]));
							file.add(strToByte(keyboardProps
									.getProperty("MODIFIERKEY_LEFT_GUI")));
						}
					} else if (instruction[0].equals("COMMAND")) {
						if (instruction.length == 1) {
							file.add(strToByte(keyboardProps
									.getProperty("KEY_COMMAND")));
							file.add((byte) 0x00);
						} else {
							file.add(strInstrToByte(instruction[1]));
							file.add(strToByte(keyboardProps
									.getProperty("MODIFIERKEY_LEFT_GUI")));
						}
					} else {
						/* treat anything else as a key */
						file.add(strInstrToByte(instruction[0]));
						file.add((byte) 0x00);
					}
					loop--;
				}
				// Default delay
				if (!delayOverride & defaultDelay > 0) {
					int delayCounter = defaultDelay;
					while (delayCounter > 0) {
						file.add((byte) 0x00);
						if (delayCounter > 255) {
							file.add((byte) 0xFF);
							delayCounter = delayCounter - 255;
						} else {
							file.add((byte) delayCounter);
							delayCounter = 0;
						}
					}
				}
			} catch (StringIndexOutOfBoundsException e) {
				// do nothing
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Error on line: " + (i + 1), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		// Write byte array to file
		byte[] data = new byte[file.size()];
		for (int i = 0; i < file.size(); i++) {
			data[i] = file.get(i);
		}
		try {
			File someFile = new File(fileDest);
			FileOutputStream fos = new FileOutputStream(someFile);
			fos.write(data);
			fos.flush();
			fos.close();
			JOptionPane.showMessageDialog(
					null,
					"Binary file sucessfully exported to "
							+ someFile.getAbsolutePath(), "Success",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Failed to write hex file.",
					"File Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	private static void addBytes(List<Byte> file, byte[] byteTab) {
		for (int i = 0; i < byteTab.length; i++)
			file.add(byteTab[i]);
		if (byteTab.length % 2 != 0) {
			file.add((byte) 0x00);
		}
	}

	private static byte[] charToBytes(char c) {
		return codeToBytes(charToCode(c));
	}

	private static String charToCode(char c) {
		String code;
		if (c < 128) {
			code = "ASCII_" + Integer.toHexString(c).toUpperCase();
		} else if (c < 256) {
			code = "ISO_8859_1_" + Integer.toHexString(c).toUpperCase();
		} else {
			code = "UNICODE_" + Integer.toHexString(c).toUpperCase();
		}
		return code;
	}

	private static byte[] codeToBytes(String str) {
		if (layoutProps.getProperty(str) != null) {
			String keys[] = layoutProps.getProperty(str).split(",");
			byte[] byteTab = new byte[keys.length];
			for (int j = 0; j < keys.length; j++) {
				String key = keys[j].trim();
				if (keyboardProps.getProperty(key) != null) {
					byteTab[j] = strToByte(keyboardProps.getProperty(key)
							.trim());
				} else if (layoutProps.getProperty(key) != null) {
					byteTab[j] = strToByte(layoutProps.getProperty(key).trim());
				} else {
					System.out.println("Key not found:" + key);
					byteTab[j] = (byte) 0x00;
				}
			}
			return byteTab;
		} else {
			System.out.println("Char not found:" + str);
			byte[] byteTab = new byte[1];
			byteTab[0] = (byte) 0x00;
			return byteTab;
		}
	}

	private static byte strToByte(String str) {
		if (str.startsWith("0x")) {
			return (byte) Integer.parseInt(str.substring(2), 16);
		} else {
			return (byte) Integer.parseInt(str);
		}
	}

	private static byte strInstrToByte(String instruction) {
		instruction = instruction.trim();
		if (keyboardProps.getProperty("KEY_" + instruction) != null)
			return strToByte(keyboardProps.getProperty("KEY_" + instruction));
		/* instruction different from the key name */
		if (instruction.equals("ESCAPE"))
			return strInstrToByte("ESC");
		if (instruction.equals("DEL"))
			return strInstrToByte("DELETE");
		if (instruction.equals("BREAK"))
			return strInstrToByte("PAUSE");
		if (instruction.equals("CONTROL"))
			return strInstrToByte("CTRL");
		if (instruction.equals("DOWNARROW"))
			return strInstrToByte("DOWN");
		if (instruction.equals("UPARROW"))
			return strInstrToByte("UP");
		if (instruction.equals("LEFTARROW"))
			return strInstrToByte("LEFT");
		if (instruction.equals("RIGHTARROW"))
			return strInstrToByte("RIGHT");
		if (instruction.equals("MENU"))
			return strInstrToByte("APP");
		if (instruction.equals("WINDOWS"))
			return strInstrToByte("GUI");
		if (instruction.equals("PLAY") || instruction.equals("PAUSE"))
			return strInstrToByte("MEDIA_PLAY_PAUSE");
		if (instruction.equals("STOP"))
			return strInstrToByte("MEDIA_STOP");
		if (instruction.equals("MUTE"))
			return strInstrToByte("MEDIA_MUTE");
		if (instruction.equals("VOLUMEUP"))
			return strInstrToByte("MEDIA_VOLUME_INC");
		if (instruction.equals("VOLUMEDOWN"))
			return strInstrToByte("MEDIA_VOLUME_DEC");
		if (instruction.equals("SCROLLLOCK"))
			return strInstrToByte("SCROLL_LOCK");
		if (instruction.equals("NUMLOCK"))
			return strInstrToByte("NUM_LOCK");
		if (instruction.equals("CAPSLOCK"))
			return strInstrToByte("CAPS_LOCK");
		/* else take first letter */
		return charToBytes(instruction.charAt(0))[0];
	}
}
