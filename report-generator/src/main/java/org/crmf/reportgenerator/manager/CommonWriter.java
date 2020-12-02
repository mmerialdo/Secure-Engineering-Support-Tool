/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="CommonWriter.java"
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

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.crmf.model.audit.AnswerTypeEnum;
import org.crmf.model.audit.Audit;
import org.crmf.model.audit.ISOControl;
import org.crmf.model.audit.ISOControls;
import org.crmf.model.audit.Question;
import org.crmf.model.audit.Questionnaire;
import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssessmentStatusEnum;
import org.crmf.model.riskassessment.SystemParticipant;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessment.VulnerabilityModel;
import org.crmf.model.riskassessmentelements.Asset;
import org.crmf.model.riskassessmentelements.BusinessActivity;
import org.crmf.model.riskassessmentelements.BusinessProcess;
import org.crmf.model.riskassessmentelements.Consequence;
import org.crmf.model.riskassessmentelements.Edge;
import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.crmf.model.riskassessmentelements.Node;
import org.crmf.model.riskassessmentelements.Organization;
import org.crmf.model.riskassessmentelements.RiskScenario;
import org.crmf.model.riskassessmentelements.RiskTreatment;
import org.crmf.model.riskassessmentelements.Safeguard;
import org.crmf.model.riskassessmentelements.SafeguardScoreEnum;
import org.crmf.model.riskassessmentelements.ScenarioResultEnum;
import org.crmf.model.riskassessmentelements.SecurityImpact;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.riskassessmentelements.Vulnerability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//This class contains methods and utilities useful for all different types of reports
@Service
public class CommonWriter {
    private static final Logger LOG = LoggerFactory.getLogger(CommonWriter.class.getName());
    public static final String CALIBRI = "Calibri";

    public void writeProject(XWPFDocument doc, AssessmentProject project) {
        XWPFParagraph projectParagraph = doc.createParagraph();

        projectParagraph.setAlignment(ParagraphAlignment.CENTER);
		/*projectParagraph.setBorderBottom(Borders.DOUBLE);
		projectParagraph.setBorderTop(Borders.DOUBLE);
		projectParagraph.setBorderRight(Borders.DOUBLE);
		projectParagraph.setBorderLeft(Borders.DOUBLE);*/
        projectParagraph.setVerticalAlignment(TextAlignment.CENTER);

        XWPFRun projectNameRun = projectParagraph.createRun();

        //Writing Project name
        projectNameRun.setBold(true);
        projectNameRun.setText("Risk Assessment Project" + " " + project.getName());
        projectNameRun.setFontFamily(CALIBRI);
        projectNameRun.setFontSize(15);
        projectNameRun.addCarriageReturn();
        projectNameRun.addCarriageReturn();

        XWPFRun projectData = projectParagraph.createRun();

        //Writing Procedure Creation Time
        projectData.setText("Creation Time: ");
        projectData.setText(project.getCreationTime());
        projectData.addCarriageReturn();

        //Writing Procedure Last Update Time
        projectData.setText("Last Update Time: ");
        projectData.setText(project.getUpdateTime());
        projectData.addCarriageReturn();

        //Writing Project Project Manager
        projectData.setBold(false);
        projectData.setFontSize(13);
        projectData.setFontFamily(CALIBRI);
        projectData.setText("Project Manager: ");
        projectData.setText(project.getProjectManager().getName() + " " + project.getProjectManager().getSurname());
        projectData.addCarriageReturn();

    }

    public void writeProcedure(XWPFDocument doc, AssessmentProcedure procedure, AssessmentProject project) {
        XWPFParagraph procedureParagraph = doc.createParagraph();

        procedureParagraph.setAlignment(ParagraphAlignment.CENTER);
		/*procedureParagraph.setBorderBottom(Borders.DOUBLE);
		procedureParagraph.setBorderTop(Borders.DOUBLE);
		procedureParagraph.setBorderRight(Borders.DOUBLE);
		procedureParagraph.setBorderLeft(Borders.DOUBLE);*/
        procedureParagraph.setVerticalAlignment(TextAlignment.CENTER);

        XWPFRun procedureNameRun = procedureParagraph.createRun();

        //Writing Procedure name
        procedureNameRun.setBold(true);
        procedureNameRun.setText("Current Risk Assessment Procedure" + " " + procedure.getName());
        procedureNameRun.setFontFamily(CALIBRI);
        procedureNameRun.setFontSize(15);
        procedureNameRun.addCarriageReturn();
        procedureNameRun.addCarriageReturn();

        XWPFRun procedureData = procedureParagraph.createRun();

        //Writing Procedure Status
        procedureData.setBold(false);
        procedureData.setFontSize(13);
        procedureData.setFontFamily(CALIBRI);
        procedureData.setText("Status: ");
        procedureData.setText(procedure.getStatus().toString());
        procedureData.addCarriageReturn();

        //Writing Procedure Creation Time
        procedureData.setText("Creation Time: ");
        procedureData.setText(procedure.getCreationTime());
        procedureData.addCarriageReturn();

        //Writing Procedure Last Update Time
        procedureData.setText("Last Update Time: ");
        procedureData.setText(procedure.getUpdateTime());
        procedureData.addCarriageReturn();

    }

    public void writeMethodology(XWPFDocument doc) {
        XWPFParagraph methodologyParagraph = doc.createParagraph();
        methodologyParagraph.setStyle("Heading1");
        methodologyParagraph.setPageBreak(true);

        methodologyParagraph.setAlignment(ParagraphAlignment.LEFT);
		/*methodologyParagraph.setBorderBottom(Borders.DOUBLE);
		methodologyParagraph.setBorderTop(Borders.DOUBLE);
		methodologyParagraph.setBorderRight(Borders.DOUBLE);
		methodologyParagraph.setBorderLeft(Borders.DOUBLE);*/
        methodologyParagraph.setVerticalAlignment(TextAlignment.CENTER);
        //methodologyParagraph.setBorderBetween(Borders.SINGLE);

        XWPFRun methodologyNameRun = methodologyParagraph.createRun();

        //Writing some information about the methodology
        methodologyNameRun.setBold(true);
        methodologyNameRun.setText("Risk Assessment Methodology");
        methodologyNameRun.setFontFamily(CALIBRI);
        methodologyNameRun.setFontSize(15);

        XWPFParagraph methodologyDataParagraph = doc.createParagraph();
		/*methodologyDataParagraph.setBorderBottom(Borders.DOUBLE);
		methodologyDataParagraph.setBorderTop(Borders.DOUBLE);
		methodologyDataParagraph.setBorderRight(Borders.DOUBLE);
		methodologyDataParagraph.setBorderLeft(Borders.DOUBLE);
		methodologyDataParagraph.setBorderBetween(Borders.SINGLE);*/
        methodologyDataParagraph.setAlignment(ParagraphAlignment.BOTH);
        XWPFRun methodologyData = methodologyDataParagraph.createRun();

        methodologyData.addCarriageReturn();
        methodologyData.setText("Mehari (http://meharipedia.x10host.com/wp/) is a popular Open Source and free information risk assessment and management method developed by CLUSIF and applicable to all types and sizes of organizations.\n" +
                "Mehari‘s principles are documented in several documents available from CLUSIF pages and follow ISO 27005 and ISO 31000 guidelines. Also, Mehari provides many features allowing compliance to various requirements such as expressed by ISO 27001/27002.\n" +
                "Mehari is the Risk Assessment methodology implemented in the SEST tool.\n" +
                "Since Mehari provides a direct mapping between Mehari safeguards and ISO 27002 security controls. It is then possible to map the Risk Treatment results of this RA report to the Statement of Applicability document required by an ISO 27001 certification. \n");

        methodologyData.addCarriageReturn();

    }

    public void writeProceduresSummary(XWPFDocument doc, AssessmentProcedure lastProcedure, AssessmentProject project) {
        XWPFParagraph summaryParagraph = doc.createParagraph();
        summaryParagraph.setStyle("Heading1");
        summaryParagraph.setPageBreak(true);

        summaryParagraph.setAlignment(ParagraphAlignment.LEFT);
        summaryParagraph.setVerticalAlignment(TextAlignment.CENTER);

        XWPFRun summaryNameRun = summaryParagraph.createRun();

        summaryNameRun.setBold(true);
        summaryNameRun.setText("Risk Assessment Summary");
        summaryNameRun.setFontFamily(CALIBRI);
        summaryNameRun.setFontSize(15);

        int numberOfProcedures = project.getProcedures().size();

        XWPFTable table = doc.createTable(numberOfProcedures + 1, 13);

        table.getRow(0).setRepeatHeader(true);

        XWPFParagraph p1 = table.getRow(0).getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r1 = p1.createRun();
        r1.setBold(true);
        r1.setFontSize(10);
        r1.setFontFamily(CALIBRI);
        r1.setText("Procedure Status");

        XWPFParagraph p2 = table.getRow(0).getCell(1).getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r2 = p2.createRun();
        r2.setBold(true);
        r2.setFontSize(10);
        r2.setText("N. of Assets");
        r2.setFontFamily(CALIBRI);

        XWPFParagraph p3 = table.getRow(0).getCell(2).getParagraphs().get(0);
        p3.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r3 = p3.createRun();
        r3.setBold(true);
        r3.setFontSize(10);
        r3.setText("N. of BAs");
        r3.setFontFamily(CALIBRI);

        XWPFParagraph p4 = table.getRow(0).getCell(3).getParagraphs().get(0);
        p4.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r4 = p4.createRun();
        r4.setBold(true);
        r4.setFontSize(10);
        r4.setText("N. of Malf");
        r4.setFontFamily(CALIBRI);

        XWPFParagraph p5 = table.getRow(0).getCell(4).getParagraphs().get(0);
        p5.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r5 = p5.createRun();
        r5.setBold(true);
        r5.setFontSize(10);
        r5.setText("N. of Risk Scenarios");
        r5.setFontFamily(CALIBRI);

        XWPFParagraph p6 = table.getRow(0).getCell(5).getParagraphs().get(0);
        p6.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r6 = p6.createRun();
        r6.setBold(true);
        r6.setFontSize(10);
        r6.setText("N. of Risk Scenarios CRIT");
        r6.setFontFamily(CALIBRI);

        XWPFParagraph p7 = table.getRow(0).getCell(6).getParagraphs().get(0);
        p7.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r7 = p7.createRun();
        r7.setBold(true);
        r7.setFontSize(10);
        r7.setText("N. of Risk Scenarios HIGH");
        r7.setFontFamily(CALIBRI);

        XWPFParagraph p8 = table.getRow(0).getCell(7).getParagraphs().get(0);
        p8.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r8 = p8.createRun();
        r8.setBold(true);
        r8.setFontSize(10);
        r8.setText("N. of Risk Scenarios MEDIUM");
        r8.setFontFamily(CALIBRI);

        XWPFParagraph p9 = table.getRow(0).getCell(8).getParagraphs().get(0);
        p9.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r9 = p9.createRun();
        r9.setBold(true);
        r9.setFontSize(10);
        r9.setText("N. of Risk Scenarios LOW");
        r9.setFontFamily(CALIBRI);

        XWPFParagraph p10 = table.getRow(0).getCell(9).getParagraphs().get(0);
        p10.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r10 = p10.createRun();
        r10.setBold(true);
        r10.setFontSize(10);
        r10.setText("N. of Treated Scenarios CRIT");
        r10.setFontFamily(CALIBRI);


        XWPFParagraph p11 = table.getRow(0).getCell(10).getParagraphs().get(0);
        p11.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r11 = p11.createRun();
        r11.setBold(true);
        r11.setFontSize(10);
        r11.setText("N. of Treated Scenarios HIGH");
        r11.setFontFamily(CALIBRI);


        XWPFParagraph p12 = table.getRow(0).getCell(11).getParagraphs().get(0);
        p12.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r12 = p12.createRun();
        r12.setBold(true);
        r12.setFontSize(10);
        r12.setText("N. of Treated Scenarios MEDIUM");
        r12.setFontFamily(CALIBRI);

        XWPFParagraph p13 = table.getRow(0).getCell(12).getParagraphs().get(0);
        p12.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r13 = p13.createRun();
        r13.setBold(true);
        r13.setFontSize(10);
        r13.setText("N. of Treated Scenarios LOW");
        r13.setFontFamily(CALIBRI);


        writeProcedureRow(doc, table.getRow(1), lastProcedure);
        numberOfProcedures = 2;

        for (AssessmentProcedure procedure : project.getProcedures()) {
            if (procedure.getIdentifier().equals(lastProcedure.getIdentifier())) {
                continue;
            }

            writeProcedureRow(doc, table.getRow(numberOfProcedures), procedure);
            numberOfProcedures++;
        }
    }

