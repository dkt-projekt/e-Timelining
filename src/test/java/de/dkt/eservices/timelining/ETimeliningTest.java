package de.dkt.eservices.timelining;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

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
import junit.framework.Assert;

/**
 * @author 
 */

public class ETimeliningTest {

	TestHelper testHelper;
	ValidationHelper validationHelper;
	String inputText = "@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ."+
"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> ."+
"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> ."+
"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> ."+
"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> ."+
""+
"<http://dkt.dfki.de/examples/#char=0,813>"+
"        a                  nif:RFC5147String , nif:String , nif:Context ;"+
"        nif:beginIndex     \"0\"^^xsd:nonNegativeInteger ;"+
"        nif:endIndex       \"813\"^^xsd:nonNegativeInteger ;"+
"        nif:isString       \"\"\"1936\n\nCoup leader Sanjurjo was killed in a plane crash on 20 July, leaving an effective command split between Mola in the North and Franco in the South. On 21 July, the fifth day of the rebellion, the Nationalists captured the main Spanish naval base at Ferrol in northwestern Spain. A rebel force under Colonel Beorlegui Canet, sent by General Emilio Mola, undertook the Campaign of Guipúzcoa from July to September. The capture of Guipúzcoa isolated the Republican provinces in the north. On 5 September, after heavy fighting the force took Irún, closing the French border to the Republicans. On 13 September, the Basques surrendered San Sebastián to the Nationalists, who then advanced toward their capital, Bilbao. The Republican militias on the border of Viscaya halted these forces at the end of September.\n\"\"\"^^xsd:string ;"+
"        nif:meanDateRange  \"02110711030000_16631213030000\"^^xsd:string .";
	
