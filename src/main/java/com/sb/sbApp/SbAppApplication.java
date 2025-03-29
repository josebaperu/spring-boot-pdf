package com.sb.sbApp;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@RestController
@RequestMapping("/app")
public class SbAppApplication {
    private static final float FONT_SIZE = 12;
    private static final float LEADING = -1.5f * FONT_SIZE;
    private static final PDFont FONT = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);

    public static void main(String[] args) {
        SpringApplication.run(SbAppApplication.class, args);

    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf() {

        String paragraph = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt" +
                " ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco" +
                " laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                " ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco" +
                " laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat" +
                " non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

        List<String> content = List.of("first line of text", paragraph, "last line of text");

        byte[] pdfBytes;
        try (final PDDocument doc = new PDDocument()) {

            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(doc, page);

            PDRectangle mediaBox = page.getMediaBox();
            float marginY = 80;
            float marginX = 60;
            float width = mediaBox.getWidth() - 2 * marginX;
            float startX = mediaBox.getLowerLeftX() + marginX;
            float startY = mediaBox.getUpperRightY() - marginY;


            contentStream.beginText();
            boolean isFirstLine = true;
            for (String text : content) {
                if (isFirstLine) {
                    isFirstLine = false;
                    addParagraph(contentStream, width, startX, startY, text);
                } else {
                    addParagraph(contentStream, width, 0, -FONT_SIZE, text);
                }
            }
            contentStream.endText();
            contentStream.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            doc.close();
            pdfBytes = baos.toByteArray();
            return new ResponseEntity<>(pdfBytes, null, HttpStatus.OK);

        } catch (IOException e) {
            System.err.println("Exception while trying to create pdf document - " + e);
        }
        return null;
    }

    private static void addParagraph(PDPageContentStream contentStream, float width, float sx,
                                     float sy, String text) throws IOException {
        List<String> lines = parseLines(text, width);
        contentStream.setFont(FONT, FONT_SIZE);
        contentStream.newLineAtOffset(sx, sy);
        for (String line : lines) {
            float charSpacing = 0;
            if (line.length() > 1) {
                float size = FONT_SIZE * FONT.getStringWidth(line) / 1000;
                float free = width - size;
                if (free > 0 && !lines.get(lines.size() - 1).equals(line)) {
                    charSpacing = free / (line.length() - 1);
                }
            }
            contentStream.setCharacterSpacing(charSpacing);
            contentStream.showText(line);
            contentStream.newLineAtOffset(0, LEADING);
        }
    }

    private static List<String> parseLines(String text, float width) throws IOException {
        List<String> lines = new ArrayList<>();
        int lastSpace = -1;
        while (text.length() > 0) {
            int spaceIndex = text.indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0)
                spaceIndex = text.length();
            String subString = text.substring(0, spaceIndex);
            float size = FONT_SIZE * FONT.getStringWidth(subString) / 1000;
            if (size > width) {
                if (lastSpace < 0) {
                    lastSpace = spaceIndex;
                }
                subString = text.substring(0, lastSpace);
                lines.add(subString);
                text = text.substring(lastSpace).trim();
                lastSpace = -1;
            } else if (spaceIndex == text.length()) {
                lines.add(text);
                text = "";
            } else {
                lastSpace = spaceIndex;
            }
        }
        return lines;
    }
}
