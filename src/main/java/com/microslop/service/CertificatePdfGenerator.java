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
                if (certificate.isParticipantCertificate()) {
                    drawBackground(contentStream, false);
                    drawParticipantCertificate(contentStream, certificate);
                } else {
                    drawBackground(contentStream, true);
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
    private void drawBackground(PDPageContentStream contentStream, boolean isWinner) throws IOException {
        // Light cream background
        float[] CREAM = {0.98f, 0.97f, 0.94f};
        contentStream.setNonStrokingColor(CREAM[0], CREAM[1], CREAM[2]);
        contentStream.fillRect(0, 0, PAGE_WIDTH, PAGE_HEIGHT);

        if (isWinner) {
            // Elegant thin golden border
            contentStream.setStrokingColor(GOLD[0], GOLD[1], GOLD[2]);
            contentStream.setLineWidth(2f);
            contentStream.addRect(20, 20, PAGE_WIDTH - 40, PAGE_HEIGHT - 40);
            contentStream.stroke();
            
            // Inner thin border
            contentStream.setLineWidth(1f);
            contentStream.addRect(25, 25, PAGE_WIDTH - 50, PAGE_HEIGHT - 50);
            contentStream.stroke();
        }
    }

    /**
     * Draw participant certificate
     */
    private void drawParticipantCertificate(PDPageContentStream contentStream, Certificate certificate) throws IOException {
        float centerX = PAGE_WIDTH / 2;
        float currentY = PAGE_HEIGHT - 100;

        PDFont boldFont = PDType1Font.HELVETICA_BOLD;
        PDFont regularFont = PDType1Font.HELVETICA;

        // Header: Logo and title
        drawCenteredText(contentStream, boldFont, 24, "Votify", centerX, currentY, DARK_BLUE);
        currentY -= 80;

        drawCenteredText(contentStream, boldFont, 36, "CERTIFICADO", centerX, currentY, DARK_BLUE);
        currentY -= 40;
        drawCenteredText(contentStream, boldFont, 36, "DE PARTICIPANTE", centerX, currentY, DARK_BLUE);
        currentY -= 70;

        // Body text
        contentStream.setNonStrokingColor(BLACK[0], BLACK[1], BLACK[2]);
        drawCenteredText(contentStream, regularFont, 16, "Este certificado reconoce que", centerX, currentY, BLACK);
        currentY -= 40;
        
        drawCenteredText(contentStream, boldFont, 20, certificate.getUser().getName(), centerX, currentY, BLACK);
        currentY -= 40;
        
        drawCenteredText(contentStream, regularFont, 14, "ha participado exitosamente en la categoría:", centerX, currentY, BLACK);
        currentY -= 25;
        drawCenteredText(contentStream, boldFont, 16, certificate.getCategory().getName(), centerX, currentY, BLACK);
        currentY -= 30;
        
        drawCenteredText(contentStream, regularFont, 14, "de la competición:", centerX, currentY, BLACK);
        currentY -= 25;
        drawCenteredText(contentStream, boldFont, 16, certificate.getCompetition().getName(), centerX, currentY, BLACK);
        currentY -= 40;
        
        drawCenteredText(contentStream, regularFont, 14, "con el proyecto titulado", centerX, currentY, BLACK);
        currentY -= 30;
        drawCenteredMultilineText(contentStream, boldFont, 16, "\"" + certificate.getProject().getName() + "\"", centerX, currentY, PAGE_WIDTH - 120);
        currentY -= 80;

        // Seal
        drawCenteredText(contentStream, boldFont, 14, "*===================*", centerX, currentY, DARK_BLUE);
        currentY -= 20;
        drawCenteredText(contentStream, boldFont, 12, "PARTICIPANTE", centerX, currentY, DARK_BLUE);
        currentY -= 18;
        drawCenteredText(contentStream, boldFont, 12, "VOTIFY", centerX, currentY, DARK_BLUE);
        currentY -= 18;
        drawCenteredText(contentStream, regularFont, 10, "EDICIÓN " + certificate.getCertificateYear(), centerX, currentY, DARK_BLUE);
        currentY -= 15;
        drawCenteredText(contentStream, boldFont, 14, "*===================*", centerX, currentY, DARK_BLUE);
        currentY -= 60;

        // Footer lines
        float leftX = 80;
        float rightX = PAGE_WIDTH - 80;
        
        contentStream.setStrokingColor(BLACK[0], BLACK[1], BLACK[2]);
        contentStream.setLineWidth(1);
        contentStream.moveTo(leftX, currentY);
        contentStream.lineTo(leftX + 160, currentY);
        contentStream.moveTo(rightX - 160, currentY);
        contentStream.lineTo(rightX, currentY);
        contentStream.stroke();

        currentY -= 20;
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        drawCenteredText(contentStream, regularFont, 12, "Fecha: " + dateStr, leftX + 80, currentY, BLACK);
        
        String rankingDisplay = certificate.getRankingType() != null && certificate.getRankingType().isPopularRanking() ? "Popular" : "Jueces";
        drawCenteredText(contentStream, regularFont, 12, "Valoración: " + rankingDisplay, rightX - 80, currentY, BLACK);
    }

    /**
     * Draw winner certificate
     */
    private void drawWinnerCertificate(PDPageContentStream contentStream, Certificate certificate) throws IOException {
        float centerX = PAGE_WIDTH / 2;
        float currentY = PAGE_HEIGHT - 100;

        PDFont boldFont = PDType1Font.HELVETICA_BOLD;
        PDFont regularFont = PDType1Font.HELVETICA;

        // Header: Logo and title
        drawCenteredText(contentStream, boldFont, 24, "Votify", centerX, currentY, GOLD);
        currentY -= 80;

        drawCenteredText(contentStream, boldFont, 38, "CERTIFICADO", centerX, currentY, GOLD);
        currentY -= 40;
        drawCenteredText(contentStream, boldFont, 38, "DE GANADOR", centerX, currentY, GOLD);
        currentY -= 60;

        // Body text
        contentStream.setNonStrokingColor(BLACK[0], BLACK[1], BLACK[2]);
        drawCenteredText(contentStream, regularFont, 16, "Este certificado otorga el", centerX, currentY, BLACK);
        currentY -= 30;
        drawCenteredText(contentStream, boldFont, 18, "PRIMER LUGAR a", centerX, currentY, BLACK);
        currentY -= 40;
        
        drawCenteredText(contentStream, boldFont, 22, certificate.getUser().getName(), centerX, currentY, BLACK);
        currentY -= 40;

        drawCenteredText(contentStream, regularFont, 14, "por su destacada participación en la categoría:", centerX, currentY, BLACK);
        currentY -= 25;
        drawCenteredText(contentStream, boldFont, 16, certificate.getCategory().getName(), centerX, currentY, BLACK);
        currentY -= 30;
        
        drawCenteredText(contentStream, regularFont, 14, "de la competición:", centerX, currentY, BLACK);
        currentY -= 25;
        drawCenteredText(contentStream, boldFont, 16, certificate.getCompetition().getName(), centerX, currentY, BLACK);
        currentY -= 40;
        
        drawCenteredText(contentStream, regularFont, 14, "con el proyecto titulado", centerX, currentY, BLACK);
        currentY -= 30;
        drawCenteredMultilineText(contentStream, boldFont, 16, "\"" + certificate.getProject().getName() + "\"", centerX, currentY, PAGE_WIDTH - 120);
        currentY -= 50;

        drawCenteredText(contentStream, regularFont, 14, "RECONOCIMIENTO DE EXCELENCIA", centerX, currentY, GOLD);
        currentY -= 60;

        // Seal
        drawCenteredText(contentStream, boldFont, 16, "+=====================+", centerX, currentY, GOLD);
        currentY -= 22;
        drawCenteredText(contentStream, boldFont, 13, "GANADOR - PRIMER LUGAR", centerX, currentY, GOLD);
        currentY -= 20;
        drawCenteredText(contentStream, boldFont, 13, "VOTIFY", centerX, currentY, GOLD);
        currentY -= 20;
        drawCenteredText(contentStream, regularFont, 11, "EDICIÓN " + certificate.getCertificateYear(), centerX, currentY, GOLD);
        currentY -= 15;
        drawCenteredText(contentStream, boldFont, 16, "+=====================+", centerX, currentY, GOLD);
        currentY -= 60;

        // Footer lines
        float leftX = 80;
        float rightX = PAGE_WIDTH - 80;
        
        contentStream.setStrokingColor(BLACK[0], BLACK[1], BLACK[2]);
        contentStream.setLineWidth(1);
        contentStream.moveTo(leftX, currentY);
        contentStream.lineTo(leftX + 160, currentY);
        contentStream.moveTo(rightX - 160, currentY);
        contentStream.lineTo(rightX, currentY);
        contentStream.stroke();

        currentY -= 20;
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        drawCenteredText(contentStream, regularFont, 12, "Fecha: " + dateStr, leftX + 80, currentY, BLACK);
        
        String rankingDisplay = certificate.getRankingType() != null && certificate.getRankingType().isPopularRanking() ? "Popular" : "Jueces";
        drawCenteredText(contentStream, regularFont, 12, "Valoración: " + rankingDisplay, rightX - 80, currentY, BLACK);
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
