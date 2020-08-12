/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SystemRequirementExcelProcessor.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.requirementimport.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.attachment.DelegatingInputStream;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;

import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/*
 Uses POI to convert an Excel spreadsheet with the system engineering project requirements to the desired JAXB XML format. doc
 version 2003
 */
public class SystemRequirementExcelProcessor {
  private final static Log LOG = LogFactory.getLog(SystemRequirementExcelProcessor.class);

  private SystemRequirementProcessor reqProcessor;

  public String process(Attachment file, String sysprojectIdentifier, String filename) {
    HSSFWorkbook workbook = null;
    DelegatingInputStream is = null;
    int itemNumber = 0;

    try {
      LOG.info("body input stream 1 " + file);
      is = file.getObject(DelegatingInputStream.class);
      workbook = new HSSFWorkbook(is.getInputStream());

      HSSFSheet sheet = workbook.getSheetAt(0);
      DatatypeFactory dateFactory = DatatypeFactory.newInstance();
      boolean headersFound = false;
      for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext(); ) {
        HSSFRow row = (HSSFRow) rit.next();
        if (!headersFound) { // Skip the first row with column headers
          headersFound = true;
          continue;
        }
        int colNum = 0;
        SystemRequirement item = new SystemRequirement();
        for (int cn = 0; cn < row.getLastCellNum(); cn++) {

          HSSFCell cell = row.getCell(cn, MissingCellPolicy.CREATE_NULL_AS_BLANK);
          switch (cn) {
            case 0: // ID
              item.setId(cell.getStringCellValue());
              LOG.info(" item.setId " + item.getId());
              break;
            case 1: // DESCRIPTION
              item.setDescription(cell.getStringCellValue());
              LOG.info(" item.setDescription " + item.getDescription());
              break;
            case 2: // NOTES
              item.setNotes(cell.getStringCellValue());
              LOG.info(" item.setNotes " + item.getNotes());
              break;
            case 3: // SOURCE
              item.setSource(cell.getStringCellValue());
              LOG.info(" item.setSource " + item.getSource());
              break;
            case 4: // PRIORITY
              item.setPriority(cell.getStringCellValue());
              LOG.info(" item.setPriority " + item.getPriority());
              break;
            case 5: // VERSION
              if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
                item.setVersion(String.valueOf(cell.getNumericCellValue()));
              } else {
                item.setVersion(cell.getStringCellValue());
              }
              LOG.info(" item.setVersion " + item.getVersion());
              break;
            case 6: // ISSUE CHANGED
              if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
                item.setIssuechanged(String.valueOf(cell.getNumericCellValue()));
              } else {
                item.setIssuechanged(cell.getStringCellValue());
              }
              LOG.info(" item.setIssuechanged " + item.getIssuechanged());
              break;
            case 7: // STABILITY
              item.setStability(cell.getStringCellValue());
              LOG.info(" item.setStability " + item.getStability());
              break;
            case 8: // TYPE
              item.setType(cell.getStringCellValue());
              LOG.info(" item.setType " + item.getType());
              break;
          }
        }
        reqProcessor.manageRequirement(item, sysprojectIdentifier, filename);
        ++itemNumber;
      }

      LOG.info("cancelling requirement");
      reqProcessor.cancelRequirement(sysprojectIdentifier, filename);
    } catch (Exception e) {
      LOG.error("Unable to import Excel SysRequirement", e);
      throw new RuntimeException("Unable to import Excel SysRequirement", e);
    } finally {
      if (is != null && !is.isClosed()) {
        try {
          is.close();
        } catch (IOException e) {
          LOG.error("Unable to import Excel SysRequirement", e);
        }
      }
      if (workbook != null) {
        try {
          workbook.close();
        } catch (IOException e) {
          LOG.error("Unable to import Excel SysRequirement", e);
        }
      }
    }
    return String.valueOf(itemNumber);
  }

  public List<String> listRequirementLoadedFile(GenericFilter filter) {

    return (filter != null && filter.getFilterValue(GenericFilterEnum.SYS_PROJECT) != null)
      ? reqProcessor.listRequirementLoadedFile(filter.getFilterValue(GenericFilterEnum.SYS_PROJECT)) : null;
  }

  public SystemRequirementProcessor getReqProcessor() {
    return reqProcessor;
  }

  public void setReqProcessor(SystemRequirementProcessor reqProcessor) {
    this.reqProcessor = reqProcessor;
  }

}
