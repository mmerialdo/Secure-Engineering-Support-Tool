/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProcedureRestServer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.core.riskassessment.project.rest;

import java.util.ArrayList;
import java.util.List;

import org.crmf.core.riskassessment.project.manager.AssessmentProcedureInputInterface;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.utility.GenericFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the business logic behind the webservices related to the AssessmentProcedures management
public class AssessmentProcedureRestServer
  implements AssessmentProcedureRestServerInterface {

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentProcedureRestServer.class.getName());
  private AssessmentProcedureInputInterface procedureInput;

  @Override
  public String createAssessmentProcedure(AssessmentProject procedurePrj) throws Exception {
    try {
      LOG.info("createAssessmentProcedure, procedure with name " + procedurePrj.getProcedures().get(0),
        procedurePrj.getName());

      String result = procedureInput.createAssessmentProcedure(procedurePrj.getProcedures().get(0),
        procedurePrj.getIdentifier());
      if (result == null) {
        throw new Exception("COMMAND_EXCEPTION");
      } else {
        return result;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  @Override
  public void editAssessmentProcedure(AssessmentProcedure procedure) throws Exception {
    try {
      LOG.info("editAssessmentProject, project with name " + procedure.getName());

      procedureInput.editAssessmentProcedure(procedure);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  @Override
  public String deleteAssessmentProcedure(String identifier) throws Exception {
    try {
      LOG.info("deleteAssessmentProcedure, procedure with identifier " + identifier);

      procedureInput.deleteAssessmentProcedure(identifier);

      return identifier;

    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    }

  }

  @Override
  public List<AssessmentProcedure> loadAssessmentProcedureList(String token, String permission) {

    try {
      return procedureInput.loadAssessmentProcedureList();
    } catch (Exception e) {
      LOG.error("Exception in loadAssessmentProcedureList: " + e.getMessage(), e);
    }
    return null;
  }

  @Override
  public List<String> loadAssessmentProcedure(GenericFilter filter) throws Exception {
    List<String> jsonProcedures = new ArrayList<String>();

    LOG.info("loadAssessmentProcedure " + filter);
    try {
      List<AssessmentProcedure> procedures = procedureInput.loadAssessmentProcedure(filter);
      LOG.info("loadAssessmentProcedure procedures size: " + procedures.size());

      if (procedures != null) {
        for (AssessmentProcedure p : procedures)
          jsonProcedures.add(p.convertToJson());
      }
      LOG.info("loadAssessmentProcedure procedures after convert");
      return jsonProcedures;
    } catch (Exception e) {
      LOG.error("Exception in loadAssessmentProcedure: " + e.getMessage(), e);
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  public AssessmentProcedureInputInterface getProcedureInput() {
    return procedureInput;
  }

  public void setProcedureInput(AssessmentProcedureInputInterface procedureInput) {
    this.procedureInput = procedureInput;
  }
}
