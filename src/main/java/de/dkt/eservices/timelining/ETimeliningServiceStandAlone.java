package de.dkt.eservices.timelining;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.freme.common.rest.BaseRestController;

@RestController
public class ETimeliningServiceStandAlone extends BaseRestController{

	Logger logger = Logger.getLogger(ETimeliningServiceStandAlone.class);
	
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

			@RequestParam(value = "source-lang") String sourceLang,

			@RequestParam(value = "storageFileName", required = false) String storageFileName,
			@RequestParam(value = "inputFilePath", required = false) String inputFilePath,
//			@RequestParam(value = "preffix", required = false) String preffix,
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "openNLPAnalysis", required = false) String openNLPAnalysis,
			@RequestParam(value = "openNLPModels", required = false) String openNLPModels,
			@RequestParam(value = "sesameStorageName", required = false) String sesameStorageName,
			@RequestParam(value = "luceneIndexName", required = false) String luceneIndexName,
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
		
    	HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.add("Content-Type", "text/plain");
    	ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
    	return response;
    	
//		EDocumentStorageService.checkNotNullOrEmpty(inputFilePath, "input data type");
////		EDocumentStorageService.checkNotNullOrEmpty(storageFileName, "storage file name");
//		EDocumentStorageService.checkNotNullOrEmpty(storageFileName, "language");
//		EDocumentStorageService.checkNotNullOrEmpty(openNLPAnalysis, "open NLP analysis type");
//		EDocumentStorageService.checkNotNullOrEmpty(sesameStorageName, "sesame storage");
//		EDocumentStorageService.checkNotNullOrEmpty(luceneIndexName, "lucene index name");
//		EDocumentStorageService.checkNotNullOrEmpty(luceneFields, "lucene fields");
//		EDocumentStorageService.checkNotNullOrEmpty(luceneAnalyzers, "lucene analyzers");

