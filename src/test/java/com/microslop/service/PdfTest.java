package com.microslop.service;

import com.microslop.entity.*;
import org.junit.jupiter.api.Test;
import java.io.FileOutputStream;

public class PdfTest {

    @Test
    public void testGeneratePdfs() throws Exception {
        CertificatePdfGenerator generator = new CertificatePdfGenerator();
        
        User user = new User();
        user.setName("Juan Pérez");
        
        Competition comp = new Competition();
        comp.setName("Competición de Diseño Innovador");
        
        Project proj = new Project();
        proj.setName("Proyecto de Ciudad Sostenible");
        
        Category cat = new Category();
        cat.setName("Diseño Arquitectónico");
        
        CertificateTypeEntity typePart = new CertificateTypeEntity();
        typePart.setCode("PARTICIPANT");
        
        CertificateTypeEntity typeWin = new CertificateTypeEntity();
        typeWin.setCode("JUDGE_WINNER");
        
        RankingTypeEntity ranking = new RankingTypeEntity();
        ranking.setCode("JUDGES_RANKING");
        
        Certificate certPart = new Certificate(user, comp, proj, cat, ranking);
        certPart.setCertificateType(typePart);
        
        Certificate certWin = new Certificate(user, comp, proj, cat, typeWin, ranking, 1);
        
        // Generate Participant PDF
        byte[] pdf1 = generator.generateCertificatePdf(certPart);
        try (FileOutputStream fos = new FileOutputStream("test_participant.pdf")) {
            fos.write(pdf1);
        }
        
        // Generate Winner PDF
        byte[] pdf2 = generator.generateCertificatePdf(certWin);
        try (FileOutputStream fos = new FileOutputStream("test_winner.pdf")) {
            fos.write(pdf2);
        }
        
        System.out.println("PDFs generated successfully in project root folder!");
    }
}
