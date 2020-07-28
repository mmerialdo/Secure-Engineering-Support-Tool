INSERT INTO SESTOBJ(IDENTIFIER, OBJTYPE) VALUES (1, 'AssetModel');
INSERT into ASSET_MODEL(ASSET_MODEL_JSON, SESTOBJ_ID) VALUES ('{
  "creationTime": " ",
  "updateTime": null,
  "edges": [],
  "nodes": [],
  "graphJson": [],
  "identifier": "1",
  "objType": "AssetModel"
}','1');


INSERT INTO SESTOBJ(IDENTIFIER, OBJTYPE) VALUES (2, 'VulnerabilityModel');
INSERT into VULNERABILITY_MODEL(VULNERABILITY_MODEL_JSON, SESTOBJ_ID) VALUES ('{
  "creationTime": "",
  "updateTime": null,
  "vulnerabilities": [],
  "identifier": "2",
  "objType": "VulnerabilityModel"
}','2');

INSERT INTO SESTOBJ(IDENTIFIER, OBJTYPE) VALUES (3, 'RiskModel');
INSERT into RISK_MODEL(RISK_MODEL_JSON, SESTOBJ_ID) VALUES ('{
  "creationTime": "",
  "updateTime": null,
  "scenarios": [],
  "identifier": "3",
  "objType": "RiskModel"
}','3');


INSERT INTO SESTOBJ(IDENTIFIER, OBJTYPE) VALUES (4, 'ThreatModel');
INSERT into THREAT_MODEL(THREAT_MODEL_JSON, SESTOBJ_ID) VALUES ('{
  "creationTime": "",
  "updateTime": null,
  "threats": [],
  "identifier": "4",
  "objType": "ThreatModel"
}','4');


INSERT INTO SESTOBJ(IDENTIFIER, OBJTYPE) VALUES (5, 'AssessmentTemplate');
INSERT INTO ASSTEMPLATE(NAME, DESCRIPTION, METHODOLOGY, PHASE, ASSET_ID, THREAT_ID, VULNERABILITY_ID, SAFEGUARD_ID, RISKMODEL_ID, SESTOBJ_ID)
		VALUES ('DEFAULT_TEMPLATE', 'TEMPLATE TO STARTUP', 'Mehari', 'Initial', '1', '1', '1', '1', '1', '5');

	
INSERT INTO SESTOBJ(IDENTIFIER, OBJTYPE) VALUES (6, 'User');	
INSERT INTO SESTUSER(EMAIL, NAME, PASSWORD, PROFILE, SURNAME, USERNAME, SESTOBJ_ID) 	
		VALUES('abcd@rhea.com', 'SysAdmin', 'Abcd1234!', 'Administrator', 'Administrator', 'admin01', '6');

INSERT INTO SESTOBJ(IDENTIFIER, OBJTYPE) VALUES (7, 'SafeguardModel');
INSERT into SAFEGUARD_MODEL(SAFEGUARD_MODEL_JSON, SESTOBJ_ID) VALUES ('{
  "creationTime": "",
  "updateTime": null,
  "safeguards": [],
  "identifier": "7",
  "objType": "SafeguardModel"
}','7');

INSERT INTO SESTOBJ(IDENTIFIER, OBJTYPE) VALUES (8, 'RiskTreatmentModel');
INSERT into RISKTREATMENT_MODEL(RISKTREATMENT_MODEL_JSON, SESTOBJ_ID) VALUES ('{
  "creationTime": "",
  "updateTime": null,
  "risktreatment": [],
  "identifier": "8",
  "objType": "RiskTreatmentModel"
}','8');


