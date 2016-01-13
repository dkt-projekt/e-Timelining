package de.dkt.eservices.timelining;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.ValidationHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;
import eu.freme.common.conversion.rdf.RDFConstants;

/**
 * @author 
 */

public class ETimeliningTest {

	String sourceLang = "en";
	String targetLang = "de";
	TestHelper testHelper;
	ValidationHelper validationHelper;

	@Before
	public void setup() {
		ApplicationContext context = IntegrationTestSetup
				.getContext(TestConstants.pathToPackage);
		testHelper = context.getBean(TestHelper.class);
		validationHelper = context.getBean(ValidationHelper.class);
	}
	
	private HttpRequestWithBody baseRequestDocument() {
		String url = testHelper.getAPIBaseUrl() + "/e-timelining/processDocument";
		return Unirest.post(url).queryString("source-lang", sourceLang)
				.queryString("target-lang", targetLang);
	}
	
	private HttpRequestWithBody baseRequestQuery() {
		String url = testHelper.getAPIBaseUrl() + "/e-timelining/processQuery";
		return Unirest.post(url).queryString("source-lang", sourceLang)
				.queryString("target-lang", targetLang);
	}
	
	@Test
	public void testETimelining() throws UnirestException, IOException,
			Exception {

		HttpResponse<String> response = baseRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle").asString();

		validationHelper.validateNIFResponse(response,
				RDFConstants.RDFSerialization.TURTLE);

		String data = FileUtils.readFileToString(new File("src/test/resources/rdftest/e-terminology/example1.ttl"));
//		String data = FileUtils.readFileToString(new File("src/test/resources/rdftest/e-translate/data.turtle"));
		response = baseRequest().header("Content-Type", "text/turtle")
				.body(data).asString();
		validationHelper.validateNIFResponse(response, RDFConstants.RDFSerialization.TURTLE);

		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);

		response = baseRequest()
				.queryString("informat", "text").queryString("outformat", "turtle").body("hello world")
				.asString();
		validationHelper.validateNIFResponse(response, RDFConstants.RDFSerialization.TURTLE);

		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);

	}
}