    private void writeProcedureRow(XWPFDocument doc, XWPFTableRow xwpfTableRow, AssessmentProcedure procedure) {
        XWPFParagraph p1 = xwpfTableRow.getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r1 = p1.createRun();
        r1.setBold(false);
        r1.setFontSize(10);

        if (procedure.getStatus().equals(AssessmentStatusEnum.OnGoing)) {
            r1.setText("Current On Going");
        } else {
            r1.setText("Closed on " + procedure.getUpdateTime());
        }
        r1.setFontFamily(CALIBRI);

        XWPFParagraph p2 = xwpfTableRow.getCell(1).getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r2 = p2.createRun();
        r2.setBold(false);
        r2.setFontSize(10);
        r2.setText(String.valueOf(procedure.getAssetModel().getAssets().size()));
        r2.setFontFamily(CALIBRI);

        XWPFParagraph p3 = xwpfTableRow.getCell(2).getParagraphs().get(0);
        p3.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r3 = p3.createRun();
        r3.setBold(false);
        r3.setFontSize(10);
        r3.setText(String.valueOf(procedure.getAssetModel().getBusinessActivities().size()));
        r3.setFontFamily(CALIBRI);

        XWPFParagraph p4 = xwpfTableRow.getCell(3).getParagraphs().get(0);
        p4.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r4 = p4.createRun();
        r4.setBold(false);
        r4.setFontSize(10);
        r4.setText(String.valueOf(procedure.getAssetModel().getMalfunctions().size()));
        r4.setFontFamily(CALIBRI);

        XWPFParagraph p5 = xwpfTableRow.getCell(4).getParagraphs().get(0);
        p5.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r5 = p5.createRun();
        r5.setBold(false);
        r5.setFontSize(10);
        r5.setText(String.valueOf(procedure.getRiskModel().getScenarios().size()));
        r5.setFontFamily(CALIBRI);

        XWPFParagraph p6 = xwpfTableRow.getCell(5).getParagraphs().get(0);
        p6.setAlignment(ParagraphAlignment.CENTER);

        Predicate<RiskScenario> byCrit = scenario -> scenario.getCalculatedSeriousness() == ImpactEnum.CRITICAL;

        List<RiskScenario> critScenarios = procedure.getRiskModel().getScenarios().stream().filter(byCrit)
                .collect(Collectors.toList());

        XWPFRun r6 = p6.createRun();
        r6.setBold(false);
        r6.setFontSize(10);
        r6.setText(String.valueOf(critScenarios.size()));
        r6.setFontFamily(CALIBRI);

        XWPFParagraph p7 = xwpfTableRow.getCell(6).getParagraphs().get(0);
        p7.setAlignment(ParagraphAlignment.CENTER);

        Predicate<RiskScenario> byHigh = scenario -> scenario.getCalculatedSeriousness() == ImpactEnum.HIGH;

        List<RiskScenario> highScenarios = procedure.getRiskModel().getScenarios().stream().filter(byHigh)
                .collect(Collectors.toList());

        XWPFRun r7 = p7.createRun();
        r7.setBold(false);
        r7.setFontSize(10);
        r7.setText(String.valueOf(highScenarios.size()));
        r7.setFontFamily(CALIBRI);

        XWPFParagraph p8 = xwpfTableRow.getCell(7).getParagraphs().get(0);
        p8.setAlignment(ParagraphAlignment.CENTER);

        Predicate<RiskScenario> byMedium = scenario -> scenario.getCalculatedSeriousness() == ImpactEnum.MEDIUM;

        List<RiskScenario> mediumScenarios = procedure.getRiskModel().getScenarios().stream().filter(byMedium)
                .collect(Collectors.toList());

        XWPFRun r8 = p8.createRun();
        r8.setBold(false);
        r8.setFontSize(10);
        r8.setText(String.valueOf(mediumScenarios.size()));
        r8.setFontFamily(CALIBRI);

        XWPFParagraph p9 = xwpfTableRow.getCell(8).getParagraphs().get(0);
        p9.setAlignment(ParagraphAlignment.CENTER);

        Predicate<RiskScenario> byLow = scenario -> scenario.getCalculatedSeriousness() == ImpactEnum.LOW;

        List<RiskScenario> lowScenarios = procedure.getRiskModel().getScenarios().stream().filter(byLow)
                .collect(Collectors.toList());

        XWPFRun r9 = p9.createRun();
        r9.setBold(false);
        r9.setFontSize(10);
        r9.setText(String.valueOf(lowScenarios.size()));
        r9.setFontFamily(CALIBRI);


        XWPFParagraph p10 = xwpfTableRow.getCell(9).getParagraphs().get(0);
        p10.setAlignment(ParagraphAlignment.CENTER);

        Predicate<RiskTreatment> byCritTreat = scenario -> scenario.getResultingSeriousness() == ImpactEnum.CRITICAL;

        List<RiskTreatment> critTreatedScenarios = procedure.getRiskTreatmentModel().getRiskTreatments().stream().filter(byCritTreat)
                .collect(Collectors.toList());

        XWPFRun r10 = p10.createRun();
        r10.setBold(false);
        r10.setFontSize(10);
        r10.setText(String.valueOf(critTreatedScenarios.size()));
        r10.setFontFamily(CALIBRI);


        XWPFParagraph p11 = xwpfTableRow.getCell(10).getParagraphs().get(0);
        p11.setAlignment(ParagraphAlignment.CENTER);

        Predicate<RiskTreatment> byHighTreat = scenario -> scenario.getResultingSeriousness() == ImpactEnum.HIGH;

        List<RiskTreatment> highTreatedScenarios = procedure.getRiskTreatmentModel().getRiskTreatments().stream().filter(byHighTreat)
                .collect(Collectors.toList());

        XWPFRun r11 = p11.createRun();
        r11.setBold(false);
        r11.setFontSize(10);
        r11.setText(String.valueOf(highTreatedScenarios.size()));
        r11.setFontFamily(CALIBRI);


        XWPFParagraph p12 = xwpfTableRow.getCell(11).getParagraphs().get(0);
        p12.setAlignment(ParagraphAlignment.CENTER);

        Predicate<RiskTreatment> byMediumTreat = scenario -> scenario.getResultingSeriousness() == ImpactEnum.MEDIUM;

        List<RiskTreatment> mediumTreatedScenarios = procedure.getRiskTreatmentModel().getRiskTreatments().stream().filter(byMediumTreat)
                .collect(Collectors.toList());

        XWPFRun r12 = p12.createRun();
        r12.setBold(false);
        r12.setFontSize(10);
        r12.setText(String.valueOf(mediumTreatedScenarios.size()));
        r12.setFontFamily(CALIBRI);

        XWPFParagraph p13 = xwpfTableRow.getCell(12).getParagraphs().get(0);
        p12.setAlignment(ParagraphAlignment.CENTER);

        Predicate<RiskTreatment> byLowTreat = scenario -> scenario.getResultingSeriousness() == ImpactEnum.LOW;

        List<RiskTreatment> lowTreatedScenarios = procedure.getRiskTreatmentModel().getRiskTreatments().stream().filter(byLowTreat)
                .collect(Collectors.toList());

        XWPFRun r13 = p13.createRun();
        r13.setBold(false);
        r13.setFontSize(10);
        r13.setText(String.valueOf(lowTreatedScenarios.size()));
        r13.setFontFamily(CALIBRI);
    }

    public void writeRiskModel(XWPFDocument doc, AssessmentProcedure procedure, AssessmentProject project) {
        XWPFParagraph riskModelParagraph = doc.createParagraph();

        riskModelParagraph.setStyle("Heading1");
        riskModelParagraph.setPageBreak(true);

        riskModelParagraph.setAlignment(ParagraphAlignment.LEFT);
		/*riskModelParagraph.setBorderBottom(Borders.DOUBLE);
		riskModelParagraph.setBorderTop(Borders.DOUBLE);
		riskModelParagraph.setBorderRight(Borders.DOUBLE);
		riskModelParagraph.setBorderLeft(Borders.DOUBLE);*/
        riskModelParagraph.setVerticalAlignment(TextAlignment.CENTER);
        //riskModelParagraph.setBorderBetween(Borders.SINGLE);

        XWPFRun riskModelNameRun = riskModelParagraph.createRun();

        //Writing RiskModel name
        riskModelNameRun.setBold(true);
        riskModelNameRun.setText("Risk Scenarios Assessment");
        riskModelNameRun.setFontFamily(CALIBRI);
        riskModelNameRun.setFontSize(15);

        XWPFParagraph riskModelDataParagraph = doc.createParagraph();
		/*riskModelDataParagraph.setBorderBottom(Borders.DOUBLE);
		riskModelDataParagraph.setBorderTop(Borders.DOUBLE);
		riskModelDataParagraph.setBorderRight(Borders.DOUBLE);
		riskModelDataParagraph.setBorderLeft(Borders.DOUBLE);
		riskModelDataParagraph.setBorderBetween(Borders.SINGLE);*/
        XWPFRun riskModelData = riskModelDataParagraph.createRun();

        //Writing RiskModel Creation Time
        riskModelData.setBold(false);
        riskModelData.setFontSize(13);
        riskModelData.setFontFamily(CALIBRI);
        riskModelData.setText("Creation Time: ");
        riskModelData.setText(procedure.getRiskModel().getCreationTime());
        riskModelData.addCarriageReturn();

        //Writing RiskModel Last Update Time
        riskModelData.setText("Last Update Time: ");
        riskModelData.setText(procedure.getRiskModel().getUpdateTime());
        riskModelData.addCarriageReturn();

        //We need to check how many complete Scenario we have (a complete Risk Scenario has all values, including Calculated Seriousness)
        int numberOfScenario = 0;
        ArrayList<RiskScenario> completeScenarios = new ArrayList<>();

        for (RiskScenario scenario : procedure.getRiskModel().getScenarios()) {
            if (scenario.getCalculatedSeriousness() == null) {
                continue;
            } else {
                completeScenarios.add(scenario);
                numberOfScenario++;
            }
        }


        sortScenarioSeriousness(completeScenarios);

        XWPFTable table = doc.createTable(numberOfScenario + 1, 10);

        table.getRow(0).setRepeatHeader(true);
        table.getRow(0).getCell(0).setText("Scenario Description");
        table.getRow(0).getCell(1).setText("Class");
        table.getRow(0).getCell(2).setText("Scope");
        table.getRow(0).getCell(3).setText("Asset Name");
        table.getRow(0).getCell(4).setText("Vulnerability Id");
        table.getRow(0).getCell(5).setText("Threat Id");
        table.getRow(0).getCell(6).setText("Calculated Seriousness");
        table.getRow(0).getCell(7).setText("Result");
        table.getRow(0).getCell(8).setText("Excluded");
        table.getRow(0).getCell(9).setText("Related Safeguard Ids");

        //Here we write all Risk Scenarios
        numberOfScenario = 1;
        for (int index = completeScenarios.size() - 1; index >= 0; index--) {
            RiskScenario scenario = completeScenarios.get(index);
            if (scenario.getCalculatedSeriousness() == null) {
                continue;
            } else {
                writeRiskScenario(doc, scenario, procedure, table.getRow(numberOfScenario));
                numberOfScenario++;
            }
        }

    }

