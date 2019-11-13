package com.nanobnk.epayment.reporting.service.utils;


import com.lowagie.text.pdf.*;
import com.lowagie.text.*;

public class PDFBackground  extends PdfPageEventHelper {

         @Override
        public void onEndPage(PdfWriter writer, Document document) {
             try {

                 Image background = Image.getInstance("./config/images/logo.png");
                 // This scales the image to the page,
                 // use the image's width & height if you don't want to scale.
                 float width = document.getPageSize().getWidth();
                 float height = document.getPageSize().getHeight();
                 writer.getDirectContentUnder()
                         .addImage(background, width, 0, 0, height, 0, 0);
             }catch (Exception ex){
                 ex.printStackTrace();
        }

    }
}
