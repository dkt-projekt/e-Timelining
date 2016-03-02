package de.dkt.eservices.timelining.modules;

import java.util.List;

public class TreeNodeTL<K extends Comparable,E> {

	TreeNodeTL<K,E> left,right;
	
	K key;
	E element;
	
	public TreeNodeTL() {
		super();
	}

	public TreeNodeTL(TreeNodeTL<K, E> left, TreeNodeTL<K, E> right, K key, E element) {
		super();
		this.left = left;
		this.right = right;
		this.key = key;
		this.element = element;
	}

	public boolean addElement(K k, E e){
		
		if(k.equals(key)){
			return false;
		}
		else if(k.compareTo(key)<0){
			if(left!=null){
				return left.addElement(k, e);
			}
			else{
				left = new TreeNodeTL<K,E>(null,null,k,e);
			}
		}
		else{
			if(right!=null){
				return right.addElement(k, e);
			}
			else{
				right = new TreeNodeTL<K,E>(null,null,k,e);
			}
		}
		return true;
	}
	
	public void getInorder(List<E> list){
		if(left!=null){
			left.getInorder(list);
		}
		list.add(element);
		if(right!=null){
			right.getInorder(list);
		}
	}

	public void getRange(List<E> list, K start, K end){
		if(left!=null){
			if(key.compareTo(start)>0){
				left.getRange(list,start,end);
			}
		}
		if(key.compareTo(start)>0 && key.compareTo(end)<0){
			list.add(element);
		}
		if(right!=null){
			if(key.compareTo(end)<0){
				right.getRange(list,start,end);
			}
		}
	}

}