    private void writeRiskScenario(XWPFDocument doc, RiskScenario scenario, AssessmentProcedure procedure, XWPFTableRow xwpfTableRow) {

        XWPFParagraph p1 = xwpfTableRow.getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r1 = p1.createRun();
        r1.setBold(false);
        r1.setFontSize(10);
        r1.setText(scenario.getDescription());
        r1.setFontFamily(CALIBRI);

        XWPFParagraph p2 = xwpfTableRow.getCell(1).getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r2 = p2.createRun();
        r2.setBold(false);
        r2.setFontSize(10);
        r2.setText(scenario.getThreatClass().toString());
        r2.setFontFamily(CALIBRI);

        XWPFParagraph p3 = xwpfTableRow.getCell(2).getParagraphs().get(0);
        p3.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r3 = p3.createRun();
        r3.setBold(false);
        r3.setFontSize(10);
        r3.setText(scenario.getImpactScope().toString());
        r3.setFontFamily(CALIBRI);

        String assetName = "";

        for (Asset asset : procedure.getAssetModel().getAssets()) {
            if (asset.getIdentifier().equals(scenario.getAssetId())) {
                assetName = asset.getName();
            }
        }

        XWPFParagraph p4 = xwpfTableRow.getCell(3).getParagraphs().get(0);
        p4.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r4 = p4.createRun();
        r4.setBold(false);
        r4.setFontSize(10);
        r4.setText(assetName);
        r4.setFontFamily(CALIBRI);

        String vulnerabilityName = "";

        for (Vulnerability vulnerability : procedure.getVulnerabilityModel().getVulnerabilities()) {
            if (vulnerability.getIdentifier().equals(scenario.getVulnerabilityId())) {
                vulnerabilityName = vulnerability.getCatalogueId();
            }
        }

        XWPFParagraph p5 = xwpfTableRow.getCell(4).getParagraphs().get(0);
        p5.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r5 = p5.createRun();
        r5.setBold(false);
        r5.setFontSize(10);
        r5.setText(vulnerabilityName);
        r5.setFontFamily(CALIBRI);

        String threatName = "";

        for (Threat threat : procedure.getThreatModel().getThreats()) {
            if (threat.getIdentifier().equals(scenario.getThreatId())) {
                threatName = threat.getCatalogueId();
            }
        }

        XWPFParagraph p6 = xwpfTableRow.getCell(5).getParagraphs().get(0);
        p6.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r6 = p6.createRun();
        r6.setBold(false);
        r6.setFontSize(10);
        r6.setText(threatName);
        r6.setFontFamily(CALIBRI);

        XWPFParagraph p7 = xwpfTableRow.getCell(6).getParagraphs().get(0);
        p7.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r7 = p7.createRun();
        r7.setBold(true);
        r7.setFontSize(10);
        r7.setText(scenario.getCalculatedSeriousness().toString());
        r7.setFontFamily(CALIBRI);

        XWPFParagraph p8 = xwpfTableRow.getCell(7).getParagraphs().get(0);
        p8.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r8 = p8.createRun();
        r8.setBold(false);
        r8.setFontSize(10);
        r8.setText(scenario.getScenarioResult().toString());
        r8.setFontFamily(CALIBRI);

        XWPFParagraph p9 = xwpfTableRow.getCell(8).getParagraphs().get(0);
        p9.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r9 = p9.createRun();
        r9.setBold(false);
        r9.setFontSize(10);
        r9.setText(String.valueOf(scenario.isExcluded()));
        r9.setFontFamily(CALIBRI);

        String safeguardIds = "";

        for (String safeguardId : scenario.getSafeguardIds()) {

            for (Safeguard safeguard : procedure.getSafeguardModel().getSafeguards()) {

                if (safeguard.getIdentifier().equals(safeguardId)) {
                    if (safeguardIds.equals("")) {
                        safeguardIds += safeguard.getCatalogueId();
                    } else {
                        safeguardIds += ", ";
                        safeguardIds += safeguard.getCatalogueId();
                    }

                }
            }


        }

        XWPFParagraph p10 = xwpfTableRow.getCell(9).getParagraphs().get(0);
        p10.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r10 = p10.createRun();
        r10.setBold(false);
        r10.setFontSize(10);
        r10.setText(safeguardIds);
        r10.setFontFamily(CALIBRI);
    }

    public void writeRiskTreatmentModel(XWPFDocument doc, AssessmentProcedure procedure, AssessmentProject project, ImpactEnum threshold) {
        HashMap<String, RiskTreatment> treatmentMap = getRiskTreatmentHashMap(procedure.getRiskTreatmentModel().getRiskTreatments());
        HashMap<String, Safeguard> safeguardsTreated = getSafeguardHashMap(procedure.getRiskTreatmentModel().getResultingSafeguards());
        HashMap<String, Safeguard> safeguards = getSafeguardHashMap(procedure.getSafeguardModel().getSafeguards());

        XWPFParagraph riskTreatmentModelParagraph = doc.createParagraph();

        riskTreatmentModelParagraph.setStyle("Heading1");
        riskTreatmentModelParagraph.setPageBreak(true);

        riskTreatmentModelParagraph.setAlignment(ParagraphAlignment.LEFT);
        riskTreatmentModelParagraph.setVerticalAlignment(TextAlignment.CENTER);

        XWPFRun riskTreatmentModelNameRun = riskTreatmentModelParagraph.createRun();

        //Writing RiskTreatmentModel name
        riskTreatmentModelNameRun.setBold(true);
        riskTreatmentModelNameRun.setText("Risk Treatment Assessment");
        riskTreatmentModelNameRun.setFontFamily(CALIBRI);
        riskTreatmentModelNameRun.setFontSize(15);

        XWPFParagraph riskTreatmentModelDataParagraph = doc.createParagraph();
        XWPFRun riskTreatmentModelData = riskTreatmentModelDataParagraph.createRun();

        //Writing RiskTreatmentModel Creation Time
        riskTreatmentModelData.setBold(false);
        riskTreatmentModelData.setFontSize(13);
        riskTreatmentModelData.setFontFamily(CALIBRI);
        riskTreatmentModelData.setText("Creation Time: ");
        riskTreatmentModelData.setText(procedure.getRiskTreatmentModel().getCreationTime());
        riskTreatmentModelData.addCarriageReturn();

        //Writing RiskTreatmentModel Last Update Time
        riskTreatmentModelData.setText("Last Update Time: ");
        riskTreatmentModelData.setText(procedure.getRiskTreatmentModel().getUpdateTime());
        riskTreatmentModelData.addCarriageReturn();

        //We need to check how many reduced Scenario we have (a reduced Risk Scenario has all values, including Calculated Seriousness. In
        //addition, a reduced Scenario has been chosen for reduction, and it is not Excluded)
        int numberOfScenario = 0;
        ArrayList<RiskScenario> completeScenarios = new ArrayList<>();

        for (RiskScenario scenario : procedure.getRiskModel().getScenarios()) {
            if (scenario.getCalculatedSeriousness() == null || scenario.isExcluded() ||
                    !scenario.getScenarioResult().equals(ScenarioResultEnum.Reduce)) {
                continue;
            } else {
                if (checkScenarioThreshold(scenario, threshold, treatmentMap)) {
                    completeScenarios.add(scenario);
                    numberOfScenario++;
                }
            }
        }

        sortScenarioSeriousness(completeScenarios);

        XWPFTable table = doc.createTable(numberOfScenario + 1, 15);

        table.getRow(0).setRepeatHeader(true);
        XWPFParagraph p1 = table.getRow(0).getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r1 = p1.createRun();
        r1.setBold(false);
        r1.setFontSize(10);
        r1.setFontFamily(CALIBRI);
        r1.setText("Scenario/Risk Description");

        XWPFParagraph p2 = table.getRow(0).getCell(1).getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r2 = p2.createRun();
        r2.setBold(false);
        r2.setFontSize(10);
        r2.setText("Risk Scope");
        r2.setFontFamily(CALIBRI);

        XWPFParagraph p3 = table.getRow(0).getCell(2).getParagraphs().get(0);
        p3.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r3 = p3.createRun();
        r3.setBold(false);
        r3.setFontSize(10);
        r3.setText("Asset Name");
        r3.setFontFamily(CALIBRI);

        XWPFParagraph p4 = table.getRow(0).getCell(3).getParagraphs().get(0);
        p4.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r4 = p4.createRun();
        r4.setBold(false);
        r4.setFontSize(10);
        r4.setText("Asset Owner");
        r4.setFontFamily(CALIBRI);

        XWPFParagraph p5 = table.getRow(0).getCell(4).getParagraphs().get(0);
        p5.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r5 = p5.createRun();
        r5.setBold(false);
        r5.setFontSize(10);
        r5.setText("Business Activities");
        r5.setFontFamily(CALIBRI);

        XWPFParagraph p6 = table.getRow(0).getCell(5).getParagraphs().get(0);
        p6.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r6 = p6.createRun();
        r6.setBold(false);
        r6.setFontSize(10);
        r6.setText("Business Processes");
        r6.setFontFamily(CALIBRI);

        XWPFParagraph p7 = table.getRow(0).getCell(6).getParagraphs().get(0);
        p7.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r7 = p7.createRun();
        r7.setBold(false);
        r7.setFontSize(10);
        r7.setText("Organizations");
        r7.setFontFamily(CALIBRI);

        XWPFParagraph p8 = table.getRow(0).getCell(7).getParagraphs().get(0);
        p8.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r8 = p8.createRun();
        r8.setBold(false);
        r8.setFontSize(10);
        r8.setText("Impact");
        r8.setFontFamily(CALIBRI);

        XWPFParagraph p9 = table.getRow(0).getCell(8).getParagraphs().get(0);
        p9.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r9 = p9.createRun();
        r9.setBold(false);
        r9.setFontSize(10);
        r9.setText("Likelihood");
        r9.setFontFamily(CALIBRI);

        XWPFParagraph p10 = table.getRow(0).getCell(9).getParagraphs().get(0);
        p10.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r10 = p10.createRun();
        r10.setBold(false);
        r10.setFontSize(10);
        r10.setText("Vulnerability Descr");
        r10.setFontFamily(CALIBRI);

        XWPFParagraph p11 = table.getRow(0).getCell(10).getParagraphs().get(0);
        p11.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r11 = p11.createRun();
        r11.setBold(false);
        r11.setFontSize(10);
        r11.setText("Threat Descr");
        r11.setFontFamily(CALIBRI);

        XWPFParagraph p12 = table.getRow(0).getCell(11).getParagraphs().get(0);
        p12.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r12 = p12.createRun();
        r12.setBold(false);
        r12.setFontSize(10);
        r12.setText("Risk Seriousness");
        r12.setFontFamily(CALIBRI);


        XWPFParagraph p13 = table.getRow(0).getCell(12).getParagraphs().get(0);
        p13.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r13 = p13.createRun();
        r13.setBold(false);
        r13.setFontSize(10);
        r13.setText("Possible Safeguards");
        r13.setFontFamily(CALIBRI);


        XWPFParagraph p14 = table.getRow(0).getCell(13).getParagraphs().get(0);
        p14.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r14 = p14.createRun();
        r14.setBold(false);
        r14.setFontSize(10);
        r14.setText("Selected Safeguards");
        r14.setFontFamily(CALIBRI);

        XWPFParagraph p15 = table.getRow(0).getCell(14).getParagraphs().get(0);
        p15.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r15 = p15.createRun();
        r15.setBold(true);
        r15.setFontSize(10);
        r15.setText("Residual Risk Seriousness");
        r15.setFontFamily(CALIBRI);

        //Here we write all Risk Scenarios with their Treatment
        numberOfScenario = 1;
        for (int index = completeScenarios.size() - 1; index >= 0; index--) {
            RiskScenario scenario = completeScenarios.get(index);
            writeRiskScenarioTreated(doc, scenario, project, procedure, table.getRow(numberOfScenario), treatmentMap, safeguardsTreated, safeguards);
            numberOfScenario++;
        }
    }

