package org.getalp.lexsema.nif;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Testlet")
public class Niflet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
   
    public Niflet() 
    {
        
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		response.getOutputStream().println("COUCOU");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		
	}
}
