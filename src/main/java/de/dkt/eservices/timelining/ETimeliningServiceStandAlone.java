package de.dkt.eservices.timelining;

import java.io.ByteArrayInputStream;

import org.apache.log4j.Logger;
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
import eu.freme.common.conversion.rdf.RDFConversionService;
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
	
	@RequestMapping(value = "/e-timelining/processDocument", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> storeData(
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

			@RequestParam(value = "source-lang", required = false) String sourceLang,

			@RequestParam(value = "storageFileName", required = false) String storageFileName,
			@RequestParam(value = "inputFilePath", required = false) String inputFilePath,
//			@RequestParam(value = "preffix", required = false) String preffix,
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "openNLPAnalysis", required = false) String openNLPAnalysis,
			@RequestParam(value = "openNLPModels", required = false) String openNLPModels,
			@RequestParam(value = "sesameStorageName", required = false) String sesameStorageName,
			@RequestParam(value = "sesameStoragePath", required = false) String sesameStoragePath,
			@RequestParam(value = "sesameCreate", required = false) boolean sesameCreate,
			@RequestParam(value = "luceneIndexName", required = false) String luceneIndexName,
			@RequestParam(value = "luceneIndexPath", required = false) String luceneIndexPath,
			@RequestParam(value = "luceneFields", required = false) String luceneFields,
			@RequestParam(value = "luceneAnalyzers", required = false) String luceneAnalyzers,
			@RequestParam(value = "luceneCreate", required = false) boolean luceneCreate,
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
    	
		ParameterChecker.checkNotNullOrEmpty(language, "language");
		ParameterChecker.checkNotNullOrEmpty(openNLPAnalysis, "open NLP analysis type");
		ParameterChecker.checkNotNullOrEmpty(sesameStorageName, "sesame storage");
		ParameterChecker.checkNotNullOrEmpty(luceneIndexName, "lucene index name");
		ParameterChecker.checkNotNullOrEmpty(luceneFields, "lucene fields");
		ParameterChecker.checkNotNullOrEmpty(luceneAnalyzers, "lucene analyzers");

		NIFParameterSet parameters = this.normalizeNif(input, informat,
				outformat, postBody, acceptHeader, contentTypeHeader, prefix);

		// create rdf model
		String plaintext = null;
		Model inputModel = ModelFactory.createDefaultModel();

		if (!parameters.getInformat().equals(
				RDFConstants.RDFSerialization.PLAINTEXT)) {
			// input is nif
			try {
				inputModel = this.unserializeNif(parameters.getInput(),
						parameters.getInformat());
			} catch (Exception e) {
				logger.error("failed", e);
				throw new BadRequestException("Error parsing NIF input");
			}
		} else {
			// input is plaintext
			plaintext = parameters.getInput();
			getRdfConversionService().plaintextToRDF(inputModel, plaintext,
					sourceLang, parameters.getPrefix());
		}

		Model responseModel = null;
		try {
			String nifString = getRdfConversionService().serializeRDF(inputModel,RDFSerialization.fromValue(informat));
			
			//TODO Check the format of the string, if it si NIF or WHAT.
			
			String contentString = nifString;
			ResponseEntity<String> responseEntity = timeliningService.storeData(
					contentString, informat, language, openNLPAnalysis, openNLPModels, 
					sesameStorageName, sesameStoragePath, sesameCreate, 
					luceneIndexName, luceneIndexPath, 
					luceneFields, luceneAnalyzers, 
					luceneCreate, postBody);

			try{
				responseModel = getRdfConversionService().unserializeRDF(responseEntity.getBody(),
						RDFSerialization.RDF_XML);
			}
			catch(Exception e){
				responseModel = getRdfConversionService().unserializeRDF(responseEntity.getBody(),
						RDFSerialization.TURTLE);
			}
			return createSuccessResponse(responseModel, parameters.getOutformat());				
		} catch (Exception e) {
			if (e instanceof ExternalServiceFailedException) {
				throw new ExternalServiceFailedException(e.getMessage(),
						((ExternalServiceFailedException) e)
								.getHttpStatusCode());
			} else {
				throw e;
			}
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
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "output", required = false) String output,
			@RequestParam(value = "openNLPAnalysis", required = false) String openNLPAnalysis,
			@RequestParam(value = "openNLPModels", required = false) String openNLPModels,
			@RequestParam(value = "sesameStorageName", required = false) String sesameStorageName,
			@RequestParam(value = "sesameStoragePath", required = false) String sesameStoragePath,
			@RequestParam(value = "luceneIndexName", required = false) String luceneIndexName,
			@RequestParam(value = "luceneIndexPath", required = false) String luceneIndexPath,
			@RequestParam(value = "luceneFields", required = false) String luceneFields,
			@RequestParam(value = "luceneAnalyzers", required = false) String luceneAnalyzers,
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
		
		ParameterChecker.checkNotNullOrEmpty(language, "language");
		ParameterChecker.checkNotNullOrEmpty(queryText, "query text");
		ParameterChecker.checkNotNullOrEmpty(openNLPAnalysis, "open NLP analysis type");
		ParameterChecker.checkNotNullOrEmpty(openNLPModels, "open NLP models");
		ParameterChecker.checkNotNullOrEmpty(sesameStorageName, "sesame storage");
		ParameterChecker.checkNotNullOrEmpty(luceneIndexName, "lucene index name");
		ParameterChecker.checkNotNullOrEmpty(luceneFields, "lucene fields");
		ParameterChecker.checkNotNullOrEmpty(luceneAnalyzers, "lucene analyzers");

		NIFParameterSet parameters = this.normalizeNif(input, informat,
				outformat, postBody, acceptHeader, contentTypeHeader, prefix);
		
		Model responseModel = null;
		try {
			String contentString = queryText;
			
			ResponseEntity<String> responseEntity = timeliningService.processQuery(contentString, 
					informat, language, output, 
					openNLPAnalysis, openNLPModels, sesameStorageName, sesameStoragePath, 
					luceneIndexName, luceneIndexPath, luceneFields, luceneAnalyzers, postBody);

//			System.out.println("OBTAINED BODY: "+responseEntity.getBody());
//			ByteArrayInputStream bis = new ByteArrayInputStream(responseEntity.getBody().getBytes());
//			responseModel = ModelFactory.createDefaultModel();
//			responseModel.read(bis, "JSON");
////			responseModel = getRdfConversionService().unserializeRDF(responseEntity.getBody(),RDFSerialization.JSON);
			return createSuccessResponse(responseEntity.getBody(), parameters.getOutformat());
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof ExternalServiceFailedException) {
				throw new ExternalServiceFailedException(e.getMessage(),
						((ExternalServiceFailedException) e)
						.getHttpStatusCode());
			} else {
				throw new ExternalServiceFailedException(e.getMessage());
			}
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
    
	public ResponseEntity<String> createSuccessResponse(String text,
			RDFConstants.RDFSerialization rdfFormat) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", rdfFormat.contentType());
		return new ResponseEntity<String>(text, responseHeaders, HttpStatus.OK);
	}
}
