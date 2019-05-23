package com.thomson.sea.athena;

import java.io.File;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
public class DBConnection {

	static Connection con=null;
		private DBConnection()
		{
			
		}
		
		public static Connection getDBConnection() throws RemoteException, SQLException {
			
			try
			{
				
				String name = "console.properties";
				System.out.println("reading console file");
				//String domainPath = System.getProperty("com.sun.aas.instanceRoot"); sai
				String domainPath = System.getenv("DOMAIN_HOME");
				System.out.println("domain path "+domainPath);
				File configDir = new File(domainPath, "config");

				

				File file = new File(configDir, name);
				
		    	String absolutePath = file.getAbsolutePath();
		    	/*System.out.println("absolutePath"+absolutePath);
		    	String datasource=prop.getProperty("datasource");
		    	String url=prop.getProperty("url");
		    	String dbname=prop.getProperty("dbname");
		    	String username=prop.getProperty("username");
		    	String password=prop.getProperty("password");
		    	String value=""+datasource+":"+url+"/"+dbname+","+""+username+""+","+""+password+"";
		    	System.out.println(value);*/
			/*Class.forName("com.sybase.jdbc2.jdbc.SybDriver");
			con = DriverManager.getConnection("jdbc:sybase:Tds:10.53.16.4:5000/entuat","backproc","skylght");
			System.out.println("Connection is........................."+con);*/
				
				/////
				
				FileInputStream fileInput = new FileInputStream(file);
				Properties properties = new Properties();
				properties.load(fileInput);
				fileInput.close();
				String username=null,password=null,url=null,dbname=null;
				Enumeration enuKeys = properties.keys();
				while (enuKeys.hasMoreElements()) {
					String key = (String) enuKeys.nextElement();
					String value = properties.getProperty(key);
					System.out.println(key + ": " + value);
					if(key.equalsIgnoreCase("username")){
						username=value;
					}
					if(key.equalsIgnoreCase("password")){
						password=value;
					}
					if(key.equalsIgnoreCase("url")){
						url=value;
					}
					if(key.equalsIgnoreCase("dbname")){
						dbname=value;
					}
				}
				Class.forName("com.sybase.jdbc2.jdbc.SybDriver");
				con = DriverManager.getConnection("jdbc:sybase:Tds:"+url+"/"+dbname+"", username, password);
				System.out.println("Connection is........................."+con);
			
			
			
			}
			
			
			
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return con;

	}
		
}
