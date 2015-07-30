package org.getalp.lexsema.ws.core;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract Servlet class which is useful by two aspects : 
 * - We process GET and POST requests the same way
 * - The processing method "handle()" can throw exceptions 
 */
public abstract class WebServiceServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    {
        handleWithoutException(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        handleWithoutException(request, response);
    }

    private void handleWithoutException(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            handle(request, response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    protected abstract void handle(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
