package de.dkt.eservices.timelining;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.common.niftools.DBO;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFManagement;
import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.timelining.modules.TimeExpressionRange;
import de.dkt.eservices.timelining.modules.TimelinedElement;
import de.dkt.eservices.timelining.modules.TimelinedElement.ElementType;
import de.dkt.eservices.timelining.modules.Timelining;
import eu.freme.common.conversion.rdf.JenaRDFConversionService;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.conversion.rdf.RDFConversionService;
import eu.freme.common.exception.BadRequestException;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 *
 */
@Component
public class ETimeliningService {

	Logger logger = Logger.getLogger(ETimeliningService.class);

	String timelinesDirectory = "timelines/";
	
	@Autowired
	RDFConversionService rdfConversion = new JenaRDFConversionService();
    
//	public boolean addDocumentToTimelining(String data,String inFormat,String timeliningName, String timeliningPath,boolean timeliningCreate, boolean addElements) throws Exception{
//		if(timeliningPath!=null && !timeliningPath.equalsIgnoreCase("")){
//			timelinesDirectory = timeliningPath;
//		}
//		String timeliningFilePath = timelinesDirectory + "" + timeliningName + ".time";
////		List<Model> inputModels = new LinkedList<Model>();
//		List<String> sInputModels = new LinkedList<String>();
//		List<String> sOutputModels = new LinkedList<String>();
//		File timelineFile;
////		List<Model> orderedModels = null;
//		try{
//			try{
//				timelineFile = FileFactory.generateFileInstance(timeliningFilePath);
//				ObjectInputStream ois = new ObjectInputStream (new FileInputStream (timelineFile));
//				sInputModels = (List<String>) ois.readObject();
//				ois.close();
//			}
//			catch(Exception e){
//				if(timeliningCreate){
//					timelineFile = FileFactory.generateOrCreateFileInstance(timeliningFilePath);
//				}
//				else{
//					logger.error(e.getMessage());
//					throw e;
//				}
//			}
//		}
//		catch(Exception e){
//			logger.error("Timeline ["+timeliningFilePath+"] not found or not readable!!!");
//			throw new BadRequestException("Timeline ["+timeliningFilePath+"] not found or not readable!!!");
//		}
//		try{
//			Model nifModel = rdfConversion.unserializeRDF(data, RDFSerialization.fromValue(inFormat));
//			TimelinedElement tle = Timelining.generateTimelinedDocumentFromModel(nifModel, addElements);
//	        sInputModels.add(tle.toString());
//	        sOutputModels = Timelining.generateTimeline(sInputModels,addElements);
//        } catch (Exception e) {
//			logger.error("Error at generating nif module or timeline!!!");
//            throw e;
//        }
//		try{
//			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(timelineFile,false));
//			oos.writeObject (sOutputModels);
//			oos.close();
//			return true;
//		} catch (Exception e) {
//			logger.error("Error at writting timeline into outputfile.",e);
//            throw e;
//        }
//	}
//	
//	public JSONObject processQuery(String queryText, String informat,String timeliningName, String timeliningPath, boolean returnElements) throws Exception {
//		if(timeliningPath!=null && !timeliningPath.equalsIgnoreCase("")){
//			timelinesDirectory = timeliningPath;
//		}
//		String timeliningFilePath = timelinesDirectory + "" + timeliningName + ".time";
//		List<String> sInputModels = new LinkedList<String>(); 
////		List<Model> inputModels = new LinkedList<Model>(); 
//		File timelineFile;
//		List<TimelinedElement> sOutputModels = null;
//		try{
//			timelineFile = FileFactory.generateFileInstance(timeliningFilePath);
//			ObjectInputStream ois = new ObjectInputStream (new FileInputStream (timelineFile));
//			sInputModels = (List<String>) ois.readObject();
//			ois.close();
////			for (String s : sInputModels) {
////				inputModels.add(NIFReader.extractModelFromFormatString(s, RDFSerialization.TURTLE));
////			}
//		}
//		catch(Exception e){
//			logger.error("Timeline ["+timeliningFilePath+"] not found or not readable!!!");
//			throw new BadRequestException("Timeline ["+timeliningFilePath+"] not found or not readable!!!");
//		}
//		try{
//			if(queryText==null || queryText.equalsIgnoreCase("all")){
//				sOutputModels = Timelining.generateTLETimeline(sInputModels,returnElements);
//			}
//			else{
//				sOutputModels = Timelining.queryTLETimeline(sInputModels, queryText, returnElements);
//			}
//			JSONObject obj = generateJSONFromTimelinedElements(sOutputModels);
//			return obj;
//		} catch (Exception e) {
//			logger.error("Error at generating nif module or timeline!!!",e);
//			throw e;
//		}
//	}
//
//	public JSONObject processDocumentsCollection(JSONObject jsonObject, boolean addElements) throws Exception {
//		try {
//			List<TimelinedElement> inputNIFModels = generateTimelinedElementsFromJSON(jsonObject);
//			List<TimelinedElement> outputNIFModels = Timelining.generateTimelineFromModels(inputNIFModels,addElements);
//			JSONObject outputJSONObject = generateJSONFromTimelinedElements(outputNIFModels);
//			return outputJSONObject;
//		} catch (BadRequestException e) {
//			logger.error(e.getMessage());
//            throw e;
//		}
//	}
//
	public JSONObject processDocumentsCollection(Model collectionModel, String granularity) throws Exception {
		try {
			List<TimelinedElement> inputNIFModels = generateTimelinedElementsFromJENACollection(collectionModel,granularity);
			List<TimelinedElement> outputNIFModels = Timelining.generateTimelineFromModels(inputNIFModels,false);
			JSONObject outputJSONObject = generateJSONFromTimelinedElements(outputNIFModels);
			return outputJSONObject;
		} catch (BadRequestException e) {
			logger.error(e.getMessage());
            throw e;
		}
	}
//
//	public List<Model> generateJENAFromJSON(JSONObject jsonObject) throws Exception {
//		try {
//			List<Model> outputList = new LinkedList<Model>();
//        	JSONArray jsonDocumentsArray = jsonObject.getJSONArray("results");
//        	for (int i = 0; i < jsonDocumentsArray.length(); i++) {
//				JSONObject obj = jsonDocumentsArray.getJSONObject(i);
//				String nifText = obj.getString("content");
//				Model m = NIFReader.extractModelFromFormatString(nifText, RDFSerialization.TURTLE);
//				outputList.add(m);
//			}
//			return outputList;
//		} catch (BadRequestException e) {
//			logger.error(e.getMessage());
//            throw e;
//		}
//	}
//
//	public JSONObject generateJSONFromJENA(List<Model> listModels) throws Exception {
//		try{
//        	JSONObject obj = new JSONObject();
//			JSONObject joDocuments = new JSONObject();
//			int i=0;
//        	for (Model m : listModels) {
//				JSONObject resultJSON = new JSONObject();
//				String uri = NIFReader.extractDocumentURI(m);
//				String nifContent = NIFReader.model2String(m, "TTL");
//				resultJSON.put("docId", i+1);
//				resultJSON.put("docURI", uri);
//				resultJSON.put("content", nifContent);
//				joDocuments.put("result"+(i+1),resultJSON);
//				i++;
//			}
//			obj.put("documents", joDocuments);
//        	return obj;
//		} catch (BadRequestException e) {
//			logger.error(e.getMessage());
//            throw e;
//		}
//	}
//
//
//	public List<TimelinedElement> generateTimelinedElementsFromJSON(JSONObject jsonObject) throws Exception {
//		try {
//			List<TimelinedElement> outputList = new LinkedList<TimelinedElement>();
//        	JSONArray jsonDocumentsArray = jsonObject.getJSONArray("results");
//        	for (int i = 0; i < jsonDocumentsArray.length(); i++) {
//				JSONObject obj = jsonDocumentsArray.getJSONObject(i);
//				ElementType type = ElementType.valueOf(obj.getString("type"));
//				String uri = obj.getString("uri");
//				TimeExpressionRange temporalExpression = new TimeExpressionRange(obj.getString("temporalexpression"));
//				String nifText = obj.getString("content");
//				Model m = NIFReader.extractModelFromFormatString(nifText, RDFSerialization.TURTLE);
//				TimelinedElement tle = new TimelinedElement(type, uri, temporalExpression, m);
//				outputList.add(tle);
//			}
//			return outputList;
//		} catch (BadRequestException e) {
//			logger.error(e.getMessage());
//            throw e;
//		}
//	}

