/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SystemRequirementRestServer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.requirementimport.rest;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.requirementimport.processor.SystemRequirementExcelProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//This class manages the webservices invoked by the SEST Client
public class SystemRequirementRestServer {

  private SystemRequirementExcelProcessor processor;
  private static final Logger LOG = LoggerFactory.getLogger(SystemRequirementRestServer.class.getName());

  @POST
  @Produces("application/json")
  @Consumes("text/html")
  public List<String> listRequirementLoadedFile(GenericFilter filter) throws Exception {

    LOG.info("Loading filenames " + filter.getFilterValue(GenericFilterEnum.SYS_PROJECT));
    List<String> filenames = processor.listRequirementLoadedFile(filter);
    // wrap filenames with "" because of gson which didn't convert it otherwise
    List<String> filenamesToJSon = new ArrayList<>();
    for (String string : filenames) {
      filenamesToJSon.add("\"".concat(string).concat("\""));
    }
    LOG.info("Loading filenames " + filenamesToJSon.size());
    return filenamesToJSon;
  }

  @POST
  @Path("/upload")
  @Produces("text/html")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public String uploadRequirement(MultipartBody body) throws Exception {

    List<Attachment> attachments = body.getAllAttachments();
    if (attachments == null || attachments.size() != 3) {

      LOG.error("=========== uploadRequirement error : attachments size is different!!! ");
      throw new Exception("COMMAND_EXCEPTION");
    }
    InputStream is1 = null;
    InputStream is2 = null;
    try {
      LOG.info("=========== uploadRequirement attachments getContentType : " + attachments.get(0).getContentType()
        + ", " + attachments.get(1).getContentType()
        + ", " + attachments.get(2).getContentType());

      Attachment file = attachments.get(0);
      Attachment sysprojectIdentifier = attachments.get(1);
      Attachment filename = attachments.get(2);
      is1 = sysprojectIdentifier.getObject(InputStream.class);
      is2 = filename.getObject(InputStream.class);

      return processor.process(file, is1.toString(), is2.toString());
    } catch (Exception e) {
      LOG.error("=========== uploadRequirement error " + e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    } finally {
      if (is1 != null)
        is1.close();
      if (is2 != null)
        is2.close();
    }
  }

  public SystemRequirementExcelProcessor getProcessor() {
    return processor;
  }

  public void setProcessor(SystemRequirementExcelProcessor processor) {
    this.processor = processor;
  }

}