    public void writeRiskScenarioTreated(XWPFDocument doc, RiskScenario scenario, AssessmentProject project, AssessmentProcedure procedure,
                                         XWPFTableRow xwpfTableRow, HashMap<String, RiskTreatment> treatmentMap, HashMap<String, Safeguard> safeguardsTreated, HashMap<String, Safeguard> safeguards) {
        XWPFParagraph p1 = xwpfTableRow.getCell(0).getParagraphs().get(0);

        XWPFRun r1 = p1.createRun();
        r1.setBold(false);
        r1.setFontSize(8);
        r1.setText(scenario.getDescription());
        r1.setFontFamily(CALIBRI);

        XWPFParagraph p2 = xwpfTableRow.getCell(1).getParagraphs().get(0);

        XWPFRun r2 = p2.createRun();
        r2.setBold(false);
        r2.setFontSize(8);
        r2.setText(scenario.getImpactScope().toString());
        r2.setFontFamily(CALIBRI);

        String assetName = "";
        String assetOwner = "";
        String businessActivities = "";
        String businessProcesses = "";
        String organizations = "";

        Asset asset = getAsset(scenario, procedure);

        if (asset != null) {
            assetName = asset.getName();
            assetOwner = getNodeOwner(project, asset);
            businessActivities = getBusinessActivitiesAssetString(procedure, asset);
            businessProcesses = getBusinessProcessesAssetString(procedure, asset);
            organizations = getOrganizationsAssetString(procedure, asset);
        }

        XWPFParagraph p3 = xwpfTableRow.getCell(2).getParagraphs().get(0);

        XWPFRun r3 = p3.createRun();
        r3.setBold(false);
        r3.setFontSize(8);
        r3.setText(assetName);
        r3.setFontFamily(CALIBRI);

        XWPFParagraph p4 = xwpfTableRow.getCell(3).getParagraphs().get(0);

        XWPFRun r4 = p4.createRun();
        r4.setBold(false);
        r4.setFontSize(8);
        r4.setText(assetOwner);
        r4.setFontFamily(CALIBRI);

        XWPFParagraph p5 = xwpfTableRow.getCell(4).getParagraphs().get(0);

        XWPFRun r5 = p5.createRun();
        r5.setBold(false);
        r5.setFontSize(8);
        r5.setText(businessActivities);
        r5.setFontFamily(CALIBRI);

        XWPFParagraph p6 = xwpfTableRow.getCell(5).getParagraphs().get(0);

        XWPFRun r6 = p6.createRun();
        r6.setBold(false);
        r6.setFontSize(8);
        r6.setText(businessProcesses);
        r6.setFontFamily(CALIBRI);

        XWPFParagraph p7 = xwpfTableRow.getCell(6).getParagraphs().get(0);

        XWPFRun r7 = p7.createRun();
        r7.setBold(false);
        r7.setFontSize(8);
        r7.setText(organizations);
        r7.setFontFamily(CALIBRI);

        XWPFParagraph p8 = xwpfTableRow.getCell(7).getParagraphs().get(0);

        XWPFRun r8 = p8.createRun();
        r8.setBold(false);
        r8.setFontSize(8);
        if (scenario.getExpertImpact() != null) {
            r8.setText(scenario.getExpertImpact().toString());
        } else {
            r8.setText(scenario.getCalculatedImpact().toString());
        }
        r8.setFontFamily(CALIBRI);

        XWPFParagraph p9 = xwpfTableRow.getCell(8).getParagraphs().get(0);

        XWPFRun r9 = p9.createRun();
        r9.setBold(false);
        r9.setFontSize(8);
        if (scenario.getExpertLikelihood() != null) {
            r9.setText(scenario.getExpertLikelihood().toString());
        } else {
            r9.setText(scenario.getCalculatedLikelihood().toString());
        }
        r9.setFontFamily(CALIBRI);

        String vulnerabilityName = "";

        for (Vulnerability vulnerability : procedure.getVulnerabilityModel().getVulnerabilities()) {
            if (vulnerability.getIdentifier().equals(scenario.getVulnerabilityId())) {
                vulnerabilityName = vulnerability.getDescription();
            }
        }

        XWPFParagraph p10 = xwpfTableRow.getCell(9).getParagraphs().get(0);

        XWPFRun r10 = p10.createRun();
        r10.setBold(false);
        r10.setFontSize(8);
        r10.setText(vulnerabilityName);
        r10.setFontFamily(CALIBRI);

        String threatName = "";

        for (Threat threat : procedure.getThreatModel().getThreats()) {
            if (threat.getIdentifier().equals(scenario.getThreatId())) {
                threatName = threat.getDescription();
            }
        }

        XWPFParagraph p11 = xwpfTableRow.getCell(10).getParagraphs().get(0);

        XWPFRun r11 = p11.createRun();
        r11.setBold(false);
        r11.setFontSize(8);
        r11.setText(threatName);
        r11.setFontFamily(CALIBRI);

        XWPFParagraph p12 = xwpfTableRow.getCell(11).getParagraphs().get(0);
        p12.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r12 = p12.createRun();
        r12.setBold(true);
        r12.setFontSize(10);
        r12.setText(scenario.getCalculatedSeriousness().toString());
        r12.setFontFamily(CALIBRI);

        String safeguardIds = "";

        for (String safeguardId : scenario.getSafeguardIds()) {

            for (Safeguard safeguard : procedure.getSafeguardModel().getSafeguards()) {

                if (safeguard.getIdentifier().equals(safeguardId)) {
                    if (safeguardIds.equals("")) {
                        safeguardIds += safeguard.getCatalogueId();
                    } else {
                        safeguardIds += ", ";
                        safeguardIds += safeguard.getCatalogueId();
                    }
                }
            }
        }

        XWPFParagraph p13 = xwpfTableRow.getCell(12).getParagraphs().get(0);
        p13.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r13 = p13.createRun();
        r13.setBold(false);
        r13.setFontSize(8);
        r13.setText(safeguardIds);
        r13.setFontFamily(CALIBRI);

        String safeguardTreatedIds = "";

        for (String safeguardId : scenario.getSafeguardIds()) {
            Safeguard safeguardTreated = safeguardsTreated.get(safeguardId);
            Safeguard safeguard = safeguards.get(safeguardId);

            if (safeguardTreated != null && safeguard != null) {

                if (!safeguard.getScore().equals(safeguardTreated.getScore())) {
                    if (safeguardTreatedIds.equals("")) {
                        safeguardTreatedIds += safeguardTreated.getCatalogueId();
                    } else {
                        safeguardTreatedIds += ", ";
                        safeguardTreatedIds += safeguardTreated.getCatalogueId();
                    }
                }

            }
        }

        XWPFParagraph p14 = xwpfTableRow.getCell(13).getParagraphs().get(0);
        p14.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r14 = p14.createRun();
        r14.setBold(false);
        r14.setFontSize(8);
        r14.setText(safeguardTreatedIds);
        r14.setFontFamily(CALIBRI);

        RiskTreatment treatment = treatmentMap.get(scenario.getIdentifier());

        XWPFParagraph p15 = xwpfTableRow.getCell(14).getParagraphs().get(0);

        XWPFRun r15 = p15.createRun();
        r15.setBold(true);
        r15.setFontSize(10);
        r15.setText(treatment.getResultingSeriousness().toString());
        r15.setFontFamily(CALIBRI);

    }

    public void writeSafeguardModel(XWPFDocument doc, AssessmentProcedure procedure, AssessmentProject project) {
        XWPFParagraph safeguardModelParagraph = doc.createParagraph();
        safeguardModelParagraph.setStyle("Heading1");
        safeguardModelParagraph.setPageBreak(true);

        safeguardModelParagraph.setAlignment(ParagraphAlignment.LEFT);
        safeguardModelParagraph.setVerticalAlignment(TextAlignment.CENTER);

        XWPFRun riskTreatmentModelNameRun = safeguardModelParagraph.createRun();

        //Writing RiskTreatmentModel name
        riskTreatmentModelNameRun.setBold(true);
        riskTreatmentModelNameRun.setText("Risk Treatment Assessment Safeguards");
        riskTreatmentModelNameRun.setFontFamily(CALIBRI);
        riskTreatmentModelNameRun.setFontSize(15);

        XWPFParagraph safeguardModelDataParagraph = doc.createParagraph();
        safeguardModelDataParagraph.setAlignment(ParagraphAlignment.BOTH);
        XWPFRun riskTreatmentModelData = safeguardModelDataParagraph.createRun();

        //Writing RiskTreatmentModel Creation Time
        riskTreatmentModelData.setBold(false);
        riskTreatmentModelData.setFontSize(13);
        riskTreatmentModelData.setFontFamily(CALIBRI);
        riskTreatmentModelData.setText("Creation Time: ");
        riskTreatmentModelData.setText(procedure.getRiskTreatmentModel().getCreationTime());
        riskTreatmentModelData.addCarriageReturn();

        //Writing RiskTreatmentModel Last Update Time
        riskTreatmentModelData.setText("Last Update Time: ");
        riskTreatmentModelData.setText(procedure.getRiskTreatmentModel().getUpdateTime());
        riskTreatmentModelData.addCarriageReturn();
        riskTreatmentModelData.addCarriageReturn();
        riskTreatmentModelData.setText("The following table summarizes the security controls (safeguards) scores resulting from the performed survey (Column 'Safeguard Score from the survey' shows the safeguard effectiveness score as it has been calculated from the survey over the current infrastructure) and the security controls scores which should be reached in order to reduce the risks to an acceptable level (Column 'Resulting Safeguard Score after Risk Treatment' hence shows the safeguards effectiveness scores to be implemented/upgraded in order to reduce the risks scenarios listed in Table ‘Risk Scenarios Assessment’ to the desirable levels shown in Table ‘Risk Treatment Assessment’). Please note that some safeguards may only need an upgrade (if the score from the survey was higher than LOW), while other safeguards may need to be fully implemented. Please note that safeguards listed in the table can be easily traced to corresponding ISO 27002 security controls.");

        riskTreatmentModelData.addCarriageReturn();

        writeSafeguardsTreatment(doc, procedure);

        //XWPFParagraph requirementsParagraph = doc.createParagraph();

        writeRequirementsTreatment(doc, procedure);
    }

    public void writeRequirementsTreatment(XWPFDocument doc, AssessmentProcedure procedure) {
        HashMap<String, SecurityRequirement> requirements = getSecurityRequirementsHashMap(procedure.getSafeguardModel().getSafeguards());
        HashMap<String, SecurityRequirement> requirementsTreated = getSecurityRequirementsHashMap(procedure.getRiskTreatmentModel().getResultingSafeguards());

        Map<String, SecurityRequirement> securityRequirementsToReport = new HashMap<>();
        for (String requirementaIdentifier : requirements.keySet()) {
            SecurityRequirement requirement = requirements.get(requirementaIdentifier);

            SecurityRequirement requirementTreated = requirementsTreated.get(requirementaIdentifier);

            if (requirementTreated != null) {
                if (!requirement.getScore().equals(requirementTreated.getScore())) {
                    securityRequirementsToReport.put(requirementTreated.getId(), requirementTreated);
                }
            }
        }

        XWPFTable table = doc.createTable(securityRequirementsToReport.size() + 1, 5);

        table.getRow(0).setRepeatHeader(true);
        table.getRow(0).getCell(0).setText("Catalogue");
        table.getRow(0).getCell(1).setText("Catalogue Id");
        table.getRow(0).getCell(2).setText("Requirement Title");
        table.getRow(0).getCell(3).setText("Description");
        table.getRow(0).getCell(4).setText("User Description");


        int numberOfRequirements = 1;
        for (String requirementaIdentifier : securityRequirementsToReport.keySet()) {
            writeRequirementTreated(doc, table.getRow(numberOfRequirements), securityRequirementsToReport.get(requirementaIdentifier));
            numberOfRequirements++;
        }

    }

