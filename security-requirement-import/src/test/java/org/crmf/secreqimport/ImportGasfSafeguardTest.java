/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ImportGasfSafeguard.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.secreqimport;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImportGasfSafeguardTest {

	@Test
	public void importGasfSafeguard() throws IOException {
		File srSgRelFile = new File(".//json/gasfSafeguard.csv");
		byte[] srSgBytes = Files.readAllBytes(srSgRelFile.toPath());
		String srSgS = new String(srSgBytes, "UTF-8");

	
		String[] lines = srSgS.split("\\r?\\n");
		for (String line : lines) {
			
			String[] values = line.split(",");
			
			try{
				//We avoid to persist empty rows
				//We avoid to persist empty rows
				if(values[1].equals("N/A") || values[1].equals("CATEGORY") || values[1].equals("DEFINED")){
					continue;
				}
				Integer i = Integer.valueOf(values[2]);
				
				
			}
			catch(Exception e){
				
				continue;
			}
		}
	}

}
