package epermit.utils;

import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.itextpdf.barcodes.Barcode39;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import epermit.models.EPermitProperties;
import epermit.models.PermitId;
import epermit.repositories.AuthorityRepository;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class PermitUtilTest {

    @Mock
    EPermitProperties properties;

    @Mock
    AuthorityRepository authorityRepository;

    @InjectMocks
    PermitUtil util;

    @Test
    void getPermitIdTest() {
        PermitId input = PermitId.builder().issuedFor("B")
                .issuer("A")
                .permitType(1)
                .permitYear(2021)
                .serialNumber(12L).build();
        String permitId = input.toString();
        assertEquals("A", PermitId.parse(permitId).getIssuer());
        assertEquals("A-B-2021-1-12", permitId);
    }

    @Test
    @SneakyThrows
    void createPdfTest() {
        String outputPdfFile = "target/epermit.pdf";
        try (PdfWriter writer = new PdfWriter(outputPdfFile)) {
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            String line = "E-PERMIT(06BB2746)";
            BarcodeQRCode qrCode = new BarcodeQRCode(
                    "0.eyJhbGciOiJFUzI1NiIsImtpZCI6IjEifQ.eyJpZCI6IlVaLVRSLTIwMjEtMS0xIiwiaWF0IjoiMjcvNS8yMDIxIiwiZXhwIjoiMzEvMS8yMDIyIiwicG4iOiJkIiwiY24iOiJkIn0.I8-dgCtal8ajgAIIaL2NLvFUboCrnIfoqz__1doK_Q1-kIoPgYbbfqm8BDfXk9INdPAUyc1R-FvQrVsgr3D2Cw");
            Barcode39 barCode = new Barcode39(pdf);
            barCode.setCode("A-B-2022-1-1");
            barCode.setAltText("A-B-2022-1-1");
            var barCodeImg = new Image(barCode.createFormXObject(pdf)).setMarginLeft(20);
            var qrCodeImg = new Image(qrCode.createFormXObject(pdf)).setWidth(200);
            document.add(new Paragraph(line).setTextAlignment(null).setMarginLeft(45));
            document.add(barCodeImg);
            document.add(qrCodeImg);
            document.close();
        }
    }
}
