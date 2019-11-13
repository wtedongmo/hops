package com.nanobnk.epayment.reporting.service.utils;

//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.*;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.nanobnk.epayment.service.utils.StringDateFormaterUtils;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class HeaderFooterPageEvent extends PdfPageEventHelper {

    private PdfTemplate t;
    private Image total;

    public void onOpenDocument(PdfWriter writer, Document document) {
        t = writer.getDirectContent().createTemplate(30, 16);
        try {
            total = Image.getInstance(t);
//            total.setRole(PdfName.ARTIFACT);
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        addHeader(writer);
        addFooter(writer);
    }

    private void addHeader(PdfWriter writer){
        PdfPTable header = new PdfPTable(2);
        try {
            // set defaults
            header.setWidths(new int[]{2, 24});
            header.setTotalWidth(527);
            header.setLockedWidth(true);
            header.getDefaultCell().setFixedHeight(40);
            header.getDefaultCell().setBorder(Rectangle.BOTTOM);
            header.getDefaultCell().setBorderColor(Color.LIGHT_GRAY);

            // add image
//            Image logo = Image.getInstance(HeaderFooterPageEvent.class.getResource("/memorynotfound-logo.jpg"));
//            header.addCell(logo);
//
//            // add text
//            PdfPCell text = new PdfPCell();
//            text.setPaddingBottom(15);
//            text.setPaddingLeft(10);
//            text.setBorder(Rectangle.BOTTOM);
//            text.setBorderColor(Color.LIGHT_GRAY);
//            text.addElement(new Phrase("iText PDF Header Footer Example", FontFactory.getFont(FontFactory.TIMES, 7f, Font.NORMAL, null)));
//            text.addElement(new Phrase("https://memorynotfound.com", FontFactory.getFont(FontFactory.TIMES, 7f, Font.NORMAL, null)));
//            header.addCell(text);

            // write content
            header.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
        } catch(DocumentException de) {
            throw new ExceptionConverter(de);
        }
//        catch (MalformedURLException e) {
//            throw new ExceptionConverter(e);
//        } catch (IOException e) {
//            throw new ExceptionConverter(e);
//        }
    }

    private void addFooter(PdfWriter writer){
        PdfPTable footer = new PdfPTable(3);
        try {
            // set defaults
            footer.setWidths(new int[]{24, 2, 1});
            footer.setTotalWidth(527);
            footer.setLockedWidth(true);
            footer.getDefaultCell().setFixedHeight(40);
            footer.getDefaultCell().setBorder(Rectangle.TOP);
            footer.getDefaultCell().setBorderColor(Color.LIGHT_GRAY);

            // add copyright
            DateTimeFormatter formatterDateTimeFoot = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss");
            Phrase phrase = new Phrase("Imprime le : " +LocalDateTime.now().format(formatterDateTimeFoot),
                    FontFactory.getFont(FontFactory.TIMES, 7f, Font.NORMAL, null));
            footer.addCell(phrase);
//            footer.addCell(new Phrase("\u00A9 Memorynotfound.com", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

            // add current page count
            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            footer.addCell(new Phrase(String.format("Page %d of", writer.getPageNumber()), FontFactory.getFont(FontFactory.TIMES, 7f, Font.NORMAL, null)));

            // add placeholder for total page count
            PdfPCell totalPageCount = new PdfPCell(total);
            totalPageCount.setBorder(Rectangle.TOP);
            totalPageCount.setBorderColor(Color.LIGHT_GRAY);
            footer.addCell(totalPageCount);

            // write page
            PdfContentByte canvas = writer.getDirectContent();
            canvas.beginMarkedContentSequence(PdfName.A); //ARTIFACT
            footer.writeSelectedRows(0, -1, 34, 50, canvas);
            canvas.endMarkedContentSequence();
        } catch(DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public void onCloseDocument(PdfWriter writer, Document document) {
        int totalLength = String.valueOf(writer.getPageNumber()).length();
        int totalWidth = totalLength * 5;
        ColumnText.showTextAligned(t, Element.ALIGN_RIGHT,
                new Phrase(String.valueOf(writer.getPageNumber()), FontFactory.getFont(FontFactory.TIMES, 7f, Font.NORMAL, null)),
                totalWidth, 6, 0);
    }
}
