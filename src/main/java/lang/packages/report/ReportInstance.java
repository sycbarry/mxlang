
//package com.sms.lang.packages.report;
//import com.sms.lang.Callable;
//import com.sms.lang.Interpreter;
//import com.sms.lang.Function;
//import com.sms.lang.Instance;
//import com.sms.lang.Token;
//import com.sms.lang.Class;

package lang.packages.report;
import lang.modules.*;
import lang.Callable;
import lang.Instance;
import lang.Class;
import lang.packages.report.ReportSectionType.*;
import lang.Interpreter;
import lang.Token;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import java.io.File;
import java.nio.file.Files;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.gson.Gson;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;


import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;


public class ReportInstance extends Instance {

    String title;
    String path;
    String description;

    String expectedResult;
    String actualResult;
    String responseData;
    String requestData;
    String url;
    String method;

    private PDDocument document;
    private static final Color WHITESMOKE = new Color(245, 245, 245);
    private PDType0Font font;
    private PDType0Font boldFont;
    private static HashMap<String, String> lineTitles;

    private InputStream fontPath;
    private InputStream fontPathBold;

    private List<HashMap<ReportSectionType, String>> sections;


    static {
        lineTitles = new HashMap<String, String>();
        lineTitles.put("Expected Result:", "Expected Result");
        lineTitles.put("POST", "POST");
        lineTitles.put("GET", "GET");
        lineTitles.put("Actual Result:", "Actual Result");
        lineTitles.put("JSON Response:", "JSON Response");
        lineTitles.put("JSON Input:", "JSON Input");
    }

    public ReportInstance(Class klass) {
        super(klass);
        this.document = new PDDocument();
        // this.fontPath = this.getClass().getResourceAsStream("/Courier New.ttf");
        // this.fontPathBold = this.getClass().getResourceAsStream("/Courier New Bold.ttf");
    }

    public Object get(Token name) {
        // NOTE changed this
        if( name.lexeme.equals("init") ) {
            return new Callable() {
                @Override
                public int arity() { return 3; }
                @Override
                public ReportInstance call(
                        Interpreter interpreter,
                        List<Object> arguments) {
                    String name = (String) arguments.get(0);
                    String description = (String) arguments.get(1);
                    String outputPath = (String) arguments.get(2);
                    return init(name, description, outputPath, false); // set true or false  - true if you are developing locally. false for production.
                }
            };
        }
        // NOTE added this.
        if(name.lexeme.equals("addSection")) {
            return new Callable() {
                @Override
                public int arity() { return 4; }
                @Override
                public Void call(Interpreter intrepreter, List<Object> arguments) {
                    String title = (String) arguments.get(0);
                    String description = (String) arguments.get(1);
                    String content = (String) arguments.get(2);
                    String type = (String) arguments.get(3);
                    return addSection(title, description, content, type);
                }
            };
        }
        if( name.lexeme.equals("build") ) {
            return new Callable() {
                @Override
                public int arity() { return 0; }
                @Override
                public Void call(Interpreter interpreter, List<Object> arguments) {
                    build();
                    return null;
                }
            };
        }
        return super.get(name);
    }

