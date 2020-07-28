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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.crmf.model.riskassessment.SystemParticipant;
import org.crmf.model.riskassessment.SystemProject;
import org.crmf.persistency.mapper.general.CleanDatabaseService;
import org.crmf.persistency.mapper.project.SysparticipantService;
import org.crmf.persistency.mapper.project.SysprojectService;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SysPartecipantTest {

	PersistencySessionFactory sessionFactory;
	private SysparticipantService syspartecipantService;
	private SysprojectService sysprojectService;
	
	@Before
	public void setUp() throws Exception {

		sessionFactory = new PersistencySessionFactory();
		sessionFactory.createSessionFactory();

		syspartecipantService = new SysparticipantService();
		syspartecipantService.setSessionFactory(sessionFactory);

		sysprojectService = new SysprojectService();
		sysprojectService.setSessionFactory(sessionFactory);
		
	}

	@After
	public void tearDown() throws Exception {

		CleanDatabaseService cleaner = new CleanDatabaseService();
		cleaner.setSessionFactory(sessionFactory);
		
		cleaner.delete();
	}
	
	@Test
	public void getByProjectIdTest() throws Exception{
		
		Integer sysprjId = fillSysprj();
		fill(sysprjId);
	
		ArrayList<SystemParticipant> participants = syspartecipantService.getByProjectId(sysprjId);
		assertEquals(4, participants.size());
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
		assertEquals(5, participants.size());
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
		assertTrue(updated01);
		assertTrue(new01);
		assertEquals(2, dev);
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
