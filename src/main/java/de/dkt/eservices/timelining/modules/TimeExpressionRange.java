package de.dkt.eservices.timelining.modules;

public class TimeExpressionRange implements Comparable{

	TimeExpression initialTime;
	TimeExpression finalTime;
	
	public TimeExpressionRange() {
	}
	
	public TimeExpressionRange(String s) {
		if(s.equalsIgnoreCase("MIN")){
			initialTime = new TimeExpression("00000101000000");
			finalTime = new TimeExpression("00000101000001");
		}
		else if(s.equalsIgnoreCase("MAX")){
			initialTime = new TimeExpression("99991231235958");
			finalTime = new TimeExpression("99991231235959");
		}
		else{
			System.out.println(s);
			String parts[] = s.split("_");
			initialTime = new TimeExpression(parts[0]);
			finalTime = new TimeExpression(parts[1]);
		}
	}
	
	@Override
	public int compareTo(Object o) {
		if(o instanceof TimeExpressionRange){
			TimeExpressionRange ter = (TimeExpressionRange) o;
			if(initialTime.compareTo(ter.initialTime)==0){
				return finalTime.compareTo(ter.finalTime);
			}
//			else if(initialTime.compareTo(ter.initialTime)<0){
//				return initialTime.compareTo(ter.initialTime);
//			}
			else{
				return initialTime.compareTo(ter.initialTime);
			}
		}
		return Integer.MIN_VALUE;
	}
	
	@Override
	public String toString() {
		return initialTime.toString()+"_"+finalTime.toString();
	}

}
