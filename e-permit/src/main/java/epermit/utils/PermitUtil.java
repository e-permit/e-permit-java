package epermit.utils;

import java.io.ByteArrayInputStream;
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
import epermit.models.dtos.CreateQrCodeDto;
import epermit.models.dtos.QuotaSufficientDto;
import epermit.repositories.LedgerQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermitUtil {
    private final JwsUtil jwsUtil;
    private final LedgerQuotaRepository quotaRepository;
    private final EPermitProperties properties;

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
                        && x.getPermitIssuedFor().equals(input.getIssuedFor()) && x.isActive()
                        && x.getPermitType() == input.getPermitType()
                        && input.getSerialNumber() >= x.getStartNumber()
                        && input.getSerialNumber() <= x.getEndNumber());
        log.info("isQuotaSufficient rusult is {}", r);
        return r;
    }

    public String generateQrCode(CreateQrCodeDto input) {
        log.info("generateQrCode started with {}", input);

        Map<String, String> claims = new HashMap<>();
        claims.put("id", input.getId());
        claims.put("iat", input.getIssuedAt());
        claims.put("exp", input.getExpireAt());
        claims.put("pn", input.getPlateNumber());
        claims.put("cn", input.getCompanyName());
        String jws = jwsUtil.createJws(claims);
        String qrCode = properties.getQrcodeVersion() + "." + jws;
        log.info("generateQrCode ended jws is {}", jws);

        return qrCode;
    }

    @SneakyThrows
    public ByteArrayInputStream generatePdf(LedgerPermit permit) {
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
        return new ByteArrayInputStream(out.toByteArray());
    }
}
