package com.microslop;

import com.microslop.service.CertificateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CertificateGenTest {

    @Autowired
    private CertificateService certificateService;

    @Test
    public void testGen() {
        System.out.println("====== STARTING TEST ======");
        try {
            certificateService.generateCertificatesForCompetition(22L);
            System.out.println("====== SUCCESS ======");
        } catch (Exception e) {
            System.out.println("====== ERROR ======");
            e.printStackTrace();
        }
    }
}
