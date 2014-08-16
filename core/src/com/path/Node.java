package com.path;

public class Node {
	public int x;
	public int y;
	float g,h,f;  //g = from start; h = to end, f = both together
	public boolean pass;
	Node parent;
	
	public Node(int x,int y,boolean pass){
		this.x = x;
		this.y = y; 
		this.pass = pass;
	}
	
	public Node updateGHFP(float g, float h, Node parent){
		this.parent = parent;
		this.g = g;
		this.h = h;
		f = g+h;
		return this;
	}
	
	public boolean setPass(boolean pass){
		this.pass = pass;
		return pass;
	}
}