    public void writeRequirementTreated(XWPFDocument doc, XWPFTableRow xwpfTableRow, SecurityRequirement requirementTreated) {
        XWPFParagraph p1 = xwpfTableRow.getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r1 = p1.createRun();
        r1.setBold(false);
        r1.setFontSize(10);
        r1.setText("GASF");
        r1.setFontFamily(CALIBRI);

        XWPFParagraph p2 = xwpfTableRow.getCell(1).getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r2 = p2.createRun();
        r2.setBold(true);
        r2.setFontSize(10);
        r2.setText(requirementTreated.getId());
        r2.setFontFamily(CALIBRI);

        XWPFParagraph p3 = xwpfTableRow.getCell(2).getParagraphs().get(0);

        XWPFRun r3 = p3.createRun();
        r3.setBold(false);
        r3.setFontSize(10);
        r3.setText(requirementTreated.getTitle());
        r3.setFontFamily(CALIBRI);

        XWPFParagraph p4 = xwpfTableRow.getCell(3).getParagraphs().get(0);

        XWPFRun r4 = p4.createRun();
        r4.setBold(false);
        r4.setFontSize(10);
        r4.setText(requirementTreated.getDescription());
        r4.setFontFamily(CALIBRI);

        XWPFParagraph p5 = xwpfTableRow.getCell(4).getParagraphs().get(0);
        p5.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r5 = p5.createRun();
        r5.setBold(true);
        r5.setFontSize(10);
        r5.setText(requirementTreated.getUserDescription());
        r5.setFontFamily(CALIBRI);

    }

    public void writeSafeguardsTreatment(XWPFDocument doc, AssessmentProcedure procedure) {

        sortSafeguardId(procedure.getSafeguardModel().getSafeguards());

        HashMap<String, Safeguard> safeguardsTreated = getSafeguardHashMap(procedure.getRiskTreatmentModel().getResultingSafeguards());

        int numberOfSafeguards = 0;
        for (Safeguard safeguard : procedure.getSafeguardModel().getSafeguards()) {
            Safeguard safeguardTreated = safeguardsTreated.get(safeguard.getIdentifier());

            if (safeguardTreated != null) {
                if (!safeguard.getScore().equals(safeguardTreated.getScore())) {
                    numberOfSafeguards++;
                }
            }
        }

        XWPFTable table = doc.createTable(numberOfSafeguards + 1, 8);

        table.getRow(0).setRepeatHeader(true);
        table.getRow(0).getCell(0).setText("Catalogue Id");
        table.getRow(0).getCell(1).setText("Safeguard Name");
        table.getRow(0).getCell(2).setText("Assigned User for Implementation");
        table.getRow(0).getCell(3).setText("Implementation Deadline");
        table.getRow(0).getCell(4).setText("Actual Safeguard Score");
        table.getRow(0).getCell(5).setText("Resulting Safeguard Score");
        table.getRow(0).getCell(6).setText("Category");

        numberOfSafeguards = 1;
        for (Safeguard safeguard : procedure.getSafeguardModel().getSafeguards()) {
            Safeguard safeguardTreated = safeguardsTreated.get(safeguard.getIdentifier());

            if (safeguardTreated != null) {
                if (!safeguard.getScore().equals(safeguardTreated.getScore())) {
                    writeSafeguardTreated(doc, table.getRow(numberOfSafeguards), safeguard, safeguardTreated);
                    numberOfSafeguards++;
                }
            }
        }
    }

    public void writeSafeguardTreated(XWPFDocument doc, XWPFTableRow xwpfTableRow, Safeguard safeguard, Safeguard safeguardTreated) {

        XWPFParagraph p1 = xwpfTableRow.getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r1 = p1.createRun();
        r1.setBold(true);
        r1.setFontSize(10);
        r1.setText(safeguard.getCatalogueId());
        r1.setFontFamily(CALIBRI);

        XWPFParagraph p2 = xwpfTableRow.getCell(1).getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r2 = p2.createRun();
        r2.setBold(false);
        r2.setFontSize(8);
        r2.setText(safeguard.getName());
        r2.setFontFamily(CALIBRI);

        XWPFParagraph p5 = xwpfTableRow.getCell(4).getParagraphs().get(0);
        p5.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r5 = p5.createRun();
        r5.setBold(true);
        r5.setFontSize(10);
        r5.setText(safeguard.getScore().toString());
        r5.setFontFamily(CALIBRI);

        XWPFParagraph p6 = xwpfTableRow.getCell(5).getParagraphs().get(0);
        p6.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r6 = p6.createRun();
        r6.setBold(true);
        r6.setFontSize(10);
        r6.setText(safeguardTreated.getScore().toString());
        r6.setFontFamily(CALIBRI);

        if (safeguard.getScope() == null) {
            return;
        }
        XWPFParagraph p7 = xwpfTableRow.getCell(6).getParagraphs().get(0);
        p7.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r7 = p7.createRun();
        r7.setBold(true);
        r7.setFontSize(10);
        r7.setText(safeguardTreated.getScope().toString());
        r7.setFontFamily(CALIBRI);

    }

    public void writeSafeguardsAnnex(XWPFDocument doc, AssessmentProcedure procedure, Audit audit, Audit auditFinal) {
        XWPFParagraph safeguardModelParagraph = doc.createParagraph();
        safeguardModelParagraph.setStyle("Heading2");
        safeguardModelParagraph.setPageBreak(false);

        safeguardModelParagraph.setAlignment(ParagraphAlignment.LEFT);
        safeguardModelParagraph.setVerticalAlignment(TextAlignment.CENTER);

        XWPFRun safeguardAnnexNameRun = safeguardModelParagraph.createRun();

        //Writing Safeguard Annex name
        safeguardAnnexNameRun.setBold(true);
        safeguardAnnexNameRun.setText("Safeguards To Implement");
        safeguardAnnexNameRun.setFontFamily(CALIBRI);
        safeguardAnnexNameRun.setFontSize(15);

        XWPFParagraph safeguardExplainationParagraph = doc.createParagraph();
        safeguardExplainationParagraph.setAlignment(ParagraphAlignment.BOTH);
        XWPFRun safeguardExplainationRun = safeguardExplainationParagraph.createRun();

        //Writing explaination about the MEHARI Weighting system
        safeguardExplainationRun.setBold(false);
        safeguardExplainationRun.setFontSize(13);
        safeguardExplainationRun.setFontFamily(CALIBRI);
        safeguardExplainationRun.setText("Within this Annex, all Safeguards selected to be improved in order to reduce the risk to the desired levels are further detailed in order to provide a concrete guidance for their implementation.");
        safeguardExplainationRun.addCarriageReturn();
        safeguardExplainationRun.setText("Each Safeguard is subdivided into a set of Questions providing a detailed highlight on the implementation of the Safeguard: some of these questions may have been positively answered during the Audit phase of the Risk Assessment," +
                "others have to be implemented in order to raise the level of the Safeguard. This reports highlights all the questions positively answered during the Audit and also suggests which questions have to be implemented, taking automatically into consideration a set of factors:");
        safeguardExplainationRun.addCarriageReturn();
        safeguardExplainationRun.setText("Weight: this parameter represents the weight of the question for the calculation of the final score of the safeguard.");
        safeguardExplainationRun.addCarriageReturn();
        safeguardExplainationRun.setText("Min Value: this parameter represents the Minimum value of the safeguard if this question is positively answered.");
        safeguardExplainationRun.addCarriageReturn();
        safeguardExplainationRun.setText("Max Value: this parameter represents the Maximum value of the safeguard if this question is NOT positively answered.");
        safeguardExplainationRun.addCarriageReturn();


        writeSafeguardsTreatedDetails(doc, procedure, audit, auditFinal);

        //XWPFParagraph requirementsParagraph = doc.createParagraph();

    }

    public void writeSafeguardsTreatedDetails(XWPFDocument doc, AssessmentProcedure procedure, Audit audit, Audit auditFinal) {

        sortSafeguardId(procedure.getSafeguardModel().getSafeguards());

        HashMap<String, Safeguard> safeguardsTreated = getSafeguardHashMap(procedure.getRiskTreatmentModel().getResultingSafeguards());

        for (Safeguard safeguard : procedure.getSafeguardModel().getSafeguards()) {
            Safeguard safeguardTreated = safeguardsTreated.get(safeguard.getIdentifier());

            if (safeguardTreated != null) {
                //We check if the Safeguard needs to be raised
                if (!safeguard.getScore().equals(safeguardTreated.getScore())) {
                    //We try to optimize the Answers
                    optimizeAnswers(safeguard, safeguardTreated, auditFinal);

                    XWPFParagraph safeguardDataParagraph = doc.createParagraph();
                    safeguardDataParagraph.setAlignment(ParagraphAlignment.BOTH);
                    XWPFRun safeguardData = safeguardDataParagraph.createRun();

                    //Writing single Safeguard Data
                    safeguardData.addCarriageReturn();
                    safeguardData.setBold(false);
                    safeguardData.setFontSize(13);
                    safeguardData.setFontFamily(CALIBRI);
                    safeguardData.setText("Safeguard Id: ");
                    safeguardData.setText(safeguard.getCatalogueId());
                    safeguardData.addCarriageReturn();
                    safeguardData.setText("Current value: ");
                    safeguardData.setText(safeguard.getScore().toString());
                    safeguardData.addCarriageReturn();
                    safeguardData.setText("Value to reach: ");
                    safeguardData.setText(safeguardTreated.getScore().toString());

                    int rows = getQuestionsCount(safeguard, audit);
                    //If, for any reason, there are no Questions, we skip the table
                    if (rows == 0) {
                        continue;
                    }

                    XWPFTable table = doc.createTable(rows + 1, 7);
                    table.setWidthType(TableWidthType.AUTO);
                    table.setCellMargins(0, 30, 0, 30);

                    table.getRow(0).setRepeatHeader(true);

                    table.getRow(0).getCell(0).setText(safeguard.getCatalogueId());
                    table.getRow(0).getCell(1).setText(safeguard.getName());
                    table.getRow(0).getCell(2).setText("Value");
                    table.getRow(0).getCell(3).setText("Answer Weight");
                    table.getRow(0).getCell(4).setText("Answer Min Value");
                    table.getRow(0).getCell(5).setText("Answer Max Value");
                    table.getRow(0).getCell(6).setText("ISO2013 related Control");


                    writeSafeguardTreatedDetails(doc, table, safeguard, safeguardTreated, audit, auditFinal);


                }
            }
        }

    }

