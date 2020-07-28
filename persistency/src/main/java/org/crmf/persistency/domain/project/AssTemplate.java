/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssTemplate.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.project;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.riskassessment.PhaseEnum;
import org.crmf.model.riskassessment.RiskMethodologyEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssTemplate {
	private Integer id;

	private String name;

	private String description;

	private String methodology;

	private Date creationTime;

	private String phase;

	private Integer procedureId;

	private Integer profileId;

	private Integer assetId;

	private Integer threatId;

	private Integer vulnerabilityId;

	private Integer safeguardId;

	private String sestobjId;

	private Integer riskModelId;

	DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private static final Logger LOG = LoggerFactory.getLogger(AssTemplate.class.getName());

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null ? null : description.trim();
	}

	public String getMethodology() {
		return methodology;
	}

	public void setMethodology(String methodology) {
		this.methodology = methodology == null ? null : methodology.trim();
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public Integer getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(Integer procedureId) {
		this.procedureId = procedureId;
	}

	public String getSestobjId() {
		return sestobjId;
	}

	public void setSestobjId(String sestobjId) {
		this.sestobjId = sestobjId;
	}

	public Integer getAssetId() {
		return assetId;
	}

	public void setAssetId(Integer assetId) {
		this.assetId = assetId;
	}

	public Integer getThreatId() {
		return threatId;
	}

	public void setThreatId(Integer threatId) {
		this.threatId = threatId;
	}

	public Integer getVulnerabilityId() {
		return vulnerabilityId;
	}

	public void setVulnerabilityId(Integer vulnerabilityId) {
		this.vulnerabilityId = vulnerabilityId;
	}

	public Integer getSafeguardId() {
		return safeguardId;
	}

	public void setSafeguardId(Integer safeguardId) {
		this.safeguardId = safeguardId;
	}

	public Integer getRiskModelId() {
		return riskModelId;
	}

	public void setRiskModelId(Integer riskModelId) {
		this.riskModelId = riskModelId;
	}

//	public Integer getRiskTreatmentModelId() {
//		return riskTreatmentModelId;
//	}
//
//	public void setRiskTreatmentModelId(Integer riskTreatmentModelId) {
//		this.riskTreatmentModelId = riskTreatmentModelId;
//	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public Integer getProfileId() {
		return profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	public AssessmentTemplate convertToModel() {

		AssessmentTemplate asstemplate = new AssessmentTemplate();

		asstemplate.setName(this.name);
		asstemplate.setDescription(this.getDescription());

		if (this.methodology != null) {
			asstemplate.setRiskMethodology(RiskMethodologyEnum.valueOf(this.getMethodology()));
		}		
		if (this.phase != null) {
			asstemplate.setPhase(PhaseEnum.valueOf(this.phase));
		}
		if (this.creationTime != null) {
			asstemplate.setCreationTime(df.format(this.creationTime));
		}
		if (this.sestobjId != null) {
			asstemplate.setIdentifier(String.valueOf(this.sestobjId));
		}

		// asstemplate.setObjType(this.);
		// asstemplate.setAssetModel(assetModel);
		// asstemplate.setRiskModel(riskModel);
		// asstemplate.setSafeguardModel(safeguardModel);
		// asstemplate.setThreatModel(threatModel);
		// asstemplate.setVulnerabilityModel(vulnerabilityModel);
		// asstemplate.setAssociatedProcedure(associatedProcedure);
		return asstemplate;
	}

	public void convertFromModel(AssessmentTemplate asstemplate) {

		this.setName(asstemplate.getName());
		this.setDescription(asstemplate.getDescription());

		if (asstemplate.getPhase() != null) {
			this.setPhase(asstemplate.getPhase().name());
		}

		if (asstemplate.getRiskMethodology() != null) {
			this.setMethodology(asstemplate.getRiskMethodology().name());
		}
		try {
			if (asstemplate.getCreationTime() != null) {
				this.setCreationTime(new Date(df.parse(asstemplate.getCreationTime()).getTime()));
			}
		} catch (ParseException e) {
			LOG.error(e.getMessage());
		}

		if (asstemplate.getIdentifier() != null) {
			this.setSestobjId(asstemplate.getIdentifier());
		}
		
		//TODO: WRONG: ID, not identifier: PUT OUT OF POJO
		//TODO: comment all models when they are integrated 
				// (removing this if code before integration is finished leads to an insert error into the DB
				// cause the models cannot be null)
//		if (asstemplate.getAssetModel() != null) {
//			this.setAssetId(Integer.valueOf(asstemplate.getAssetModel().getIdentifier())); 
//		}
//		if (asstemplate.getRiskModel() != null) {
//			this.setRiskModelId(Integer.valueOf(asstemplate.getRiskModel().getIdentifier()));
//		}
//		if (asstemplate.getSafeguardModel() != null) {
//			this.setSafeguardId(Integer.valueOf(asstemplate.getSafeguardModel().getIdentifier()));
//		}
//		if (asstemplate.getThreatModel() != null) {
//			this.setThreatId(Integer.valueOf(asstemplate.getThreatModel().getIdentifier()));
//		}
//		if (asstemplate.getVulnerabilityModel() != null) {
//			this.setVulnerabilityId(Integer.valueOf(asstemplate.getVulnerabilityModel().getIdentifier()));
//		}
		// this.setId(id);
	}
}