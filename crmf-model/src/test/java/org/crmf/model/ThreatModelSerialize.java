/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatModelSerialize.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.PhaseEnum;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessmentelements.ApplicablePlatform;
import org.crmf.model.riskassessmentelements.ElementTypeEnum;
import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.crmf.model.riskassessmentelements.LikelihoodEnum;
import org.crmf.model.riskassessmentelements.SecondaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.SecurityImpact;
import org.crmf.model.riskassessmentelements.SecurityImpactScopeEnum;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.riskassessmentelements.ThreatAccess;
import org.crmf.model.riskassessmentelements.ThreatActor;
import org.crmf.model.riskassessmentelements.ThreatClassEnum;
import org.crmf.model.riskassessmentelements.ThreatEvent;
import org.crmf.model.riskassessmentelements.ThreatPlace;
import org.crmf.model.riskassessmentelements.ThreatProcess;
import org.crmf.model.riskassessmentelements.ThreatScore;
import org.crmf.model.riskassessmentelements.ThreatScoreEnum;
import org.crmf.model.riskassessmentelements.ThreatSourceEnum;
import org.crmf.model.riskassessmentelements.ThreatTime;
import org.crmf.model.utility.threatmodel.ThreatModelSerializerDeserializer;
import org.crmf.model.utility.vulnerabilitymodel.VulnerabilityModelSerializerDeserializer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreatModelSerialize {

	private static final Logger LOG = LoggerFactory.getLogger(ThreatModelSerialize.class.getName());
	@Test
	public void serializeThreatModel() throws IOException {
        
		ThreatModelSerializerDeserializer tmSerDes = new ThreatModelSerializerDeserializer();
		
		ThreatModel tm = new ThreatModel();
		UUID uuid = UUID.randomUUID();
		tm.setIdentifier(uuid.toString());
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date now = new Date();
		tm.setCreationTime(df.format(now));
		tm.setUpdateTime(df.format(now));
		tm.setObjType(SESTObjectTypeEnum.ThreatModel);
		
		
		/*//IF.L.Exp-Exp----
		assetsCategory.add(SecondaryAssetCategoryEnum.Data_File);
		assetsCategory.add(SecondaryAssetCategoryEnum.Electronic_Media);
		assetsCategory.add(SecondaryAssetCategoryEnum.Non_Electronic_Media);
		assetsCategory.add(SecondaryAssetCategoryEnum.Software_Configuration);
		
		associatedVulnerabilities.add("Fic.eff");
		associatedVulnerabilities.add("Med.ine");
		associatedVulnerabilities.add("Cfl.eff");
		
		
		tm.getThreats().add(createMehariThreat(false, ThreatSourceEnum.MEHARI, "IF.L.Exp-Exp----", "production incident within the production premises (Accidental)", "Accidental Production Incident-IF.L.Exp-Exp----", ThreatClassEnum.Accidental, LikelihoodEnum.HIGH, ThreatScoreEnum.HIGH, assetsCategory, associatedVulnerabilities, "IF.L.Exp", "Exp", "", "", "", ""));
		assetsCategory.clear();
		associatedVulnerabilities.clear();
		
		
		//ER.P.Pro.Exp--Ain--Ual
		assetsCategory.add(SecondaryAssetCategoryEnum.Data_File);
		
		associatedVulnerabilities.add("Fic.eff");
		associatedVulnerabilities.add("Fic.alt");
		
		
		tm.getThreats().add(createMehariThreat(false, ThreatSourceEnum.MEHARI, "ER.P.Pro-Exp--Ain--Ual", "error by a user authorized legitimately, connected from the internal network (Error)", "Legitimate user error -ER.P-Pro.Exp--Ain--Ual", ThreatClassEnum.Error, LikelihoodEnum.HIGH, ThreatScoreEnum.HIGH, assetsCategory, associatedVulnerabilities, "ER.P.Pro", "Exp", "", "Ain", "", "Ual"));
		assetsCategory.clear();
		associatedVulnerabilities.clear();*/
		
		readScenariosCSV(tm);
		
		
		String jsonString = tmSerDes.getJSONStringFromTM(tm);
		
		FileWriter fw = new FileWriter(".//json/threatmodel.json");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(jsonString);
		bw.close();
		fw.close();
	}
	
	private void readScenariosCSV(ThreatModel tm){
		String csvFile = ".//json/scenarios.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] scenarioString = line.split(cvsSplitBy);
                
                String eventType = scenarioString[7];
            	String eventSubType = scenarioString[8];
            	String place = scenarioString[9];
            	if(place == null){
            		place = "";
            	}
            	String time = scenarioString[10];
            	if(time == null){
            		time = "";
            	}
            	String access = scenarioString[11];
            	if(access == null){
            		access = "";
            	}
            	String process = scenarioString[12];
            	if(process == null){
            		process = "";
            	}
            	String actor = scenarioString[13];
            	if(actor == null){
            		actor = "";
            	}
            	String type = scenarioString[14];
            	if(type == null){
            		type = "";
            	}
            	
            	// vulnerability supporting asset
            	String category = scenarioString[4];
            	// vulnerability code
            	String vulnerability = scenarioString[6];

            	boolean alreadyExistingThreat = false;
            	Threat threat = null;
            	
            	// for each threat (empty at the beginning, adding the threats incrementally into the else block)
                for(Threat threatInList : tm.getThreats()){
                	
                	if(threatInList.getEvent().getCatalogueId().equals(eventType + "." + eventSubType) && threatInList.getPlace().getCatalogueId().equals(place) &&
                			threatInList.getTime().getCatalogueId().equals(time) && threatInList.getAccess().getCatalogueId().equals(access) &&
                			threatInList.getProcess().getCatalogueId().equals(process) && threatInList.getActor().getCatalogueId().equals(actor)){
                		alreadyExistingThreat = true;
                		threat = threatInList;
                		break;
                	}
                }
                
                if(alreadyExistingThreat){
                	//translate the Vulnerability supporting Asset into our ENUM standard
                	ArrayList<SecondaryAssetCategoryEnum> categories =  checkSecondaryAssetCategory(category);
                	
                	ArrayList<SecondaryAssetCategoryEnum> categoriesToAdd = new ArrayList<>();
                	
                	// update the list of secondary assets associated to the threat
                	for(SecondaryAssetCategoryEnum cate : categories){
                		boolean existingCategory = false;
                		
                		for(SecondaryAssetCategoryEnum cat : threat.getAffectedAssetsCategories()){
                			if(cate.equals(cat)){
                				existingCategory = true;
                				break;
                			}
                		}
                		
                		if(!existingCategory){
                			categoriesToAdd.add(cate);
                		}
                	}
                	
                	threat.getAffectedAssetsCategories().addAll(categoriesToAdd);
                	
                	// update the list of vulnerabilities associated to the threat
                	boolean existingVulnerability = false;
                	for(String vuln : threat.getAssociatedVulnerabilities()){
                		if(vulnerability.equals(vuln)){
                			existingVulnerability = true;
                			break;
                		}
                	}
                	
                	if(!existingVulnerability){
                		threat.getAssociatedVulnerabilities().add(vulnerability);
                	}
                	
                }
                else{
                	//translate the Vulnerability supporting Asset into our ENUM standard
                	ArrayList<SecondaryAssetCategoryEnum> assetsCategory = checkSecondaryAssetCategory(category);
                	// add the  vulnerability code associated to the threat
                	ArrayList<String> associatedVulnerabilities = new  ArrayList<>();
            		associatedVulnerabilities.add(vulnerability);
            		
            		ThreatClassEnum threatType = ThreatClassEnum.Accidental;
            		
            		// translate the AEM value into SEST standard
            		if(type.equals("E")){
            			threatType = ThreatClassEnum.Error;
            		}
            		if(type.equals("M")){
            			threatType = ThreatClassEnum.Deliberate;
            		}
            		
            		// create the threat event
            		String event = eventType + "." + eventSubType;
            		//read the natural exposure csv and retrieve the value associated to threat event
            		String naturalExposureString = readNaturalExposureCSV(event);
            		String eventDescription = readEventDescription(event);
            		LikelihoodEnum likelihood = LikelihoodEnum.VERY_HIGH;
            		ThreatScoreEnum score = ThreatScoreEnum.VERY_HIGH;
            		
            		if(naturalExposureString.equals("1")){
            			likelihood = LikelihoodEnum.LOW;
            			score = ThreatScoreEnum.LOW;
            		}
            		if(naturalExposureString.equals("2")){
            			likelihood = LikelihoodEnum.MEDIUM;
            			score = ThreatScoreEnum.MEDIUM;
            		}
            		if(naturalExposureString.equals("3")){
            			likelihood = LikelihoodEnum.HIGH;
            			score = ThreatScoreEnum.HIGH;
            		}
            		
            		// the description can be divided into multiple token because of the presence of commas
            		// so we merge them
            		String description = scenarioString[16].replaceAll("\"", "");
            		
            		if(scenarioString.length == 18){
            			description += ", " + scenarioString[17].replaceAll("\"", "");
            		}
            		
            		if(scenarioString.length == 19){
            			description += ", " + scenarioString[18].replaceAll("\"", "");
            		}
            		
            		if(scenarioString.length == 20){
            			description += ", " + scenarioString[19].replaceAll("\"", "");
            		}
            		
            		if(scenarioString.length == 21){
            			description += ", " + scenarioString[20].replaceAll("\"", "");
            		}
            		
            		if(scenarioString.length == 22){
            			description += ", " + scenarioString[21].replaceAll("\"", "");
            		}
            		
            		String catalogueId = eventType + "." + eventSubType + "-" + place + "-" + time + "-" + access + "-" + process + "-" + actor;
            		
            		tm.getThreats().add(createMehariThreat(false, ThreatSourceEnum.MEHARI, catalogueId, description, "(" + threatType.toString() + ") " + scenarioString[16].replaceAll("\"", "") + "-" + catalogueId , threatType, likelihood, score, assetsCategory, associatedVulnerabilities, event, eventDescription, place, time, access, process, actor));

                }
                
            }

        } catch (FileNotFoundException e) {
					LOG.error(e.getMessage());
        } catch (IOException e) {
					LOG.error(e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
									LOG.error(e.getMessage());
                }
            }
        }

    }
	
	private String readEventDescription(String event) {
		String csvFile = ".//json/events.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] descriptionString = line.split(cvsSplitBy);
                
                if(descriptionString[1].equals(event)){
                	return descriptionString[0].trim();
                }
                
            }
            return "";

        } catch (FileNotFoundException e) {
					LOG.error(e.getMessage());
            return "";
        } catch (IOException e) {
            return "";
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                }
            }
            
        }
	}

	private String readNaturalExposureCSV(String event){
		String csvFile = ".//json/naturalExposure.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] naturalExposureString = line.split(cvsSplitBy);
                
                if(naturalExposureString[0].equals(event)){
                	return naturalExposureString[1];
                }
                
            }
            return "4";

        } catch (FileNotFoundException e) {
					LOG.error(e.getMessage());
            return "4";
        } catch (IOException e) {
					LOG.error(e.getMessage());
            return "4";
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
									LOG.error(e.getMessage());
                }
            }
            
        }

    }
	
	private ArrayList<SecondaryAssetCategoryEnum> checkSecondaryAssetCategory(String category){
		ArrayList<SecondaryAssetCategoryEnum> categories = new ArrayList<>();
		
		if(category.equals("File")){
			categories.add(SecondaryAssetCategoryEnum.Data_File);
		}
		if(category.equals("Files")){
			categories.add(SecondaryAssetCategoryEnum.Data_File);
		}
		if(category.equals("data file")){
			categories.add(SecondaryAssetCategoryEnum.Data_File);
		}
		if(category.equals("e-mail file")){
			categories.add(SecondaryAssetCategoryEnum.Data_File);
		}
		if(category.equals("fax file")){
			categories.add(SecondaryAssetCategoryEnum.Data_File);
		}
		if(category.equals("Media")){
			categories.add(SecondaryAssetCategoryEnum.Electronic_Media);
			categories.add(SecondaryAssetCategoryEnum.Non_Electronic_Media);
		}
		if(category.equals("Access means")){
			categories.add(SecondaryAssetCategoryEnum.Data_Access_Mean);
		}
		if(category.equals("PC")){
			categories.add(SecondaryAssetCategoryEnum.Electronic_Media);
		}
		if(category.equals("Document")){
			categories.add(SecondaryAssetCategoryEnum.Non_Electronic_Media);
		}
		if(category.equals("printouts")){
			categories.add(SecondaryAssetCategoryEnum.Non_Electronic_Media);
		}
		if(category.equals("Messages")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("Transaction")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("data")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("data transferred")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("screen")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("Message")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("transaction")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("e-mails")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("fax")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("post mail")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("premises")){
			categories.add(SecondaryAssetCategoryEnum.Premise);
		}
		
		if(category.equals("Software")){
			categories.add(SecondaryAssetCategoryEnum.Software_Off_the_Shelf);
			categories.add(SecondaryAssetCategoryEnum.Software_Custom);
		}
		if(category.equals("application")){
			categories.add(SecondaryAssetCategoryEnum.Software_Off_the_Shelf);
			categories.add(SecondaryAssetCategoryEnum.Software_Custom);
		}
		if(category.equals("program file")){
			categories.add(SecondaryAssetCategoryEnum.Software_Off_the_Shelf);
			categories.add(SecondaryAssetCategoryEnum.Software_Custom);
		}
		if(category.equals("software configuration")){
			categories.add(SecondaryAssetCategoryEnum.Software_Configuration);
		}
		if(category.equals("connection process")){
			categories.add(SecondaryAssetCategoryEnum.Software_Configuration);
		}
		if(category.equals("security configuration")){
			categories.add(SecondaryAssetCategoryEnum.Software_Configuration);
		}
		if(category.equals("hardware configuration")){
			categories.add(SecondaryAssetCategoryEnum.Hardware_Configuration);
		}
		if(category.equals("equipment configuration")){
			categories.add(SecondaryAssetCategoryEnum.Software_Configuration);
			categories.add(SecondaryAssetCategoryEnum.Hardware_Configuration);
		}
		if(category.equals("telecom equipment")){
			categories.add(SecondaryAssetCategoryEnum.Hardware);
		}
		if(category.equals("network equipment")){
			categories.add(SecondaryAssetCategoryEnum.Hardware);
		}
		if(category.equals("auxiliary elements")){
			categories.add(SecondaryAssetCategoryEnum.Hardware);
		}
		if(category.equals("Server")){
			categories.add(SecondaryAssetCategoryEnum.Hardware);
		}
		if(category.equals("work stations")){
			categories.add(SecondaryAssetCategoryEnum.Hardware);
		}
		if(category.equals("shared equipment")){
			categories.add(SecondaryAssetCategoryEnum.Hardware);
		}
		if(category.equals("application server")){
			categories.add(SecondaryAssetCategoryEnum.Hardware);
		}
		if(category.equals("media storing program")){
			categories.add(SecondaryAssetCategoryEnum.Electronic_Media);
		}
		if(category.equals("account")){
			categories.add(SecondaryAssetCategoryEnum.Service_Access_Mean);
		}
		if(category.equals("compliance of processes")){
			categories.add(SecondaryAssetCategoryEnum.Policy);
		}
		
		return categories;
	}
	

	private Threat createMehariThreat(boolean assessmentThreat, ThreatSourceEnum catalogue, String catalogueId, String description, String name, ThreatClassEnum threatClass, LikelihoodEnum likelihood, ThreatScoreEnum score, ArrayList<SecondaryAssetCategoryEnum> assetsCategory, ArrayList<String> associatedVulnerabilities, String event, String eventDescription, String place, String time, String access, String process, String actor){
		
		Threat threat = new Threat();
		threat.setObjType(SESTObjectTypeEnum.ThreatModel);
		threat.setPhase(PhaseEnum.Initial);
		
		UUID uuid = UUID.randomUUID();
		threat.setIdentifier(uuid.toString());
		threat.setAssessmentThreat(assessmentThreat);
		threat.setCatalogue(catalogue);
		threat.setDescription(description);
		threat.setElementType(ElementTypeEnum.Element);
		
		threat.setAssociatedVulnerabilities(associatedVulnerabilities);
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date now = new Date();
		threat.setLastUpdate(df.format(now));
		
		threat.setName(name);
		threat.setCatalogueId(catalogueId);
		threat.setThreatClass(threatClass);
		threat.setRawText("");
		
		ApplicablePlatform platform = createDefaultApplicablePlatform(); 
		threat.setApplicablePlatform(platform);
		
		ThreatScore threatScore = new ThreatScore();
		threatScore.setScore(score);
		threatScore.setLikelihood(likelihood);

		ArrayList<SecurityImpact> securityImpacts = createsecurityImpacts(false, false, false, false);
		threatScore.setSecurityImpacts(securityImpacts);
		threat.setScore(threatScore);
		threat.setCanBeSelected(true);
		
		threat.setAffectedAssetsCategories(assetsCategory);
		
		ThreatEvent threatEvent = new ThreatEvent();
		threatEvent.setName(event);
		threatEvent.setCatalogue(catalogue);
		threatEvent.setCatalogueId(event);
		threatEvent.setDescription(eventDescription);
		
		threat.setEvent(threatEvent);
		
		ThreatPlace threatPlace = new ThreatPlace();
		threatPlace.setName(place);
		threatPlace.setCatalogue(catalogue);
		threatPlace.setCatalogueId(place);
		if(place.equals("Arc")){
			threatPlace.setDescription("within the archival premises");
		}
		if(place.equals("Bur")){
			threatPlace.setDescription("in the offices");
		}
		if(place.equals("Dis")){
			threatPlace.setDescription("in the distribution office");
		}
		if(place.equals("Exp")){
			threatPlace.setDescription("within the production premises");
		}
		if(place.equals("Ext")){
			threatPlace.setDescription("outside the premises");
		}
		if(place.equals("Loc")){
			threatPlace.setDescription("in the post mail office");
		}
		if(place.equals("Lof")){
			threatPlace.setDescription("in the fax office");
		}
		if(place.equals("Med")){
			threatPlace.setDescription("in the media storage premises");
		}
		
		threat.setPlace(threatPlace);
		
		
		ThreatTime threatTime = new ThreatTime();
		threatTime.setName(time);
		threatTime.setCatalogue(catalogue);
		threatTime.setCatalogueId(time);
		
		if(time.equals("Abs")){
			threatTime.setDescription("while the user is absent");
		}
		if(time.equals("Hho")){
			threatTime.setDescription("outside working hours");
		}
		if(time.equals("Hou")){
			threatTime.setDescription("during working hours");
		}
		
		threat.setTime(threatTime);
		
		ThreatAccess threatAccess = new ThreatAccess();
		threatAccess.setName(access);
		threatAccess.setCatalogue(catalogue);
		threatAccess.setCatalogueId(access);
		
		if(access.equals("Aru")){
			threatAccess.setDescription("having access to user resources not erased after use");
		}
		if(access.equals("Asan")){
			threatAccess.setDescription("connected on the storage network");
		}
		if(access.equals("Atm")){
			threatAccess.setDescription("connected through a remote maintenance port");
		}
		if(access.equals("Auie")){
			threatAccess.setDescription("using an equipment usurping the identity of an entity also connected to the extended network");
		}
		if(access.equals("Auis")){
			threatAccess.setDescription("by usurpation of the identity of an application server");
		}
		if(access.equals("Aem")){
			threatAccess.setDescription("through electro-magnetic tapping");
		}
		if(access.equals("Aerl")){
			threatAccess.setDescription("connected, from outside, to the local area network");
		}
		if(access.equals("Ain")){
			threatAccess.setDescription("connected from the internal network");
		}
		if(access.equals("Amdertm")){
			threatAccess.setDescription("after a remote modification of a network equipment, using a remote maintenance line");
		}
		if(access.equals("Amderuf")){
			threatAccess.setDescription("after a remote modification of a network equipment, using a flaw not yet fixed");
		}
		if(access.equals("Amer")){
			threatAccess.setDescription("after modification of a network equipment");
		}
		if(access.equals("Apc")){
			threatAccess.setDescription("directly connected to the workstation");
		}
		if(access.equals("Are")){
			threatAccess.setDescription("connected on the extended network");
		}
		
		threat.setAccess(threatAccess);
		

		ThreatProcess threatProcess = new ThreatProcess();
		threatProcess.setName(process);
		threatProcess.setCatalogue(catalogue);
		threatProcess.setCatalogueId(process);
		
		if(process.equals("Act")){
			threatProcess.setDescription("open on the workstation");
		}
		if(process.equals("Are")){
			threatProcess.setDescription("during an access to the network from outside");
		}
		if(process.equals("Col")){
			threatProcess.setDescription("during collection or diffusion");
		}
		if(process.equals("Con")){
			threatProcess.setDescription("during the connection to the service");
		}
		if(process.equals("Crc")){
			threatProcess.setDescription("within the collection circuit of paper bins");
		}
		if(process.equals("Dif")){
			threatProcess.setDescription("during the distribution");
		}
		if(process.equals("Emi")){
			threatProcess.setDescription("during emission or receipt");
		}
		if(process.equals("Ere")){
			threatProcess.setDescription("over the extended network");
		}
		if(process.equals("Ste")){
			threatProcess.setDescription("during an externalized storage");
		}
		if(process.equals("Sti")){
			threatProcess.setDescription("during a storage in the site");
		}
		if(process.equals("Stm")){
			threatProcess.setDescription("stored on a removable media");
		}
		if(process.equals("Stp")){
			threatProcess.setDescription("stored on the work station");
		}
		if(process.equals("Tde")){
			threatProcess.setDescription("during development tests");
		}
		if(process.equals("Tran")){
			threatProcess.setDescription("during the transportation between sites");
		}
		if(process.equals("Tru")){
			threatProcess.setDescription("during user treatmentsn");
		}
		if(process.equals("Erl")){
			threatProcess.setDescription("during exchanges on the local area network");
		}
		if(process.equals("Exp")){
			threatProcess.setDescription("during production");
		}
		if(process.equals("Imp")){
			threatProcess.setDescription("during the printing on a shared printer");
		}
		if(process.equals("Mac")){
			threatProcess.setDescription("during a hot maintenance operation");
		}
		if(process.equals("Mai")){
			threatProcess.setDescription("during a maintenance operation");
		}
		if(process.equals("Mal")){
			threatProcess.setDescription("during a software maintenance operation");
		}
		if(process.equals("Mam")){
			threatProcess.setDescription("during a hardware maintenance operation");
		}
		if(process.equals("Res")){
			threatProcess.setDescription("during the setting up after an intervention");
		}
		
		threat.setProcess(threatProcess);
		
		ThreatActor threatActor = new ThreatActor();
		threatActor.setName(actor);
		threatActor.setCatalogue(catalogue);
		threatActor.setCatalogueId(actor);
		
		if(actor.equals("D")){
			threatActor.setDescription("a member of the development team");
		}
		if(actor.equals("E")){
			threatActor.setDescription("a member of the production team");
		}
		if(actor.equals("M")){
			threatActor.setDescription("a member of the maintenance team");
		}
		if(actor.equals("Pa")){
			threatActor.setDescription("an authorized staff member");
		}
		if(actor.equals("Per")){
			threatActor.setDescription("a member of the enterprise staff");
		}
		if(actor.equals("Pna")){
			threatActor.setDescription("a member of the staff not authorized");
		}
		if(actor.equals("Pse")){
			threatActor.setDescription("a member of the services team");
		}
		if(actor.equals("Tie")){
			threatActor.setDescription("an unauthorized third party");
		}
		if(actor.equals("Tve")){
			threatActor.setDescription("vandals or terrorists acting from outside");
		}
		if(actor.equals("Tvi")){
			threatActor.setDescription("vandals or terrorists acting from inside (after an intrusion)");
		}
		if(actor.equals("Ua")){
			threatActor.setDescription("an authorized user");
		}
		if(actor.equals("Uai")){
			threatActor.setDescription("a user authorized illegitimately");
		}
		if(actor.equals("Ual")){
			threatActor.setDescription("a user authorized legitimately");
		}
		if(actor.equals("Una")){
			threatActor.setDescription("a user not authorized");
		}
		if(actor.equals("Vis")){
			threatActor.setDescription("a visitor");
		}

		
		threat.setActor(threatActor);
		
		
		return threat;
	}
	
	
	ArrayList<SecurityImpact> createsecurityImpacts(boolean availability, boolean confidenciality, boolean integrity,
			boolean efficiency) {
		ArrayList<SecurityImpact> securityImpacts = new ArrayList<SecurityImpact>();

		if (availability) {
			SecurityImpact secImpact1 = new SecurityImpact();
			secImpact1.setScope(SecurityImpactScopeEnum.Availability);
			secImpact1.setImpact(ImpactEnum.CRITICAL);

			securityImpacts.add(secImpact1);
		}

		if (confidenciality) {
			SecurityImpact secImpact2 = new SecurityImpact();
			secImpact2.setScope(SecurityImpactScopeEnum.Confidentiality);
			secImpact2.setImpact(ImpactEnum.CRITICAL);

			securityImpacts.add(secImpact2);
		}

		if (integrity) {
			SecurityImpact secImpact2 = new SecurityImpact();
			secImpact2.setScope(SecurityImpactScopeEnum.Integrity);
			secImpact2.setImpact(ImpactEnum.CRITICAL);

			securityImpacts.add(secImpact2);
		}

		if (efficiency) {
			SecurityImpact secImpact2 = new SecurityImpact();
			secImpact2.setScope(SecurityImpactScopeEnum.Efficiency);
			secImpact2.setImpact(ImpactEnum.CRITICAL);

			securityImpacts.add(secImpact2);
		}

		return securityImpacts;
	}
	
	private ApplicablePlatform createDefaultApplicablePlatform(){
		ApplicablePlatform p = new ApplicablePlatform();
		p.setArchitecturalParadigms("");
		p.setHardwareArchitectures("");
		p.setOperatingSystems("");
		p.setPlatformNotes("");
		p.setProducts("");
		p.setProgrammingLanguages("");
		p.setTechnologyClasses("");

		return p;
	}
}



