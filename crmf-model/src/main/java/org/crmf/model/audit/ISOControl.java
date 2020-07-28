/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="ISOControl.java"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

package org.crmf.model.audit;

public class ISOControl {
    private String clauseId;
    private String clause;
    private String objectiveId;
    private String objective;
    private String controlId;
    private String control;
    private String controlJson;


    // Getter Methods

    public String getClauseId() {
        return clauseId;
    }

    public String getClause() {
        return clause;
    }

    public String getObjectiveId() {
        return objectiveId;
    }

    public String getObjective() {
        return objective;
    }

    public String getControlId() {
        return controlId;
    }

    public String getControl() {
        return control;
    }

    // Setter Methods

    public void setClauseId(String clauseId) {
        this.clauseId = clauseId;
    }

    public void setClause(String clause) {
        this.clause = clause;
    }

    public void setObjectiveId(String objectiveId) {
        this.objectiveId = objectiveId;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public void setControlId(String controlId) {
        this.controlId = controlId;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public String getControlJson() {
        return controlJson;
    }

    public void setControlJson(String controlJson) {
        this.controlJson = controlJson;
    }
}