    //This method writes, for each Safeguard, all its Questions, including the ones not yet answered (this provides the reader a clear indication about how to
    //practically act to increase the level of the Safeguard
    private void writeSafeguardTreatedDetails(XWPFDocument doc, XWPFTable table, Safeguard safeguard, Safeguard safeguardTreated, Audit audit, Audit auditFinal) {

        List<Question> questions = getSafeguardQuestions(safeguard, audit);
        List<Question> questionsOptimized = getSafeguardQuestions(safeguard, auditFinal);

        int count = 1;
        for (Question question : questions) {
            String answer1 = question.getAnswers().get(AnswerTypeEnum.MEHARI_R_V1);
            String answer3 = question.getAnswers().get(AnswerTypeEnum.MEHARI_W);
            String answer4 = question.getAnswers().get(AnswerTypeEnum.MEHARI_Min);
            String answer5 = question.getAnswers().get(AnswerTypeEnum.MEHARI_Max);
            String answer7 = question.getAnswers().get(AnswerTypeEnum.MEHARI_ISO13);

            XWPFParagraph p1 = table.getRow(count).getCell(0).getParagraphs().get(0);
            p1.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun r1 = p1.createRun();
            r1.setBold(true);
            r1.setFontSize(10);
            r1.setText(question.getCategory());
            r1.setFontFamily(CALIBRI);

            XWPFParagraph p2 = table.getRow(count).getCell(1).getParagraphs().get(0);
            p2.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun r2 = p2.createRun();
            r2.setBold(false);
            r2.setFontSize(10);
            r2.setText(question.getValue());
            r2.setFontFamily(CALIBRI);


            XWPFParagraph p3 = table.getRow(count).getCell(2).getParagraphs().get(0);
            p3.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun r3 = p3.createRun();
            r3.setBold(true);
            r3.setFontSize(10);
            if (answer1 != null && !answer1.equals("")) {
                //r3.setText(answer1.getValue());
                r3.setText("Implemented");
            } else {
                r3.setColor("ffa500");
                //We check if this Answer is part of the optimization
                for (Question questionOptimized : questionsOptimized) {
                    if (question.getCategory().equals(questionOptimized.getCategory())) {
                        String answer1Optimized = questionOptimized.getAnswers().get(AnswerTypeEnum.MEHARI_R_V1);
                        if (answer1Optimized != null && !answer1Optimized.equals("")) {

                            r3.setText("To be implemented");
                        }
                        break;
                    }
                }
            }
            r3.setFontFamily(CALIBRI);


            XWPFParagraph p4 = table.getRow(count).getCell(3).getParagraphs().get(0);
            p4.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun r4 = p4.createRun();
            r4.setBold(false);
            r4.setFontSize(10);
            if (answer3 != null) {
                r4.setText(answer3);
            } else {
                r4.setText("");
            }
            r4.setFontFamily(CALIBRI);

            XWPFParagraph p5 = table.getRow(count).getCell(4).getParagraphs().get(0);
            p5.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun r5 = p5.createRun();
            r5.setBold(false);
            r5.setFontSize(10);
            if (answer4 != null) {
                r5.setText(answer4);
            } else {
                r5.setText("");
            }
            r5.setFontFamily(CALIBRI);

            XWPFParagraph p6 = table.getRow(count).getCell(5).getParagraphs().get(0);
            p6.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun r6 = p6.createRun();
            r6.setBold(false);
            r6.setFontSize(10);
            if (answer5 != null) {
                r6.setText(answer5);
            } else {
                r6.setText("");
            }
            r6.setFontFamily(CALIBRI);

            XWPFParagraph p7 = table.getRow(count).getCell(6).getParagraphs().get(0);
            p7.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun r7 = p7.createRun();
            r7.setBold(false);
            r7.setFontSize(10);
            if (answer7 != null) {
                r7.setText(answer7);
            } else {
                r7.setText("");
            }
            r7.setFontFamily(CALIBRI);

            count++;
        }


    }

    //This method tries to optimize new Answers to reach the desired Safeguard Score
    private void optimizeAnswers(Safeguard safeguard, Safeguard safeguardTreated, Audit optimizedAudit) {
        //If no optimization is needed on the Safeguard, we skip over
        if (safeguard.getScore() == safeguardTreated.getScore()) {
            return;
        }

        List<Question> safeguardQuestions = getSafeguardQuestions(safeguard, optimizedAudit);
        if (safeguardQuestions == null) {
            return;
        }

        for (Question question : safeguardQuestions) {
            Map<AnswerTypeEnum, String> newAnswers = attemptOptimizationAnswersMaxMin(safeguardTreated.getScore(), question.getAnswers());
            if (newAnswers != null) {
                question.setAnswers(newAnswers);
            }
        }

        optimizeAnswers(safeguardQuestions, safeguardTreated);
    }

    private void optimizeAnswers(List<Question> safeguardQuestions, Safeguard safeguardTreated) {
        //After the optimization, we compute the actual Score to see if it is sufficient
        SafeguardScoreEnum optimizedScore = checkSafeguardScore(safeguardQuestions);

        //We check if the optimization is sufficient
        if (optimizedScore.compareTo(safeguardTreated.getScore()) >= 0) {
            return;
        }
        //If it is not sufficient, we need to add, one by one starting from the smallest, other Answers in order to reach the level
        Question smallestQuestion = null;
        int smallestAnswerWeigth = 5;
        for (Question question : safeguardQuestions) {
            String v1 = question.getAnswers().get(AnswerTypeEnum.MEHARI_R_V1);
            String weight = question.getAnswers().get(AnswerTypeEnum.MEHARI_W);
            if (v1 != null && v1.equals("1")) {
                continue;
            }
            int tempWeight = weight != null && !weight.equals("") ? Integer.valueOf(weight) : 0;
            if (tempWeight < smallestAnswerWeigth) {
                smallestAnswerWeigth = tempWeight;
                smallestQuestion = question;
            }
        }

        //It should be impossible it is a null...
        if (smallestQuestion == null) {
            return;
        }
        smallestQuestion.getAnswers().put(AnswerTypeEnum.MEHARI_R_V1, "1");

        optimizeAnswers(safeguardQuestions, safeguardTreated);
    }

    private SafeguardScoreEnum checkSafeguardScore(List<Question> safeguardQuestions) {

        SafeguardScoreEnum score = null;
        //Check total weight of the Questions
        double totalWeigth = 0;
        double relativeWeigth = 0;
        for (Question question : safeguardQuestions) {
            Map<AnswerTypeEnum, String> answers = question.getAnswers();
            String v1 = answers.get(AnswerTypeEnum.MEHARI_R_V1);
            String weight = answers.get(AnswerTypeEnum.MEHARI_W);
            int answerValue = weight != null && !weight.equals("") ? Integer.valueOf(weight) : 0;
            totalWeigth += answerValue;
            if (v1 != null && v1.equals("1")) {
                relativeWeigth += answerValue;
            }
        }
        //This is the weighted value following MEHARI
        double weightedScore = (4 * relativeWeigth) / totalWeigth;

        //Check Minimum Score
        double minimumScore = 0;
        for (Question question : safeguardQuestions) {
            Map<AnswerTypeEnum, String> answers = question.getAnswers();
            String v1 = answers.get(AnswerTypeEnum.MEHARI_R_V1);
            String min = answers.get(AnswerTypeEnum.MEHARI_Min);
            if (v1 != null && v1.equals("1")) {
                int tempMinimumScore = min != null && !min.equals("") ?
                        Integer.valueOf(min) : 0;
                if (tempMinimumScore > minimumScore) {
                    minimumScore = tempMinimumScore;
                }
            }
        }

        double maximumScore = 4;
        for (Question question : safeguardQuestions) {
            Map<AnswerTypeEnum, String> answers = question.getAnswers();
            String v1 = answers.get(AnswerTypeEnum.MEHARI_R_V1);
            String max = answers.get(AnswerTypeEnum.MEHARI_Max);
            if (v1 == null || !v1.equals("1")) {
                int tempMaximumScore = max != null && !max.equals("") ?
                        Integer.valueOf(max) : 0;
                if (tempMaximumScore < maximumScore) {
                    maximumScore = tempMaximumScore;
                }
            }
        }

        double finalScore = weightedScore;
        if (weightedScore < minimumScore) {
            finalScore = minimumScore;
        }
        if (finalScore > maximumScore) {
            finalScore = maximumScore;
        }
        int intScore = (int) Math.round(finalScore);

        if (intScore == 0 || intScore == 1) {
            score = SafeguardScoreEnum.LOW;
        }
        if (intScore == 2) {
            score = SafeguardScoreEnum.MEDIUM;
        }
        if (intScore == 3) {
            score = SafeguardScoreEnum.HIGH;
        }
        if (intScore == 4) {
            score = SafeguardScoreEnum.VERY_HIGH;
        }

        return score;
    }

    //This method tries to optimize Max and Mins (easiest way to get optimization)
    private Map<AnswerTypeEnum, String> attemptOptimizationAnswersMaxMin(SafeguardScoreEnum target, Map<AnswerTypeEnum, String> actualAnswers) {
        int max = 0;
        int min = 0;
        String v1 = actualAnswers.get(AnswerTypeEnum.MEHARI_R_V1);
        String vmin = actualAnswers.get(AnswerTypeEnum.MEHARI_Min);
        String vmax = actualAnswers.get(AnswerTypeEnum.MEHARI_Max);
        if (v1 != null && v1.equals("1")) {
            //If the question was answered, there is no point on continuing the algorithm
            return null;
        }
        if (vmin != null && !vmin.equals("")) {
            min = Integer.parseInt(vmin);
        }
        if (vmax != null && !vmax.equals("")) {
            max = Integer.parseInt(vmax);
        }

        //This Answer has not been implemented. We therefore check if there are mandatory Max/Min to fulfil
        if (min > 0) {
            if (target.getScore() >= min) {
                //In some  cases, an Answer with Index 1 may be empty
                actualAnswers.put(AnswerTypeEnum.MEHARI_R_V1, "1");
            }
        }
        if (min == 0 && max > 0) {
            if (target.getScore() > max) {
                //In some  cases, an Answer with Index 1 may be empty
                actualAnswers.put(AnswerTypeEnum.MEHARI_R_V1, "1");
            }
        }

        return actualAnswers;
    }

    public void writeThreatAnnex(XWPFDocument doc, AssessmentProcedure procedure) {
        //Writing Threat Annex
        XWPFParagraph threatAnnexParagraph = doc.createParagraph();

        threatAnnexParagraph.setStyle("Heading2");
        threatAnnexParagraph.setPageBreak(true);

        threatAnnexParagraph.setAlignment(ParagraphAlignment.LEFT);
		/*threatAnnexParagraph.setBorderBottom(Borders.DOUBLE);
		threatAnnexParagraph.setBorderTop(Borders.DOUBLE);
		threatAnnexParagraph.setBorderRight(Borders.DOUBLE);
		threatAnnexParagraph.setBorderLeft(Borders.DOUBLE);*/
        threatAnnexParagraph.setVerticalAlignment(TextAlignment.CENTER);
        //threatAnnexParagraph.setBorderBetween(Borders.SINGLE);

        XWPFRun threatAnnexRun = threatAnnexParagraph.createRun();

        threatAnnexRun.setBold(true);
        threatAnnexRun.setText("Threats References");
        threatAnnexRun.setFontFamily(CALIBRI);
        threatAnnexRun.setFontSize(15);

        sortThreats(procedure.getThreatModel());
        int numberOfThreats = procedure.getThreatModel().getThreats().size();

        XWPFTable table = doc.createTable(numberOfThreats + 1, 7);

        table.getRow(0).setRepeatHeader(true);
        table.getRow(0).getCell(0).setText("Id");
        table.getRow(0).getCell(1).setText("Catalogue");
        table.getRow(0).getCell(2).setText("Name");
        table.getRow(0).getCell(3).setText("Description");
        table.getRow(0).getCell(4).setText("Class");
        table.getRow(0).getCell(5).setText("Likelihood");
        table.getRow(0).getCell(6).setText("Last Update");


        numberOfThreats = 1;

        for (Threat threat : procedure.getThreatModel().getThreats()) {

            writeThreat(doc, table.getRow(numberOfThreats), threat);
            numberOfThreats++;
        }

    }

