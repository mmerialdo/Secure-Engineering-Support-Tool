/* ---------------------------------------------------- */
/*  Generated by Enterprise Architect Version 13.0 		*/
/*  Created On : 11-Nov-2016 15:40:43 				*/
/*  DBMS       : MySql 						*/
/* ---------------------------------------------------- */

SET FOREIGN_KEY_CHECKS=0 
;

/* Drop Tables */

DROP TABLE IF EXISTS `Answer` CASCADE
;

DROP TABLE IF EXISTS `User` CASCADE
;

/* Create Tables */

CREATE TABLE `Answer`
(
	`Index` varchar(0) NULL,
	`Type` varchar(0) NULL,
	`Value` varchar(0) NULL,
	`AnswerID` varchar(0) NOT NULL,
	`QuestionID` varchar(0) NULL,
	CONSTRAINT `PK_Answer` PRIMARY KEY (`AnswerID` ASC)
)

;

CREATE TABLE `User`
(
	`Email` varchar(0) NULL,
	`Name` varchar(0) NULL,
	`Password` varchar(0) NULL,
	`Profile` varchar(0) NULL,
	`Surname` varchar(0) NULL,
	`Username` varchar(0) NULL,
	`UserID` varchar(0) NOT NULL,
	`AssessmentprojectID` varchar(0) NULL,
	`experience` varchar(0) NOT NULL,
	`AssessmentprocedureID` varchar(50) NULL,
	CONSTRAINT `PK_User` PRIMARY KEY (`UserID` ASC)
)

;

/* Create Foreign Key Constraints */

ALTER TABLE `Answer` 
 ADD CONSTRAINT `FK_Answer_Question`
	FOREIGN KEY (`QuestionID`) REFERENCES `Question` (`QuestionID`) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE `Answer` 
 ADD CONSTRAINT `FK_Answer_SESTObject`
	FOREIGN KEY (`AnswerID`) REFERENCES `Sestobject` (`SestobjectID`) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE `User` 
 ADD CONSTRAINT `FK_User_users`
	FOREIGN KEY (`AssessmentprojectID`) REFERENCES `Assessmentproject` (`AssessmentprojectID`) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE `User` 
 ADD CONSTRAINT `FK_User_projectManager`
	FOREIGN KEY (`AssessmentprojectID`) REFERENCES `Assessmentproject` (`AssessmentprojectID`) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE `User` 
 ADD CONSTRAINT `FK_User_SESTObject`
	FOREIGN KEY (`UserID`) REFERENCES `Sestobject` (`SestobjectID`) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE `User` 
 ADD CONSTRAINT `FK_User_Userexperience`
	FOREIGN KEY (`experience`) REFERENCES `Userexperience` (`UserexperienceID`) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE `User` 
 ADD CONSTRAINT `FK_User_lastUserUpdate`
	FOREIGN KEY (`AssessmentprocedureID`) REFERENCES `Assessmentprocedure` (`AssessmentprocedureID`) ON DELETE No Action ON UPDATE No Action
;

SET FOREIGN_KEY_CHECKS=1 
;
