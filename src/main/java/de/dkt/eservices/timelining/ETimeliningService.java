package de.dkt.eservices.timelining;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import de.dkt.eservices.edocumentstorage.DocumentStorage;
import de.dkt.eservices.edocumentstorage.EDocumentStorageService;
import de.dkt.eservices.elucene.ELuceneService;
import de.dkt.eservices.eopennlp.EOpenNLPService;
import de.dkt.eservices.esesame.ESesameService;
import de.dkt.eservices.eweka.EWekaService;
import eu.freme.common.conversion.rdf.RDFConversionService;
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
	EDocumentStorageService docStorageService;
	
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
	

	
    public ResponseEntity<String> storeFileByString(String storageName, String content, String preffix)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ETimeliningService.checkNotNullOrEmpty(storageName, "storage");
        	ETimeliningService.checkNotNullOrEmpty(content, "content");
        	
       		String nifResult = DocumentStorage.storeFileByString(storageName, content, preffix);
       		
           	return ETimeliningService.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} catch (ExternalServiceFailedException e2) {
    		throw e2;
    	}
    }

    public ResponseEntity<String> storeFileByPath(String storageName, String inputFilePath, String preffix)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ETimeliningService.checkNotNullOrEmpty(storageName, "No Storage specified");
        	ETimeliningService.checkNotNullOrEmpty(inputFilePath, "No inputFilePath specified");
        	
       		String nifResult = DocumentStorage.storeFileByPath(storageName, inputFilePath, preffix);
       		
           	return ETimeliningService.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} catch (ExternalServiceFailedException e2) {
    		throw e2;
    	}
    }

    public ResponseEntity<String> storeFileByFile(String storageName, File inputFile, String preffix)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ETimeliningService.checkNotNullOrEmpty(storageName, "No Storage specified");
        	ETimeliningService.checkNotNull(inputFile, "No inputFilePath specified");
        	
       		String nifResult = DocumentStorage.storeFileByFile(storageName, inputFile, preffix);
       		
           	return ETimeliningService.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} catch (ExternalServiceFailedException e2) {
    		throw e2;
    	}
    }

    public ResponseEntity<String> getNifContent(String storageFile)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ETimeliningService.checkNotNullOrEmpty(storageFile, "stored file");

        	String nifResult = DocumentStorage.getNIFContent(storageFile);
       		
           	return ETimeliningService.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} catch (ExternalServiceFailedException e2) {
    		throw e2;
    	}
    }

    public ResponseEntity<String> getFileContent(String storageFile)
            throws ExternalServiceFailedException, BadRequestException {
        try {
        	ETimeliningService.checkNotNullOrEmpty(storageFile, "stored file");

        	String nifResult = DocumentStorage.getNIFContent(storageFile);
       		
           	return ETimeliningService.successResponse(nifResult, "RDF/XML");
        } catch (BadRequestException e) {
            throw e;
    	} catch (ExternalServiceFailedException e2) {
    		throw e2;
    	}
    }
    
    public static void checkNotNullOrEmpty (String param, String message) throws BadRequestException {
    	if( param==null || param.equals("") ){
            throw new BadRequestException("No "+message+" param specified");
    	}
    }

    public static void checkNotNull (Object param, String message) throws BadRequestException {
    	if( param==null ){
            throw new BadRequestException("No "+message+" param specified");
    	}
    }

    public static ResponseEntity<String> successResponse(String body, String contentType) throws BadRequestException {
    	HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.add("Content-Type", contentType);
    	ResponseEntity<String> response = new ResponseEntity<String>(body, responseHeaders, HttpStatus.OK);
    	return response;
    }
}