    public void writeThreat(XWPFDocument doc, XWPFTableRow xwpfTableRow, Threat threat) {
        XWPFParagraph p1 = xwpfTableRow.getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r1 = p1.createRun();
        r1.setBold(false);
        r1.setFontSize(10);
        r1.setText(threat.getCatalogueId());
        r1.setFontFamily(CALIBRI);

        XWPFParagraph p2 = xwpfTableRow.getCell(1).getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r2 = p2.createRun();
        r2.setBold(true);
        r2.setFontSize(10);
        r2.setText(threat.getCatalogue().toString());
        r2.setFontFamily(CALIBRI);

        XWPFParagraph p3 = xwpfTableRow.getCell(2).getParagraphs().get(0);

        XWPFRun r3 = p3.createRun();
        r3.setBold(false);
        r3.setFontSize(10);
        r3.setText(threat.getName());
        r3.setFontFamily(CALIBRI);

        XWPFParagraph p4 = xwpfTableRow.getCell(3).getParagraphs().get(0);

        XWPFRun r4 = p4.createRun();
        r4.setBold(false);
        r4.setFontSize(10);
        r4.setText(threat.getDescription());
        r4.setFontFamily(CALIBRI);

        XWPFParagraph p5 = xwpfTableRow.getCell(4).getParagraphs().get(0);
        p5.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r5 = p5.createRun();
        r5.setBold(false);
        r5.setFontSize(10);
        r5.setText(threat.getThreatClass().toString());
        r5.setFontFamily(CALIBRI);

        XWPFParagraph p6 = xwpfTableRow.getCell(5).getParagraphs().get(0);
        p6.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r6 = p6.createRun();
        r6.setBold(false);
        r6.setFontSize(10);
        r6.setText(threat.getScore().getLikelihood().toString());
        r6.setFontFamily(CALIBRI);

        XWPFParagraph p7 = xwpfTableRow.getCell(6).getParagraphs().get(0);
        p7.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r7 = p7.createRun();
        r7.setBold(false);
        r7.setFontSize(10);
        r7.setText(threat.getLastUpdate());
        r7.setFontFamily(CALIBRI);

    }

    public void writeVulnerabilityAnnex(XWPFDocument doc, AssessmentProcedure procedure) {
        //Writing Vulnerability Annex
        XWPFParagraph vulnerabilityAnnexParagraph = doc.createParagraph();

        vulnerabilityAnnexParagraph.setStyle("Heading2");
        vulnerabilityAnnexParagraph.setPageBreak(false);

        vulnerabilityAnnexParagraph.setAlignment(ParagraphAlignment.LEFT);
		/*vulnerabilityAnnexParagraph.setBorderBottom(Borders.DOUBLE);
		vulnerabilityAnnexParagraph.setBorderTop(Borders.DOUBLE);
		vulnerabilityAnnexParagraph.setBorderRight(Borders.DOUBLE);
		vulnerabilityAnnexParagraph.setBorderLeft(Borders.DOUBLE);*/
        vulnerabilityAnnexParagraph.setVerticalAlignment(TextAlignment.CENTER);
        //vulnerabilityAnnexParagraph.setBorderBetween(Borders.SINGLE);

        XWPFRun vulnerabilityAnnexRun = vulnerabilityAnnexParagraph.createRun();

        vulnerabilityAnnexRun.setBold(true);
        vulnerabilityAnnexRun.setText("Vulnerabilities References");
        vulnerabilityAnnexRun.setFontFamily(CALIBRI);
        vulnerabilityAnnexRun.setFontSize(15);

        sortVulnerabilities(procedure.getVulnerabilityModel());
        int numberOfVulnerabilities = procedure.getVulnerabilityModel().getVulnerabilities().size();

        XWPFTable table = doc.createTable(numberOfVulnerabilities + 1, 7);

        table.getRow(0).setRepeatHeader(true);
        table.getRow(0).getCell(0).setText("Id");
        table.getRow(0).getCell(1).setText("Catalogue");
        table.getRow(0).getCell(2).setText("Name");
        table.getRow(0).getCell(3).setText("Description");
        table.getRow(0).getCell(4).setText("Security Impact");
        table.getRow(0).getCell(5).setText("Consequences");
        table.getRow(0).getCell(6).setText("Last Update");


        numberOfVulnerabilities = 1;

        for (Vulnerability vulnerability : procedure.getVulnerabilityModel().getVulnerabilities()) {

            writeVulnerability(doc, table.getRow(numberOfVulnerabilities), vulnerability);
            numberOfVulnerabilities++;
        }

    }

    public void writeVulnerability(XWPFDocument doc, XWPFTableRow xwpfTableRow, Vulnerability vulnerability) {
        XWPFParagraph p1 = xwpfTableRow.getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r1 = p1.createRun();
        r1.setBold(false);
        r1.setFontSize(10);
        r1.setText(vulnerability.getCatalogueId());
        r1.setFontFamily(CALIBRI);

        XWPFParagraph p2 = xwpfTableRow.getCell(1).getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r2 = p2.createRun();
        r2.setBold(true);
        r2.setFontSize(10);
        r2.setText(vulnerability.getCatalogue().toString());
        r2.setFontFamily(CALIBRI);

        XWPFParagraph p3 = xwpfTableRow.getCell(2).getParagraphs().get(0);

        XWPFRun r3 = p3.createRun();
        r3.setBold(false);
        r3.setFontSize(10);
        r3.setText(vulnerability.getName());
        r3.setFontFamily(CALIBRI);

        XWPFParagraph p4 = xwpfTableRow.getCell(3).getParagraphs().get(0);

        XWPFRun r4 = p4.createRun();
        r4.setBold(false);
        r4.setFontSize(10);
        r4.setText(vulnerability.getDescription());
        r4.setFontFamily(CALIBRI);

        XWPFParagraph p5 = xwpfTableRow.getCell(4).getParagraphs().get(0);
        p5.setAlignment(ParagraphAlignment.CENTER);

        String consequences = "";
        String securityDimension = "";

        for (Consequence consequence : (vulnerability.getScore()).getConsequences()) {

            if (consequences.equals("")) {
                consequences += consequence.getDescription();
            } else {
                consequences += ". ";
                consequences += consequence.getDescription();
            }

            for (SecurityImpact scopeVuln : consequence.getSecurityImpacts()) {
                if (securityDimension.equals("")) {
                    securityDimension += scopeVuln.getScope().toString();
                } else {
                    securityDimension += ", ";
                    securityDimension += scopeVuln.getScope().toString();
                }

            }

        }

        XWPFRun r5 = p5.createRun();
        r5.setBold(true);
        r5.setFontSize(10);
        r5.setText(securityDimension);
        r5.setFontFamily(CALIBRI);

        XWPFParagraph p6 = xwpfTableRow.getCell(5).getParagraphs().get(0);
        p6.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r6 = p6.createRun();
        r6.setBold(false);
        r6.setFontSize(10);
        r6.setText(consequences);
        r6.setFontFamily(CALIBRI);

        XWPFParagraph p7 = xwpfTableRow.getCell(6).getParagraphs().get(0);
        p7.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r7 = p7.createRun();
        r7.setBold(false);
        r7.setFontSize(10);
        r7.setText(vulnerability.getLastUpdate());
        r7.setFontFamily(CALIBRI);

    }

    //This method retrieves the count of questions for a specific Safeguard
    private int getQuestionsCount(Safeguard safeguard, Audit audit) {
        int count = 0;
        String safeguardCategory = safeguard.getCatalogueId().substring(0, 2);

        for (Questionnaire questionnaire : audit.getQuestionnaires()) {
            if (questionnaire.getCategory().equals(safeguardCategory)) {

                for (Question categoryQuestion : questionnaire.getQuestions()) {
                    for (Question safeguardQuestion : categoryQuestion.getChildren()) {
                        if (safeguardQuestion.getCategory().equals(safeguard.getCatalogueId())) {
                            count = safeguardQuestion.getChildren().size();
                            return count;

                        }
                    }
                }
            }
        }

        return count;
    }

    //This method retrieves the count of questions for a specific Safeguard
    private List<Question> getSafeguardQuestions(Safeguard safeguard, Audit audit) {
        String safeguardCategory = safeguard.getCatalogueId().substring(0, 2);

        for (Questionnaire questionnaire : audit.getQuestionnaires()) {
            if (questionnaire.getCategory().equals(safeguardCategory)) {

                for (Question categoryQuestion : questionnaire.getQuestions()) {
                    for (Question safeguardQuestion : categoryQuestion.getChildren()) {
                        if (safeguardQuestion.getCategory().equals(safeguard.getCatalogueId())) {
                            return safeguardQuestion.getChildren();

                        }
                    }
                }
            }
        }

        return null;
    }

