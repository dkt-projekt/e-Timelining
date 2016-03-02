package de.dkt.eservices.timelining.modules;

public class TimeExpression {

	String text;
	
	public TimeExpression() {
		super();
	}

	public TimeExpression(String text) {
		super();
		if(text.equalsIgnoreCase("MIN")){
			text = "00000101000000";
		}
		else if(text.equalsIgnoreCase("MAX")){
			text = "99991231235959";
		}
		else{
			this.text = text;
		}
	}

	public int compareTo(Object o) {
		if(o instanceof TimeExpression){
			TimeExpression te = (TimeExpression) o;
//			if(text.compareTo(te.text)==0){
//				return 0;
//			}
//			else if(text.compareTo(te.text)>0){
//				return text.compareTo(te.text);
//			}
//			else{
//				return text.compareTo(te.text);
//			}
			return text.compareTo(te.text);
		}
		return Integer.MIN_VALUE;
	}
	
	@Override
	public String toString() {
		return text;
	}
	
}
