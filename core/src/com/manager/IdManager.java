package com.manager;

public class IdManager {

		public static int objectId = 100000;
		
		public static synchronized int getNextId(){
			return objectId++;
		}
}
