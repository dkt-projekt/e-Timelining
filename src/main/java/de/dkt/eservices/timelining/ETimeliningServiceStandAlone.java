package de.dkt.eservices.timelining;

import java.io.ByteArrayInputStream;

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

import de.dkt.common.tools.ParameterChecker;
import eu.freme.common.conversion.rdf.RDFConstants;
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

	@RequestMapping(value = "/e-timelining/testURL", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> testURL(
			@RequestParam(value = "preffix", required = false) String preffix,
            @RequestBody(required = false) String postBody) throws Exception {

    	HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.add("Content-Type", "text/plain");
    	ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
    	return response;
	}
	
	@RequestMapping(value = "/e-timelining/addDocumentToTimeline", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> addDocumentToTimelining(
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "i", required = false) String i,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,

			@RequestParam(value = "timelineName", required = false) String timelineName,
			@RequestParam(value = "timelinePath", required = false) String timelinePath,
			@RequestParam(value = "timelineCreate", required = false) boolean timelineCreate,
			@RequestHeader(value = "addElements", required = false) boolean addElements,
			@RequestParam(value = "language", required = false) String language,
            @RequestBody(required = false) String postBody) throws Exception {

		ParameterChecker.checkNotNullOrEmpty(timelineName, "timeline name", logger);
		ParameterChecker.checkNotNullOrEmpty(timelinePath, "timeline path", logger);

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
    	
		NIFParameterSet parameters = this.normalizeNif(input, informat,
				outformat, postBody, acceptHeader, contentTypeHeader, prefix);

		String plaintext = null;
		Model inputModel = ModelFactory.createDefaultModel();

		if (!parameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
			try {
				inputModel = this.unserializeNif(parameters.getInput(),parameters.getInformat());
			} catch (Exception e) {
				logger.error("BAD REQUEST: error parsing NIF input", e);
						
				throw new BadRequestException("Error parsing NIF input");
			}
		} else {
			// input is plaintext
			plaintext = parameters.getInput();
			getRdfConversionService().plaintextToRDF(inputModel, plaintext,language, parameters.getPrefix());
		}
		try {
			if(timeliningService.addDocumentToTimelining(parameters.getInput(), informat, timelineName, timelinePath, timelineCreate, addElements)){
				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.add("Content-Type", RDFSerialization.PLAINTEXT.contentType());
				return new ResponseEntity<String>("Document correctly added to timeline: ["+timelineName+"]", responseHeaders, HttpStatus.OK);
			}else{
				throw new ExternalServiceFailedException("ERROR at adding the document to the timelining.");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	@RequestMapping(value = "/e-timelining/processQuery", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> processQuery(
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "i", required = false) String i,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,

			@RequestParam(value = "queryText", required = false) String queryText,
			@RequestParam(value = "timelineName", required = false) String timelineName,
			@RequestParam(value = "timelinePath", required = false) String timelinePath,
			@RequestHeader(value = "addElements", required = false) boolean addElements,
			@RequestParam(value = "language", required = false) String language,
            @RequestBody(required = false) String postBody) throws Exception {

		ParameterChecker.checkNotNullOrEmpty(timelineName, "timeline name", logger);
		ParameterChecker.checkNotNullOrEmpty(timelinePath, "timeline path", logger);
		
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
		
		ParameterChecker.checkNotNullOrEmpty(input, "query text", logger);
		ParameterChecker.checkNotNullOrEmpty(timelineName, "timeline name", logger);

		NIFParameterSet parameters = this.normalizeNif(input, informat,
				outformat, postBody, acceptHeader, contentTypeHeader, prefix);

		String plaintext = null;
		Model inputModel = ModelFactory.createDefaultModel();

		if (!parameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
			try {
				inputModel = this.unserializeNif(parameters.getInput(),parameters.getInformat());
			} catch (Exception e) {
				logger.error("BAD REQUEST: error parsing NIF input", e);
				throw new BadRequestException("Error parsing NIF input");
			}
		} else {
			// input is plaintext
			plaintext = parameters.getInput();
			getRdfConversionService().plaintextToRDF(inputModel, plaintext, language, parameters.getPrefix());
		}
		try {
			JSONObject obj = timeliningService.processQuery(queryText, informat, timelineName, timelinePath, addElements);

			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("Content-Type", RDFSerialization.JSON.contentType());
			return new ResponseEntity<String>(obj.toString(), responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	@RequestMapping(value = "/e-timelining/processDocumentsCollection", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> timelineDocumentCollection(
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "i", required = false) String i,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestHeader(value = "addElements", required = false) boolean addElements,
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
		ParameterChecker.checkNotNullOrEmpty(input, "input text", logger);

		if(!informat.equalsIgnoreCase("json") && !informat.equalsIgnoreCase("text/json") ){
			logger.error("Bad input INFORMAT, only JSON (json or text/json) acccepted");
			throw new BadRequestException("Bad input INFORMAT, only JSON acccepted");
		}
		try {
			JSONObject inputJSONObject = new JSONObject(input);
			JSONObject obj = timeliningService.processDocumentsCollection(inputJSONObject,addElements);

			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("Content-Type", RDFSerialization.JSON.contentType());
			return new ResponseEntity<String>(obj.toString(), responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

    public static void main(String[] args) {
		Model responseModel = null;
		try {
			String input = "{\"results\":{\"documents\":{\"document1000\":{\"score\":0.028767451643943787,\"docId\":1,\"content\":\"1936\n\nCoup leader Sanjurjo was killed in a plane crash on 20 July, leaving an effective command split between Mola in the North and Franco in the South. On 21 July, the fifth day of the rebellion, the Nationalists captured the main Spanish naval base at Ferrol in northwestern Spain. A rebel force under Colonel Beorlegui Canet, sent by General Emilio Mola, undertook the Campaign of Guipúzcoa from July to September. The capture of Guipúzcoa isolated the Republican provinces in the north. On 5 September, after heavy fighting the force took Irún, closing the French border to the Republicans. On 13 September, the Basques surrendered San Sebastián to the Nationalists, who then advanced toward their capital, Bilbao. The Republican militias on the border of Viscaya halted these forces at the end of September.\n\"},\"document1\":{\"score\":0.028767451643943787,\"docId\":1,\"content\":\"1936\n\nCoup leader Sanjurjo was killed in a plane crash on 20 July, leaving an effective command split between Mola in the North and Franco in the South. On 21 July, the fifth day of the rebellion, the Nationalists captured the main Spanish naval base at Ferrol in northwestern Spain. A rebel force under Colonel Beorlegui Canet, sent by General Emilio Mola, undertook the Campaign of Guipúzcoa from July to September. The capture of Guipúzcoa isolated the Republican provinces in the north. On 5 September, after heavy fighting the force took Irún, closing the French border to the Republicans. On 13 September, the Basques surrendered San Sebastián to the Nationalists, who then advanced toward their capital, Bilbao. The Republican militias on the border of Viscaya halted these forces at the end of September.\n\"}},\"numberResults\":1,\"querytext\":\"content:sanjurjo\"}}";
			ByteArrayInputStream bis = new ByteArrayInputStream(input.getBytes());
			responseModel = ModelFactory.createDefaultModel();
			responseModel.read(bis, "JSON");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}
