/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskScenario.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.riskassessmentelements;

import java.util.HashMap;
import java.util.Map;

import org.crmf.model.general.SESTObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RiskScenarioReference extends SESTObject {
	private static final Logger LOG = LoggerFactory.getLogger(RiskScenarioReference.class.getName());
	// asset fields
    private PrimaryAssetCategoryEnum assetType;
    
    //vulnerability fields
	private SecurityImpactScopeEnum aice;
	private SecondaryAssetCategoryEnum supportingAsset;

	private String vulnerabilityCode;
	
	//threat fields
	private String eventType;
	private String eventSubType;
	private String place;
	private String time;
	private String access;
	private String process;
	private String actor;
	
	//security measures fields
	private String dissuasion;
	private String prevention;
	private String confining;
	private String palliative;
	
	public enum SecurityMeasureEnum{
		DISSUASION,
		PREVENTION,
		CONFINING,
		PALLIATION;	
	}
	
	public RiskScenarioReference(){
		
	}
	
	public RiskScenarioReference(PrimaryAssetCategoryEnum assetType, SecurityImpactScopeEnum aice, SecondaryAssetCategoryEnum supportingAsset, String vulnerabilityCode,
			String eventType, String eventSubType, String place, String time, String access, String process, String actor, 
			String dissuasion, String prevention, String confining, String palliative){
		
	    this.assetType = assetType;
	    this.aice = aice;
	    this.supportingAsset =  supportingAsset;
	    this.vulnerabilityCode =  vulnerabilityCode;
	    this.eventType = eventType;
		this.eventSubType = eventSubType;
		this.place = place;
		this.time = time;
		this.access = access;
		this.process = process;
		this.actor = actor;
		this.dissuasion = dissuasion;
		this.prevention = prevention;
		this.confining = confining;
		this.palliative =  palliative;
	}
	  
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		RiskScenarioReference other = (RiskScenarioReference) obj;
		if (access == null) {
			if (other.access != null)
				return false;
		} 
		else if (!access.equals(other.access))
			return false;
		
		if (actor == null) {
			if (other.actor != null)
				return false;
		}
		else if (!actor.equals(other.actor))
			return false;
		
		if (!aice.equals(other.aice))
			return false;
		
		if (!assetType.equals(other.assetType))
			return false;
		
		if (eventSubType == null) {
			if (other.eventSubType != null)
				return false;
		} 
		else if (!eventSubType.equals(other.eventSubType))
			return false;
		
		if (eventType == null) {
			if (other.eventType != null)
				return false;
		} 
		else if (!eventType.equals(other.eventType))
			return false;
		
		if (place == null) {
			if (other.place != null)
				return false;
		} 
		else if (!place.equals(other.place))
			return false;
		
		if (process == null) {
			if (other.process != null)
				return false;
		} 
		else if (!process.equals(other.process))
			return false;
		
		if (!supportingAsset.equals(other.supportingAsset))
			return false;
		
		if (time == null) {
			if (other.time != null)
				return false;
		}
		else if (!time.equals(other.time))
			return false;
		
		if (vulnerabilityCode == null) {
			if (other.vulnerabilityCode != null)
				return false;
		} 
		else if (!vulnerabilityCode.equals(other.vulnerabilityCode))
			return false;
		
		return true;
	}
	
	 // Consistent with equals(). Two objects which are equal have the same hash code.
	   public int hashCodess() {
	      int hash = 1;
		  
	      hash = hash * 17 + ((assetType == null) ? 0 : assetType.hashCode());
	      hash = hash * 31 + ((aice == null) ? 0 : aice.hashCode());
	      hash = hash * 13 + ((supportingAsset == null) ? 0 : supportingAsset.hashCode());
	      hash = hash * 37 + vulnerabilityCode.hashCode();
	      hash = hash * 41 + eventType.hashCode();
	      hash = hash * 19 + eventSubType.hashCode();
	      hash = hash * 23 + place.hashCode();
	      hash = hash * 29 + time.hashCode();
	      hash = hash * 7 + access.hashCode();
	      hash = hash * 43 + process.hashCode();
	      hash = hash * 53 + actor.hashCode();
	      
	      return hash;
	   }
	 
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((access == null) ? 0 : access.hashCode());
			result = prime * result + ((actor == null) ? 0 : actor.hashCode());
			result = prime * result + ((aice == null) ? 0 : aice.hashCode());
			result = prime * result + ((assetType == null) ? 0 : assetType.hashCode());
			result = prime * result + ((confining == null) ? 0 : confining.hashCode());
			result = prime * result + ((dissuasion == null) ? 0 : dissuasion.hashCode());
			result = prime * result + ((eventSubType == null) ? 0 : eventSubType.hashCode());
			result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
			result = prime * result + ((palliative == null) ? 0 : palliative.hashCode());
			result = prime * result + ((place == null) ? 0 : place.hashCode());
			result = prime * result + ((prevention == null) ? 0 : prevention.hashCode());
			result = prime * result + ((process == null) ? 0 : process.hashCode());
			result = prime * result + ((supportingAsset == null) ? 0 : supportingAsset.hashCode());
			result = prime * result + ((time == null) ? 0 : time.hashCode());
			result = prime * result + ((vulnerabilityCode == null) ? 0 : vulnerabilityCode.hashCode());
			return result;
		}
	/**
	    * Create a Map with all security measure formulas contained into the Risk Scenario Reference
	    * @return a Map Security Measure/associated formula
	    */
		public Map<SecurityMeasureEnum, String> createSecurityMeasureMap() {
			Map<SecurityMeasureEnum, String> smMap = new HashMap<SecurityMeasureEnum, String>();	
			smMap.put(SecurityMeasureEnum.DISSUASION, this.getDissuasion());
			smMap.put(SecurityMeasureEnum.PREVENTION, this.getPrevention());
			smMap.put(SecurityMeasureEnum.CONFINING, this.getConfining());
			smMap.put(SecurityMeasureEnum.PALLIATION, this.getPalliative());
			return smMap;
		}
		
		/**
		 * Compares this risk scenario reference to a model risk scenario and all the models in input
		 * @param riskScenario
		 * @return true if the input risk scenario and models are compliant to risk scenario reference
		 */
		public boolean compareRiskScenarioReference(RiskScenario rs, Asset asset, Threat threat,
				Vulnerability vulnerability){
		
			if(!this.assetType.equals(asset.getPrimaryCategories().get(0)) ||
			   !this.aice.equals(rs.getImpactScope()) ||
			   !this.supportingAsset.equals(asset.getCategory()) || 
			   !this.vulnerabilityCode.equals(vulnerability.getCatalogueId()) ||
			   !(this.eventType+"."+this.eventSubType).equals(threat.getEvent().getCatalogueId()) ||
			   !this.place.equals(threat.getPlace().getCatalogueId()) ||
			   !this.time.equals(threat.getTime().getCatalogueId()) ||
			   !this.access.equals(threat.getAccess().getCatalogueId()) ||
			   !this.process.equals(threat.getProcess().getCatalogueId()) ||
			   !this.actor.equals(threat.getActor().getCatalogueId()))
				return false;
			
//			   if( !this.aice.equals(rs.getImpactScope()) ||
//				   !this.supportingAsset.equals(asset.getCategory()) || 
//				   !this.vulnerabilityCode.equals(vulnerability.getCatalogueId()) ||
//				   !(this.eventType+"."+this.eventSubType).equals(threat.getEvent().getCatalogueId()) ||
//				   !this.place.equals(threat.getPlace().getCatalogueId()) ||
//				   !this.time.equals(threat.getTime().getCatalogueId()) ||
//				   !this.access.equals(threat.getAccess().getCatalogueId()) ||
//				   !this.process.equals(threat.getProcess().getCatalogueId()) ||
//				   !this.actor.equals(threat.getActor().getCatalogueId()))
//					return false;
			
			return true;
		}
	
	public PrimaryAssetCategoryEnum getAssetType() {
		return assetType;
	}
	public void setAssetType(PrimaryAssetCategoryEnum assetType) {
		this.assetType = assetType;
	}
	public SecurityImpactScopeEnum getAice() {
		return aice;
	}
	public void setAice(SecurityImpactScopeEnum aice) {
		this.aice = aice;
	}
	public SecondaryAssetCategoryEnum getSupportingAsset() {
		return supportingAsset;
	}
	public void setSupportingAsset(SecondaryAssetCategoryEnum supportingAsset) {
		this.supportingAsset = supportingAsset;
	}
	public String getVulnerabilityCode() {
		return vulnerabilityCode;
	}
	public void setVulnerabilityCode(String vulnerabilityCode) {
		this.vulnerabilityCode = vulnerabilityCode;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getEventSubType() {
		return eventSubType;
	}
	public void setEventSubType(String eventSubType) {
		this.eventSubType = eventSubType;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
	}
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
	public String getActor() {
		return actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public String getDissuasion() {
		return dissuasion;
	}
	public void setDissuasion(String dissuasion) {
		this.dissuasion = dissuasion;
	}
	public String getPrevention() {
		return prevention;
	}
	public void setPrevention(String prevention) {
		this.prevention = prevention;
	}
	public String getConfining() {
		return confining;
	}
	public void setConfining(String confining) {
		this.confining = confining;
	}
	public String getPalliative() {
		return palliative;
	}
	public void setPalliative(String palliative) {
		this.palliative = palliative;
	}	
	
	
}
