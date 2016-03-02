package de.dkt.eservices.timelining.modules;

import java.util.LinkedList;
import java.util.List;

public class TreeTL<K extends Comparable,E> {

	TreeNodeTL<K, E> root;

	public TreeTL() {
		super();
	}
	
	public TreeTL(TreeNodeTL<K, E> root) {
		super();
		this.root = root;
	}
	
	public void addElement(K k,E e){
		if(root==null){
			root = new TreeNodeTL<K,E>(null, null, k, e);
		}
		else{
			root.addElement(k, e);
		}
	}
	
	public List<E> getInorder(){
		List<E> list = new LinkedList<E>();
		root.getInorder(list);
		return list;
	}
	
	public List<E> getRange(K start,K end){
		List<E> list = new LinkedList<E>();
		root.getRange(list, start, end);
		return list;
	}
	
}
