/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SysParticipant.java"
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

import org.crmf.model.riskassessment.SystemParticipant;

public class SysParticipant {
    private Integer id;

    private String name;

    private String surname;

    private String role;

    private Integer sysprojectId;

	private String sestobjId;
	
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname == null ? null : surname.trim();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role == null ? null : role.trim();
    }

    public Integer getSysprojectId() {
        return sysprojectId;
    }

    public void setSysprojectId(Integer sysprojectId) {
        this.sysprojectId = sysprojectId;
    }
    
	public String getSestobjId() {
		return sestobjId;
	}

	public void setSestobjId(String sestobjId) {
		this.sestobjId = sestobjId;
	}

	public SystemParticipant convertToModel() {

		SystemParticipant participant = new SystemParticipant();
		
		participant.setName(this.name);
		participant.setRole(this.role);
		participant.setSurname(this.surname);

		if (this.sestobjId != null) {
			participant.setIdentifier(String.valueOf(this.sestobjId));
		}
		
		return participant;
	}

	public void convertFromModel(SystemParticipant participant) {

		this.setName(participant.getName());
		this.setRole(participant.getRole());
		this.setSurname(participant.getSurname());

		if (participant.getIdentifier() != null) {
			this.setSestobjId(participant.getIdentifier());
		}
	}
}