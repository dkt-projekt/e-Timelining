package de.dkt.eservices.timelining.modules;

import java.io.Serializable;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFReader;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;

public class TimelinedElement implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public enum ElementType {DOCUMENT,CONCEPT,TEMPORALEXPRESSION};
	
	public ElementType type;
	public String uri;
	public TimeExpressionRange temporalExpression;
	public Model model;
	
	public TimelinedElement(ElementType type, String uri, Model model) {
		super();
		this.type = type;
		this.uri = uri;
		this.model = model;
	}
	
	public TimelinedElement(ElementType type, String uri, TimeExpressionRange temporalExpression, Model model) {
		super();
		this.type = type;
		this.uri = uri;
		this.temporalExpression = temporalExpression;
		this.model = model;
	}

	public TimelinedElement(String s) throws Exception {
		super();
		String parts[] = s.split("<-->");
		this.type = ElementType.valueOf(parts[0]);
		this.uri = parts[1];
		this.temporalExpression = new TimeExpressionRange(parts[2]);
		this.model = NIFReader.extractModelFromFormatString(parts[3], RDFSerialization.TURTLE);
	}

	@Override
	public String toString() {
		return type.name()+"<-->"+uri+"<-->"+temporalExpression.toString()+"<-->"+NIFReader.model2String(model, RDFSerialization.TURTLE);
	}
}
