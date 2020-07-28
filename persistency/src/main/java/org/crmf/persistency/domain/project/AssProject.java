/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssProject.java"
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

import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssessmentStatusEnum;
import org.crmf.model.riskassessment.RiskMethodologyEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssProject {
	private Integer id;

	private String name;

	private String description;

	private String methodology;

	private Date creationTime;

	private Date updateTime;

	private String status;

	private Integer sysprojectId;

	private Integer userpm;

	private Integer templateId;

	private Integer profileId;

	private String sestobjId;

	DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private static final Logger LOG = LoggerFactory.getLogger(AssProject.class.getName());

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

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status == null ? null : status.trim();
	}

	public Integer getSysprojectId() {
		return sysprojectId;
	}

	public void setSysprojectId(Integer sysprojectId) {
		this.sysprojectId = sysprojectId;
	}

	public Integer getUserpm() {
		return userpm;
	}

	public void setUserpm(Integer userpm) {
		this.userpm = userpm;
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public Integer getProfileId() {
		return profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	public String getSestobjId() {
		return sestobjId;
	}

	public void setSestobjId(String sestobjId) {
		this.sestobjId = sestobjId;
	}

	public AssessmentProject convertToModel() {

		AssessmentProject project = new AssessmentProject();

		project.setName(this.name);
		project.setDescription(this.description);
		if (this.status != null) {
			project.setStatus(AssessmentStatusEnum.valueOf(this.status));
		}
		if (this.getMethodology() != null) {
			project.setRiskMethodology(RiskMethodologyEnum.valueOf(this.getMethodology()));
		}

		if (this.creationTime != null) {
			project.setCreationTime(df.format(this.creationTime));
		}
		if (this.updateTime != null) {
			project.setUpdateTime(df.format(this.updateTime));
		}
		if (this.sestobjId != null) {
			project.setIdentifier(String.valueOf(this.sestobjId));
		}
		// asstemplate.setProfile(profile);
		// asstemplate.setTemplate(template);
		// asstemplate.setSystemProject(systemProject);
		// asstemplate.setProjectManager(projectManager);

		return project;
	}

	public void convertFromModel(AssessmentProject project) {

		this.setName(project.getName());
		this.setDescription(project.getDescription());
		if (project.getRiskMethodology() != null) {
			this.setMethodology(project.getRiskMethodology().name());
		}
		if (project.getStatus() != null) {
			this.setStatus(project.getStatus().name());
		}

		try {
			if (project.getCreationTime() != null) {
				this.setCreationTime(new Date(df.parse(project.getCreationTime()).getTime()));
			}
			if (project.getUpdateTime() != null) {
				this.setUpdateTime(new Date(df.parse(project.getUpdateTime()).getTime()));
			}
		} catch (ParseException e) {
			LOG.error(e.getMessage());
		}

		if (project.getIdentifier() != null) {
			this.setSestobjId(project.getIdentifier());
		}
		// this.setId(id);
	}
}