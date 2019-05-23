package com.thomsonreuters.athena.sea.console.upload;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hpsf.HPSFException;

import com.thomsonreuters.athena.sea.console.upload.entity.AbstractXlsEntity;
import com.thomsonreuters.athena.sea.console.upload.xls.util.SEAConsoleException;
import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadAction;
import com.thomsonreuters.athena.sea.console.upload.xls.util.UploadResult;

/**
 * @author Miro Zorboski
 * 
 */
public class XlsFileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = -8044135270862058549L;

	/**
	 * Instantiates a new xls file upload servlet.
	 */
	public XlsFileUploadServlet() {
        super();
    }

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		
		String errorMessage = null;
		UploadAction uploadAction = null;
		boolean fatalError = false;
	
		try {
			if(ServletFileUpload.isMultipartContent(request)) {			
				//Get file content
				byte[] xlsFileContent = null;
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> files = (List<FileItem>) upload.parseRequest(request);
				for(FileItem fileItem : files) {
					if(fileItem.getFieldName().equals("xlsFile")) {
						String fileName = fileItem.getName();
						request.setAttribute("fileName", fileName);
						xlsFileContent = fileItem.get();
					} else if (fileItem.getFieldName().equals("uploadAction")) {
						String uploadActionParam = fileItem.getString();
						if(uploadActionParam == null) {
							//Error: undefined upload action
							fatalError = true;
							errorMessage = "Required parameter missing.";
						}
						uploadAction = UploadAction.fromString(uploadActionParam);
					}
				}
				
				AbstractUploadBean uploadBean = UploadFactory.getUploadBean(uploadAction);
				UploadResult<AbstractXlsEntity> results = uploadBean.upload(xlsFileContent);
				request.setAttribute("results", results);
				
				if(results != null && results.getResults().size() > 0 && results.isValidationErrorsFound()) {
					errorMessage = "Validation failed.<br/>Change and resubmit the xls file.";
				} else if(results != null && results.getResults().size() == 0) {
					errorMessage = "Xls file is empty.";
				}
			} else {
				//Error: File not found in request
				fatalError = true;
				errorMessage="No file or empty file";
			}
		} catch (SEAConsoleException swce) {
			errorMessage = swce.getMessage();
			swce.printStackTrace();
		} catch (HPSFException hpsfe) {
			errorMessage = hpsfe.getMessage();
			hpsfe.printStackTrace();
		} catch (FileUploadException fue) {
			errorMessage = "Error uploading file";
			fue.printStackTrace();
		} catch (Exception e) {
			errorMessage = "Unknown error occured";
			e.printStackTrace();
		} finally {
			if(fatalError) {
				forward("index.jsp?view=error&error=" + errorMessage, request, response);
			} else if(errorMessage != null) {
				forward("index.jsp?view=" + uploadAction.name().toLowerCase() + "UploadView&parsingError=" + errorMessage, request, response);
			} else {
				request.setAttribute("success", true);
				forward("index.jsp?view=" + uploadAction.name().toLowerCase() + "UploadView", request, response);
			}
		}
	}
	
	/**
	 * Forward.
	 * 
	 * @param url the url
	 * @param request the request
	 * @param response the response
	 * 
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void forward(String url, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher requestDispatcher = request.getRequestDispatcher(url);
		requestDispatcher.forward(request, response);
	}

}
