package com.microslop.service;

import com.microslop.entity.Certificate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates PDF certificates using Apache PDFBox
 */
@Component
public class CertificatePdfGenerator {

    private static final Logger log = LoggerFactory.getLogger(CertificatePdfGenerator.class);

    // Page dimensions (A4)
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();

    // Color constants (RGB 0-1 scale for PDFBox)
    private static final float[] DARK_BLUE = {0.13f, 0.20f, 0.34f};      // Dark blue
    private static final float[] GOLD = {0.85f, 0.71f, 0.23f};            // Golden
    private static final float[] LIGHT_CREAM = {0.98f, 0.97f, 0.94f};    // Light cream
    private static final float[] BLACK = {0f, 0f, 0f};

    /**
     * Generate a complete PDF certificate
     */
    public byte[] generateCertificatePdf(Certificate certificate) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Draw background
                drawBackground(contentStream);

                if (certificate.isParticipantCertificate()) {
                    drawParticipantCertificate(contentStream, certificate);
                } else {
                    drawWinnerCertificate(contentStream, certificate);
                }
            }

            // Convert to bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error generating PDF certificate for ID {}: {}", certificate.getId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Draw background (light cream with border)
     */
    private void drawBackground(PDPageContentStream contentStream) throws IOException {
        // Light cream background
        contentStream.setNonStrokingColor(LIGHT_CREAM[0], LIGHT_CREAM[1], LIGHT_CREAM[2]);
        contentStream.fillRect(0, 0, PAGE_WIDTH, PAGE_HEIGHT);

        // Dark blue border (top and bottom)
        contentStream.setNonStrokingColor(DARK_BLUE[0], DARK_BLUE[1], DARK_BLUE[2]);
        contentStream.fillRect(0, PAGE_HEIGHT - 40, PAGE_WIDTH, 40);  // Top bar
        contentStream.fillRect(0, 0, PAGE_WIDTH, 30);                  // Bottom bar
    }

    /**
     * Draw participant certificate
     */
    private void drawParticipantCertificate(PDPageContentStream contentStream, Certificate certificate) throws IOException {
        float centerX = PAGE_WIDTH / 2;
        float currentY = PAGE_HEIGHT - 80;

        // Header: Logo and title
        PDFont titleFont = PDType1Font.HELVETICA_BOLD;
        PDFont boldFont = PDType1Font.HELVETICA_BOLD;
        PDFont regularFont = PDType1Font.HELVETICA;
        PDFont smallFont = PDType1Font.HELVETICA;

        // Logo (simple V)
        drawCenteredText(contentStream, boldFont, 48, "[V]", centerX, currentY, DARK_BLUE);
        currentY -= 50;

        // "Votición App"
        drawCenteredText(contentStream, titleFont, 20, "Votición App", centerX, currentY, DARK_BLUE);
        currentY -= 50;

        // Main title
        drawCenteredText(contentStream, boldFont, 32, "CERTIFICADO DE PARTICIPANTE", centerX, currentY, DARK_BLUE);
        currentY -= 70;

        // Body text
        contentStream.setNonStrokingColor(BLACK[0], BLACK[1], BLACK[2]);
        drawCenteredMultilineText(contentStream, regularFont, 12,
                "Este certificado reconoce que " + certificate.getUser().getName() +
                        " ha participado exitosamente en la " + certificate.getCompetition().getName() +
                        " con el proyecto titulado '" + certificate.getProject().getName() +
                        "' en la categoría " + certificate.getCategory().getName(),
                centerX, currentY, PAGE_WIDTH - 80);
        currentY -= 100;

        // Seal (circular design in text)
        drawCenteredText(contentStream, boldFont, 14, "*===================*", centerX, currentY, GOLD);
        currentY -= 20;
        drawCenteredText(contentStream, boldFont, 12, "PARTICIPANTE", centerX, currentY, GOLD);
        currentY -= 18;
        drawCenteredText(contentStream, boldFont, 12, "VOTICIÓN APP", centerX, currentY, GOLD);
        currentY -= 18;
        drawCenteredText(contentStream, regularFont, 10, "EDICIÓN " + certificate.getCertificateYear(), centerX, currentY, GOLD);
        currentY -= 40;

        // Signature lines
        float leftX = 100;
        float rightX = PAGE_WIDTH - 100;
        contentStream.setNonStrokingColor(BLACK[0], BLACK[1], BLACK[2]);
        contentStream.setLineWidth(1);
        contentStream.moveTo(leftX, currentY);
        contentStream.lineTo(leftX + 120, currentY);
        contentStream.moveTo(rightX - 120, currentY);
        contentStream.lineTo(rightX, currentY);
        contentStream.stroke();

        currentY -= 30;
        drawLeftAlignedText(contentStream, regularFont, 10, "Firma del Organizador", leftX, currentY, BLACK);
        drawRightAlignedText(contentStream, regularFont, 10, "Firma del Jurado", rightX, currentY, BLACK);

        currentY -= 40;
        drawCenteredText(contentStream, regularFont, 10, 
                "Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                centerX, currentY, BLACK);
    }

    /**
     * Draw winner certificate
     */
    private void drawWinnerCertificate(PDPageContentStream contentStream, Certificate certificate) throws IOException {
        float centerX = PAGE_WIDTH / 2;
        float currentY = PAGE_HEIGHT - 80;

        PDFont titleFont = PDType1Font.HELVETICA_BOLD;
        PDFont boldFont = PDType1Font.HELVETICA_BOLD;
        PDFont regularFont = PDType1Font.HELVETICA;

        // Header: Logo (in gold) and title
        drawCenteredText(contentStream, boldFont, 48, "[V]", centerX, currentY, GOLD);
        currentY -= 50;

        // "Votición App"
        drawCenteredText(contentStream, titleFont, 20, "Votición App", centerX, currentY, GOLD);
        currentY -= 50;

        // Main title in gold
        drawCenteredText(contentStream, boldFont, 36, "CERTIFICADO DE GANADOR", centerX, currentY, GOLD);
        currentY -= 40;

        // Subtitle
        drawCenteredText(contentStream, regularFont, 14, "RECONOCIMIENTO DE EXCELENCIA", centerX, currentY, DARK_BLUE);
        currentY -= 60;

        // Body text
        String rankingText = certificate.getRankingType().isJudgesRanking()
                ? "1st Place - Judges Ranking" 
                : "1st Place - Popular Vote";

        contentStream.setNonStrokingColor(BLACK[0], BLACK[1], BLACK[2]);
        drawCenteredMultilineText(contentStream, regularFont, 12,
                "Este certificado otorga el " + rankingText + " a " + certificate.getUser().getName() +
                        " por su destacada participación en la " + certificate.getCompetition().getName() +
                        " con el proyecto titulado '" + certificate.getProject().getName() +
                        "' en la categoría " + certificate.getCategory().getName(),
                centerX, currentY, PAGE_WIDTH - 80);
        currentY -= 110;

        // Seal (larger, golden, detailed)
        drawCenteredText(contentStream, boldFont, 16, "+=====================+", centerX, currentY, GOLD);
        currentY -= 22;
        drawCenteredText(contentStream, boldFont, 13, "GANADOR - " + rankingText, centerX, currentY, GOLD);
        currentY -= 20;
        drawCenteredText(contentStream, boldFont, 13, "VOTICIÓN APP", centerX, currentY, GOLD);
        currentY -= 20;
        drawCenteredText(contentStream, regularFont, 11, "EDICIÓN " + certificate.getCertificateYear(), centerX, currentY, GOLD);
        currentY -= 50;

        // Signature lines
        float leftX = 100;
        float rightX = PAGE_WIDTH - 100;
        contentStream.setNonStrokingColor(BLACK[0], BLACK[1], BLACK[2]);
        contentStream.setLineWidth(1.5f);
        contentStream.moveTo(leftX, currentY);
        contentStream.lineTo(leftX + 120, currentY);
        contentStream.moveTo(rightX - 120, currentY);
        contentStream.lineTo(rightX, currentY);
        contentStream.stroke();

        currentY -= 30;
        drawLeftAlignedText(contentStream, regularFont, 10, "Firma del Organizador", leftX, currentY, BLACK);
        drawRightAlignedText(contentStream, regularFont, 10, "Firma del Jurado", rightX, currentY, BLACK);

        currentY -= 40;
        drawCenteredText(contentStream, regularFont, 10, 
                "Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                centerX, currentY, BLACK);
    }

    /**
     * Draw centered text
     */
    private void drawCenteredText(PDPageContentStream contentStream, PDFont font, float fontSize, 
                                  String text, float centerX, float y, float[] color) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(color[0], color[1], color[2]);
        
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        float x = centerX - textWidth / 2;
        
        contentStream.beginText();
        contentStream.setTextMatrix(1, 0, 0, 1, x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    /**
     * Draw left-aligned text
     */
    private void drawLeftAlignedText(PDPageContentStream contentStream, PDFont font, float fontSize,
                                     String text, float x, float y, float[] color) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(color[0], color[1], color[2]);
        
        contentStream.beginText();
        contentStream.setTextMatrix(1, 0, 0, 1, x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    /**
     * Draw right-aligned text
     */
    private void drawRightAlignedText(PDPageContentStream contentStream, PDFont font, float fontSize,
                                      String text, float rightX, float y, float[] color) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(color[0], color[1], color[2]);
        
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        float x = rightX - textWidth;
        
        contentStream.beginText();
        contentStream.setTextMatrix(1, 0, 0, 1, x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    /**
     * Draw multiline text centered
     */
    private void drawCenteredMultilineText(PDPageContentStream contentStream, PDFont font, float fontSize,
                                          String text, float centerX, float startY, float maxWidth) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(BLACK[0], BLACK[1], BLACK[2]);
        
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float lineHeight = fontSize + 5;
        float currentY = startY;

        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            float textWidth = font.getStringWidth(testLine) / 1000 * fontSize;

            if (textWidth > maxWidth && line.length() > 0) {
                // Draw current line
                drawCenteredText(contentStream, font, fontSize, line.toString(), centerX, currentY, BLACK);
                currentY -= lineHeight;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }

        // Draw last line
        if (line.length() > 0) {
            drawCenteredText(contentStream, font, fontSize, line.toString(), centerX, currentY, BLACK);
        }
    }
}