	public List<TimelinedElement> generateTimelinedElementsFromJENACollection(Model collectionModel,String granularity) throws Exception {
		try {
			List<TimelinedElement> outputList = new LinkedList<TimelinedElement>();
			List<Model> documentModels = NIFManagement.extractDocumentsModels(collectionModel);
			
			for (Model m : documentModels) {
				String uri = NIFReader.extractDocumentWholeURI(m);
				TimeExpressionRange temporalExpression = new TimeExpressionRange(NIFReader.extractMeanDateRange(m));
				TimelinedElement tle = new TimelinedElement(ElementType.DOCUMENT, uri, temporalExpression, m);
				outputList.add(tle);
				if(granularity.equalsIgnoreCase("entity")){
					
					Map<String, Map<String,String>> entitiesMap = NIFReader.extractEntitiesExtended(m);
					
					Set<String> keyset = entitiesMap.keySet();
					for (String ks : keyset) {
						Map<String,String> entityMap = entitiesMap.get(ks);
						
						String uriEntity = ks;
						TimeExpressionRange ter = new TimeExpressionRange();
						boolean teExist=false;
//							System.out.println(s);
						Set<String> ss2 = entityMap.keySet();
						for (String s2 : ss2) {
							if(s2.equalsIgnoreCase(NIF.normalizedDate.getURI())){
								ter = new TimeExpressionRange(entityMap.get(s2));
								teExist=true;
							}
							else if(s2.equalsIgnoreCase(DBO.birthDate.getURI()) || s2.equalsIgnoreCase(DBO.deathDate.getURI())){
							String parts[] = entityMap.get(s2).split("-");
								ter = new TimeExpressionRange(parts[0]+""+parts[1]+""+parts[2]+"000000_"+parts[0]+""+parts[1]+""+parts[2]+"235959");
								teExist=true;
							}
							else if(entityMap.get(s2).contains("http://dkt.dfki.de/ontologies/nif#date=")){
								String aux = entityMap.get(s2);
								ter = new TimeExpressionRange(aux.substring(aux.lastIndexOf('=')+1));
								teExist=true;
							}
						}
						if(teExist){
							TimelinedElement tle2 = new TimelinedElement(ElementType.CONCEPT, uriEntity, ter, m);
							outputList.add(tle2);
						}
					}
				}
			}
			return outputList;
		} catch (BadRequestException e) {
			logger.error(e.getMessage());
            throw e;
		}
	}

