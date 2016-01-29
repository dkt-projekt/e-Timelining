package de.dkt.eservices.timelining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.elucene.ELuceneService;
import de.dkt.eservices.eopennlp.EOpenNLPService;
import de.dkt.eservices.esesame.ESesameService;
import de.dkt.eservices.eweka.EWekaService;
import eu.freme.common.conversion.rdf.RDFConversionService;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;


/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 *
 */
@Component
public class ETimeliningService {
	
	@Value("${documentstorage.storage}")
	String documentStorageLocation;
	
	@Value("${sesame.storage}")
	String sesameStorageLocation;
	
	@Autowired
	ESesameService sesameService;
	
	@Autowired
	ELuceneService luceneService;
	
	@Autowired
	EOpenNLPService openNLPService;
	
	@Autowired
	EWekaService wekaService;
	
//	@Autowired
//	ESMTService smtService;
	
	@Autowired
	RDFConversionService rdfConversion;
    
	public ResponseEntity<String> storeData(String data,String inFormat,
//			@RequestParam(value = "preffix", required = false) String preffix,
			String language,String openNLPAnalysis,String openNLPModels,
			String sesameStorageName, String sesameStoragePath,boolean sesameCreate,
			String luceneIndexName,String luceneIndexPath,
			String luceneFields,String luceneAnalyzers,boolean luceneCreate,
			String postBody) throws Exception {

		try{
			String contentString = data;
	    	System.out.println("INPUT FOR NLP"+contentString);
	    	ResponseEntity<String> response2 = openNLPService.analyzeText(contentString, language, openNLPAnalysis, openNLPModels, inFormat);
	    	String content3 = response2.getBody();
	    	
	    	System.out.println("INPUT FOR SESAME"+content3);
	    	ResponseEntity<String> response3 = sesameService.storeEntitiesFromString(sesameStorageName,
	    			sesameStoragePath,sesameCreate,content3, "NIF");
	    	String content4 = response3.getBody();
	    	
	    	System.out.println("INPUT FOR LUCENE"+content4);
	    	ResponseEntity<String> response4 = luceneService.callLuceneIndexing("string", content4, "NIF", 
	    			language, luceneFields, luceneAnalyzers, 
	    			luceneIndexName, luceneIndexPath, luceneCreate);
	    	String content5 = response4.getBody();
	    	System.out.println("OUTPUT OF LUCENE"+content5);
			
//		    ResponseEntity<String> response5 = wekaService.;
//		    String content6 = response5.getBody();
			
//		    ResponseEntity<String> response6 = smtService.;
//        	String content7 = response6.getBody();
				
	    	HttpHeaders responseHeaders = new HttpHeaders();
	    	responseHeaders.add("Content-Type", "RDF/XML");
			String rdfString = content5;
			return new ResponseEntity<String>(rdfString, responseHeaders, HttpStatus.OK);
		} catch (BadRequestException e) {
            throw e;
        } catch (ExternalServiceFailedException e) {
            throw e;
        }
	}
	
	public ResponseEntity<String> processQuery(String queryText, String informat, String language, String output,
			String openNLPAnalysis,String openNLPModels,
			String sesameStorageName, String sesameStoragePath,
			String luceneIndexName,String luceneIndexPath,
			String luceneFields,String luceneAnalyzers,
			String postBody) throws Exception {
		try {
			String contentString = queryText;
			
			Model model = ModelFactory.createDefaultModel(); 
			NIFWriter.addInitialString(model, contentString, "http://dkt.dfki.de/query");

			Model outModel = ModelFactory.createDefaultModel();

			//ResponseEntity<String> response1 = docStorageService.storeFileByString("testFile"+fileCounter+"_"+d1.toGMTString()+".txt", contentString, "http://jmschnei.dfki.de/test1/");

			String content2 = NIFReader.model2String(model,"TTL");
			
//        	System.out.println("INPUT FOR NLP"+content2);
        	ResponseEntity<String> response2 = openNLPService.analyzeText(content2, language, openNLPAnalysis, openNLPModels, informat);
           	String content3 = response2.getBody();
//        	System.out.println("OUTPUT OF NLP"+content3);
        	
        	ResponseEntity<String> response3 = sesameService.retrieveEntitiesFromNIF(sesameStorageName, sesameStoragePath, content3);
        	String content4 = response3.getBody();
//        	System.out.println("OUTPUT OF SESAME"+content4);

        	//Merge results from document retrieval and NIF??? HOW???
        	
        	ResponseEntity<String> response4 = luceneService.callLuceneExtraction("nif", content3, language, 
        			luceneIndexName, luceneIndexPath, luceneFields, luceneAnalyzers, 30);
        	String content5 = response4.getBody();
//        	System.out.println("OUTPUT OF LUCENE"+content5);

        	//Merge results from document retrieval and NIF??? HOW???

        	
        	//TODO Include the ordering stuff
			
			//TODO Convert output to be seen as NIF or as Website.
//			
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("Content-Type", RDFSerialization.JSON.contentType());
//			
//			StringWriter writer = new StringWriter();
//			outModel.write(writer, "RDF/XML");
//			try {
//				writer.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			String rdfString = writer.toString();
//			return new ResponseEntity<String>(rdfString, responseHeaders, HttpStatus.OK);
			return new ResponseEntity<String>(content5, responseHeaders, HttpStatus.OK);
		} catch (BadRequestException e) {
            throw e;
		}
	}

    public static ResponseEntity<String> successResponse(String body, String contentType) throws BadRequestException {
    	HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.add("Content-Type", contentType);
    	ResponseEntity<String> response = new ResponseEntity<String>(body, responseHeaders, HttpStatus.OK);
    	return response;
    }
}
