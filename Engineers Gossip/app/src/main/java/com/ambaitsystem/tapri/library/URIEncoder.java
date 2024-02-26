package com.ambaitsystem.tapri.library;

public class URIEncoder {

 
  public static String encodeURI(String argString) 
  {
	  
	        String url = null;
	        try{
	        url = new String(argString.trim().replace(" ", "%20")
	            .replace(",", "%2c").replace("(", "%28").replace(")", "%29")
	            .replace("!", "%21").replace("<", "%3C")
	            .replace(">", "%3E").replace("#", "%23").replace("$", "%24")
	            .replace("'", "%27").replace("*", "%2A")
	           
	            .replace(";", "%3B").replace("@", "%40")
	            .replace("[", "%5B").replace("]", "%5D")
	          .replace("`", "%60").replace("{", "%7B")
	            .replace("|", "%7C").replace("}", "%7D").replace("\"", ""));
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	        return url;
	    } 

}