	public JSONObject generateJSONFromTimelinedElements(List<TimelinedElement> listTimelinedElements) throws Exception {
		try{
        	JSONObject obj = new JSONObject();
			JSONObject joDocuments = new JSONObject();
			int i=0;
        	for (TimelinedElement tle : listTimelinedElements) {
				JSONObject resultJSON = new JSONObject();
				Model m = tle.model;
				String uri = NIFReader.extractDocumentURI(m);
				String nifContent = NIFReader.model2String(m, RDFSerialization.TURTLE);
				resultJSON.put("resultId", i+1);
				resultJSON.put("type", tle.type.name());
				resultJSON.put("uri", uri);
				resultJSON.put("temporalexpression", tle.temporalExpression.toString());
				resultJSON.put("content", nifContent);
				joDocuments.put("result"+(i+1),resultJSON);
				i++;
			}
			obj.put("documents", joDocuments);
        	return obj;
		} catch (BadRequestException e) {
			logger.error(e.getMessage());
            throw e;
		}
	}
//	
//	public boolean addDateToTest(ETimeliningService t,String date) throws Exception {
//		String inputText = "@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ."+
//				"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> ."+
//				"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> ."+
//				"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> ."+
//				"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> ."+
//				""+
//				"<http://dkt.dfki.de/examples/doc1#char=0,813>"+
//				"        a                  nif:RFC5147String , nif:String , nif:Context ;"+
//				"        nif:beginIndex     \"0\"^^xsd:nonNegativeInteger ;"+
//				"        nif:endIndex       \"813\"^^xsd:nonNegativeInteger ;"+
//				"        nif:isString       \"\"\"1936\n\nCoup leader Sanjurjo was killed in a plane crash on 20 July, leaving an effective command split between Mola in the North and Franco in the South. On 21 July, the fifth day of the rebellion, the Nationalists captured the main Spanish naval base at Ferrol in northwestern Spain. A rebel force under Colonel Beorlegui Canet, sent by General Emilio Mola, undertook the Campaign of Guipúzcoa from July to September. The capture of Guipúzcoa isolated the Republican provinces in the north. On 5 September, after heavy fighting the force took Irún, closing the French border to the Republicans. On 13 September, the Basques surrendered San Sebastián to the Nationalists, who then advanced toward their capital, Bilbao. The Republican militias on the border of Viscaya halted these forces at the end of September.\n\"\"\"^^xsd:string ;"+
//				"        nif:meanDateRange  \""+date+"\"^^xsd:string .";
//		return t.addDocumentToTimelining(inputText, "text/turtle", "timeliningName1", "", true, false);
//	}
//	public static void main(String[] args) throws Exception {
//		ETimeliningService t = new ETimeliningService();
////		if(t.addDateToTest(t, "20000101000000_20000131000000")){
////			System.out.println("document properly added 1");
////		}
////		if(t.addDateToTest(t, "19991231000000_20000505000000")){
////			System.out.println("document properly added 2");
////		}
////		if(t.addDateToTest(t, "20020202000000_20020303000000")){
////			System.out.println("document properly added 3");
////		}
////		if(t.addDateToTest(t, "19980101000000_20050101000000")){
////			System.out.println("document properly added 4");
////		}
////		if(t.addDateToTest(t, "20000110000000_20020220000000")){
////			System.out.println("document properly added 5");
////		}
//		
////		JSONObject obj = t.processQuery("all", "", "timeliningName1", "", false);
////		System.out.println(obj.toString(2));
//		JSONObject obj = t.processQuery("20000101000000_20000101000001<-->20050220000000_20050220000001", "", "timeliningName1", "", false);
//		System.out.println(obj.toString(2));
//	}
}
