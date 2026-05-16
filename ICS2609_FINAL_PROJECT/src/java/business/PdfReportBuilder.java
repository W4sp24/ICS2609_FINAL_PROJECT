/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.OutputStream;
import java.util.List;
import dao.DerbyAuthDAO;

import java.time.ZonedDateTime;
import java.time.ZoneId;

/**
 *
 * @author ethan
 */
public class PdfReportBuilder {
    
    private final DerbyAuthDAO db;
    private final Rectangle ADMIN_PAGE_SIZE = PageSize.LETTER;
    //colors
    private final BaseColor headerColor = new BaseColor(52, 73, 94);   
    private final BaseColor rowEven = new BaseColor(245, 245, 245);  
    private final Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    private final Font dataFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.DARK_GRAY);
    private final Rectangle GUEST_PAGE_SIZE = new Rectangle(450, 400); // custom size for guest report
    //fonts
    
    private final Font TITLE_FONT = new Font(Font.FontFamily.COURIER, 34, Font.BOLD, BaseColor.DARK_GRAY);
    
    public PdfReportBuilder(DerbyAuthDAO db){
        this.db = db;
    }
    
    public void generate(String email, OutputStream out) throws DocumentException { //this is the only external callable method in this class
        // where to generate shi
    }
}
