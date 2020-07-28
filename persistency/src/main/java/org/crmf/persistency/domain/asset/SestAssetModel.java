/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SestAssetModel.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.asset;

import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.utility.assetmodel.AssetModelSerializerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SestAssetModel {
	private static final Logger LOG = LoggerFactory.getLogger(SestAssetModel.class.getName());
	private Integer id;
	private String sestobjId;
	private String assetModelJson;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSestobjId() {
		return sestobjId;
	}

	public void setSestobjId(String sestobjId) {
		this.sestobjId = sestobjId;
	}

	public String getAssetModelJson() {
		return assetModelJson;
	}

	public void setAssetModelJson(String assetModelJson) {
		this.assetModelJson = assetModelJson;
	}

	public AssetModel convertToModel() {
		LOG.info("SestAssetModel convertToModel");
		try {
			AssetModelSerializerDeserializer assetModelDeserializer = new AssetModelSerializerDeserializer();

			return assetModelDeserializer.getAMFromJSONString(this.getAssetModelJson());
		} catch (Exception ex) {
			LOG.error("Unable to deserialize asset model!!! "+this.getSestobjId(), ex);
			return null;
		}
	}

}
