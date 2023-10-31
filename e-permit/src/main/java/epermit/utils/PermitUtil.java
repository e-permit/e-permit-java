package epermit.utils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import org.springframework.stereotype.Component;
import com.itextpdf.barcodes.Barcode39;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import epermit.entities.LedgerPermit;
import epermit.models.EPermitProperties;
import epermit.models.dtos.CreatePermitIdDto;
import epermit.models.dtos.QuotaSufficientDto;
import epermit.repositories.LedgerQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermitUtil {
    private final LedgerQuotaRepository quotaRepository;

    public String getPermitId(CreatePermitIdDto input) {
        StringJoiner joiner = new StringJoiner("-");
        String permitId = joiner.add(input.getIssuer()).add(input.getIssuedFor())
                .add(Integer.toString(input.getPermitYear())).add(input.getPermitType().getCode())
                .add(Integer.toString(input.getSerialNumber())).toString();
        return permitId;
    }

    public boolean isQuotaSufficient(QuotaSufficientDto input) {
        Boolean r = quotaRepository.findAll().stream()
                .anyMatch(x -> x.getPermitIssuer().equals(input.getIssuer())
                        && x.getPermitIssuedFor().equals(input.getIssuedFor())
                        && x.getPermitType() == input.getPermitType());
        log.info("isQuotaSufficient rusult is {}", r);
        return r;
    }

    @SneakyThrows
    public byte[] generatePdf(LedgerPermit permit) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        String line = "E-PERMIT(" + permit.getPlateNumber() + ")";
        BarcodeQRCode qrCode = new BarcodeQRCode(permit.getQrCode());
        Barcode39 barCode = new Barcode39(pdf);
        barCode.setCode(permit.getPermitId());
        barCode.setAltText(permit.getPermitId());
        var barCodeImg = new Image(barCode.createFormXObject(pdf)).setMarginLeft(20);
        var qrCodeImg = new Image(qrCode.createFormXObject(pdf)).setWidth(200);
        document.add(new Paragraph(line).setTextAlignment(null).setMarginLeft(45));
        document.add(barCodeImg);
        document.add(qrCodeImg);
        document.close();
        return out.toByteArray();
    }
}
