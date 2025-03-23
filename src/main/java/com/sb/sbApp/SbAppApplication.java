package com.sb.sbApp;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.util.UUID;
import java.io.*;

import static com.sb.sbApp.Constant.HTML;


@SpringBootApplication
@RestController
@RequestMapping("/app")
public class SbAppApplication {
    //http://localhost:8080/rest/app/htmlpdf
    //http://localhost:8080/rest/app/html

    public static void main(String[] args) {
        SpringApplication.run(SbAppApplication.class, args);

    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf() {
        byte[] pdfBytes;
        try (final PDDocument doc = new PDDocument()) {

            PDPage page = new PDPage();
            doc.addPage(page);

            PDPageContentStream content = new PDPageContentStream(doc, page);

            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
            content.newLineAtOffset(100, 700);
            content.showText("Apache PDFBox Create PDF Document");
            content.endText();
            content.close();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            pdfBytes = baos.toByteArray();
            return new ResponseEntity<>(pdfBytes, null, HttpStatus.OK);

        } catch (IOException e) {
            System.err.println("Exception while trying to create pdf document - " + e);
        }
        return null;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/htmlpdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> htmlpdf() throws IOException {
        final String outputFile = UUID.randomUUID().toString().replace("-", "").concat(".pdf");
        ITextRenderer renderer = new ITextRenderer();
        final Document jsoupDoc = Jsoup.parseBodyFragment(HTML);
        W3CDom w3cDom = new W3CDom();
        org.w3c.dom.Document w3cDoc = w3cDom.fromJsoup(jsoupDoc);
        renderer.setDocument(w3cDoc);
        renderer.layout();
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        renderer.createPDF(outputStream);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(
                new FileInputStream(outputFile));
        byte[] bytes = IOUtils.toByteArray(bufferedInputStream);
        File file = new File(outputFile);
        if(file.exists()){
            file.delete();
        }
        return new ResponseEntity<>(bytes, null, HttpStatus.OK);

    }
}
