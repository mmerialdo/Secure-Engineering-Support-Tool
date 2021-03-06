/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatModelManagerInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.core.threatmodel.manager;


import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.ModelObject;
import org.crmf.model.utility.TaxonomyReferenceBuilder;
import org.crmf.persistency.mapper.general.SestObjServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.threat.ThreatServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

//This class is called by the Proxy and manages the entrypoint for the business logic (including the interactions with the Persistency) related to the ThreatModel
@Service
public class ThreatModelManagerInput {
  // the logger of ThreatModelManagerInput class
  private static final Logger LOG = LoggerFactory.getLogger(ThreatModelManagerInput.class.getName());
  // Threat service variable of persistency component
  @Autowired
  @Qualifier("default")
  private ThreatServiceInterface threatService;
  // Procedure service variable of persistency component
  @Autowired
  @Qualifier("default")
  private AssprocedureServiceInterface assprocedureService;
  @Autowired
  @Qualifier("default")
  private SestObjServiceInterface sestObjService;
  @Autowired
  private TaxonomyReferenceBuilder taxonomyReferenceBuilder;

  public void editThreatModel(String threatModelJson, String threatModelIdentifier) {
    LOG.info("editThreatModel with identifier: {} and Json {}", threatModelIdentifier, threatModelJson.substring(0, (threatModelJson.length() > 1000 ? 1000 : threatModelJson.length())));
    // updateQuestionnaireJSON the json threat model whose identifier is the threat model identifier in input
    threatService.update(threatModelJson, threatModelIdentifier);
  }

  public ModelObject loadThreatModel(GenericFilter filter) throws Exception {
    // get the procedure identifier passed in input
    String procedureIdentifier = filter.getFilterValue(GenericFilterEnum.PROCEDURE);
    LOG.debug("loadThreatModel:: input procedure filter = {} ", procedureIdentifier);

    // if the value of procedure identifier is not null
    if (procedureIdentifier != null) {
      // retrieve the assessment procedure associated to the procedure
      // identifier in input
      AssessmentProcedure procedure = assprocedureService.getByIdentifierFull(procedureIdentifier);

      // retrieve the threat model identifier associated to the
      // retrieved assessment procedure
      String sestobjId = procedure.getThreatModel().getIdentifier();

      // return the json threat model associated to the
      // threat model identifier retrieved
      ModelObject modelObject = new ModelObject();
      modelObject.setJsonModel(threatService.getByIdentifier(sestobjId).getThreatModelJson());
      modelObject.setObjectIdentifier(sestobjId);
      modelObject.setLockedBy(sestObjService.getByIdentifier(sestobjId).getLockedBy());
      return modelObject;
    } else {
      throw new Exception("Incorrect procedure identifier in input");
    }
  }

  public String loadThreatRepository(GenericFilter filter) {
    // This method load all threats saved in the database, for a given Catalogue (MEHARI, etc..)
    String catalogue = filter.getFilterValue(GenericFilterEnum.METHODOLOGY);
    LOG.info("loadThreatRepository {}", catalogue);
    return threatService.getThreatRepository(catalogue).getThreatModelJson();
  }

  public String createThreat(Threat threat) throws Exception {
    Threat threatChecked = taxonomyReferenceBuilder.threatCheckAndFill(threat);
    return threatService.insertThreatReference(threatChecked);
  }

  public void editThreat(Threat threat) throws Exception {
    Threat threatChecked = taxonomyReferenceBuilder.threatCheckAndFill(threat);
    threatService.editThreatReference(threatChecked);
  }

  public void deleteThreat(List<String> identifier) throws Exception {
    threatService.deleteThreatReference(identifier);
  }
}
