/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ReportGeneratorDOCX.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.reportgenerator.manager;

import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.crmf.model.audit.Audit;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ReportGeneratorDOCX {

  @Autowired
  private AssetModelWriter amWriter;
  @Autowired
  private CommonWriter commonWriter;

  public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";

  public void generateReport(String filename, AssessmentProcedure procedure, AssessmentProject project, Audit audit) throws Exception {

    XWPFDocument doc = new XWPFDocument();
    //Write Risk Assessment Project
    commonWriter.writeProject(doc, project);

    //Write Risk Assessment Procedure
    commonWriter.writeProcedure(doc, procedure, project);

    //Write Methodology
    commonWriter.writeMethodology(doc);

    //Write Procedures summary
    commonWriter.writeProceduresSummary(doc, procedure, project);

    //Extracting the Asset Model in order to write all Nodes in the Report
    amWriter.writeAssetModel(doc, procedure, project, false);

    commonWriter.writeRiskModel(doc, procedure, project);

    commonWriter.writeRiskTreatmentModel(doc, procedure, project, ImpactEnum.LOW);

    commonWriter.writeSafeguardModel(doc, procedure, project);

    XWPFParagraph annexParagraph = doc.createParagraph();

    //Writing Annex Heading
    annexParagraph.setStyle("Heading1");
    annexParagraph.setPageBreak(true);

    annexParagraph.setAlignment(ParagraphAlignment.LEFT);
    annexParagraph.setVerticalAlignment(TextAlignment.CENTER);

    XWPFRun annexRun = annexParagraph.createRun();

    annexRun.setBold(true);
    annexRun.setText("Annex");
    annexRun.setFontFamily("Calibri");
    annexRun.setFontSize(15);

    amWriter.writeAssetModelAnnex(doc, procedure, project);

    commonWriter.writeVulnerabilityAnnex(doc, procedure);

    commonWriter.writeThreatAnnex(doc, procedure);

    // create header/footer functions insert an empty paragraph
    XWPFHeader head = doc.createHeader(HeaderFooterType.DEFAULT);


    XWPFParagraph headerText = head.createParagraph();
    headerText.setAlignment(ParagraphAlignment.CENTER);
    XWPFRun headerRunText = headerText.createRun();
    headerRunText.setFontFamily("Calibri");
    headerRunText.setFontSize(15);
    headerRunText.setText("Report for Risk Assessment Project" + " " + project.getName());

    XWPFStyles styles = doc.createStyles();
    String heading1 = "Heading1";
    addCustomHeadingStyle(doc, styles, heading1, 1, 36, "000000");

    String heading2 = "Heading2";
    addCustomHeadingStyle(doc, styles, heading2, 2, 36, "000000");

    String heading3 = "Heading3";
    addCustomHeadingStyle(doc, styles, heading3, 3, 36, "000000");

    String heading4 = "Heading4";
    addCustomHeadingStyle(doc, styles, heading4, 4, 36, "000000");


    XWPFFooter foot = doc.createFooter(HeaderFooterType.DEFAULT);
    DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
    Date now = new Date();

    XWPFParagraph footer = foot.createParagraph();
    footer.setAlignment(ParagraphAlignment.CENTER);

    footer.createRun().setText("Creation time: " + df.format(now));

    FileOutputStream out = null;
    try {
      out = new FileOutputStream(filename);
      doc.write(out);
    } finally {
      if (doc != null) {
        doc.close();
      }
      if (out != null) {
        out.close();
      }
    }

  }

  private void addCustomHeadingStyle(XWPFDocument docxDocument, XWPFStyles styles, String strStyleId, int headingLevel, int pointSize, String hexColor) {

    CTStyle ctStyle = CTStyle.Factory.newInstance();
    ctStyle.setStyleId(strStyleId);


    CTString styleName = CTString.Factory.newInstance();
    styleName.setVal(strStyleId);
    ctStyle.setName(styleName);

    CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
    indentNumber.setVal(BigInteger.valueOf(headingLevel));

    // lower number > style is more prominent in the formats bar
    ctStyle.setUiPriority(indentNumber);

    CTOnOff onoffnull = CTOnOff.Factory.newInstance();
    ctStyle.setUnhideWhenUsed(onoffnull);

    // style shows up in the formats bar
    ctStyle.setQFormat(onoffnull);

    // style defines a heading of the given level
    CTPPr ppr = CTPPr.Factory.newInstance();
    ppr.setOutlineLvl(indentNumber);
    ctStyle.setPPr(ppr);

    XWPFStyle style = new XWPFStyle(ctStyle);

    CTHpsMeasure size = CTHpsMeasure.Factory.newInstance();
    size.setVal(new BigInteger(String.valueOf(pointSize)));
    CTHpsMeasure size2 = CTHpsMeasure.Factory.newInstance();
    size2.setVal(new BigInteger("24"));

    CTFonts fonts = CTFonts.Factory.newInstance();
    fonts.setAscii("Loma");

    CTRPr rpr = CTRPr.Factory.newInstance();
    rpr.setRFonts(fonts);
    rpr.setSz(size);
    rpr.setSzCs(size2);

    CTColor color = CTColor.Factory.newInstance();
    color.setVal(hexToBytes(hexColor));
    rpr.setColor(color);
    style.getCTStyle().setRPr(rpr);
    // is a null op if already defined

    style.setType(STStyleType.PARAGRAPH);
    styles.addStyle(style);

  }

  public static byte[] hexToBytes(String hexString) {
    HexBinaryAdapter adapter = new HexBinaryAdapter();
    byte[] bytes = adapter.unmarshal(hexString);
    return bytes;
  }


}
