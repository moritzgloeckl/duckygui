package encoder;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
 
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
 
public class CodeDocument extends DefaultStyledDocument {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String word = "";
    private SimpleAttributeSet bold = new SimpleAttributeSet();
    private SimpleAttributeSet string = new SimpleAttributeSet();
    private SimpleAttributeSet normal = new SimpleAttributeSet();
    private SimpleAttributeSet number = new SimpleAttributeSet();
    private SimpleAttributeSet comments = new SimpleAttributeSet();
    private SimpleAttributeSet javadoc = new SimpleAttributeSet();
    private SimpleAttributeSet annotation = new SimpleAttributeSet();
    private int currentPos = 0;
    private Map<String, SimpleAttributeSet> keywordSets = new HashMap<String, SimpleAttributeSet>();
    public static int STRING_MODE = 10;
    public static int TEXT_MODE = 11;
    public static int NUMBER_MODE = 12;
    public static int COMMENT_MODE = 13;
    public static int LINE_COMMENT_MODE = 14;
    public static int JAVADOC_MODE = 15;
    public static int ANNOTATION_MODE = 16;
 
    private int mode = TEXT_MODE;
 
    public CodeDocument() {
        // set the bold attribute
        StyleConstants.setBold(bold, true);
        StyleConstants.setForeground(string, Color.blue);
        //StyleConstants.setForeground(number, Color.red);
        StyleConstants.setForeground(comments, Color.green);
        StyleConstants.setItalic(comments, true);
        StyleConstants.setForeground(javadoc, Color.cyan);
        StyleConstants.setItalic(javadoc, true);
        StyleConstants.setForeground(annotation, Color.GRAY);
        StyleConstants.setBold(annotation, true);
    }
 
    public void setCommentColor(Color color) {
        StyleConstants.setForeground(comments, color);
    }
 
    public void setJavadocColor(Color color) {
        StyleConstants.setForeground(javadoc, color);
    }
 
    public void setAnnotationColor(Color color) {
        StyleConstants.setForeground(annotation, color);
    }
 
