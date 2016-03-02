package de.dkt.eservices.timelining;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.context.ApplicationContext;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.ValidationHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;
import junit.framework.Assert;

/**
 * @author 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ETimeliningTest {

	TestHelper testHelper;
	ValidationHelper validationHelper;
	
	String timelineTestPathForWindows = "C:/tests/timeliningStorage/";
	String timelineTestPathForMac = "/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/timeliningStorage/";
	String timelinePathForLinux = "/tmp/storage/timelining/storage1/";

	private String getTimelinePathForOS() {
		String OS = System.getProperty("os.name");
		if(OS.startsWith("Mac")){
			return timelineTestPathForMac;
		}
		else if(OS.startsWith("Windows")){
			return timelineTestPathForWindows;
		}
		else if(OS.startsWith("Linux")){
			return timelinePathForLinux;
		}
		return null;
	}

	@Before
	public void setup() {
		ApplicationContext context = IntegrationTestSetup
				.getContext(TestConstants.pathToPackage);
		testHelper = context.getBean(TestHelper.class);
		validationHelper = context.getBean(ValidationHelper.class);
	}
	
	private HttpRequestWithBody baseRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-timelining/testURL";
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody documentRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-timelining/addDocumentToTimeline";
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody queryRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-timelining/processQuery";
		return Unirest.post(url);
	}
	
	@Test
	public void test1ETimeliningBasic() throws Exception {
				HttpResponse<String> response = baseRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle").asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
	}
	
	@Test
	public void test2ETimeliningProcessDocument() throws UnirestException, IOException,Exception {
		String timelineName = "timelineTest2";
		String timelinePath = getTimelinePathForOS();
		HttpResponse<String> response = documentRequest()
				.queryString("informat", "text/turtle")
				.queryString("input", TestConstants.inputText)
				.queryString("outformat", "turtle")
				.queryString("language", "en")
				.queryString("timelineName", timelineName)
				.queryString("timelinePath", timelinePath)
				.queryString("timelineCreate", true)
				.asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
		assertTrue("The response of the addDocumentToTimeline method is not correct.", response.getBody().equalsIgnoreCase("Document correctly added to timeline: ["+timelineName+"]"));
	}


	@Test
	public void test3ETimeliningProcessQuery() throws UnirestException, IOException,Exception {
		String timelineName = "timelineTest";
		String timelinePath = getTimelinePathForOS();
		
		String queryText = "20000101000000_20000101000001<-->20050220000000_20050220000001";
		
		HttpResponse<String> response = queryRequest()
				.queryString("informat", "text/plain")
				.queryString("input", queryText)
				.queryString("outformat", "turtle")
				.queryString("language", "en")
				.queryString("timelineName", timelineName)
				.queryString("timelinePath", timelinePath)
				.asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals("The output is not as expected", TestConstants.expectedOutputRetrieval, response.getBody());
	}


}
