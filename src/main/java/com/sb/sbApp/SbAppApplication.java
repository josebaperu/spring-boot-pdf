package com.sb.sbApp;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_PDF;

@SpringBootApplication
@RestController
@RequestMapping("/app")
public class SbAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbAppApplication.class, args);
	}
	@GetMapping(value = "/hello", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> get(){
		byte[] pdfBytes;
		try (final PDDocument doc = new PDDocument()){

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

		} catch (IOException e){
			System.err.println("Exception while trying to create pdf document - " + e);
		}
		return null;
	}

}
