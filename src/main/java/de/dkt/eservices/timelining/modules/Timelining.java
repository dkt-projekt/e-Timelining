package de.dkt.eservices.timelining.modules;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.DBO;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.timelining.modules.TimelinedElement.ElementType;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;

public class Timelining {

	static Logger logger = Logger.getLogger(Timelining.class);

	public static TimeExpressionRange[] parseTemporalQuery(String t) throws BadRequestException {
		TimeExpressionRange[] queries = new TimeExpressionRange[2];
		String parts[] = t.split("<-->");
		if(parts[0]!=null){
			queries[0] = new TimeExpressionRange(parts[0]);
		}
		else{
			queries[0] = new TimeExpressionRange("MIN");
		}
		if(parts[1]!=null){
			queries[1] = new TimeExpressionRange(parts[1]);
		}
		else{
			queries[1] = new TimeExpressionRange("MAX");
		}
		return queries;
	}
	
	public static List<TimelinedElement> queryTLETimeline(List<String> listTexts,String query,boolean addElements) throws Exception {
		try{
			List<TimelinedElement> inputTimelines = new LinkedList<TimelinedElement>();
			for (String t : listTexts) {
				TimelinedElement tle = new TimelinedElement(t);
				inputTimelines.add(tle);
			}
			List<TimelinedElement> outputTimelinedElements = queryTimelineFromModels(inputTimelines,query,addElements);
			return outputTimelinedElements;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public static List<String> queryTimeline(List<String> listTexts,String query,boolean addElements) throws ExternalServiceFailedException {
		try{
			List<TimelinedElement> inputTimelines = new LinkedList<TimelinedElement>();
			for (String t : listTexts) {
				TimelinedElement tle = new TimelinedElement(t);
				inputTimelines.add(tle);
			}
			List<TimelinedElement> outputTimelinedElements = queryTimelineFromModels(inputTimelines,query,addElements);
			List<String> outputStrings = new LinkedList<String>();
			for (TimelinedElement te : outputTimelinedElements) {
				outputStrings.add(te.toString());
			}
			return outputStrings;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}
	
	public static List<TimelinedElement> queryTimelineFromModels(List<TimelinedElement> listNifModels,String query,boolean addElements) throws Exception {
		try{
			TreeTL<TimeExpressionRange, TimelinedElement> tree = generateTimelineTreeFromModels(listNifModels,addElements);
			TimeExpressionRange[] queries = parseTemporalQuery(query);
			List<TimelinedElement> retrievedModels = tree.getRange(queries[0],queries[1]);
	        return retrievedModels;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public static List<String> generateTimeline(List<String> listTexts, boolean addElements) throws ExternalServiceFailedException {
		try{
			List<TimelinedElement> inputModels = new LinkedList<TimelinedElement>();
			for (String t : listTexts) {
				TimelinedElement tle = new TimelinedElement(t);
				inputModels.add(tle);
			}
			List<TimelinedElement> outputTimelinedElements = generateTimelineFromModels(inputModels,addElements);
			List<String> outputStrings = new LinkedList<String>();
			for (TimelinedElement te : outputTimelinedElements) {
				outputStrings.add(te.toString());
			}
			return outputStrings;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	public static List<TimelinedElement> generateTLETimeline(List<String> listTexts, boolean addElements) throws ExternalServiceFailedException {
		try{
			List<TimelinedElement> inputModels = new LinkedList<TimelinedElement>();
			for (String t : listTexts) {
				TimelinedElement tle = new TimelinedElement(t);
				inputModels.add(tle);
			}
			List<TimelinedElement> outputTimelinedElements = generateTimelineFromModels(inputModels,addElements);
			return outputTimelinedElements;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	public static List<TimelinedElement> generateTimelineFromModels(List<TimelinedElement> listNifModels, boolean addElements) throws ExternalServiceFailedException {
		try{
			TreeTL<TimeExpressionRange, TimelinedElement> tree = generateTimelineTreeFromModels(listNifModels, addElements);
			List<TimelinedElement> orderedModels = tree.getInorder();
	        return orderedModels;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public static TreeTL<TimeExpressionRange, TimelinedElement> generateTimelineTreeFromModels(List<TimelinedElement> listNifModels, boolean addElements) throws ExternalServiceFailedException {
		try{
			TreeTL<TimeExpressionRange, TimelinedElement> tree = new TreeTL<TimeExpressionRange, TimelinedElement>();
			for (TimelinedElement tle: listNifModels) {
				Model model = tle.model;
				String meanDate = NIFReader.extractMeanDateRange(model);
				if(meanDate!=null){
					TimeExpressionRange ter = new TimeExpressionRange(meanDate);
					TimelinedElement te = new TimelinedElement(ElementType.DOCUMENT, NIFReader.extractDocumentURI(model), ter, model);
					tree.addElement(ter, te);
				}
				if(addElements){
					List<TimelinedElement> times = generateTimelinedConceptsFromModel(model);
					for (TimelinedElement t : times) {
						tree.addElement(t.temporalExpression, t);
					}
				}
			}
	        return tree;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}

	public static TimelinedElement generateTimelinedDocumentFromModel(Model model, boolean addElements) throws ExternalServiceFailedException {
		try{
			String meanDate = NIFReader.extractMeanDateRange(model);
			if(meanDate!=null){
				TimeExpressionRange ter = new TimeExpressionRange(meanDate);
				TimelinedElement te = new TimelinedElement(ElementType.DOCUMENT, NIFReader.extractDocumentURI(model), ter, model);
				return te;
			}
			throw new ExternalServiceFailedException("No mean date found in the documents.!!!");
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}

	public static List<TimelinedElement> generateTimelinedConceptsFromModel(Model model) throws ExternalServiceFailedException {
		try{
			List<TimelinedElement> listConcepts = new LinkedList<TimelinedElement>();

			Map<String,Map<String,String>> map = NIFReader.extractEntitiesExtended(model);
			
			Set<String> ss = map.keySet();
			for (String s : ss) {
				String uri = s;
				TimeExpressionRange ter = new TimeExpressionRange();
				boolean teExist=false;
//				System.out.println(s);
				Set<String> ss2 = map.get(s).keySet();
				for (String s2 : ss2) {
					if(s2.equalsIgnoreCase(NIF.normalizedDate.getURI())){
						ter = new TimeExpressionRange(map.get(s).get(s2));
						teExist=true;
					}
					else if(s2.equalsIgnoreCase(DBO.birthDate.getURI()) || s2.equalsIgnoreCase(DBO.deathDate.getURI())){
						String parts[] = map.get(s).get(s2).split("-");
						ter = new TimeExpressionRange(parts[0]+""+parts[1]+""+parts[2]+"000000_"+parts[0]+""+parts[1]+""+parts[2]+"235959");
						teExist=true;
					}
					else if(map.get(s).get(s2).contains("http://dkt.dfki.de/ontologies/nif#date=")){
						String aux = map.get(s).get(s2);
						ter = new TimeExpressionRange(aux.substring(aux.lastIndexOf('=')+1));
						teExist=true;
					}
				}
				if(teExist){
					TimelinedElement te = new TimelinedElement(ElementType.CONCEPT, uri, ter, model);
					listConcepts.add(te);
				}
			}
			return listConcepts;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public static void main(String[] args) throws Exception {
		String inputText = "@prefix geo:   <http://www.w3.org/2003/01/geo/wgs84_pos/> .\n" +
						"@prefix dbo:   <http://dbpedia.org/ontology/> .\n" +
						"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
						"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
						"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
						"@prefix dfkinif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
						"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
						"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=650,662>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"Nationalists\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"650\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"662\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            dfkinif:organization ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Kuomintang> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=18,26>\n" +
						"        a                     nif:String , nif:RFC5147String ;\n" +
						"        dbo:birthDate         \"1872-03-28\"^^xsd:date ;\n" +
						"        dbo:deathDate         \"1936-07-20\"^^xsd:date ;\n" +
						"        nif:anchorOf          \"Sanjurjo\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"18\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"26\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            dfkinif:person ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/José_Sanjurjo> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=277,282>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"Spain\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"277\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"282\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            dfkinif:location ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
						"        geo:lat               \"40.43333333333333\"^^xsd:double ;\n" +
						"        geo:long              \"-3.7\"^^xsd:double ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Spain> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=254,260>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"Ferrol\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"254\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"260\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            dfkinif:location ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
						"        geo:lat               \"43.46666666666667\"^^xsd:double ;\n" +
						"        geo:long              \"-8.25\"^^xsd:double ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Ferrol,_Galicia> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=704,710>\n" +
						"        a                     nif:String , nif:RFC5147String ;\n" +
						"        nif:anchorOf          \"Bilbao\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"704\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"710\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            dfkinif:location ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
						"        geo:lat               \"43.25694444444444\"^^xsd:double ;\n" +
						"        geo:long              \"-2.923611111111111\"^^xsd:double ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Bilbao> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=201,213>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"Nationalists\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"201\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"213\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            dfkinif:organization ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Kuomintang> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=0,805>\n" +
						"        a                         nif:String , nif:Context , nif:RFC5147String ;\n" +
						"        dfkinif:averageLatitude   \"42.17561833333333\"^^xsd:double ;\n" +
						"        dfkinif:averageLongitude  \"-4.075817777777778\"^^xsd:double ;\n" +
						"        dfkinif:standardDeviationLatitude\n" +
						"                \"1.4446184970002673\"^^xsd:double ;\n" +
						"        dfkinif:standardDeviationLongitude\n" +
						"                \"2.202362615152696\"^^xsd:double ;\n" +
						"        nif:beginIndex            \"0\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex              \"805\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:isString              \"1936\\n\\nCoup leader Sanjurjo was killed in a plane crash on 20 July, leaving an effective command split between Mola in the North and Franco in the South. On 21 July, the fifth day of the rebellion, the Nationalists captured the main Spanish naval base at Ferrol in northwestern Spain. A rebel force under Colonel Beorlegui Canet, sent by General Emilio Mola, undertook the Campaign of Guipuzcoa from July to September. The capture of Guipuzcoa isolated the Republican provinces in the north. On 5 September, after heavy fighting the force took Irun, closing the French border to the Republicans. On 13 September, the Basques surrendered Madrid to the Nationalists, who then advanced toward their capital, Bilbao. The Republican militias on the border of Viscaya halted these forces at the end of September.\"^^xsd:string .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=543,547>\n" +
						"        a                     nif:String , nif:RFC5147String ;\n" +
						"        nif:anchorOf          \"Irun\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"543\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"547\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            dfkinif:location ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
						"        geo:lat               \"43.33781388888889\"^^xsd:double ;\n" +
						"        geo:long              \"-1.788811111111111\"^^xsd:double ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Irun> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=372,393>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"Campaign of Guipuzcoa\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"372\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"393\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            dfkinif:organization ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Political_campaign> , <http://dbpedia.org/resource/Gipuzkoa> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=146,151>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"South\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"146\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"151\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            dfkinif:location ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Southern_United_States> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=636,642>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"Madrid\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"636\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"642\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            dfkinif:location ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
						"        geo:lat               \"40.38333333333333\"^^xsd:double ;\n" +
						"        geo:long              \"-3.716666666666667\"^^xsd:double ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Madrid> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=21,25>\n" +
						"        a                  nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf       \"2016\"^^xsd:string ;\n" +
						"        nif:beginIndex     \"21\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex       \"25\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n" +
						"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=20160101000000_20170101000000> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=345,356>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        dbo:birthDate         \"1887-06-09\"^^xsd:date ;\n" +
						"        dbo:deathDate         \"1937-06-03\"^^xsd:date ;\n" +
						"        nif:anchorOf          \"Emilio Mola\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"345\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"356\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            dfkinif:person ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Emilio_Mola> .\n" +
						"";
		Model m = NIFReader.extractModelFromFormatString(inputText, RDFSerialization.TURTLE);
		Timelining.generateTimelinedConceptsFromModel(m);
	}
}