	String expectedOutput = "@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" + 
"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" + 
"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" + 
"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" + 
"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=146,151>\n" + 
"        a                     nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf          \"South\"^^xsd:string ;\n" + 
"        nif:beginIndex        \"146\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex          \"151\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity            <http://dkt.dfki.de/ontologies/nif#location> ;\n" + 
"        nif:referenceContext  <http://dkt.dfki.de/examples/#char=0,813> ;\n" + 
"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Southern_United_States> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=201,213>\n" + 
"        a                     nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf          \"Nationalists\"^^xsd:string ;\n" + 
"        nif:beginIndex        \"201\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex          \"213\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity            <http://dkt.dfki.de/ontologies/nif#organization> ;\n" + 
"        nif:referenceContext  <http://dkt.dfki.de/examples/#char=0,813> ;\n" + 
"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Kuomintang> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=399,403>\n" + 
"        a                  nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf       \"July\"^^xsd:string ;\n" + 
"        nif:beginIndex     \"399\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex       \"403\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n" + 
"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=19360701000000_19360702000000> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=494,505>\n" + 
"        a                  nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf       \"5 September\"^^xsd:string ;\n" + 
"        nif:beginIndex     \"494\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex       \"505\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n" + 
"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=19360905000000_19360906000000> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=345,356>\n" + 
"        a                     nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf          \"Emilio Mola\"^^xsd:string ;\n" + 
"        nif:beginIndex        \"345\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex          \"356\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" + 
"        nif:referenceContext  <http://dkt.dfki.de/examples/#char=0,813> ;\n" + 
"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Emilio_Mola> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=18,26>\n" + 
"        a                     nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf          \"Sanjurjo\"^^xsd:string ;\n" + 
"        nif:beginIndex        \"18\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex          \"26\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" + 
"        nif:referenceContext  <http://dkt.dfki.de/examples/#char=0,813> ;\n" + 
"        itsrdf:taIdentRef     <http://dbpedia.org/resource/José_Sanjurjo> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=372,393>\n" + 
"        a                     nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf          \"Campaign of Guipúzcoa\"^^xsd:string ;\n" + 
"        nif:beginIndex        \"372\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex          \"393\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity            <http://dkt.dfki.de/ontologies/nif#organization> ;\n" + 
"        nif:referenceContext  <http://dkt.dfki.de/examples/#char=0,813> ;\n" + 
"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Political_campaign> , <http://dbpedia.org/resource/Gipuzkoa> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=0,813>\n" + 
"        a                    nif:Context , nif:RFC5147String , nif:String ;\n" + 
"        nif:beginIndex       \"0\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:centralGeoPoint  \"42.69566666666667_-4.171833333333334\"^^xsd:string ;\n" + 
"        nif:endIndex         \"813\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:geoStandardDevs  \"1.1412075124269716_2.155062225906914\"^^xsd:string ;\n" + 
"        nif:indexName        \"test2\"^^xsd:string ;\n" + 
"        nif:indexPath        \"/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/luceneindexes/\"^^xsd:string ;\n" + 
"        nif:isString         \"1936\\n\\nCoup leader Sanjurjo was killed in a plane crash on 20 July, leaving an effective command split between Mola in the North and Franco in the South. On 21 July, the fifth day of the rebellion, the Nationalists captured the main Spanish naval base at Ferrol in northwestern Spain. A rebel force under Colonel Beorlegui Canet, sent by General Emilio Mola, undertook the Campaign of Guipúzcoa from July to September. The capture of Guipúzcoa isolated the Republican provinces in the north. On 5 September, after heavy fighting the force took Irún, closing the French border to the Republicans. On 13 September, the Basques surrendered San Sebastián to the Nationalists, who then advanced toward their capital, Bilbao. The Republican militias on the border of Viscaya halted these forces at the end of September.\\n\"^^xsd:string ;\n" + 
"        nif:meanDateRange    \"19360531090000_19361022090000\"^^xsd:string , \"02110711030000_16631213030000\"^^xsd:string .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=407,416>\n" + 
"        a                  nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf       \"September\"^^xsd:string ;\n" + 
"        nif:beginIndex     \"407\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex       \"416\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n" + 
"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=19360901000000_19360902000000> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=254,260>\n" + 
"        a                     nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf          \"Ferrol\"^^xsd:string ;\n" + 
"        nif:beginIndex        \"254\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex          \"260\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity            <http://dkt.dfki.de/ontologies/nif#location> ;\n" + 
"        nif:geoPoint          \"43.46666666666667_-8.25\"^^xsd:string ;\n" + 
"        nif:referenceContext  <http://dkt.dfki.de/examples/#char=0,813> ;\n" + 
"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Ferrol,_Galicia> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=277,282>\n" + 
"        a                     nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf          \"Spain\"^^xsd:string ;\n" + 
"        nif:beginIndex        \"277\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex          \"282\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity            <http://dkt.dfki.de/ontologies/nif#location> ;\n" + 
"        nif:geoPoint          \"43.0_-4.0\"^^xsd:string ;\n" + 
"        nif:referenceContext  <http://dkt.dfki.de/examples/#char=0,813> ;\n" + 
"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Spain> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=657,669>\n" + 
"        a                     nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf          \"Nationalists\"^^xsd:string ;\n" + 
"        nif:beginIndex        \"657\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex          \"669\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity            <http://dkt.dfki.de/ontologies/nif#organization> ;\n" + 
"        nif:referenceContext  <http://dkt.dfki.de/examples/#char=0,813> ;\n" + 
"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Kuomintang> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=58,65>\n" + 
"        a                  nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf       \"20 July\"^^xsd:string ;\n" + 
"        nif:beginIndex     \"58\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex       \"65\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n" + 
"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=19360720000000_19360721000000> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=636,649>\n" + 
"        a                     nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf          \"San Sebastián\"^^xsd:string ;\n" + 
"        nif:beginIndex        \"636\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex          \"649\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity            <http://dkt.dfki.de/ontologies/nif#location> ;\n" + 
"        nif:geoPoint          \"43.32138888888889_-1.9855555555555555\"^^xsd:string ;\n" + 
"        nif:referenceContext  <http://dkt.dfki.de/examples/#char=0,813> ;\n" + 
"        itsrdf:taIdentRef     <http://dbpedia.org/resource/San_Sebastián> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=795,811>\n" + 
"        a                  nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf       \"end of September\"^^xsd:string ;\n" + 
"        nif:beginIndex     \"795\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex       \"811\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n" + 
"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=19360920000000_19360930000000> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=156,163>\n" + 
"        a                  nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf       \"21 July\"^^xsd:string ;\n" + 
"        nif:beginIndex     \"156\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex       \"163\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n" + 
"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=19360721000000_19360722000000> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=711,717>\n" + 
"        a                     nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf          \"Bilbao\"^^xsd:string ;\n" + 
"        nif:beginIndex        \"711\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex          \"717\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity            <http://dkt.dfki.de/ontologies/nif#location> ;\n" + 
"        nif:geoPoint          \"43.25694444444444_-2.923611111111111\"^^xsd:string ;\n" + 
"        nif:referenceContext  <http://dkt.dfki.de/examples/#char=0,813> ;\n" + 
"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Bilbao> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=598,610>\n" + 
"        a                  nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf       \"13 September\"^^xsd:string ;\n" + 
"        nif:beginIndex     \"598\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex       \"610\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n" + 
"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=19360913000000_19360914000000> .\n" + 
"\n" + 
"<http://dkt.dfki.de/examples/#char=0,4>\n" + 
"        a                  nif:RFC5147String , nif:String ;\n" + 
"        nif:anchorOf       \"1936\"^^xsd:string ;\n" + 
"        nif:beginIndex     \"0\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:endIndex       \"4\"^^xsd:nonNegativeInteger ;\n" + 
"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n" + 
"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=19360101000000_19370101000000> .\n";

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
		String url = testHelper.getAPIBaseUrl() + "/e-timelining/processDocument";
		return Unirest.post(url);
	}
	
	private HttpRequestWithBody queryRequest() {
		String url = testHelper.getAPIBaseUrl() + "/e-timelining/processQuery";
		return Unirest.post(url);
	}
	
	@Test
	public void testETimeliningBasic() throws UnirestException, IOException,
			Exception {

				HttpResponse<String> response = baseRequest()
				.queryString("informat", "text")
				.queryString("input", "hello world")
				.queryString("outformat", "turtle").asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);

	}
	
	@Test
	public void testETimeliningProcessDocument() throws UnirestException, IOException,Exception {
		String indexPath = "";
		String sesamePath = "";
		String OS = System.getProperty("os.name");
		
		if(OS.startsWith("Mac")){
			indexPath = "/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/luceneindexes/";
			sesamePath = "/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/";
		}
		else if(OS.startsWith("Windows")){
			indexPath = "C://tests/luceneindexes/";
			sesamePath = "C://tests/sesamestorage/";
		}
		else if(OS.startsWith("Linux")){
			indexPath = "/tmp/storage/documents/index1/";
			sesamePath = "/tmp/storage/sesame/storage1/";
		}
		
		HttpResponse<String> response = documentRequest()
				.queryString("informat", "text/turtle")
				.queryString("input", inputText)
				.queryString("outformat", "turtle")
				.queryString("language", "en")
				.queryString("openNLPAnalysis", "ner")
				.queryString("openNLPModels", "ner-wikinerEn_LOC;ner-wikinerEn_ORG;ner-wikinerEn_PER;englishDates")
				.queryString("sesameStorageName", "test2")
				.queryString("sesameStoragePath", sesamePath)
				.queryString("sesameCreate", true)
				.queryString("luceneIndexName", "test2")
				.queryString("luceneIndexPath", indexPath)
				.queryString("luceneFields", "content")
				.queryString("luceneAnalyzers", "standard")
				.queryString("luceneCreate", true)
				.asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals(expectedOutput, response.getBody());
	}


	@Test
	public void testETimeliningProcessQuery() throws UnirestException, IOException,Exception {
		String indexPath = "";
		String sesamePath = "";
		String OS = System.getProperty("os.name");
		
		if(OS.startsWith("Mac")){
			indexPath = "/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/luceneindexes/";
			sesamePath = "/Users/jumo04/Documents/DFKI/DKT/dkt-test/tests/sesamestorages/";
		}
		else if(OS.startsWith("Windows")){
			indexPath = "C://tests/luceneindexes/";
			sesamePath = "C://tests/sesamestorage/";
		}
		else if(OS.startsWith("Linux")){
			indexPath = "/tmp/storage/documents/index1/";
			sesamePath = "/tmp/storage/sesame/storage1/";
		}
		
		String queryText = "Sanjurjo";
		
		HttpResponse<String> response = queryRequest()
				.queryString("informat", "text/turtle")
				.queryString("input", queryText)
				.queryString("queryText", queryText)
				.queryString("outformat", "turtle")
				.queryString("language", "en")
				.queryString("openNLPAnalysis", "ner")
				.queryString("openNLPModels", "ner-wikinerEn_LOC;ner-wikinerEn_ORG;ner-wikinerEn_PER;englishDates")
				.queryString("sesameStorageName", "test2")
				.queryString("sesameStoragePath", sesamePath)
				.queryString("luceneIndexName", "test2")
				.queryString("luceneIndexPath", indexPath)
				.queryString("luceneFields", "content")
				.queryString("luceneAnalyzers", "standard")
				.asString();
		assertTrue(response.getStatus() == 200);
		assertTrue(response.getBody().length() > 0);
		Assert.assertEquals(expectedOutputRetrieval, response.getBody());
	}
	
	String expectedOutputRetrieval = "{\"results\":{\"documents\":{\"document1\":{\"score\":0.028767451643943787,\"docId\":1,\"content\":\"1936\\n\\nCoup leader Sanjurjo was killed in a plane crash on 20 July, leaving an effective command split between Mola in the North and Franco in the South. On 21 July, the fifth day of the rebellion, the Nationalists captured the main Spanish naval base at Ferrol in northwestern Spain. A rebel force under Colonel Beorlegui Canet, sent by General Emilio Mola, undertook the Campaign of Guipúzcoa from July to September. The capture of Guipúzcoa isolated the Republican provinces in the north. On 5 September, after heavy fighting the force took Irún, closing the French border to the Republicans. On 13 September, the Basques surrendered San Sebastián to the Nationalists, who then advanced toward their capital, Bilbao. The Republican militias on the border of Viscaya halted these forces at the end of September.\\n\"}},\"numberResults\":1,\"querytext\":\"content:sanjurjo\"}}";

}
