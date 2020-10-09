/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SysPartecipantTest.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency;

import org.crmf.model.riskassessment.SystemParticipant;
import org.crmf.model.riskassessment.SystemProject;
import org.crmf.persistency.mapper.project.SysparticipantService;
import org.crmf.persistency.mapper.project.SysprojectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
@MybatisTest
@Import({SysparticipantService.class, SysprojectService.class})
@ContextConfiguration(classes=Application.class)
@ActiveProfiles("test")
public class SysPartecipantTest {

	@Autowired
	private SysparticipantService syspartecipantService;
	@Autowired
	private SysprojectService sysprojectService;

	@Test
	public void getByProjectIdTest() throws Exception{
		
		Integer sysprjId = fillSysprj();
		fill(sysprjId);
	
		ArrayList<SystemParticipant> participants = syspartecipantService.getByProjectId(sysprjId);
		Assertions.assertEquals(4, participants.size());
	}

	@Test
	public void updateTest() throws Exception{
		
		Integer sysprjId = fillSysprj();
		fill(sysprjId);

		ArrayList<SystemParticipant> participants = syspartecipantService.getByProjectId(sysprjId);
		participants.remove(0);
		participants.get(0).setName("updated01");
		participants.get(1).setRole("dev");
		SystemParticipant sp01 = new SystemParticipant();
		sp01.setName("new01");
		sp01.setRole("dev");
		SystemParticipant sp02 = new SystemParticipant();
		sp02.setName("new02");
		sp02.setRole("test");
		participants.add(sp01);
		participants.add(sp02);
	
		syspartecipantService.update(participants, sysprjId);
		
		participants = syspartecipantService.getByProjectId(sysprjId);
		Assertions.assertEquals(5, participants.size());
		boolean updated01 = false;
		boolean new01 = false;
		int dev = 0;
		for (SystemParticipant systemParticipant : participants) {
			if(systemParticipant.getName().equals("updated01")){
				updated01 = true;
			} else if(systemParticipant.getName().equals("new01")){
				new01 = true;
			} 
			if(systemParticipant.getRole().equals("dev")){
				++dev;
			}
		}
		Assertions.assertTrue(updated01);
		Assertions.assertTrue(new01);
		Assertions.assertEquals(2, dev);
	}
	
	private void fill(Integer sysprjId) throws Exception {
		SystemParticipant sp01 = new SystemParticipant();
		sp01.setName("test01");
		sp01.setRole("pm");
		SystemParticipant sp02 = new SystemParticipant();
		sp02.setName("test02");
		sp02.setRole("pm");
		SystemParticipant sp03 = new SystemParticipant();
		sp03.setName("test03");
		sp03.setRole("pm");
		SystemParticipant sp04 = new SystemParticipant();
		sp04.setName("test04");
		sp04.setRole("pm");
		
		ArrayList<SystemParticipant> participants = new ArrayList<>();
		participants.add(sp01);
		participants.add(sp02);
		participants.add(sp03);
		participants.add(sp04);
		
		syspartecipantService.insert(participants, sysprjId);
	}

	private Integer fillSysprj() throws Exception {
		SystemProject systemprj = new SystemProject();
		systemprj.setDescription("sysprj test desc");
		systemprj.setMandate("sysprj mandate test");
		systemprj.setName("sysprjname");
		systemprj.setScope("scopex");
		Integer sysprjId = sysprojectService.insert(systemprj);
		return sysprjId;
	}
}