    private boolean checkScenarioThreshold(RiskScenario scenario, ImpactEnum threshold, HashMap<String, RiskTreatment> treatmentMap) {
        RiskTreatment treatment = treatmentMap.get(scenario.getIdentifier());
        if (treatment.getResultingSeriousness().compareTo(threshold) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    private void sortThreats(ThreatModel threatModel) {
        //Sorting Threats
        Threat[] threatArray = threatModel.getThreats().toArray(new Threat[threatModel.getThreats().size()]);

        Arrays.sort(threatArray, Comparator.comparing(Threat::getCatalogueId));
        threatModel.getThreats().clear();
        threatModel.getThreats().addAll(Arrays.asList(threatArray));
    }

    private void sortVulnerabilities(VulnerabilityModel vulnerabilityModel) {
        //Sorting Vulnerabilities
        Vulnerability[] vulnArray = vulnerabilityModel.getVulnerabilities().toArray(new Vulnerability[vulnerabilityModel.getVulnerabilities().size()]);

        Arrays.sort(vulnArray, Comparator.comparing(Vulnerability::getCatalogueId));
        vulnerabilityModel.getVulnerabilities().clear();
        vulnerabilityModel.getVulnerabilities().addAll(Arrays.asList(vulnArray));
    }

    private void sortSafeguardId(ArrayList<Safeguard> safeguards) {
        Safeguard[] safeguardArray = safeguards.toArray(new Safeguard[safeguards.size()]);

        Arrays.sort(safeguardArray, Comparator.comparing(Safeguard::getCatalogueId));
        safeguards.clear();
        safeguards.addAll(Arrays.asList(safeguardArray));

    }

    private HashMap<String, SecurityRequirement> getSecurityRequirementsHashMap(
            ArrayList<Safeguard> resultingSafeguards) {
        // Here we put the SecurityRequirements on an HashMap (faster to be used
        // later)
        HashMap<String, SecurityRequirement> resultingSecurityRequirementMap = new HashMap<>();

        for (Safeguard safeguard : resultingSafeguards) {

            for (SecurityRequirement securityRequirement : safeguard.getRelatedSecurityRequirements()) {

                if (!resultingSecurityRequirementMap.containsKey(securityRequirement.getId())) {
                    resultingSecurityRequirementMap.put(securityRequirement.getId().concat("-").concat(safeguard.getCatalogueId()), securityRequirement);
                }

                getSecurityRequirementsHashMap(resultingSecurityRequirementMap, securityRequirement);
            }
        }

        return resultingSecurityRequirementMap;
    }

    private void getSecurityRequirementsHashMap(HashMap<String, SecurityRequirement> resultingSecurityRequirementMap,
                                                SecurityRequirement securityRequirement) {

        if (securityRequirement.getChildren() != null) {
            for (SecurityRequirement innerSecurityRequirement : securityRequirement.getChildren()) {

                if (innerSecurityRequirement instanceof SecurityRequirement) {

                    if (!resultingSecurityRequirementMap.containsKey(innerSecurityRequirement.getId())) {
                        resultingSecurityRequirementMap.put(innerSecurityRequirement.getId(),
                                (SecurityRequirement) innerSecurityRequirement);
                    }

                    getSecurityRequirementsHashMap(resultingSecurityRequirementMap,
                            (SecurityRequirement) innerSecurityRequirement);
                }
            }
        }
    }

    private HashMap<String, Safeguard> getSafeguardHashMap(ArrayList<Safeguard> safeguards) {
        // Here we put the Safeguard on an HashMap (faster to be used later)
        HashMap<String, Safeguard> safeguardMap = new HashMap<String, Safeguard>();

        for (Safeguard safeguard : safeguards) {
            String safeguardId = safeguard.getIdentifier();

            if (!safeguardMap.containsKey(safeguardId)) {
                safeguardMap.put(safeguardId, safeguard);
            }
        }

        return safeguardMap;
    }

    private HashMap<String, RiskTreatment> getRiskTreatmentHashMap(ArrayList<RiskTreatment> riskTreatments) {
        // Here we put the RiskTreatment on an HashMap (faster to be used later)
        HashMap<String, RiskTreatment> treatmentMap = new HashMap<String, RiskTreatment>();

        for (RiskTreatment risktreatment : riskTreatments) {
            String scenarioId = risktreatment.getRiskScenarioId();

            treatmentMap.put(scenarioId, risktreatment);
        }

        return treatmentMap;
    }

    private void sortScenarioSeriousness(ArrayList<RiskScenario> scenarios) {

//		BeanComparator fieldComparator = new BeanComparator(
//                "calculatedSeriousness");
//        Collections.sort(scenarios, fieldComparator);
        RiskScenario[] scenarioArray = scenarios.toArray(new RiskScenario[scenarios.size()]);

        Arrays.sort(scenarioArray, new Comparator<RiskScenario>() {
            @Override
            public int compare(RiskScenario first, RiskScenario second) {
                return first.getCalculatedSeriousness().compareTo(second.getCalculatedSeriousness());
            }
        });
        scenarios.clear();
        scenarios.addAll(Arrays.asList(scenarioArray));
    }

    private String getNodeOwner(AssessmentProject project, Node item) {

        if (item.getSystemParticipantOwnerId() == null || item.getSystemParticipantOwnerId().equals("")) {
            return "";
        }

        String owner = "";

        for (SystemParticipant participant : project.getSystemProject().getParticipants()) {

            if ((participant.getName() + " " + participant.getSurname()).equals(item.getSystemParticipantOwnerId())) {
                owner = (participant.getName() + " " + participant.getSurname());
            }
        }

        return owner;
    }

    private Asset getAsset(RiskScenario scenario, AssessmentProcedure procedure) {
        for (Asset asset : procedure.getAssetModel().getAssets()) {
            if (asset.getIdentifier().equals(scenario.getAssetId())) {
                return asset;
            }
        }
        return null;
    }

    private String getOrganizationsAssetString(AssessmentProcedure procedure, Asset asset) {
        String organizationsString = "";

        ArrayList<Organization> organizations = new ArrayList<Organization>();
        organizations = getOrganizationsAsset(procedure, asset);

        for (Organization organization : organizations) {
            if (organizationsString.equals("")) {
                organizationsString += organization.getName();
            } else {
                organizationsString += ", ";
                organizationsString += organization.getName();
            }
        }
        return organizationsString;
    }

    private String getBusinessProcessesAssetString(AssessmentProcedure procedure, Asset asset) {
        String processesString = "";

        ArrayList<BusinessProcess> processes = new ArrayList<BusinessProcess>();
        processes = getBusinessProcessesAsset(procedure, asset);

        for (BusinessProcess process : processes) {
            if (processesString.equals("")) {
                processesString += process.getName();
            } else {
                processesString += ", ";
                processesString += process.getName();
            }
        }
        return processesString;
    }

    private String getBusinessActivitiesAssetString(AssessmentProcedure procedure, Asset asset) {
        String activitiesString = "";

        ArrayList<BusinessActivity> activities = new ArrayList<BusinessActivity>();
        activities = getBusinessActivitiesAsset(procedure, asset);

        for (BusinessActivity activity : activities) {
            if (activitiesString.equals("")) {
                activitiesString += activity.getName();
            } else {
                activitiesString += ", ";
                activitiesString += activity.getName();
            }
        }
        return activitiesString;
    }

    private ArrayList<Organization> getOrganizationsAsset(AssessmentProcedure procedure, Asset asset) {
        ArrayList<Organization> organizations = new ArrayList<Organization>();
        ArrayList<BusinessProcess> processes = getBusinessProcessesAsset(procedure, asset);
        for (BusinessProcess process : processes) {
            for (Edge parent : process.getParents()) {
                Organization organization = (Organization) parent.getSource();
                boolean alreadyAdded = false;
                for (Organization addedOrganization : organizations) {
                    if (addedOrganization.getName().equals(organization.getName())) {
                        alreadyAdded = true;
                        break;
                    }
                }
                if (!alreadyAdded) {
                    organizations.add(organization);
                }
            }
        }

        return organizations;
    }

    private ArrayList<BusinessProcess> getBusinessProcessesAsset(AssessmentProcedure procedure, Asset asset) {
        ArrayList<BusinessProcess> processes = new ArrayList<BusinessProcess>();
        ArrayList<BusinessActivity> activities = getBusinessActivitiesAsset(procedure, asset);
        for (BusinessActivity activity : activities) {
            for (Edge parent : activity.getParents()) {
                BusinessProcess process = (BusinessProcess) parent.getSource();
                boolean alreadyAdded = false;
                for (BusinessProcess addedProcess : processes) {
                    if (addedProcess.getName().equals(process.getName())) {
                        alreadyAdded = true;
                        break;
                    }
                }
                if (!alreadyAdded) {
                    processes.add(process);
                }
            }
        }

        return processes;
    }

    private ArrayList<BusinessActivity> getBusinessActivitiesAsset(AssessmentProcedure procedure, Asset asset) {
        ArrayList<BusinessActivity> activities = new ArrayList<BusinessActivity>();
        for (Edge parent : asset.getParents()) {
            BusinessActivity activity = (BusinessActivity) parent.getSource();
            activities.add(activity);
        }

        return activities;
    }

    public void writeSOA(XWPFDocument doc, AssessmentProcedure procedure, Audit audit, Audit auditFinal, ISOControls controls) {
        this.optimizeSafeguardsAnswers(procedure, auditFinal);

        XWPFParagraph soaParagraph = doc.createParagraph();
        soaParagraph.setStyle("Heading1");
        soaParagraph.setPageBreak(true);

        soaParagraph.setAlignment(ParagraphAlignment.LEFT);
        soaParagraph.setVerticalAlignment(TextAlignment.CENTER);

        XWPFRun soaNameRun = soaParagraph.createRun();

        //Writing SOA paragraph name
        soaNameRun.setBold(true);
        soaNameRun.setText("Statement of Applicability");
        soaNameRun.setFontFamily(CALIBRI);
        soaNameRun.setFontSize(15);

        writeSOADetails(doc, procedure, audit, auditFinal, controls);
    }

    private void writeSOADetails(XWPFDocument doc, AssessmentProcedure procedure, Audit audit, Audit auditFinal, ISOControls controls) {
        XWPFParagraph safeguardDataParagraph = doc.createParagraph();
        safeguardDataParagraph.setAlignment(ParagraphAlignment.BOTH);
        XWPFRun safeguardData = safeguardDataParagraph.createRun();

        //Writing Legend
        safeguardData.setBold(false);
        safeguardData.setFontSize(13);
        safeguardData.setFontFamily(CALIBRI);
        safeguardData.setText("LR: legal requirements, CO: contractual obligations, BR/BP: business requirements/adopted best practices, RRA: results of risk assessment");
        safeguardData.addCarriageReturn();
        safeguardData.setText("Y: 'Implemented' or 'To be Implemented'");
        safeguardData.addCarriageReturn();
        safeguardData.setText("N: 'Not Implemented' or 'Not to be Implemented'");
        safeguardData.addCarriageReturn();
        safeguardData.setText("P: 'Partially Implemented' or 'To be implemented Partially'");

        int rows = controls.getControls().size();

        XWPFTable table = doc.createTable(rows + 1, 10);
        table.setWidthType(TableWidthType.AUTO);
        table.setCellMargins(0, 30, 0, 30);

        table.getRow(0).setRepeatHeader(true);

        table.getRow(0).getCell(0).setText("Clause");
        table.getRow(0).getCell(1).setText("Objective");
        table.getRow(0).getCell(2).setText("Control");
        table.getRow(0).getCell(3).setText("Current Control (Y/N/P)");
        table.getRow(0).getCell(4).setText("LR (Y/N)");
        table.getRow(0).getCell(5).setText("CO (Y/N)");
        table.getRow(0).getCell(6).setText("BR/BP (Y/N)");
        table.getRow(0).getCell(7).setText("RRA (Y/N/P)");
        table.getRow(0).getCell(8).setText("Justification for Exclusion");
        table.getRow(0).getCell(9).setText("Overview of implementation");

        int count = 1;
        for (ISOControl control : controls.getControls()) {

            XWPFParagraph p1 = table.getRow(count).getCell(0).getParagraphs().get(0);
            p1.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun r1 = p1.createRun();
            r1.setBold(true);
            r1.setFontSize(10);
            r1.setText(control.getClauseId() + " " + control.getClause());
            r1.setFontFamily(CALIBRI);

            XWPFParagraph p2 = table.getRow(count).getCell(1).getParagraphs().get(0);
            p2.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun r2 = p2.createRun();
            r2.setBold(false);
            r2.setFontSize(10);
            r2.setText(control.getObjectiveId() + " " + control.getObjective());
            r2.setFontFamily(CALIBRI);

            XWPFParagraph p3 = table.getRow(count).getCell(2).getParagraphs().get(0);
            p3.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun r3 = p3.createRun();
            r3.setBold(false);
            r3.setFontSize(10);
            r3.setText(control.getControlId() + " " + control.getControl());
            r3.setFontFamily(CALIBRI);

            XWPFParagraph p4 = table.getRow(count).getCell(3).getParagraphs().get(0);
            p4.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun r4 = p4.createRun();
            r4.setBold(true);
            r4.setFontSize(14);
            double controlValue = checkISOControlValue(control.getControlId(), audit);
            if (controlValue == 0) {
                r4.setText("N");
            }
            if (controlValue == 1) {
                r4.setText("Y");
            }
            if (controlValue > 0 && controlValue < 1) {
                r4.setText("P");
            }
            r4.setFontFamily(CALIBRI);

            XWPFParagraph p8 = table.getRow(count).getCell(7).getParagraphs().get(0);
            p8.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun r8 = p8.createRun();
            r8.setBold(true);
            r8.setFontSize(14);
            double desiredControlValue = checkISOControlValue(control.getControlId(), auditFinal);
            if (desiredControlValue == 0) {
                r8.setText("N");
            }
            if (desiredControlValue == 1) {
                r8.setText("Y");
            }
            if (desiredControlValue > 0 && desiredControlValue < 1) {
                r8.setText("P");
            }
            r8.setFontFamily(CALIBRI);

            count++;
        }
    }

    //Using MEHARI formula, we compute the value of the ISO control
    private double checkISOControlValue(String control, Audit audit) {
        double value = 0;
        double controlsCount = 0;
        double answeredControlsCount = 0;
        for (Questionnaire questionnaire : audit.getQuestionnaires()) {
            for (Question categoryQuestion : questionnaire.getQuestions()) {
                for (Question safeguardQuestion : categoryQuestion.getChildren()) {
                    for (Question answerQuestion : safeguardQuestion.getChildren()) {
                        String answer7 = answerQuestion.getAnswers().get(AnswerTypeEnum.MEHARI_ISO13);

                        if (answer7 != null && !answer7.equals("")) {
                            if (answer7.contains(control)) {
                                controlsCount++;
                                String answer1 = answerQuestion.getAnswers().get(AnswerTypeEnum.MEHARI_R_V1);

                                if (answer1 != null && !answer1.equals("")) {
                                    answeredControlsCount++;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (answeredControlsCount == 0) {
            return 0;
        }

        value = answeredControlsCount / controlsCount;
        return value;
    }

    private void optimizeSafeguardsAnswers(AssessmentProcedure procedure, Audit auditFinal) {

        sortSafeguardId(procedure.getSafeguardModel().getSafeguards());

        HashMap<String, Safeguard> safeguardsTreated = getSafeguardHashMap(procedure.getRiskTreatmentModel().getResultingSafeguards());

        for (Safeguard safeguard : procedure.getSafeguardModel().getSafeguards()) {
            Safeguard safeguardTreated = safeguardsTreated.get(safeguard.getIdentifier());

            if (safeguardTreated != null) {
                //We check if the Safeguard needs to be raised
                if (!safeguard.getScore().equals(safeguardTreated.getScore())) {
                    //We try to optimize the Answers
                    optimizeAnswers(safeguard, safeguardTreated, auditFinal);
                }
            }
        }

    }
}
