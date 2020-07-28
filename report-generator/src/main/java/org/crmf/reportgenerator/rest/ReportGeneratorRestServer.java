/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="ReportGeneratorRestServer.java"
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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.reportgenerator.manager.ReportGeneratorInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportGeneratorRestServer {

	private ReportGeneratorInputInterface reportgeneratorInput;
	private static final Logger LOG = LoggerFactory.getLogger(ReportGeneratorRestServer.class.getName());

	@POST
	@Produces("text/html")
	@Consumes("text/html")
	public String editReport(GenericFilter filter) throws Exception {
		LOG.info("editReport " + filter.getFilterValue(GenericFilterEnum.PROCEDURE));
		try {
			return reportgeneratorInput.editReport(filter.getFilterValue(GenericFilterEnum.PROCEDURE));
			
		} catch (Exception e) {
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@POST
	@Produces("text/html")
	@Consumes("text/html")
	public String editLightReport(GenericFilter filter) throws Exception {
		LOG.info("editLightReport " + filter.getFilterValue(GenericFilterEnum.PROCEDURE) + " " + ImpactEnum.valueOf(filter.getFilterValue(GenericFilterEnum.IMPACT)));
		try {
			return reportgeneratorInput.editLightReport(filter.getFilterValue(GenericFilterEnum.PROCEDURE),
				ImpactEnum.valueOf(filter.getFilterValue(GenericFilterEnum.IMPACT)));

		} catch (Exception e) {
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@POST
	@Produces("text/html")
	@Consumes("text/html")
	public String editISOReport(GenericFilter filter) throws Exception {
		LOG.info("editISOReport " + filter.getFilterValue(GenericFilterEnum.PROCEDURE));
		try {
			return reportgeneratorInput.editISOReport(filter.getFilterValue(GenericFilterEnum.PROCEDURE));

		} catch (Exception e) {
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@POST
	@Path("/load")
	@Produces("application/octet-stream")
	@Consumes("application/json")
	public byte[] downloadReport(String filterS) {
		LOG.info("downloadReport ");

		//no action as this code is not reached from cxfrs web service. Processor is used to perform action.
		return null;
	}

	public ReportGeneratorInputInterface getReportgeneratorInput() {
		return reportgeneratorInput;
	}

	public void setReportgeneratorInput(ReportGeneratorInputInterface reportgeneratorInput) {
		this.reportgeneratorInput = reportgeneratorInput;
	}

}
