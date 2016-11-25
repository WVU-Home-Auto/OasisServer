package edu.wvu.solar.oasisserver.web;

import java.util.concurrent.ConcurrentMap;

import org.json.JSONObject;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.mapdb.DB.HashMapMaker;

public class Account {
	private String username;
	private String email;
	private String password;
	private Boolean success,login;
	private DB database;
	private ConcurrentMap<String, String> map;
	private final String MAP_NAME = "accounts";
	private final String dbPath = "/home/chrx/git/OasisServer/oasisserver/src/main/java/edu/wvu/solar/oasisserver/web/password.db";
	
	
	public Account(String username, String email, String password){
		this.email=email;
		this.username=username;
		this.password=password;
		success = false;
		
		database = DBMaker.fileDB(dbPath).closeOnJvmShutdown().transactionEnable().fileMmapEnableIfSupported().make();
    	HashMapMaker<String, String> mapMaker = database.hashMap(MAP_NAME, Serializer.STRING,Serializer.STRING);
		map = mapMaker.createOrOpen();
		if(map.get(username)!=null){
			success = false;	
		}
		else{
			success = true;
			JSONObject obj = new JSONObject();
			obj.put("email", email);
			obj.put("password", password);
			map.put(username, obj.toString());
		    database.commit();
		}
		database.close();
	}
	
	public Account(String username, String password) {
        this.username = username;
        this.password = password;
    	
        database = DBMaker.fileDB(dbPath).closeOnJvmShutdown().transactionEnable().fileMmapEnableIfSupported().make();
    	HashMapMaker<String, String> mapMaker = database.hashMap(MAP_NAME, Serializer.STRING,Serializer.STRING);
		if(database.exists(MAP_NAME)){
			map = mapMaker.open();
			if(map.containsKey(username)){
				JSONObject obj = new JSONObject(map.get(username));
				if(obj.getString("password").compareTo(password)==0){
					login = true;
				}
			}
			
		}
		else{
			login = false;
		}
		database.close();	        
    }
	
	public String getEmail(){
		return email;
	}
	public String getUsername(){
		return username;
	}
	public Boolean getSuccess(){
		return success;	
	}
	public Boolean getLogin(){
		return login;
	}
}
