/**
 * 
 */
package com.thomsonreuters.athena.sea.console;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.sun.web.security.WebPrincipal;
import com.thomsonreuters.athena.services.auth.ICoordinator;
import com.thomsonreuters.athena.services.auth.impl.CoordinatorPrincipalImpl;

/**
 * @author Aleksandar Pecanov
 *
 */

public class HttpFilter implements Filter {
	
	private static ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpFilter.request.set( request );
		chain.doFilter(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	/**
	 * 
	 * @return
	 */
	static HttpServletRequest getRequest() {
		return request.get();
	}
	
	/**
	 * 
	 * @return
	 */
	public static ICoordinator getCoordinator() {
		CoordinatorPrincipalImpl coordinator = (CoordinatorPrincipalImpl) getRequest().getSession().getAttribute("user");
		System.out.println("authentication page>>>>>>"+coordinator);
		if( coordinator!=null )
			return coordinator;
		
		/*Principal principal = getRequest().getUserPrincipal();
		if( principal instanceof WebPrincipal ) {
			WebPrincipal webPrincipal = (WebPrincipal) principal;
			coordinator = new CoordinatorPrincipalImpl();
			coordinator.setLogin("vikash@1");//webconsole
			coordinator.setPassword("Password@1");//webconsole
		}*/
		
		//getRequest().getSession().setAttribute("user", coordinator);
		return coordinator;
	}
	
}