//		Date d1 = new Date();
//		int fileCounter=1;
//		
//		NIFParameterSet parameters = this.normalizeNif(input, informat,
//				outformat, postBody, acceptHeader, contentTypeHeader, prefix);
//
//		// create rdf model
//		String plaintext = null;
//		Model inputModel = ModelFactory.createDefaultModel();
//
//		if (!parameters.getInformat().equals(
//				RDFConstants.RDFSerialization.PLAINTEXT)) {
//			// input is nif
//			try {
//				inputModel = this.unserializeNif(parameters.getInput(),
//						parameters.getInformat());
//			} catch (Exception e) {
//				logger.error("failed", e);
//				throw new BadRequestException("Error parsing NIF input");
//			}
//		} else {
//			// input is plaintext
//			plaintext = parameters.getInput();
//			getRdfConversionService().plaintextToRDF(inputModel, plaintext,
//					sourceLang, parameters.getPrefix());
//
//		}
//
//		File inputFile = FileFactory.generateFileInstance(inputFilePath);
//		if(inputFile==null || !inputFile.exists()){
//			throw new ExternalServiceFailedException("No input file provided.");
//		}
//
//		Model responseModel = null;
//		try {
//			String nifString = getRdfConversionService().serializeRDF(inputModel,RDFSerialization.TURTLE);
//			
//			//TODO Check the format of the string, if it si NIF or WHAT.
//			
//			String contentString = plaintext;
//
//			ResponseEntity<String> response1 = docStorageService.storeFileByString("testFile"+fileCounter+"_"+d1.toGMTString().replace(' ', '_')+".txt", contentString, "http://jmschnei.dfki.de/test1/");
//			String content2 = response1.getBody();
//		        	
//			System.out.println("INPUT FOR NLP"+content2);
//			ResponseEntity<String> response2 = openNLPService.analyzeText(content2, language, openNLPAnalysis, openNLPModels);
//			String content3 = response2.getBody();
//		        	
//			System.out.println("INPUT FOR SESAME"+content3);
//			ResponseEntity<String> response3 = sesameService.storeEntitiesFromString(sesameStorageName, content3, "NIF");
//			String content4 = response3.getBody();
//			System.out.println("OUTPUT OF SESAME"+content4);
//		        	
//			System.out.println("INPUT FOR LUCENE"+content3);
//			ResponseEntity<String> response4 = luceneService.callLuceneIndexing("string", content3, "NIF", language, luceneFields, luceneAnalyzers, luceneIndexName, luceneCreate);
//			String content5 = response4.getBody();
//			System.out.println("OUTPUT OF LUCENE"+content5);
//					
////	        	ResponseEntity<String> response5 = wekaService.;
////	        	String content6 = response5.getBody();
//					
////	        	ResponseEntity<String> response6 = smtService.;
////	        	String content7 = response6.getBody();
//		
//			responseModel = getRdfConversionService().unserializeRDF(content5,
//					RDFSerialization.RDF_XML);
//			return createSuccessResponse(responseModel, parameters.getOutformat());
//				
//		} catch (Exception e) {
//			if (e instanceof ExternalServiceFailedException) {
//				throw new ExternalServiceFailedException(e.getMessage(),
//						((ExternalServiceFailedException) e)
//								.getHttpStatusCode());
//			} else {
//				throw new ExternalServiceFailedException(e.getMessage());
//			}
//		}
		
	}

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
//			@RequestParam(value = "source-lang") String sourceLang,
//
//			@RequestParam(value = "queryText", required = false) String queryText,
//			@RequestParam(value = "language", required = false) String language,
//			@RequestParam(value = "output", required = false) String output,
//			@RequestParam(value = "openNLPAnalysis", required = false) String openNLPAnalysis,
//			@RequestParam(value = "openNLPModels", required = false) String openNLPModels,
//			@RequestParam(value = "sesameStorageName", required = false) String sesameStorageName,
//			@RequestParam(value = "luceneIndexName", required = false) String luceneIndexName,
//			@RequestParam(value = "luceneFields", required = false) String luceneFields,
//			@RequestParam(value = "luceneAnalyzers", required = false) String luceneAnalyzers,
//			@RequestParam(value = "luceneCreate", required = false) boolean luceneCreate,
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
//		
//		EDocumentStorageService.checkNotNullOrEmpty(language, "language");
//		EDocumentStorageService.checkNotNullOrEmpty(queryText, "query text");
//		EDocumentStorageService.checkNotNullOrEmpty(openNLPAnalysis, "open NLP analysis type");
//		EDocumentStorageService.checkNotNullOrEmpty(openNLPModels, "open NLP models");
//		EDocumentStorageService.checkNotNullOrEmpty(sesameStorageName, "sesame storage");
//		EDocumentStorageService.checkNotNullOrEmpty(luceneIndexName, "lucene index name");
//		EDocumentStorageService.checkNotNullOrEmpty(luceneFields, "lucene fields");
//		EDocumentStorageService.checkNotNullOrEmpty(luceneAnalyzers, "lucene analyzers");
//
//		Date d1 = new Date();
//
//		NIFParameterSet parameters = this.normalizeNif(input, informat,
//				outformat, postBody, acceptHeader, contentTypeHeader, prefix);
//
////		// create rdf model
////		String plaintext = null;
////		Model inputModel = ModelFactory.createDefaultModel();
////
////		if (!parameters.getInformat().equals(
////				RDFConstants.RDFSerialization.PLAINTEXT)) {
////			// input is nif
////			try {
////				inputModel = this.unserializeNif(parameters.getInput(),
////						parameters.getInformat());
////			} catch (Exception e) {
////				logger.error("failed", e);
////				throw new BadRequestException("Error parsing NIF input");
////			}
////		} else {
////			// input is plaintext
////			plaintext = parameters.getInput();
////			getRdfConversionService().plaintextToRDF(inputModel, plaintext,
////					sourceLang, parameters.getPrefix());
////
////		}
//		
//		try {
//			String contentString = queryText;
//			
//			Model model = ModelFactory.createDefaultModel(); 
//			NIFWriter.addInitialString(model, contentString, "http://dkt.dfki.de/query");
//			
//			//ResponseEntity<String> response1 = docStorageService.storeFileByString("testFile"+fileCounter+"_"+d1.toGMTString()+".txt", contentString, "http://jmschnei.dfki.de/test1/");
//
//			String content2 = NIFReader.model2String(model,"RDF/XML");
//			
//        	System.out.println("INPUT FOR NLP"+content2);
//        	ResponseEntity<String> response2 = openNLPService.analyzeText(content2, language, openNLPAnalysis, openNLPModels);
//        	String content3 = response2.getBody();
//        	System.out.println("OUTPUT OF NLP"+content3);
//        	
////        	ResponseEntity<String> response3 = sesameService.retrieveEntitiesFromNIF(sesameStorageName, content3, "NIF");
////        	String content4 = response3.getBody();
//        	
//        	System.out.println("INPUT FOR LUCENE"+content3);
//        	ResponseEntity<String> response4 = luceneService.callLuceneExtraction("nif", content3, language, luceneIndexName, luceneFields, luceneAnalyzers, 30);
//        	String content5 = response4.getBody();
//
//        	
//        	//TODO Ordering of documents based on times.
//        	
//        	
//        	//TODO Merge results from document retrieval and NIF??? HOW???
//			Model outModel = ModelFactory.createDefaultModel();
//			//TODO Convert output to be seen as NIF or as Website.
////			
//			HttpHeaders responseHeaders = new HttpHeaders();
//			responseHeaders.add("Content-Type", RDFSerialization.JSON.getMimeType());
////			
////			StringWriter writer = new StringWriter();
////			outModel.write(writer, "RDF/XML");
////			try {
////				writer.close();
////			} catch (IOException e) {
////				e.printStackTrace();
////			}
////			String rdfString = writer.toString();
////			return new ResponseEntity<String>(rdfString, responseHeaders, HttpStatus.OK);
//			return new ResponseEntity<String>(content5, responseHeaders, HttpStatus.OK);
//		} catch (BadRequestException e) {
//            throw e;
//		}
//	}
}
