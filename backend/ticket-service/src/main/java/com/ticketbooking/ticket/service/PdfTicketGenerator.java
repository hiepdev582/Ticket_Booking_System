package com.ticketbooking.ticket.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.ticketbooking.ticket.entity.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdfTicketGenerator {

    private final QrCodeGenerator qrCodeGenerator;

    public byte[] generateTicketPdf(Ticket ticket) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A5.rotate(), 20, 20, 20, 20);
            PdfWriter.getInstance(document, out);
            document.open();

            // Font styles
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.GRAY);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
            Font valueBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(13, 110, 253));

            // Main Ticket Table (Boarding Pass Layout)
            PdfPTable mainTable = new PdfPTable(2);
            mainTable.setWidthPercentage(100);
            mainTable.setWidths(new float[]{70f, 30f});

            // Left Section: Ticket Info
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.BOX);
            leftCell.setBorderColor(new Color(220, 224, 230));
            leftCell.setPadding(15);
            leftCell.setBackgroundColor(Color.WHITE);

            // Header Banner
            Paragraph title = new Paragraph("VE DIEN TU / E-BOARDING PASS", titleFont);
            title.setAlignment(Element.ALIGN_LEFT);
            leftCell.addElement(title);

            Paragraph ticketNo = new Paragraph("Ma ve: " + ticket.getTicketNumber() + " | Ma don: " + ticket.getBookingId().substring(0, 8), labelFont);
            ticketNo.setSpacingAfter(10);
            leftCell.addElement(ticketNo);

            // Journey Info
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{50f, 50f});

            addTableCell(infoTable, "HANH KHACH / PASSENGER", ticket.getPassengerName() != null ? ticket.getPassengerName() : "Hanh khach", labelFont, valueBoldFont);
            addTableCell(infoTable, "SO DIEN THOAI / PHONE", ticket.getPassengerPhone() != null ? ticket.getPassengerPhone() : "N/A", labelFont, valueFont);
            addTableCell(infoTable, "TUYEN DUONG / ROUTE", ticket.getRouteName() != null ? ticket.getRouteName() : "Tuyen xe", labelFont, valueBoldFont);
            addTableCell(infoTable, "MA CHUYEN / TRIP CODE", ticket.getTripCode() != null ? ticket.getTripCode() : "TRIP-01", labelFont, valueFont);
            
            String depTime = ticket.getDepartureTime() != null ? ticket.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")) : "N/A";
            addTableCell(infoTable, "GIO KHOI HANH / DEPARTURE", depTime, labelFont, valueBoldFont);
            addTableCell(infoTable, "SO GHE / SEAT NUMBER", ticket.getSeatNumber(), labelFont, valueBoldFont);

            leftCell.addElement(infoTable);
            mainTable.addCell(leftCell);

            // Right Section: QR Code & Price
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.BOX);
            rightCell.setBorderColor(new Color(220, 224, 230));
            rightCell.setPadding(15);
            rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            rightCell.setBackgroundColor(new Color(248, 249, 250));

            // Embed QR Code
            String qrContent = "TICKET:" + ticket.getTicketNumber() + "|TRIP:" + ticket.getTripCode() + "|SEAT:" + ticket.getSeatNumber();
            byte[] qrImageBytes = qrCodeGenerator.generateQrCodeImage(qrContent, 140, 140);
            Image qrImage = Image.getInstance(qrImageBytes);
            qrImage.setAlignment(Element.ALIGN_CENTER);
            qrImage.scaleToFit(120, 120);
            rightCell.addElement(qrImage);

            Paragraph qrLabel = new Paragraph("Quet ma khi len xe", labelFont);
            qrLabel.setAlignment(Element.ALIGN_CENTER);
            qrLabel.setSpacingBefore(5);
            rightCell.addElement(qrLabel);

            Paragraph priceText = new Paragraph(ticket.getPrice() != null ? String.format("%,d VND", ticket.getPrice().longValue()) : "PAID", valueBoldFont);
            priceText.setAlignment(Element.ALIGN_CENTER);
            priceText.setSpacingBefore(8);
            rightCell.addElement(priceText);

            mainTable.addCell(rightCell);

            document.add(mainTable);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            log.error("Lỗi khi render PDF vé cho vé #{}: {}", ticket.getTicketNumber(), e.getMessage());
            throw new RuntimeException("Lỗi sinh file PDF vé", e);
        }
    }

    private void addTableCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(6);
        cell.addElement(new Paragraph(label, labelFont));
        cell.addElement(new Paragraph(value, valueFont));
        table.addCell(cell);
    }
}
