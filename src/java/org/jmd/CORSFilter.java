package org.jmd;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Classe permettant de spécifiers aux services qu'ils sont appelables sur 
 * tous les domaines.
 * 
 * @author jordi charpentier - yoann vanhoeserlande
 */
public class CORSFilter implements Filter {

    public CORSFilter() {

    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException { }

    @Override
    public void destroy() { }

    /**
     * Classe permettant de spécifiers aux services qu'ils sont appelables sur 
     * tous les domaines.
     * 
     * @param request
     * @param response
     * @param chain
     * 
     * @throws IOException
     * @throws ServletException 
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ((HttpServletResponse) response).addHeader(
            "Access-Control-Allow-Origin", "*"
        );
        
        chain.doFilter(request, response);
    }
}
