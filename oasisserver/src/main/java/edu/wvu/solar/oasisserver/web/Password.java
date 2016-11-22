package edu.wvu.solar.oasisserver.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Password  {
	 	private String username;
	    private String password;
	    private Scanner in;
	    private boolean login;
	    
	    public Password(String username, String password) {
	        this.username = username;
	        this.password = password;
	    	
	        String[] user = accessFile();	        
	        if(user[0].compareTo(username)==0 && user[1].compareTo(password)==0){
	        	login = true;
	        }
	        else{
	        	login = false;
	        }
	    }

	    public String getUsername() {
	        return username;
	    }
	    public Boolean getLogin() {
	        return login;
	    }

	    public String[] accessFile(){
	    	try{
	    	in = new Scanner(new File("/home/chrx/git/OasisServer/oasisserver/src/main/java/edu/wvu/solar/oasisserver/web/password.txt"));
	    	}
	    	catch(FileNotFoundException e){
	    		System.out.println(e.getMessage());
	    	}
	    	String[] user = new String[2];
	    	user[0]= in.nextLine();
	    	user[1]= in.nextLine();
	    	return user;
	    }
}
