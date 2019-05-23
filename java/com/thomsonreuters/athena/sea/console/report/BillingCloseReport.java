package com.thomsonreuters.athena.sea.console.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BillingCloseReport extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private static final String REPORTPATH = "/offlineReports/BillingCloseReport/";
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		PrintWriter pw = response.getWriter();

		// Get the absolute file path.
		String fileName = request.getParameter("filename");
		//String filePath = System.getProperty("com.sun.aas.instanceRoot") + REPORTPATH + fileName;
		String filePath = System.getenv("DOMAIN_HOME")+REPORTPATH+fileName;
		
		// Download the file
		File f = new File(filePath);
		if (f.exists()) {
			response.setContentType("application/ms-excel");
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String data;
			while ((data = br.readLine()) != null) {
				pw.println(data);
			}
		} else {
			response.setContentType("text/html");
			String outp = "<html><head><title>Getting Report</title></head><body>File Not Found</body></html>";
			pw.println(outp);
		}
		pw.flush();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}

}
