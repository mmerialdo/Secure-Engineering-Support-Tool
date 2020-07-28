/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="PersistencySessionFactory.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.crmf.persistency.mapper.asset.AssetMapper;
import org.crmf.persistency.mapper.audit.AssAuditDefaultMapper;
import org.crmf.persistency.mapper.audit.AssAuditMapper;
import org.crmf.persistency.mapper.audit.QuestionnaireMapper;
import org.crmf.persistency.mapper.general.CleanDatabaseMapper;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.crmf.persistency.mapper.project.AssprocedureMapper;
import org.crmf.persistency.mapper.project.AssprofileMapper;
import org.crmf.persistency.mapper.project.AssprojectMapper;
import org.crmf.persistency.mapper.project.AsstemplateMapper;
import org.crmf.persistency.mapper.project.SysparticipantMapper;
import org.crmf.persistency.mapper.project.SysprojectMapper;
import org.crmf.persistency.mapper.requirement.RequirementMapper;
import org.crmf.persistency.mapper.risk.RiskMapper;
import org.crmf.persistency.mapper.risk.RiskTreatmentMapper;
import org.crmf.persistency.mapper.safeguard.SafeguardMapper;
import org.crmf.persistency.mapper.secrequirement.SecRequirementMapper;
import org.crmf.persistency.mapper.threat.ThreatMapper;
import org.crmf.persistency.mapper.user.RoleMapper;
import org.crmf.persistency.mapper.user.UserMapper;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityMapper;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistencySessionFactory {

	private static final Logger LOG = LoggerFactory.getLogger(PersistencySessionFactory.class.getName());

	private SqlSessionFactory sessionFactory;
	private BundleContext context;

	public void createSessionFactory() {

		String pathConfig = "config".concat(File.separator).concat("config_test.xml");
		LOG.info("pathConfig " + pathConfig);
		Reader reader = null;
		Properties prop = null;
		try {
			if (context == null) { //if not OSGI bundle -> unit test or Spring configuration
				reader = Resources.getResourceAsReader(pathConfig);
			} else {
				prop = new Properties();
				prop.load(new FileInputStream("etc".concat(File.separator).concat("SEST.cfg")));
				
				URL url = context.getBundle().getEntry("config/config.xml");
				if (url != null) {
					reader = new InputStreamReader(url.openStream());
				} else {
					LOG.error("Unvalid path " + pathConfig);
					return;
				}
			}
			LOG.info("reader " + reader);
			
			this.sessionFactory = new SqlSessionFactoryBuilder().build(reader, prop);
			this.sessionFactory.getConfiguration().addMapper(UserMapper.class);
			this.sessionFactory.getConfiguration().addMapper(SestobjMapper.class);
			this.sessionFactory.getConfiguration().addMapper(RoleMapper.class);
			this.sessionFactory.getConfiguration().addMapper(CleanDatabaseMapper.class);
			this.sessionFactory.getConfiguration().addMapper(AssprocedureMapper.class);
			this.sessionFactory.getConfiguration().addMapper(AssprofileMapper.class);
			this.sessionFactory.getConfiguration().addMapper(AssprojectMapper.class);
			this.sessionFactory.getConfiguration().addMapper(AsstemplateMapper.class);
			this.sessionFactory.getConfiguration().addMapper(SysprojectMapper.class);
			this.sessionFactory.getConfiguration().addMapper(SysparticipantMapper.class);
			this.sessionFactory.getConfiguration().addMapper(AssAuditDefaultMapper.class);
			this.sessionFactory.getConfiguration().addMapper(QuestionnaireMapper.class);
			this.sessionFactory.getConfiguration().addMapper(AssAuditMapper.class);
			this.sessionFactory.getConfiguration().addMapper(RequirementMapper.class);
			this.sessionFactory.getConfiguration().addMapper(AssetMapper.class);
			this.sessionFactory.getConfiguration().addMapper(VulnerabilityMapper.class);
			this.sessionFactory.getConfiguration().addMapper(ThreatMapper.class);
			this.sessionFactory.getConfiguration().addMapper(RiskMapper.class);
			this.sessionFactory.getConfiguration().addMapper(SafeguardMapper.class);
			this.sessionFactory.getConfiguration().addMapper(SecRequirementMapper.class);
			this.sessionFactory.getConfiguration().addMapper(RiskTreatmentMapper.class);
			
		} catch (IOException e) {
			LOG.error(e.getMessage());
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}
	}

	public SqlSession getSession() {

		SqlSession session = this.sessionFactory.openSession();
		return session;
	}

	public SqlSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SqlSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public BundleContext getContext() {
		return context;
	}

	public void setContext(BundleContext context) {
		this.context = context;
	}

}
