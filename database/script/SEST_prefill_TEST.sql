	  INSERT INTO 
	  ASSAUDIT_DEFAULT(`ID`, `IX`, `AVALUE`, `ATYPE`, `CATEGORY`, `PARENT`, `VW`, `VMAX`, `VMIN`, `VTYPE`, `VISO13`, `VISO5`, `QUESTIONNAIRE_JSON`) 
	  VALUES (1, 1, 'MEHARI_OrganizationSecurity', 'QUESTIONNAIRE', '01', '', '', '', '', '', '', '', null);
	  INSERT INTO 
	  ASSAUDIT_DEFAULT(`ID`, `IX`, `AVALUE`, `ATYPE`, `CATEGORY`, `PARENT`, `VW`, `VMAX`, `VMIN`, `VTYPE`, `VISO13`, `VISO5`, `QUESTIONNAIRE_JSON`)
	  VALUES(2, 1,'CATEGORY','Roles and structures ...','01A', '01', '', '', '', '', '', '', null);
	  INSERT INTO
	  ASSAUDIT_DEFAULT(`ID`, `IX`, `AVALUE`, `ATYPE`, `CATEGORY`, `PARENT`, `VW`, `VMAX`, `VMIN`, `VTYPE`, `VISO13`, `VISO5`, `QUESTIONNAIRE_JSON`) 
	  VALUES(3, 2,'CATEGORY','Organization and piloting...','02A', '01', '', '', '', '', '', '',
	  null); 
	  INSERT INTO
	  ASSAUDIT_DEFAULT(`ID`, `IX`, `AVALUE`, `ATYPE`, `CATEGORY`, `PARENT`, `VW`, `VMAX`, `VMIN`, `VTYPE`, `VISO13`, `VISO5`, `QUESTIONNAIRE_JSON`) 
	  VALUES(4, 1,'QUESTION','Do all areas
	  ..','01A01','01A', '', '', '', '', '', '', null);
	  INSERT INTO
	  ASSAUDIT_DEFAULT(`ID`, `IX`, `AVALUE`, `ATYPE`, `CATEGORY`, `PARENT`, `VW`, `VMAX`, `VMIN`, `VTYPE`, `VISO13`, `VISO5`, `QUESTIONNAIRE_JSON`) 
	  VALUES(5, 2,'QUESTION','Do other areas
	  ..','01A02','01A','', '', '', '', '', '', null);
	  INSERT INTO
	  ASSAUDIT_DEFAULT(`ID`, `IX`, `AVALUE`, `ATYPE`, `CATEGORY`, `PARENT`, `VW`, `VMAX`, `VMIN`, `VTYPE`, `VISO13`, `VISO5`, `QUESTIONNAIRE_JSON`) 
	  VALUES(6, 1,'QUESTION','Do again other areas
	  ..','02A01','02A','', '', '', '', '', '', null);