    public Void addSection(
            String title,
            String description,
            Object content, String type) {
        if(title == null) { throw new RuntimeException("no title has been declared for this section"); }
        if(description == null) { throw new RuntimeException("no description has been declared for this section"); }
        if(content == null) { throw new RuntimeException("no content has been declared for this section"); }
        if(type == null) { throw new RuntimeException("no type has been declared for this section"); }
        if(type.split("=").length < 1 || type.split("=").length > 2) { throw new RuntimeException("ensure to include type definition for section as type=[json,text]..."); }
        if(type.split("=")[0].trim().indexOf("type" ) == -1 ) { throw new RuntimeException("please include type= as the convention for declaring section types."); }
        HashMap<ReportSectionType, String> map = new HashMap<>();
        map.put(ReportSectionType.TITLE, title);
        map.put(ReportSectionType.DESCRIPTION, description);
        map.put(ReportSectionType.CONTENT, content.toString());
        map.put(ReportSectionType.TYPE, type.split("=")[1].trim());
        sections.add(map);
        return null;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setoutputPath(String path) {
        this.path = path;
    }
    public void setSections(ArrayList<HashMap<ReportSectionType, String>> sections) {
        this.sections = sections;
    }

    public ReportInstance init(String name, String description, String outputPath, boolean debug) {
        ReportInstance instance = new ReportInstance(new Class("reportinstance", null));
        instance.setSections(new ArrayList<HashMap<ReportSectionType, String>>());
        instance.setTitle(name);
        instance.setoutputPath(outputPath);
        instance.setDescription(description);
        if(debug == true) {
            try {
                instance.fontPath = Files.newInputStream(Paths.get("./fonts/Courier New.ttf"));
                instance.fontPathBold = Files.newInputStream(Paths.get("./fonts/Courier New Bold.ttf"));
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
            finally {  }
        }
        else {
            instance.fontPath = this.getClass().getResourceAsStream("/Courier New.ttf");
            instance.fontPathBold = this.getClass().getResourceAsStream("/Courier New Bold.ttf");
        }
        return instance;
    }

    public void build() {
        if(path == null) {
            throw new RuntimeException("no path has been declared for this report");
        }
        if(title == null) {
            throw new RuntimeException("no title for the report has been specified");
        }
        if(description == null) {
            throw new RuntimeException("no description for the report has been defined.");
        }
        try {
            this.font = PDType0Font.load(document, fontPath);
            this.boldFont = PDType0Font.load(document, fontPathBold);
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // adding the title of the report.
                // and the description of the report.
                addText(contentStream, title, 25, 50, 720);
                addText(contentStream, description, 18, 50, 700);
                for( HashMap<ReportSectionType, String> section : sections ) {
                    addSectionToReport(section);
                }
            }
            document.save(path);
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(document != null) {
                    document.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void addSectionToReport(
            HashMap<ReportSectionType, String>
            inboundSection) throws IOException {

        float margin = 50;
        float width = PDRectangle.A4.getWidth() - 2 * margin;
        float startX = margin;
        float startY = PDRectangle.A4.getHeight() - margin;
        float fontSize = 12;
        float leading = fontSize * 1.5f;

        // Assemble the test case info
        //prettyPrintJson(jsonResponse),

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, startY);

        float currentY = startY;

        String type = inboundSection.get(ReportSectionType.TYPE);

        // title
        contentStream.setFont(boldFont, fontSize);
        String titleContent = inboundSection.get(ReportSectionType.TITLE);
        String[] titleWords = titleContent.split(" ");
        StringBuilder titleCurrentLine = new StringBuilder();
        for (String word : titleWords) {
            if (getStringWidth(titleCurrentLine.toString() + word, fontSize) < width) {
                titleCurrentLine.append(word).append(" ");
            } else {
                contentStream.showText(titleCurrentLine.toString());
                contentStream.newLineAtOffset(0, -leading);
                currentY -= leading;
                if (currentY - leading < margin) { // Check if new page is needed
                    contentStream.endText();
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(font, fontSize);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(startX, startY);
                    currentY = startY;
                }
                titleCurrentLine = new StringBuilder(word + " ");
            }
        }
        contentStream.showText(titleCurrentLine.toString());
        contentStream.newLineAtOffset(0, -leading);
        currentY -= leading;

        // description
        contentStream.setFont(font, fontSize);
        startY -= 10;
        String descriptionContent = inboundSection.get(ReportSectionType.DESCRIPTION);
        String[] descriptionWords = descriptionContent.split(" ");
        StringBuilder descriptionCurrentLine = new StringBuilder();
        currentY -= leading;
        for (String word : descriptionWords) {
            if (getStringWidth(descriptionCurrentLine.toString() + word, fontSize) < width) {
                descriptionCurrentLine.append(word).append(" ");
            } else {
                contentStream.showText(descriptionCurrentLine.toString());
                contentStream.newLineAtOffset(0, -leading);
                currentY -= leading;
                if (currentY - leading < margin) { // Check if new page is needed
                    contentStream.endText();
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(font, fontSize);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(startX, startY);
                    currentY = startY;
                }
                descriptionCurrentLine = new StringBuilder(word + " ");
            }
        }
        contentStream.showText(descriptionCurrentLine.toString());
        contentStream.newLineAtOffset(0, -leading);
        currentY -= leading;


        // content
        contentStream.setFont(font, fontSize);
        if( type.trim().indexOf("json") != -1 ) {
            fontSize -= 5;
            startY -= 20;
            contentStream.setFont(font, fontSize);
            String sectionContent = prettyPrintJson(inboundSection.get(ReportSectionType.CONTENT));
            String[] sectionContentWords = sectionContent.split("\n");
            StringBuilder sectionContentCurrentLine = new StringBuilder();
            for (String word : sectionContentWords) {
                if ( getStringWidth( sectionContentCurrentLine.toString() + word, fontSize) < width ) {
                    sectionContentCurrentLine.append(word).append(" ");
                    contentStream.showText(sectionContentCurrentLine.toString());
                    contentStream.newLineAtOffset(0, -leading);
                    currentY -= leading;
                    sectionContentCurrentLine = new StringBuilder();
                } else {
                    contentStream.showText(sectionContentCurrentLine.toString());
                    contentStream.newLineAtOffset(0, -leading);
                    currentY -= leading;
                    sectionContentCurrentLine = new StringBuilder(word + " ");
                }
                if (currentY - leading < margin) { // Check if new page is needed
                    contentStream.endText();
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(font, fontSize);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(startX, startY);
                    currentY = startY;
                }
            }
            contentStream.showText(sectionContentCurrentLine.toString());
            contentStream.newLineAtOffset(0, -leading);
            currentY -= leading;
            fontSize += 5;
        }
        if( type.trim().indexOf("text") != -1 ) {
            contentStream.setFont(font, fontSize);
            String sectionContent = inboundSection.get(ReportSectionType.CONTENT);
            String[] sectionContentWords = sectionContent.split(" ");
            StringBuilder sectionContentTextCurrentLine = new StringBuilder();
            for (String word : sectionContentWords) {
                if (getStringWidth(sectionContentTextCurrentLine.toString() + word, fontSize) < width) {
                    sectionContentTextCurrentLine.append(word).append(" ");
                } else {
                    contentStream.showText(sectionContentTextCurrentLine.toString());
                    contentStream.newLineAtOffset(0, -leading);
                    currentY -= leading;
                    if (currentY - leading < margin) { // Check if new page is needed
                        contentStream.endText();
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(font, fontSize);
                        contentStream.setNonStrokingColor(Color.BLACK);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(startX, startY);
                        currentY = startY;
                    }
                    sectionContentTextCurrentLine = new StringBuilder(word + " ");
                }
            }
            contentStream.showText(sectionContentTextCurrentLine.toString());
            contentStream.newLineAtOffset(0, -leading);
            currentY -= leading;
        }


        contentStream.endText();
        contentStream.close();
    }


    private void drawRoundedRectangle(PDPageContentStream contentStream, float x, float y, float width, float height, float radius, Color color) throws IOException {
        // Set color
        PDColor pdColor = new PDColor(new float[]{color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f}, PDDeviceRGB.INSTANCE);
        contentStream.setNonStrokingColor(pdColor);

        // Draw rounded rectangle
        contentStream.addRect(x, y, width, height);
        contentStream.fill();
        // Note: This is a simple rectangle. For true rounded corners, additional work is needed to draw arcs.
    }

    private float getStringWidth(String text, float fontSize) throws IOException {
        return font.getStringWidth(text) / 1000 * fontSize;
    }


    public String prettyPrintJson(String jsonString) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = JsonParser.parseString​(jsonString.trim());
        String prettyJsonString = gson.toJson(je);
        return prettyJsonString;
    }

    private void appendIndent(StringBuilder sb, int indentLevel, String indent) {
        for (int i = 0; i < indentLevel; i++) {
            sb.append(indent);
        }
    }


    private void addText(
            PDPageContentStream contentStream,
            String text,
            int fontSize,
            float x,
            float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(boldFont, fontSize);
        contentStream.newLineAtOffset(x, y);
        if(text != null) {
            try{
                contentStream.showText(text);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        contentStream.endText();
    }




}
