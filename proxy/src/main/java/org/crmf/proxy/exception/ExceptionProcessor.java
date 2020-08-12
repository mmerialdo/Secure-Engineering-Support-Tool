package org.crmf.proxy.exception;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/************************************************************************
 * Created: 26/11/2019                                                  *
 * Author: Gabriela Mihalachi                                        *
 ************************************************************************/
public class ExceptionProcessor implements Processor {

  private static final Logger LOG = LoggerFactory.getLogger(ExceptionProcessor.class.getName());

  @Override
  public void process(Exchange exchange) throws Exception {
    Exception cause = exchange.getException();
    LOG.error("Exception : " + exchange.getException());

    //  exchange.getIn().setBody(cause);
    //  exchange.getIn().setFault(true);

    Message outMessage = exchange.getIn();
    outMessage.setHeader(org.apache.cxf.message.Message.RESPONSE_CODE, 500);
    outMessage.setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
    //  outMessage.setHeader("Access-Control-Allow-Origin", "*");
    //  outMessage.setHeader("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
    //  outMessage.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, CamelHttpResponseCode");
    //
    outMessage.setFault(true);

    outMessage.setBody(cause);
    exchange.setOut(outMessage);
  }

 /*   HttpHeaders headers = createHttpHeaders();

    if (apiErrors == null) {
      LOG.error(UNKNOWN_ERROR, exception);
      return new ResponseEntity<>(ApiErrors.from(GENERAL_ERROR), headers, determinResponseStatus(exception, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    return new ResponseEntity<>(apiErrors, headers, determinResponseStatus(exception, HttpStatus.BAD_REQUEST));
  }

  private HttpHeaders createHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private HttpStatus determinResponseStatus(Exception ex, HttpStatus defaultResponseCode) {
    ResponseStatus responseStatusAnnotation = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);

    if (responseStatusAnnotation != null) {
      return responseStatusAnnotation.value();
    } else if (ex instanceof AccessDeniedException) {
      return HttpStatus.FORBIDDEN;
    } else if(ex instanceof ResourceAccessException) {
      if(ex.getCause() instanceof ConnectException) {
        return HttpStatus.SERVICE_UNAVAILABLE;
      }
    }

    return defaultResponseCode;
  } */
}
