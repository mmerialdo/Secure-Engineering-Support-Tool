/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetModelWriter.java"
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

import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.riskassessment.SystemParticipant;
import org.crmf.model.riskassessmentelements.Asset;
import org.crmf.model.riskassessmentelements.BusinessActivity;
import org.crmf.model.riskassessmentelements.BusinessProcess;
import org.crmf.model.riskassessmentelements.Edge;
import org.crmf.model.riskassessmentelements.Malfunction;
import org.crmf.model.riskassessmentelements.MalfunctionValueScale;
import org.crmf.model.riskassessmentelements.Node;
import org.crmf.model.riskassessmentelements.NodeTypeEnum;
import org.crmf.model.riskassessmentelements.Organization;
import org.crmf.model.riskassessmentelements.SecurityImpact;
import org.crmf.model.riskassessmentelements.TechnicalMalfunctionTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class AssetModelWriter {
  private static final Logger LOG = LoggerFactory.getLogger(AssetModelWriter.class.getName());
  public static final String CALIBRI = "Calibri";

  public void writeAssetModel(XWPFDocument doc, AssessmentProcedure procedure, AssessmentProject project, boolean lightReport) {

    sortAssetModel(procedure.getAssetModel());

    XWPFParagraph assetModelParagraph = doc.createParagraph();

    if (lightReport) {
      assetModelParagraph.setStyle("Heading2");
    } else {
      assetModelParagraph.setStyle("Heading1");
    }
    assetModelParagraph.setPageBreak(true);

    assetModelParagraph.setAlignment(ParagraphAlignment.LEFT);
		/*assetModelParagraph.setBorderBottom(Borders.DOUBLE);
		assetModelParagraph.setBorderTop(Borders.DOUBLE);
		assetModelParagraph.setBorderRight(Borders.DOUBLE);
		assetModelParagraph.setBorderLeft(Borders.DOUBLE);*/
    assetModelParagraph.setVerticalAlignment(TextAlignment.CENTER);
    /*assetModelParagraph.setBorderBetween(Borders.SINGLE);*/

    XWPFRun assetModelNameRun = assetModelParagraph.createRun();

    //Writing AssetModel Heading
    assetModelNameRun.setBold(true);

    assetModelNameRun.setText("Business-Asset Model");
    assetModelNameRun.setFontFamily(CALIBRI);
    assetModelNameRun.setFontSize(15);

    XWPFParagraph assetModelDataParagraph = doc.createParagraph();
		/*assetModelDataParagraph.setBorderBottom(Borders.DOUBLE);
		assetModelDataParagraph.setBorderTop(Borders.DOUBLE);
		assetModelDataParagraph.setBorderRight(Borders.DOUBLE);
		assetModelDataParagraph.setBorderLeft(Borders.DOUBLE);*/
    assetModelDataParagraph.setBorderBetween(Borders.SINGLE);
    XWPFRun assetModelData = assetModelDataParagraph.createRun();

    //Writing AssetModel Creation Time
    assetModelData.setBold(false);
    assetModelData.setFontSize(13);
    assetModelData.setFontFamily(CALIBRI);
    assetModelData.setText("Creation Time: ");
    assetModelData.setText(procedure.getAssetModel().getCreationTime());
    assetModelData.addCarriageReturn();

    //Writing AssetModel Last Update Time
    assetModelData.setText("Last Update Time: ");
    assetModelData.setText(procedure.getAssetModel().getUpdateTime());
    assetModelData.addCarriageReturn();
		

		/*XWPFParagraph assetModelParagraphTable = doc.createParagraph();
			
		assetModelParagraphTable.setStyle("Heading2");
		assetModelParagraphTable.setPageBreak(false);
		
		assetModelParagraphTable.setAlignment(ParagraphAlignment.LEFT);
		assetModelParagraphTable.setBorderBottom(Borders.DOUBLE);
		assetModelParagraphTable.setBorderTop(Borders.DOUBLE);
		assetModelParagraphTable.setBorderRight(Borders.DOUBLE);
		assetModelParagraphTable.setBorderLeft(Borders.DOUBLE);
		assetModelParagraphTable.setVerticalAlignment(TextAlignment.CENTER);
		assetModelParagraphTable.setBorderBetween(Borders.SINGLE);
		
		XWPFRun assetModelNameRunTable = assetModelParagraphTable.createRun();
		
		//Writing AssetModel Heading 1
		assetModelNameRunTable.setBold(true);
		assetModelNameRunTable.setText("Business-Asset Model Table");
		assetModelNameRunTable.setFontFamily("Calibri");
		assetModelNameRunTable.setFontSize(15);*/

    writeAssetModelTable(doc, procedure, project);

  }

  public void writeAssetModelAnnex(XWPFDocument doc, AssessmentProcedure procedure, AssessmentProject project) {

    XWPFParagraph assetModelParagraphDetails = doc.createParagraph();

    assetModelParagraphDetails.setStyle("Heading2");
    //assetModelParagraphDetails.setPageBreak(true);

    assetModelParagraphDetails.setAlignment(ParagraphAlignment.LEFT);
	        /*assetModelParagraphDetails.setBorderBottom(Borders.DOUBLE);
			assetModelParagraphDetails.setBorderTop(Borders.DOUBLE);
			assetModelParagraphDetails.setBorderRight(Borders.DOUBLE);
			assetModelParagraphDetails.setBorderLeft(Borders.DOUBLE);*/
    assetModelParagraphDetails.setVerticalAlignment(TextAlignment.CENTER);
    //assetModelParagraphDetails.setBorderBetween(Borders.SINGLE);

    XWPFRun assetModelNameRunDetails = assetModelParagraphDetails.createRun();

    //Writing AssetModel Heading 2
    assetModelNameRunDetails.setBold(true);
    assetModelNameRunDetails.setText("Business-Asset Model Details");
    assetModelNameRunDetails.setFontFamily(CALIBRI);
    assetModelNameRunDetails.setFontSize(14);

    //Here we write all Organizations
    XWPFParagraph organizationParagraphDetails = doc.createParagraph();
    organizationParagraphDetails.setStyle("Heading3");
    //organizationParagraphDetails.setPageBreak(true);

    organizationParagraphDetails.setAlignment(ParagraphAlignment.LEFT);
			 /*organizationParagraphDetails.setBorderBottom(Borders.DOUBLE);
			 organizationParagraphDetails.setBorderTop(Borders.DOUBLE);
			 organizationParagraphDetails.setBorderRight(Borders.DOUBLE);
			 organizationParagraphDetails.setBorderLeft(Borders.DOUBLE);*/
    organizationParagraphDetails.setVerticalAlignment(TextAlignment.CENTER);
    organizationParagraphDetails.setBorderBottom(Borders.SINGLE);

    XWPFRun organizationNameRunDetails = organizationParagraphDetails.createRun();

    //Writing Organization Heading 3
    organizationNameRunDetails.setBold(true);
    organizationNameRunDetails.setText("Organizations");
    organizationNameRunDetails.setFontFamily(CALIBRI);
    organizationNameRunDetails.setFontSize(13);

    for (Organization item : procedure.getAssetModel().getOrganizations()) {
      writeOrganizations(doc, item, project);
    }

    //Here we write all Business Processes
    XWPFParagraph bpParagraphDetails = doc.createParagraph();
    bpParagraphDetails.setStyle("Heading3");
    //bpParagraphDetails.setPageBreak(true);

    bpParagraphDetails.setAlignment(ParagraphAlignment.LEFT);
    bpParagraphDetails.setBorderTop(Borders.SINGLE);
			/*bpParagraphDetails.setBorderBottom(Borders.DOUBLE);
			bpParagraphDetails.setBorderTop(Borders.DOUBLE);
			bpParagraphDetails.setBorderRight(Borders.DOUBLE);
			bpParagraphDetails.setBorderLeft(Borders.DOUBLE);*/
    bpParagraphDetails.setVerticalAlignment(TextAlignment.CENTER);
    bpParagraphDetails.setBorderBottom(Borders.SINGLE);

    XWPFRun bpRunDetails = bpParagraphDetails.createRun();

    //Writing BusinessProcess Heading 3
    bpRunDetails.setBold(true);
    bpRunDetails.setText("Business Processes");
    bpRunDetails.setFontFamily(CALIBRI);
    bpRunDetails.setFontSize(13);

    for (BusinessProcess item : procedure.getAssetModel().getBusinessProcesses()) {
      writeBusinessProcesses(doc, item, procedure.getAssetModel(), project);
    }

    //Here we write all Business Activities
    XWPFParagraph baParagraphDetails = doc.createParagraph();
    baParagraphDetails.setStyle("Heading3");
    //baParagraphDetails.setPageBreak(true);

    baParagraphDetails.setAlignment(ParagraphAlignment.LEFT);
    baParagraphDetails.setBorderTop(Borders.SINGLE);
			/*baParagraphDetails.setBorderBottom(Borders.DOUBLE);
			baParagraphDetails.setBorderTop(Borders.DOUBLE);
			baParagraphDetails.setBorderRight(Borders.DOUBLE);
			baParagraphDetails.setBorderLeft(Borders.DOUBLE);*/
    baParagraphDetails.setVerticalAlignment(TextAlignment.CENTER);
    baParagraphDetails.setBorderBottom(Borders.SINGLE);

    XWPFRun baNameRunDetails = baParagraphDetails.createRun();

    //Writing Business Process Heading 3
    baNameRunDetails.setBold(true);
    baNameRunDetails.setText("Business Activities");
    baNameRunDetails.setFontFamily(CALIBRI);
    baNameRunDetails.setFontSize(13);

    for (BusinessActivity item : procedure.getAssetModel().getBusinessActivities()) {
      writeBusinessActivities(doc, item, procedure.getAssetModel(), project);
    }

    //Here we write all Malfunctions
    XWPFParagraph malParagraphDetails = doc.createParagraph();
    malParagraphDetails.setStyle("Heading3");
    //malParagraphDetails.setPageBreak(true);

    malParagraphDetails.setAlignment(ParagraphAlignment.LEFT);
    malParagraphDetails.setBorderTop(Borders.SINGLE);
			/*malParagraphDetails.setBorderBottom(Borders.DOUBLE);
			malParagraphDetails.setBorderTop(Borders.DOUBLE);
			malParagraphDetails.setBorderRight(Borders.DOUBLE);
			malParagraphDetails.setBorderLeft(Borders.DOUBLE);*/
    malParagraphDetails.setVerticalAlignment(TextAlignment.CENTER);
    malParagraphDetails.setBorderBottom(Borders.SINGLE);

    XWPFRun malNameRunDetails = malParagraphDetails.createRun();

    //Writing Malfunctions Heading 3
    malNameRunDetails.setBold(true);
    malNameRunDetails.setText("Malfunctions");
    malNameRunDetails.setFontFamily(CALIBRI);
    malNameRunDetails.setFontSize(13);
    for (Malfunction item : procedure.getAssetModel().getMalfunctions()) {
      writeMalfunction(doc, item, procedure.getAssetModel(), project);
    }

    //Here we write all Assets
    XWPFParagraph assetParagraphDetails = doc.createParagraph();
    assetParagraphDetails.setStyle("Heading3");
    //assetParagraphDetails.setPageBreak(true);

    assetParagraphDetails.setAlignment(ParagraphAlignment.LEFT);
    assetParagraphDetails.setBorderTop(Borders.SINGLE);
			/*assetParagraphDetails.setBorderBottom(Borders.DOUBLE);
			assetParagraphDetails.setBorderTop(Borders.DOUBLE);
			assetParagraphDetails.setBorderRight(Borders.DOUBLE);
			assetParagraphDetails.setBorderLeft(Borders.DOUBLE);*/
    assetParagraphDetails.setVerticalAlignment(TextAlignment.CENTER);
    assetParagraphDetails.setBorderBottom(Borders.SINGLE);

    XWPFRun assetNameRunDetails = assetParagraphDetails.createRun();

    //Writing Assets Heading 3
    assetNameRunDetails.setBold(true);
    assetNameRunDetails.setText("Assets");
    assetNameRunDetails.setFontFamily(CALIBRI);
    assetNameRunDetails.setFontSize(13);

    for (Asset item : procedure.getAssetModel().getAssets()) {
      writeAsset(doc, item, procedure.getAssetModel(), project);
    }
  }

  private void countNodes(Node node, int[] tableSize) {

    if (node.getChildren() == null) {
      return;
    }
    tableSize[0] = tableSize[0] + 1;
    for (Edge edge : node.getChildren()) {

      Node target = edge.getTarget();

      countNodes(target, tableSize);
    }

  }

  private void writeAssetModelTable(XWPFDocument doc, AssessmentProcedure procedure, AssessmentProject project) {

    int[] tableSize = new int[1];
    tableSize[0] = 0;
    for (Organization org : procedure.getAssetModel().getOrganizations()) {
      countNodes(org, tableSize);
    }


    XWPFTable table = doc.createTable(tableSize[0] + 1, 3);

    int[] cols = {7000, 1000, 1000};

    for (int i = 0; i < table.getNumberOfRows(); i++) {
      XWPFTableRow row = table.getRow(i);
      int numCells = row.getTableCells().size();
      for (int j = 0; j < numCells; j++) {
        XWPFTableCell cell = row.getCell(j);

        cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(cols[j]));
      }
    }

    table.getRow(0).setRepeatHeader(true);

    XWPFParagraph p1 = table.getRow(0).getCell(0).getParagraphs().get(0);
    p1.setAlignment(ParagraphAlignment.CENTER);

    XWPFRun r1 = p1.createRun();
    r1.setBold(true);
    r1.setFontSize(10);
    r1.setText("Element");

    XWPFParagraph p2 = table.getRow(0).getCell(1).getParagraphs().get(0);
    p2.setAlignment(ParagraphAlignment.CENTER);

    XWPFRun r2 = p2.createRun();
    r2.setBold(true);
    r2.setFontSize(10);
    r2.setText("Type");

    XWPFParagraph p3 = table.getRow(0).getCell(2).getParagraphs().get(0);
    p3.setAlignment(ParagraphAlignment.CENTER);

    XWPFRun r3 = p3.createRun();
    r3.setBold(true);
    r3.setFontSize(10);
    r3.setText("Owner");

    int[] position = new int[1];
    position[0] = 1;
    for (Organization org : procedure.getAssetModel().getOrganizations()) {
      writeNodesRows(doc, org, position, table, table.getRow(position[0]), project);
    }

  }

  private void writeNodesRows(XWPFDocument doc, Node node, int[] position, XWPFTable table, XWPFTableRow xwpfTableRow, AssessmentProject project) {

    if (node.getName() == null) {
      return;
    }

    XWPFParagraph p1 = xwpfTableRow.getCell(0).getParagraphs().get(0);

    p1.setAlignment(ParagraphAlignment.LEFT);

    XWPFRun r1 = p1.createRun();
    r1.setFontSize(9);
    if (node.getNodeType().equals(NodeTypeEnum.Organization)) {
      r1.setText(node.getName());
      r1.setBold(true);
    }
    if (node.getNodeType().equals(NodeTypeEnum.BusinessProcess)) {
      r1.setText("         " + node.getName());
      r1.setBold(true);
    }
    if (node.getNodeType().equals(NodeTypeEnum.BusinessActivity)) {
      r1.setText("                   " + node.getName());
      r1.setBold(true);
    }
    if (node.getNodeType().equals(NodeTypeEnum.Malfunction)) {
      r1.setText("                            " + node.getName());
    }
    if (node.getNodeType().equals(NodeTypeEnum.Asset)) {
      r1.setText("                                     " + node.getName());
    }

    r1.setFontFamily(CALIBRI);

    XWPFParagraph p2 = xwpfTableRow.getCell(1).getParagraphs().get(0);
    p2.setAlignment(ParagraphAlignment.CENTER);

    XWPFRun r2 = p2.createRun();
    r2.setBold(false);
    r2.setFontSize(9);


    r2.setText(node.getNodeType().toString());
    r2.setFontFamily(CALIBRI);

    XWPFParagraph p3 = xwpfTableRow.getCell(2).getParagraphs().get(0);
    p3.setAlignment(ParagraphAlignment.CENTER);

    XWPFRun r3 = p3.createRun();
    r3.setBold(false);
    r3.setFontSize(9);

    writeNodeOwnerTable(project, r3, node);

    position[0] = position[0] + 1;

    if (node.getChildren() == null) {
      return;
    }

    ArrayList<Node> nodesChildren = new ArrayList();

    for (Edge edge : node.getChildren()) {

      Node target = edge.getTarget();

      if (target.getName() != null) {
        nodesChildren.add(target);
      }
    }

    sortNodes(nodesChildren);

    for (Node target : nodesChildren) {
      writeNodesRows(doc, target, position, table, table.getRow(position[0]), project);
    }

  }

  private void writeNodeOwnerTable(AssessmentProject project, XWPFRun r3, Node node) {
    if (node.getSystemParticipantOwnerId() == null || node.getSystemParticipantOwnerId().equals("")) {
      r3.setText("");
      return;
    }

    for (SystemParticipant participant : project.getSystemProject().getParticipants()) {
      if ((participant.getName() + " " + participant.getSurname()).equals(node.getSystemParticipantOwnerId())) {
        r3.setText(participant.getName() + " " + participant.getSurname());
      }
    }

  }

  private void writeAsset(XWPFDocument doc, Asset item, AssetModel assetModel, AssessmentProject project) {

    XWPFParagraph assetHeading = doc.createParagraph();

    assetHeading.setStyle("Heading4");
    assetHeading.setPageBreak(false);

    assetHeading.setAlignment(ParagraphAlignment.LEFT);
        /*assetHeading.setBorderBottom(Borders.DOUBLE);
        assetHeading.setBorderTop(Borders.DOUBLE);
        assetHeading.setBorderRight(Borders.DOUBLE);
        assetHeading.setBorderLeft(Borders.DOUBLE);*/
    assetHeading.setVerticalAlignment(TextAlignment.CENTER);
    //assetHeading.setBorderBetween(Borders.SINGLE);

    XWPFRun assetHeadingRun = assetHeading.createRun();

    //Writing Asset Heading
    assetHeadingRun.setBold(true);
    assetHeadingRun.setText(item.getName());
    assetHeadingRun.setFontFamily(CALIBRI);
    assetHeadingRun.setFontSize(12);


    XWPFParagraph assetParagraph = doc.createParagraph();
		/*assetParagraph.setBorderBottom(Borders.DOUBLE);
		assetParagraph.setBorderTop(Borders.DOUBLE);
		assetParagraph.setBorderRight(Borders.DOUBLE);
		assetParagraph.setBorderLeft(Borders.DOUBLE);*/
    //assetParagraph.setBorderBetween(Borders.SINGLE);
    assetParagraph.setAlignment(ParagraphAlignment.BOTH);

    XWPFRun assetDetailsRun = assetParagraph.createRun();
    assetDetailsRun.setBold(false);

    //Writing Asset details
    writeNodeOwner(project, assetDetailsRun, item);

    assetDetailsRun.setText("Description: ");
    assetDetailsRun.setText(item.getDescription());
    assetDetailsRun.addCarriageReturn();

    assetDetailsRun.setText("Primary Asset Category: ");
    assetDetailsRun.setText(item.getPrimaryCategories().get(0).toString());
    assetDetailsRun.addCarriageReturn();

    assetDetailsRun.setText("Secondary Asset Category: ");
    assetDetailsRun.setText(item.getCategory().toString());
    assetDetailsRun.addCarriageReturn();

    for (SecurityImpact securityImpact : item.getSecurityImpacts()) {
      assetDetailsRun.setText("Security Impact Scope: ");
      assetDetailsRun.setText(securityImpact.getScope().toString());
      assetDetailsRun.addCarriageReturn();
      assetDetailsRun.setText("Security Impact: ");
      assetDetailsRun.setText(securityImpact.getImpact().toString());
      assetDetailsRun.addCarriageReturn();
    }

    assetDetailsRun.addCarriageReturn();

    //Writing Asset Malfunctions
    assetDetailsRun.setText("Related Malfunctions: ");
    for (String malfunctionId : item.getMalfunctionsIds()) {

      for (Malfunction malfunction : assetModel.getMalfunctions()) {
        if (malfunction.getIdentifier().equals(malfunctionId)) {
          assetDetailsRun.addCarriageReturn();
          assetDetailsRun.setText(malfunction.getName());
          //assetDetailsRun.setText("   ");
        }
      }

    }
    assetDetailsRun.addCarriageReturn();
    assetDetailsRun.addCarriageReturn();

    //Writing Asset parents
    assetDetailsRun.setText("Parents (Business Activities): ");
    for (org.crmf.model.riskassessmentelements.Edge edge : item.getParents()) {
      assetDetailsRun.setText(edge.getSource().getName());
      assetDetailsRun.setText("   ");
    }
    assetDetailsRun.addCarriageReturn();

  }

  private void writeNodeOwner(AssessmentProject project, XWPFRun run, Node item) {

    if (item.getSystemParticipantOwnerId() == null || item.getSystemParticipantOwnerId().equals("")) {
      return;
    }

    for (SystemParticipant participant : project.getSystemProject().getParticipants()) {

      if ((participant.getName() + " " + participant.getSurname()).equals(item.getSystemParticipantOwnerId())) {
        run.setText("Owner: ");
        run.setText(participant.getName() + " " + participant.getSurname());
        run.addCarriageReturn();
        run.setText("Owner Role: ");
        run.setText(participant.getRole());
        run.addCarriageReturn();

      }
    }
  }

  private void writeMalfunction(XWPFDocument doc, Malfunction item, AssetModel assetModel, AssessmentProject project) {
    XWPFParagraph malfunctionHeading = doc.createParagraph();

    malfunctionHeading.setStyle("Heading4");
    malfunctionHeading.setPageBreak(false);

    malfunctionHeading.setAlignment(ParagraphAlignment.LEFT);
        /*malfunctionHeading.setBorderBottom(Borders.DOUBLE);
        malfunctionHeading.setBorderTop(Borders.DOUBLE);
        malfunctionHeading.setBorderRight(Borders.DOUBLE);
        malfunctionHeading.setBorderLeft(Borders.DOUBLE);*/
    malfunctionHeading.setVerticalAlignment(TextAlignment.CENTER);
    //malfunctionHeading.setBorderBetween(Borders.SINGLE);

    XWPFRun malfunctionHeadingRun = malfunctionHeading.createRun();

    //Writing Malfunction Heading
    malfunctionHeadingRun.setBold(true);
    malfunctionHeadingRun.setText(item.getName());
    malfunctionHeadingRun.setFontFamily(CALIBRI);
    malfunctionHeadingRun.setFontSize(12);


    XWPFParagraph malfunctionParagraph = doc.createParagraph();
		/*malfunctionParagraph.setBorderBottom(Borders.DOUBLE);
		malfunctionParagraph.setBorderTop(Borders.DOUBLE);
		malfunctionParagraph.setBorderRight(Borders.DOUBLE);
		malfunctionParagraph.setBorderLeft(Borders.DOUBLE);*/
    //malfunctionParagraph.setBorderBetween(Borders.SINGLE);
    malfunctionParagraph.setAlignment(ParagraphAlignment.BOTH);

    XWPFRun malfunctionDetailsRun = malfunctionParagraph.createRun();

    writeNodeOwner(project, malfunctionDetailsRun, item);
    malfunctionDetailsRun.addCarriageReturn();

    //Writing Malfunction description
    malfunctionDetailsRun.setBold(false);

    malfunctionDetailsRun.setText("Functional description: ");
    malfunctionDetailsRun.setText(item.getFunctionalDescription());
    malfunctionDetailsRun.addCarriageReturn();

    malfunctionDetailsRun.setText("Functional consequence: ");
    malfunctionDetailsRun.setText(item.getFunctionalConsequence());
    malfunctionDetailsRun.addCarriageReturn();

    malfunctionDetailsRun.setText("Technical description: ");
    malfunctionDetailsRun.setText(item.getTechnicalDescription());
    malfunctionDetailsRun.addCarriageReturn();

    malfunctionDetailsRun.setText("Technical consequence: ");
    malfunctionDetailsRun.setText(item.getTechnicalConsequence());
    malfunctionDetailsRun.addCarriageReturn();
    malfunctionDetailsRun.addCarriageReturn();


    for (MalfunctionValueScale scale : item.getScales()) {
      malfunctionDetailsRun.setText("Impact Description: ");
      malfunctionDetailsRun.setText(scale.getDescription());
      malfunctionDetailsRun.addCarriageReturn();
      malfunctionDetailsRun.setText("Impact: ");
      malfunctionDetailsRun.setText(scale.getSeriousness().toString());
      malfunctionDetailsRun.addCarriageReturn();
    }

    malfunctionDetailsRun.setText("Security Dimension: ");

    for (TechnicalMalfunctionTypeEnum type : item.getTechnicalTypes()) {
      malfunctionDetailsRun.setText(type.toString());
      malfunctionDetailsRun.setText(" ");
    }
    malfunctionDetailsRun.addCarriageReturn();
    malfunctionDetailsRun.addCarriageReturn();

    //Writing Malfunction parents
    malfunctionDetailsRun.setText("Parents (Business Activities): ");
    for (org.crmf.model.riskassessmentelements.Edge edge : item.getParents()) {
      malfunctionDetailsRun.setText(edge.getSource().getName());
      malfunctionDetailsRun.setText("   ");
    }
    malfunctionDetailsRun.addCarriageReturn();
  }

  private void writeBusinessActivities(XWPFDocument doc, BusinessActivity item, AssetModel assetModel, AssessmentProject project) {
    XWPFParagraph baHeading = doc.createParagraph();

    baHeading.setStyle("Heading4");
    baHeading.setPageBreak(false);

    baHeading.setAlignment(ParagraphAlignment.LEFT);
		/*baHeading.setBorderBottom(Borders.DOUBLE);
		baHeading.setBorderTop(Borders.DOUBLE);
		baHeading.setBorderRight(Borders.DOUBLE);
		baHeading.setBorderLeft(Borders.DOUBLE);*/
    baHeading.setVerticalAlignment(TextAlignment.CENTER);
    //baHeading.setBorderBetween(Borders.SINGLE);

    XWPFRun baHeadingRun = baHeading.createRun();

    //Writing BA Heading
    baHeadingRun.setBold(true);
    baHeadingRun.setText(item.getName());
    baHeadingRun.setFontFamily(CALIBRI);
    baHeadingRun.setFontSize(12);

    XWPFParagraph activityParagraph = doc.createParagraph();
		/*activityParagraph.setBorderBottom(Borders.DOUBLE);
		activityParagraph.setBorderTop(Borders.DOUBLE);
		activityParagraph.setBorderRight(Borders.DOUBLE);
		activityParagraph.setBorderLeft(Borders.DOUBLE);*/
    //activityParagraph.setBorderBetween(Borders.SINGLE);
    activityParagraph.setAlignment(ParagraphAlignment.BOTH);


    XWPFRun activityDetailsRun = activityParagraph.createRun();

    writeNodeOwner(project, activityDetailsRun, item);

    //Writing BusinessActivity description
    activityDetailsRun.setBold(false);
    activityDetailsRun.setText("Description: ");
    activityDetailsRun.setText(item.getDescription());
    activityDetailsRun.addCarriageReturn();
    activityDetailsRun.addCarriageReturn();

    activityDetailsRun.setText("Goal: ");
    activityDetailsRun.setText(item.getGoal());
    activityDetailsRun.addCarriageReturn();
    activityDetailsRun.addCarriageReturn();

    //Writing BusinessActivity parents
    activityDetailsRun.setText("Parents (Business Processes): ");
    for (org.crmf.model.riskassessmentelements.Edge edge : item.getParents()) {
      activityDetailsRun.setText(edge.getSource().getName());
      activityDetailsRun.setText("   ");
    }
    activityDetailsRun.addCarriageReturn();

  }

  private void writeBusinessProcesses(XWPFDocument doc, BusinessProcess item, AssetModel assetModel, AssessmentProject project) {
    XWPFParagraph bpHeading = doc.createParagraph();

    bpHeading.setStyle("Heading4");
    bpHeading.setPageBreak(false);

    bpHeading.setAlignment(ParagraphAlignment.LEFT);
        /*bpHeading.setBorderBottom(Borders.DOUBLE);
        bpHeading.setBorderTop(Borders.DOUBLE);
        bpHeading.setBorderRight(Borders.DOUBLE);
        bpHeading.setBorderLeft(Borders.DOUBLE);*/
    bpHeading.setVerticalAlignment(TextAlignment.CENTER);
    //bpHeading.setBorderBetween(Borders.SINGLE);

    XWPFRun bpHeadingRun = bpHeading.createRun();

    //Writing BP Heading
    bpHeadingRun.setBold(true);
    bpHeadingRun.setText(item.getName());
    bpHeadingRun.setFontFamily(CALIBRI);
    bpHeadingRun.setFontSize(12);

    XWPFParagraph processParagraph = doc.createParagraph();
		/*processParagraph.setBorderBottom(Borders.DOUBLE);
		processParagraph.setBorderTop(Borders.DOUBLE);
		processParagraph.setBorderRight(Borders.DOUBLE);
		processParagraph.setBorderLeft(Borders.DOUBLE);*/
    //processParagraph.setBorderBetween(Borders.SINGLE);
    processParagraph.setAlignment(ParagraphAlignment.BOTH);

    XWPFRun processDetailsRun = processParagraph.createRun();

    writeNodeOwner(project, processDetailsRun, item);

    //Writing BusinessProcess description
    processDetailsRun.setBold(false);
    processDetailsRun.setText("Description: ");
    processDetailsRun.setText(item.getDescription());
    processDetailsRun.addCarriageReturn();
    processDetailsRun.addCarriageReturn();

    processDetailsRun.setText("Goal: ");
    processDetailsRun.setText(item.getGoal());
    processDetailsRun.addCarriageReturn();
    processDetailsRun.addCarriageReturn();

    //Writing BusinessProcess parents
    processDetailsRun.setText("Parents (Organizations): ");
    for (org.crmf.model.riskassessmentelements.Edge edge : item.getParents()) {
      processDetailsRun.setText(edge.getSource().getName());
      processDetailsRun.setText("   ");
    }
    processDetailsRun.addCarriageReturn();
  }

  private void writeOrganizations(XWPFDocument doc, Organization item, AssessmentProject project) {

    XWPFParagraph organizationHeading = doc.createParagraph();

    organizationHeading.setStyle("Heading4");
    organizationHeading.setPageBreak(false);

    organizationHeading.setAlignment(ParagraphAlignment.LEFT);
		/*organizationHeading.setBorderBottom(Borders.DOUBLE);
		organizationHeading.setBorderTop(Borders.DOUBLE);
		organizationHeading.setBorderRight(Borders.DOUBLE);
		organizationHeading.setBorderLeft(Borders.DOUBLE);*/
    organizationHeading.setVerticalAlignment(TextAlignment.CENTER);
    //organizationHeading.setBorderBetween(Borders.SINGLE);

    XWPFRun organizationHeadingRun = organizationHeading.createRun();

    //Writing Organization Heading
    organizationHeadingRun.setBold(true);
    organizationHeadingRun.setText(item.getName());
    organizationHeadingRun.setFontFamily(CALIBRI);
    organizationHeadingRun.setFontSize(12);

    XWPFParagraph organizationParagraph = doc.createParagraph();
		/*organizationParagraph.setBorderBottom(Borders.DOUBLE);
		organizationParagraph.setBorderTop(Borders.DOUBLE);
		organizationParagraph.setBorderRight(Borders.DOUBLE);
		organizationParagraph.setBorderLeft(Borders.DOUBLE);*/
    //organizationParagraph.setBorderBetween(Borders.SINGLE);
    organizationParagraph.setAlignment(ParagraphAlignment.BOTH);

    XWPFRun organizationDetailsRun = organizationParagraph.createRun();

    writeNodeOwner(project, organizationDetailsRun, item);

    //Writing Organization description
    organizationDetailsRun.setBold(false);
    organizationDetailsRun.setText("Description: ");
    organizationDetailsRun.setText(item.getDescription());
    organizationDetailsRun.addCarriageReturn();
    organizationDetailsRun.addCarriageReturn();

    organizationDetailsRun.setText("Goal: ");
    organizationDetailsRun.setText(item.getGoal());
    organizationDetailsRun.addCarriageReturn();

  }

  private void sortNodes(ArrayList<Node> nodes) {

//		//Sorting Nodes
//		Node[] nodeArray = nodes.toArray(new Node[nodes.size()]);
//		
//		Arrays.sort(nodeArray, new Comparator<Node>(){
//			@Override
//			public int compare(Node first, Node second){
//				
//				return first.getName().compareTo(second.getName());
//			}
//		});
//		nodes.clear();
//		nodes.addAll(Arrays.asList(nodeArray));

    Collections.sort(nodes, new NodeChainedComparator(
      new NodeTypeComparator(),
      new NodeNameComparator()
    ));
  }

  private void sortAssetModel(AssetModel assetModel) {
    //Sorting Organizations
    Organization[] organizationArray = assetModel.getOrganizations().toArray(new Organization[assetModel.getOrganizations().size()]);

    Arrays.sort(organizationArray, new Comparator<Organization>() {
      @Override
      public int compare(Organization first, Organization second) {
        return first.getName().compareTo(second.getName());
      }
    });
    assetModel.getOrganizations().clear();
    assetModel.getOrganizations().addAll(Arrays.asList(organizationArray));

    //Sorting Business Processes
    BusinessProcess[] bpArray = assetModel.getBusinessProcesses().toArray(new BusinessProcess[assetModel.getBusinessProcesses().size()]);

    Arrays.sort(bpArray, new Comparator<BusinessProcess>() {
      @Override
      public int compare(BusinessProcess first, BusinessProcess second) {
        return first.getName().compareTo(second.getName());
      }
    });
    assetModel.getBusinessProcesses().clear();
    assetModel.getBusinessProcesses().addAll(Arrays.asList(bpArray));

    //Sorting Business Activities
    BusinessActivity[] baArray = assetModel.getBusinessActivities().toArray(new BusinessActivity[assetModel.getBusinessActivities().size()]);

    Arrays.sort(baArray, new Comparator<BusinessActivity>() {
      @Override
      public int compare(BusinessActivity first, BusinessActivity second) {
        return first.getName().compareTo(second.getName());
      }
    });
    assetModel.getBusinessActivities().clear();
    assetModel.getBusinessActivities().addAll(Arrays.asList(baArray));

    //Sorting Malfunctions
    Malfunction[] malArray = assetModel.getMalfunctions().toArray(new Malfunction[assetModel.getMalfunctions().size()]);

    Arrays.sort(malArray, new Comparator<Malfunction>() {
      @Override
      public int compare(Malfunction first, Malfunction second) {
        return first.getName().compareTo(second.getName());
      }
    });
    assetModel.getMalfunctions().clear();
    assetModel.getMalfunctions().addAll(Arrays.asList(malArray));

    //Sorting Assets
    Asset[] assetArray = assetModel.getAssets().toArray(new Asset[assetModel.getAssets().size()]);

    Arrays.sort(assetArray, new Comparator<Asset>() {
      @Override
      public int compare(Asset first, Asset second) {
        return first.getName().compareTo(second.getName());
      }
    });
    assetModel.getAssets().clear();
    assetModel.getAssets().addAll(Arrays.asList(assetArray));

  }

  public class NodeChainedComparator implements Comparator<Node> {

    private List<Comparator<Node>> listComparators;

    @SafeVarargs
    public NodeChainedComparator(Comparator<Node>... comparators) {
      this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(Node node1, Node node2) {
      for (Comparator<Node> comparator : listComparators) {
        int result = comparator.compare(node1, node2);
        if (result != 0) {
          return result;
        }
      }
      return 0;
    }
  }

  public class NodeNameComparator implements Comparator<Node> {

    @Override
    public int compare(Node node1, Node node2) {
      return node1.getName().compareTo(node2.getName());
    }
  }

  public class NodeTypeComparator implements Comparator<Node> {

    @Override
    public int compare(Node node1, Node node2) {
      return -node1.getNodeType().toString().compareToIgnoreCase(node2.getNodeType().toString());
    }
  }

}


