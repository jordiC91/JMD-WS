package org.jmd;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Classe permettant de spécifier aux services qu'ils sont appelables sur 
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

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ((HttpServletResponse) response).addHeader(
            "Access-Control-Allow-Origin", "*"
        );
        ((HttpServletResponse) response).addHeader(
             "Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT"
        );
        ((HttpServletResponse) response).addHeader(
             "Access-Control-Max-Age", "1000"
        );
        ((HttpServletResponse) response).addHeader(
             "Access-Control-Allow-Headers", "x-requested-with, Content-Type, origin, authorization, accept, client-security-token"
        );
        
        chain.doFilter(request, response);
    }
}
