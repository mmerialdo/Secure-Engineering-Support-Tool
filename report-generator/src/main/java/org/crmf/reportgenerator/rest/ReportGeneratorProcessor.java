/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="ReportGeneratorProcessor.java"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

package org.crmf.reportgenerator.rest;

import com.google.gson.Gson;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.reportgenerator.manager.ReportGeneratorInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ReportGeneratorProcessor implements Processor {

  private static final Logger LOG = LoggerFactory.getLogger(ReportGeneratorProcessor.class.getName());

  private ReportGeneratorInputInterface reportgeneratorInput;
  private static final String PREFIX = "report_";
  private static final String PREFIXLIGHT = "report_light_";
  private static final String PREFIXISO = "report_ISO_";
  private static final String SUFFIX = ".docx";

  @Override
  public void process(Exchange exchange) throws Exception {

    LOG.info("downloadReport process ");

    // gets procedureId filter from input
    String procedureId = null;
    String reportType = null;
    String prefix = PREFIX;
    if (exchange.getIn() != null && exchange.getIn().getBody() != null) {
      Gson gsonFilter = new Gson();
      GenericFilter[] filter = gsonFilter.fromJson(exchange.getIn().getBody().toString(), GenericFilter[].class);
      procedureId = filter[0].getFilterValue(GenericFilterEnum.PROCEDURE);
      reportType = filter[0].getFilterValue(GenericFilterEnum.REPORT_TYPE);
      LOG.info("downloadReport procedure Id " + procedureId);
      LOG.info("downloadReport report Type " + reportType);
    }
    if (reportType != null) {
      switch (reportType) {
        case "LIGHT":
          prefix = PREFIXLIGHT;
          break;
        case "ISO":
          prefix = PREFIXISO;
          break;
      }
    }

    File file = new File(prefix.concat(procedureId).concat(SUFFIX));
    if (!file.exists()) {
      throw new Exception("REPORT_MISSING");
    } else {

      // sets in output the report file
      exchange.getIn().setBody(file);
    }
  }

  public ReportGeneratorInputInterface getReportgeneratorInput() {
    return reportgeneratorInput;
  }

  public void setReportgeneratorInput(ReportGeneratorInputInterface reportgeneratorInput) {
    this.reportgeneratorInput = reportgeneratorInput;
  }
}