    private void insertKeyword(String str, int pos, SimpleAttributeSet as) {
        try {
            // remove the old word and formatting
            this.remove(pos - str.length(), str.length());
            /*
             * replace it with the same word, but new formatting we MUST call
             * the super class insertString method here, otherwise we would end
             * up in an infinite loop !!
             */
            super.insertString(pos - str.length(), str, as);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
 
    private void insertTextString(String str, int pos) {
        try {
            // remove the old word and formatting
            this.remove(pos, str.length());
            super.insertString(pos, str, string);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
 
    private void insertNumberString(String str, int pos) {
        try {
            // remove the old word and formatting
            this.remove(pos, str.length());
            super.insertString(pos, str, number);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
 
    private void insertCommentString(String str, int pos) {
        try {
            // remove the old word and formatting
            this.remove(pos, str.length());
            super.insertString(pos, str, comments);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
 
    private void insertJavadocString(String str, int pos) {
        try {
            // remove the old word and formatting
            this.remove(pos, str.length());
            super.insertString(pos, str, javadoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
 
    private void insertAnnotationString(String str, int pos) {
        try {
            // remove the old word and formatting
            this.remove(pos, str.length());
            super.insertString(pos, str, annotation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
 
    private void checkForString() {
        int offs = this.currentPos;
        Element element = this.getParagraphElement(offs);
        String elementText = "";
        try {
            // this gets our chuck of current text for the element we're on
            elementText = this.getText(element.getStartOffset(), element
                    .getEndOffset()
                    - element.getStartOffset());
        } catch (Exception ex) {
            // whoops!
            System.out.println("no text");
        }
        int strLen = elementText.length();
        if (strLen == 0) {
            return;
        }
        int i = 0;
 
        if (element.getStartOffset() > 0) {
            // translates backward if neccessary
            offs = offs - element.getStartOffset();
        }
        int quoteCount = 0;
        if ((offs >= 0) && (offs <= strLen - 1)) {
            i = offs;
            while (i > 0) {
                // the while loop walks back until we hit a delimiter
 
                char charAt = elementText.charAt(i);
                if ((charAt == '"')) {
                    quoteCount++;
                }
                i--;
            }
            int rem = quoteCount % 2;
            // System.out.println(rem);
            mode = (rem == 0) ? TEXT_MODE : STRING_MODE;
        }
    }
 
    private void checkForKeyword() {
        if (mode != TEXT_MODE) {
            return;
        }
        int offs = this.currentPos;
        Element element = this.getParagraphElement(offs);
        String elementText = "";
        try {
            // this gets our chuck of current text for the element we're on
            elementText = this.getText(element.getStartOffset(), element
                    .getEndOffset()
                    - element.getStartOffset());
        } catch (Exception ex) {
            // whoops!
            System.out.println("no text");
        }
        int strLen = elementText.length();
        if (strLen == 0) {
            return;
        }
        int i = 0;
 
        if (element.getStartOffset() > 0) {
            // translates backward if neccessary
            offs = offs - element.getStartOffset();
        }
        if ((offs >= 0) && (offs <= strLen - 1)) {
            i = offs;
            while (i > 0) {
                // the while loop walks back until we hit a delimiter
                i--;
                char charAt = elementText.charAt(i);
                if ((charAt == ' ') | (i == 0) | (charAt == '(')
                        | (charAt == ')') | (charAt == '{') | (charAt == '}')) { // if i
                    // == 0
                    // then
                    // we're
                    // at
                    // the
                    // begininng
                    if (i != 0) {
                        i++;
                    }
                    word = elementText.substring(i, offs);// skip the period
 
                    String s = word.trim().toLowerCase();
                    // this is what actually checks for a matching keyword
                    if (keywordSets.containsKey(s)) {
                        insertKeyword(word, currentPos, keywordSets.get(s));
                    }
                    break;
                }
            }
        }
    }
 
    private void checkForNumber() {
        int offs = this.currentPos;
        Element element = this.getParagraphElement(offs);
        String elementText = "";
        try {
            // this gets our chuck of current text for the element we're on
            elementText = this.getText(element.getStartOffset(), element
                    .getEndOffset()
                    - element.getStartOffset());
        } catch (Exception ex) {
            // whoops!
            System.out.println("no text");
        }
        int strLen = elementText.length();
        if (strLen == 0) {
            return;
        }
        int i = 0;
 
        if (element.getStartOffset() > 0) {
            // translates backward if neccessary
            offs = offs - element.getStartOffset();
        }
        mode = TEXT_MODE;
        if ((offs >= 0) && (offs <= strLen - 1)) {
            i = offs;
            while (i > 0) {
                // the while loop walks back until we hit a delimiter
                char charAt = elementText.charAt(i);
                if ((charAt == ' ') | (i == 0) | (charAt == '(')
                        | (charAt == ')') | (charAt == '{') | (charAt == '}') /* | */) { // if i
                    // == 0
                    // then
                    // we're
                    // at
                    // the
                    // begininng
                    if (i != 0) {
                        i++;
                    }
                    mode = NUMBER_MODE;
                    break;
                } else if (!(charAt >= '0' & charAt <= '9' | charAt == '.'
                        | charAt == '+' | charAt == '-' | charAt == '/'
                        | charAt == '*' | charAt == '%' | charAt == '=')) {
                    mode = TEXT_MODE;
                    break;
                }
                i--;
            }
        }
    }
 
    private void checkForComment() {
        int offs = this.currentPos;
        Element element = this.getParagraphElement(offs);
        String elementText = "";
        try {
            // this gets our chuck of current text for the element we're on
            elementText = this.getText(element.getStartOffset(), element
                    .getEndOffset()
                    - element.getStartOffset());
        } catch (Exception ex) {
            // whoops!
            System.out.println("no text");
        }
        int strLen = elementText.length();
        if (strLen == 0) {
            return;
        }
        int i = 0;
 
        if (element.getStartOffset() > 0) {
            // translates backward if neccessary
            offs = offs - element.getStartOffset();
        }
        if ((offs >= 1) && (offs <= strLen - 1)) {
            i = offs;
            char commentStartChar1 = elementText.charAt(i - 1);
            char commentStartChar2 = elementText.charAt(i);
            if (mode == COMMENT_MODE && commentStartChar1 == '*'
                    && commentStartChar2 == '*') {
                mode = JAVADOC_MODE;
                this.insertJavadocString("/**", currentPos - 2);
            } else if (commentStartChar1 == '/' && commentStartChar2 == '*') {
                mode = COMMENT_MODE;
                this.insertCommentString("/*", currentPos - 1);
            } else if (commentStartChar1 == '/' && commentStartChar2 == '/') {
                mode = LINE_COMMENT_MODE;
                this.insertCommentString("//", currentPos - 1);
            } else if (commentStartChar1 == '*' && commentStartChar2 == '/') {
                boolean javadoc = false;
                if (mode == JAVADOC_MODE) {
                    javadoc = true;
                }
                mode = TEXT_MODE;
                if (javadoc) {
                    this.insertJavadocString("*/", currentPos - 1);
                } else {
                    this.insertCommentString("*/", currentPos - 1);
                }
            }
 
        }
    }
 
    private void processChar(String str) {
        char strChar = str.charAt(0);
        if (mode != COMMENT_MODE && mode != LINE_COMMENT_MODE
                && mode != JAVADOC_MODE && mode != ANNOTATION_MODE) {
            mode = TEXT_MODE;
        }
        switch (strChar) {
        case ('@'):
            if (mode == TEXT_MODE) {
                mode = ANNOTATION_MODE;
            }
            break;
        case ('{'):
        case ('}'):
        case (' '):
        case ('\n'):
        case ('('):
        case (')'):
        case (';'):
        case ('.'): {
            checkForKeyword();
            if (mode == ANNOTATION_MODE && strChar == '(') {
                mode = TEXT_MODE;
            }
            if ((mode == STRING_MODE || mode == LINE_COMMENT_MODE || mode == ANNOTATION_MODE)
                    && strChar == '\n') {
                mode = TEXT_MODE;
            }
        }
            break;
        case ('"'): {
            insertTextString(str, currentPos);
            this.checkForString();
        }
            break;
        case ('0'):
        case ('1'):
        case ('2'):
        case ('3'):
        case ('4'):
        case ('5'):
        case ('6'):
        case ('7'):
        case ('8'):
        case ('9'): {
            checkForNumber();
        }
            break;
        case ('*'):
        case ('/'): {
            checkForComment();
        }
            break;
        }
        if (mode == TEXT_MODE) {
            this.checkForString();
        }
        if (mode == STRING_MODE) {
            insertTextString(str, this.currentPos);
        } else if (mode == NUMBER_MODE) {
            insertNumberString(str, this.currentPos);
        } else if (mode == COMMENT_MODE) {
            insertCommentString(str, this.currentPos);
        } else if (mode == LINE_COMMENT_MODE) {
            insertCommentString(str, this.currentPos);
        } else if (mode == JAVADOC_MODE) {
            insertJavadocString(str, this.currentPos);
        } else if (mode == ANNOTATION_MODE) {
            insertAnnotationString(str, this.currentPos);
        }
 
    }
 
    private void processChar(char strChar) {
        char[] chrstr = new char[1];
        chrstr[0] = strChar;
        String str = new String(chrstr);
        processChar(str);
    }
 
    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
        super.insertString(offs, str, normal);
 
        int strLen = str.length();
        int endpos = offs + strLen;
        int strpos;
        for (int i = offs; i < endpos; i++) {
            currentPos = i;
            strpos = i - offs;
            processChar(str.charAt(strpos));
        }
        currentPos = offs;
    }
 
    public Set<String> getKeywords() {
        return this.keywordSets.keySet();
    }
 
    public void setKeywords(Map<String, Color> aKeywordList) {
        if (aKeywordList != null) {
            for (Map.Entry<String, Color> entry : aKeywordList.entrySet()) {
                SimpleAttributeSet temp = new SimpleAttributeSet();
                StyleConstants.setForeground(temp, entry.getValue());
                StyleConstants.setBold(temp, true);
                this.keywordSets.put(entry.getKey(), temp);
            }
            // this.keywords = aKeywordList;
        }
    }
}