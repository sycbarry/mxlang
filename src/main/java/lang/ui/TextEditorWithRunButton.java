//package com.sms.lang.ui;
//import com.sms.lang.Interpreter;
//import com.sms.lang.Token;
//import com.sms.lang.Stmt;
//import com.sms.lang.Parser;
//import com.sms.lang.Scanner;
//import com.sms.lang.Resolver;

package lang.ui;
import lang.Interpreter;
import lang.Token;
import lang.Stmt;
import lang.Parser;
import lang.Scanner;
import lang.Resolver;


import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.io.OutputStream;
import java.io.IOException;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.FileReader;


import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;

public class TextEditorWithRunButton extends JFrame {


    private JTextPane textPane;

    private SimpleAttributeSet keywordStyle;
    private SimpleAttributeSet stringStyle;
    private SimpleAttributeSet commentStyle;
    private SimpleAttributeSet numberStyle;
    private SimpleAttributeSet classStyle;
    private SimpleAttributeSet annotationStyle;

    private Pattern keywordPattern;
    private Pattern stringPattern;
    private Pattern commentPattern;
    private Pattern numberPattern;
    private Pattern annotationPattern;
    private Pattern classPattern;

    private boolean updatingStyles = false;

    public TextEditorWithRunButton() {
        // Set up the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("mxlang Editor");

        // Create text pane for syntax highlighting
        textPane = new JTextPane();
        textPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),  // Margin
            BorderFactory.createEmptyBorder(10, 10, 10, 10)  // Margin
        ));
        textPane.setMargin(new Insets(5, 5, 5, 5));

        Font consolasFont = new Font("Jetbrains Mono", Font.PLAIN, 14);
        textPane.setFont(consolasFont);
        Color backgroundColor = new Color(235, 235, 235); // Use the RGB values you obtained
        textPane.setBackground(backgroundColor);



        Font currentFont = textPane.getFont();
        float size = currentFont.getSize2D();
        Font newFont = currentFont.deriveFont(size * 1.05f); // Increase font size by 5%
        textPane.setFont(newFont);


        // Set the text pane to wrap lines
        textPane.setEditorKit(new WrapEditorKit());

        // Create the Run button
        JButton runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCode();
            }
        });

        // Create buttons
        JButton saveButton = new JButton("Save File");
        JButton openButton = new JButton("Open File");

        // Add action listener for Save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Specify a file to save");
                int userSelection = fileChooser.showSaveDialog(textPane);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                        String content = textPane.getText();
                        writer.write(content);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Add action listener for Open button
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choose a file to open");
                int result = fileChooser.showOpenDialog(textPane);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen = fileChooser.getSelectedFile();
                    try (BufferedReader reader = new BufferedReader(new FileReader(fileToOpen))) {
                        String line;
                        StringBuilder content = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            // Process the line, here we just print it out
                            content.append(line).append("\n");
                        }
                        textPane.setText(content.toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });




        // Create a panel for the button to reside in the top-right corner
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(runButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(openButton);



        // Set the layout and add components
        setLayout(new BorderLayout());
        add(new JScrollPane(textPane), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.NORTH);



               // Initialize styles and patterns
        initStylesAndPatterns();

        // Set the combined document filter
        ((AbstractDocument) textPane.getDocument()).setDocumentFilter(new CustomDocumentFilter());

        // Set tab size
        setTabSize(textPane, 4);

        // Add the text pane to the frame wrapped in a scroll pane
        JScrollPane scrollPane = new JScrollPane(textPane);
        add(scrollPane, BorderLayout.CENTER);

        // Set size of the JFrame
        setSize(800, 600);

        // Center the window on the screen
        setLocationRelativeTo(null);

        // Display the window
        setVisible(true);

    }


    private void initStylesAndPatterns() {
        // Define styles for syntax elements
        keywordStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(keywordStyle, new Color(0, 0, 128)); // Dark blue
        StyleConstants.setItalic(keywordStyle, true);
        StyleConstants.setBold(keywordStyle, true);

        stringStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(stringStyle, new Color(0, 128, 0)); // Green
        StyleConstants.setItalic(stringStyle, true);

        commentStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(commentStyle, Color.GRAY); // Gray

        numberStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(numberStyle, new Color(255, 165, 0)); // Orange
        StyleConstants.setBold(numberStyle, true);

        classStyle = new SimpleAttributeSet();
        StyleConstants.setBold(classStyle, true); // Bold for types like class names

        annotationStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(annotationStyle, new Color(128, 0, 128)); // Dark purple


        // Patterns (make sure to define the patterns accordingly)
        keywordPattern = Pattern.compile("\\b(fun|var|init|abstract|print|fun|use|continue|for|new|switch|assert|default|goto|package|synchronized|boolean|do|if|private|this|break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|int|short|try|char|final|interface|static|void|class|finally|long|strictfp|volatile|const|float|native|super|while)\\b");
        stringPattern = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"");
        commentPattern = Pattern.compile("///[^\n]*|/\\*.*?\\*/", Pattern.DOTALL);
        numberPattern = Pattern.compile("\\b\\d+\\.?\\d*\\b");
        annotationPattern = Pattern.compile("@\\w+");
        classPattern = Pattern.compile("\\b(class)");


    }

    private void setTabSize(JTextPane textPane, int charactersPerTab) {
        FontMetrics fm = textPane.getFontMetrics(textPane.getFont());
        int charWidth = fm.charWidth(' ');
        int tabWidth = charWidth * charactersPerTab;

        TabStop[] tabs = new TabStop[10];
        for (int j = 0; j < tabs.length; j++) {
            tabs[j] = new TabStop((j + 1) * tabWidth);
        }

        TabSet tabSet = new TabSet(tabs);
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setTabSet(attributes, tabSet);
        textPane.getStyledDocument().setParagraphAttributes(0, textPane.getDocument().getLength(), attributes, false);
    }

    private class CustomDocumentFilter extends DocumentFilter {


        @Override
        public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
            if ("([{".contains(str) && (offs == fb.getDocument().getLength() || !nextCharMatches(fb, offs, matchingBrace(str.charAt(0))))) {
                // Insert matching closing brace and move the caret back to be between the braces
                super.insertString(fb, offs, str + matchingBrace(str.charAt(0)), a);
                textPane.setCaretPosition(offs + 1);
            } else if (")]}".contains(str) && nextCharMatches(fb, offs, str.charAt(0))) {
                // Move the caret to skip the auto-inserted brace instead of adding a new one
                textPane.setCaretPosition(offs + 1);
            } else {
                // For other characters, just insert them
                super.insertString(fb, offs, str, a);
            }
            // Update syntax highlighting
            updateSyntaxHighlighting(fb.getDocument());
        }

        private boolean nextCharMatches(FilterBypass fb, int offs, char c) throws BadLocationException {
            if (offs < fb.getDocument().getLength()) {
                String nextChar = fb.getDocument().getText(offs, 1);
                return nextChar.charAt(0) == c;
            }
            return false;
        }


        private char matchingBrace(char c) {
            switch (c) {
                case '(': return ')';
                case '[': return ']';
                case '{': return '}';
                case ')': return '(';
                case ']': return '[';
                case '}': return '{';
                default: return c;
            }
        }


        @Override
        public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
            if ("([{".contains(str)) {
                super.replace(fb, offs, length, str + matchingBrace(str.charAt(0)), a);
                textPane.setCaretPosition(offs + 1);
            } else if (")]}".contains(str) && nextCharMatches(fb, offs, str.charAt(0))) {
                textPane.setCaretPosition(offs + 1); // Skip over the existing closing brace
            } else {
                super.replace(fb, offs, length, str, a);
                if ("\n".equals(str)) {
                    handleNewline(fb, offs, a);
                }
            }
            updateSyntaxHighlighting(fb.getDocument());
        }


        private void handleNewline(FilterBypass fb, int offs, AttributeSet a) throws BadLocationException {
            String textBeforeCaret = fb.getDocument().getText(0, offs);
            int lastNewlineIndex = textBeforeCaret.lastIndexOf('\n');
            int lastBraceIndex = textBeforeCaret.lastIndexOf('{');
            String indentation = "";

            // Determine the current indentation level based on the last line's indentation
            if (lastNewlineIndex >= 0) {
                String lastLine = textBeforeCaret.substring(lastNewlineIndex + 1);
                for (char c : lastLine.toCharArray()) {
                    if (Character.isWhitespace(c)) {
                        indentation += c;
                    } else {
                        break;
                    }
                }
            }

            // If the last non-whitespace character is an opening brace, increase the indentation level
            if (lastBraceIndex > lastNewlineIndex) {
                indentation += "    ";
            }

            // Insert a newline followed by the current indentation
            super.insertString(fb, offs, "\n" + indentation, a);

            // Move the caret to the new position after the indentation
            textPane.setCaretPosition(offs + 1 + indentation.length());
        }


        private String makeIndentation(int length) {
            return " ".repeat(Math.max(0, length));
        }

        private String currentIndentation(String text) {
            int length = text.length() - 1;
            while (length >= 0 && (text.charAt(length) == ' ' || text.charAt(length) == '\t')) {
                length--;
            }
            return text.substring(length + 1);
        }

        @Override
        public void remove(FilterBypass fb, int offs, int length) throws BadLocationException {
            super.remove(fb, offs, length);
            updateSyntaxHighlighting(fb.getDocument());
        }

        private void updateSyntaxHighlighting(Document doc) {
            if (updatingStyles) {
                return;
            }
            updatingStyles = true;
            SwingUtilities.invokeLater(() -> {
                try {
                    String text = doc.getText(0, doc.getLength());
                    clearStyles(doc);
                    applyPattern(doc, keywordPattern, keywordStyle);
                    applyPattern(doc, stringPattern, stringStyle);
                    applyPattern(doc, commentPattern, commentStyle);
                    applyPattern(doc, numberPattern, numberStyle);
                    applyPattern(doc, annotationPattern, annotationStyle);
                    applyPattern(doc, classPattern, classStyle);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                } finally {
                    updatingStyles = false;
                }
            });
        }

        private void clearStyles(Document doc) {
            ((StyledDocument) doc).setCharacterAttributes(0, doc.getLength(), SimpleAttributeSet.EMPTY, true);
        }

        private void applyPattern(Document doc, Pattern pattern, AttributeSet attributeSet) {
            Matcher matcher = pattern.matcher(textPane.getText());
            while (matcher.find()) {
                ((StyledDocument) doc).setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), attributeSet, false);
            }
        }

    }






    //////



    private void runCode() {
        String userCode = textPane.getText().toString();

        Interpreter interpreter =  new Interpreter();
        Scanner scanner = new Scanner(userCode);
        ArrayList<Token> tokens = scanner.chop();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        // Display the command output/error in a new window or console area
        JFrame outputFrame = new JFrame("Command Line Output");
        JTextArea outputArea = new JTextArea(20, 60);
        outputArea.setEditable(false); // Make the output area read-only
        JScrollPane scrollPane = new JScrollPane(outputArea);
        outputFrame.add(scrollPane);
        outputFrame.pack();
        outputFrame.setLocationRelativeTo(null); // Center the window
        outputFrame.setVisible(true);


        // Redirect System.out to the JTextArea
        TextAreaOutputStream taOutputStream = new TextAreaOutputStream(outputArea);
        PrintStream ps = new PrintStream(taOutputStream);

        System.setOut(ps);
        interpreter.interpret(statements);


    }


    public static void main(String[] args) {
        // Set the look and feel to the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run the application
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TextEditorWithRunButton();
            }
        });
    }

    // Editor kit to allow word wrapping in JTextPane
    private static class WrapEditorKit extends StyledEditorKit {
        ViewFactory defaultFactory=new WrapColumnFactory();
        public ViewFactory getViewFactory() {
            return defaultFactory;
        }

        private static class WrapColumnFactory implements ViewFactory {
            public View create(Element elem) {
                String kind = elem.getName();
                if (kind != null) {
                    if (kind.equals(AbstractDocument.ContentElementName)) {
                        return new WrapLabelView(elem);
                    } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                        return new ParagraphView(elem);
                    } else if (kind.equals(AbstractDocument.SectionElementName)) {
                        return new BoxView(elem, View.Y_AXIS);
                    } else if (kind.equals(StyleConstants.ComponentElementName)) {
                        return new ComponentView(elem);
                    } else if (kind.equals(StyleConstants.IconElementName)) {
                        return new IconView(elem);
                    }
                }
                return new LabelView(elem);
            }
        }
    }

    private class TextAreaOutputStream extends OutputStream {
        private final JTextArea textArea;
        private final StringBuilder sb = new StringBuilder();

        public TextAreaOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            if (b == '\r') return; // Ignore carriage return

            if (b == '\n') {
                final String text = sb.toString() + "\n";

                // Append to the JTextArea in the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    textArea.append(text);
                });

                sb.setLength(0); // Clear the StringBuilder
            } else {
                sb.append((char) b);
            }
        }
    }

    // Custom label view to allow word wrapping
    private static class WrapLabelView extends LabelView {
        public WrapLabelView(Element elem) {
            super(elem);
        }

        public float getMinimumSpan(int axis) {
            switch (axis) {
                case View.X_AXIS:
                    return 0;
                case View.Y_AXIS:
                    return super.getMinimumSpan(axis);
                default:
                    throw new IllegalArgumentException("Invalid axis: " + axis);
            }
        }
    }
}
