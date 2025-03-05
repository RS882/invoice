package com.billing.invoice.services;

import com.billing.invoice.domain.model.InvoiceDataForFile;
import com.billing.invoice.exception_handler.exceptions.server_exception.exceptions.PdfGeneratingException;
import com.billing.invoice.domain.model.BillData;
import com.billing.invoice.services.interfaces.PdfGeneratorService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    @Override
    public InputStream generatePdf(InvoiceDataForFile data) {

        BillData billData = data.getBillData();

        long invoiceNumber = data.getInvoiceNumber();
        LocalDate invoiceDate = data.getInvoiceDate();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            Style boldStyle = new Style().setFontSize(14).setFont(boldFont);
            Style normalStyle = new Style().setFontSize(12);
            DecimalFormat df = new DecimalFormat("#0.00");

            document.add(new Paragraph(new Text("Invoice # ").addStyle(boldStyle))
                    .add(new Text(String.valueOf(invoiceNumber))).addStyle(boldStyle)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(new Text("Date ").addStyle(boldStyle))
                    .add(new Text(invoiceDate.toString())).addStyle(boldStyle)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(new Text("Tariff Plan: ").addStyle(boldStyle))
                    .add(new Text(Optional.ofNullable(billData.getPlan().toString())
                            .orElse("N/A")).addStyle(normalStyle)));

            document.add(new Paragraph(new Text("Price: ").addStyle(boldStyle))
                    .add(new Text(df.format(billData.getPrice()) + " €").addStyle(normalStyle)));

            document.add(new Paragraph(new Text("Limit (GB): ").addStyle(boldStyle))
                    .add(new Text(df.format(billData.getLimit())).addStyle(normalStyle)));

            document.add(new Paragraph(new Text("Overage Fee (GB): ").addStyle(boldStyle))
                    .add(new Text(df.format(billData.getOverageCharge()) + " €").addStyle(normalStyle)));

            document.add(new Paragraph(new Text("Additional Usage: ").addStyle(boldStyle))
                    .add(new Text(df.format(billData.getExtraGBPrice()) + " €").addStyle(normalStyle)));

            document.add(new Paragraph(new Text("Discount: ").addStyle(boldStyle))
                    .add(new Text(df.format(billData.getDiscountRate()) + "% → " + df.format(billData.getDiscount()) + " €")
                            .addStyle(normalStyle)));

            document.add(new Paragraph(new Text("VAT(" + df.format(billData.getVatRate()) + "%) : ").addStyle(boldStyle))
                    .add(new Text(df.format(billData.getVat()) + " €").addStyle(normalStyle)));

            document.add(new Paragraph(new Text("Total Amount: ").addStyle(boldStyle))
                    .add(new Text(df.format(billData.getTotal()) + " €").addStyle(normalStyle)));

            document.close();
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new PdfGeneratingException(e.getMessage());
        }
    }
}
