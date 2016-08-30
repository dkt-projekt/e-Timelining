package de.dkt.eservices.timelining;

import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.dkt.common.feedback.InteractionManagement;
import de.dkt.common.niftools.NIFManagement;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.tools.ParameterChecker;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFConversionService;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import eu.freme.common.rest.BaseRestController;
import eu.freme.common.rest.NIFParameterSet;

@RestController
public class ETimeliningServiceStandAlone extends BaseRestController{

	Logger logger = Logger.getLogger(ETimeliningServiceStandAlone.class);
	
	@Autowired
	ETimeliningService timeliningService;

	@Autowired
	RDFConversionService rdfConversionService;

	@RequestMapping(value = "/e-timelining/testURL", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> testURL(
			@RequestParam(value = "preffix", required = false) String preffix,
            @RequestBody(required = false) String postBody) throws Exception {

    	HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.add("Content-Type", "text/plain");
    	ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
    	return response;
	}
	
//	@RequestMapping(value = "/e-timelining/addDocumentToTimeline", method = { RequestMethod.POST, RequestMethod.GET })
//	public ResponseEntity<String> addDocumentToTimelining(
//			@RequestParam(value = "input", required = false) String input,
//			@RequestParam(value = "i", required = false) String i,
//			@RequestParam(value = "informat", required = false) String informat,
//			@RequestParam(value = "f", required = false) String f,
//			@RequestParam(value = "outformat", required = false) String outformat,
//			@RequestParam(value = "o", required = false) String o,
//			@RequestParam(value = "prefix", required = false) String prefix,
//			@RequestParam(value = "p", required = false) String p,
//			@RequestHeader(value = "Accept", required = false) String acceptHeader,
//			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
//
//			@RequestParam(value = "timelineName", required = false) String timelineName,
//			@RequestParam(value = "timelinePath", required = false) String timelinePath,
//			@RequestParam(value = "timelineCreate", required = false) boolean timelineCreate,
//			@RequestHeader(value = "addElements", required = false) boolean addElements,
//			@RequestParam(value = "language", required = false) String language,
//            @RequestBody(required = false) String postBody) throws Exception {
//
//		ParameterChecker.checkNotNullOrEmpty(timelineName, "timeline name", logger);
//		ParameterChecker.checkNotNullOrEmpty(timelinePath, "timeline path", logger);
//
//		if (input == null) {
//			input = i;
//		}
//		if (informat == null) {
//			informat = f;
//		}
//		if (outformat == null) {
//			outformat = o;
//		}
//		if (prefix == null) {
//			prefix = p;
//		}
//    	
//		NIFParameterSet parameters = this.normalizeNif(input, informat,
//				outformat, postBody, acceptHeader, contentTypeHeader, prefix);
//
//		String plaintext = null;
//		Model inputModel = ModelFactory.createDefaultModel();
//
//		if (!parameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
//			try {
//				inputModel = this.unserializeNif(parameters.getInput(),parameters.getInformat());
//			} catch (Exception e) {
//				logger.error("BAD REQUEST: error parsing NIF input", e);
//						
//				throw new BadRequestException("Error parsing NIF input");
//			}
//		} else {
//			// input is plaintext
//			plaintext = parameters.getInput();
//			getRdfConversionService().plaintextToRDF(inputModel, plaintext,language, parameters.getPrefix());
//		}
//		try {
//			if(timeliningService.addDocumentToTimelining(parameters.getInput(), informat, timelineName, timelinePath, timelineCreate, addElements)){
//				HttpHeaders responseHeaders = new HttpHeaders();
//				responseHeaders.add("Content-Type", RDFSerialization.PLAINTEXT.contentType());
//				return new ResponseEntity<String>("Document correctly added to timeline: ["+timelineName+"]", responseHeaders, HttpStatus.OK);
//			}else{
//				throw new ExternalServiceFailedException("ERROR at adding the document to the timelining.");
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			throw e;
//		}
//	}
//
//	@RequestMapping(value = "/e-timelining/processQuery", method = { RequestMethod.POST, RequestMethod.GET })
//	public ResponseEntity<String> processQuery(
//			@RequestParam(value = "input", required = false) String input,
//			@RequestParam(value = "i", required = false) String i,
//			@RequestParam(value = "informat", required = false) String informat,
//			@RequestParam(value = "f", required = false) String f,
//			@RequestParam(value = "outformat", required = false) String outformat,
//			@RequestParam(value = "o", required = false) String o,
//			@RequestParam(value = "prefix", required = false) String prefix,
//			@RequestParam(value = "p", required = false) String p,
//			@RequestHeader(value = "Accept", required = false) String acceptHeader,
//			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
//
//			@RequestParam(value = "queryText", required = false) String queryText,
//			@RequestParam(value = "timelineName", required = false) String timelineName,
//			@RequestParam(value = "timelinePath", required = false) String timelinePath,
//			@RequestHeader(value = "addElements", required = false) boolean addElements,
//			@RequestParam(value = "language", required = false) String language,
//            @RequestBody(required = false) String postBody) throws Exception {
//
//		ParameterChecker.checkNotNullOrEmpty(timelineName, "timeline name", logger);
//		ParameterChecker.checkNotNullOrEmpty(timelinePath, "timeline path", logger);
//		
//		if (input == null) {
//			input = i;
//		}
//		if (informat == null) {
//			informat = f;
//		}
//		if (outformat == null) {
//			outformat = o;
//		}
//		if (prefix == null) {
//			prefix = p;
//		}
//		
//		ParameterChecker.checkNotNullOrEmpty(input, "query text", logger);
//		ParameterChecker.checkNotNullOrEmpty(timelineName, "timeline name", logger);
//
//		NIFParameterSet parameters = this.normalizeNif(input, informat,
//				outformat, postBody, acceptHeader, contentTypeHeader, prefix);
//
//		String plaintext = null;
//		Model inputModel = ModelFactory.createDefaultModel();
//
//		if (!parameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
//			try {
//				inputModel = this.unserializeNif(parameters.getInput(),parameters.getInformat());
//			} catch (Exception e) {
//				logger.error("BAD REQUEST: error parsing NIF input", e);
//				throw new BadRequestException("Error parsing NIF input");
//			}
//		} else {
//			// input is plaintext
//			plaintext = parameters.getInput();
//			getRdfConversionService().plaintextToRDF(inputModel, plaintext, language, parameters.getPrefix());
//		}
//		try {
//			JSONObject obj = timeliningService.processQuery(queryText, informat, timelineName, timelinePath, addElements);
//
//			HttpHeaders responseHeaders = new HttpHeaders();
//			responseHeaders.add("Content-Type", RDFSerialization.JSON.contentType());
//			return new ResponseEntity<String>(obj.toString(), responseHeaders, HttpStatus.OK);
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			throw e;
//		}
//	}
//
//	@RequestMapping(value = "/e-timelining/processDocumentsCollection", method = { RequestMethod.POST, RequestMethod.GET })
//	public ResponseEntity<String> timelineDocumentCollection(
//			@RequestParam(value = "input", required = false) String input,
//			@RequestParam(value = "i", required = false) String i,
//			@RequestParam(value = "informat", required = false) String informat,
//			@RequestParam(value = "f", required = false) String f,
//			@RequestParam(value = "outformat", required = false) String outformat,
//			@RequestParam(value = "o", required = false) String o,
//			@RequestParam(value = "prefix", required = false) String prefix,
//			@RequestParam(value = "p", required = false) String p,
//			@RequestHeader(value = "Accept", required = false) String acceptHeader,
//			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
//			@RequestHeader(value = "addElements", required = false) boolean addElements,
//			@RequestBody(required = false) String postBody) throws Exception {
//
//		if (input == null) {
//			input = i;
//		}
//		if (informat == null) {
//			informat = f;
//		}
//		if (outformat == null) {
//			outformat = o;
//		}
//		if (prefix == null) {
//			prefix = p;
//		}
//		ParameterChecker.checkNotNullOrEmpty(input, "input text", logger);
//
//		if(!informat.equalsIgnoreCase("json") && !informat.equalsIgnoreCase("text/json") ){
//			logger.error("Bad input INFORMAT, only JSON (json or text/json) acccepted");
//			throw new BadRequestException("Bad input INFORMAT, only JSON acccepted");
//		}
//		try {
//			JSONObject inputJSONObject = new JSONObject(input);
//			JSONObject obj = timeliningService.processDocumentsCollection(inputJSONObject,addElements);
//
//			HttpHeaders responseHeaders = new HttpHeaders();
//			responseHeaders.add("Content-Type", RDFSerialization.JSON.contentType());
//			return new ResponseEntity<String>(obj.toString(), responseHeaders, HttpStatus.OK);
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			throw e;
//		}
//	}
    
	@RequestMapping(value = "/e-timelining/timelineCollection", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> timelineCollection(
			HttpServletRequest request,
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "i", required = false) String i,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestParam(value = "language", required = false) String language,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestHeader(value = "granularity", required = false) String granularity,
			@RequestBody(required = false) String postBody) throws Exception {

		if (input == null) {
			input = i;
		}
		if (informat == null) {
			informat = f;
		}
		if (outformat == null) {
			outformat = o;
		}
		if (prefix == null) {
			prefix = p;
		}
//		ParameterChecker.checkNotNullOrEmpty(inputDataFormat, "input data type", logger);
        NIFParameterSet nifParameters = this.normalizeNif(input, informat, outformat, postBody, acceptHeader, contentTypeHeader, prefix);
        Model inModel = null;
        if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
			rdfConversionService.plaintextToRDF(inModel, nifParameters.getInput(),language, nifParameters.getPrefix());
        } else {
            inModel = rdfConversionService.unserializeRDF(nifParameters.getInput(), nifParameters.getInformat());
        }
		try {
			JSONObject outputObject = timeliningService.processDocumentsCollection(inModel,granularity);
//			String nifOutput = NIFReader.model2String(outputModel, RDFSerialization.TURTLE);
			HttpHeaders responseHeaders = new HttpHeaders();
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "usage", "e-timelining/timelineCollection", "Success", "", "", "", "");
			
			return new ResponseEntity<String>(outputObject.toString(), responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			InteractionManagement.sendInteraction("dkt-usage@"+request.getRemoteAddr(), "error", "e-timelining/timelineCollection", e.getMessage(), "", "Exception", e.getMessage(), "");
			throw e;
		}
	}
}
