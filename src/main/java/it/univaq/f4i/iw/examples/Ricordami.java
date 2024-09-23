package it.univaq.f4i.iw.examples;

import it.univaq.f4i.iw.framework.result.HTMLResult;
import it.univaq.f4i.iw.framework.utils.ServletHelpers;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Giuseppe Della Penna
 */
public class Ricordami extends HttpServlet {

    private void action_saluta_noto(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //preleviamo il nome dalla sessione
        //get the name from the session
        HttpSession s = request.getSession(false);
        String nome = (String) s.getAttribute("n");
        //qualche controllo di sicurezza (ridondante)
        //some (redundant) security check
        if (nome == null || nome.isBlank()) {
            nome = "Unknown";
        }

        HTMLResult result = new HTMLResult(getServletContext());
        result.setTitle("Hello!");
        result.setBody("<p>Hello, " + HTMLResult.sanitizeHTMLOutput(nome) + ", I remember you!</p>");
        result.appendToBody("<p><a href=\"ricorda?logout=1\">Forget me</a></p>");
        result.activate(request, response);
    }

    private void action_saluta_anonimo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HTMLResult result = new HTMLResult(getServletContext());
        result.setTitle("Remember me!");
        result.appendToBody("<p>Hello!</p>");
        result.appendToBody("<form method=\"get\" action=\"ricorda\">");
        result.appendToBody("<p>What is your name?");
        result.appendToBody("<input type=\"text\" name=\"n\"/></p>");
        result.appendToBody("<p><input type=\"submit\" name=\"s\" value=\"Remember me!\"/></p>");
        result.appendToBody("</form>");
        result.activate(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        try {
            //prima, otteniamo la sessione
            //first, get the session
            HttpSession s = request.getSession(true);
            //controlliamo se è richiesto il logout
            //check if a logout is required
            if (request.getParameter("logout") == null) {
                //controlliamo se il nome è già presente in sessione
                //check if the name has been already set in the session
                if (s.getAttribute("n") != null && !((String) s.getAttribute("n")).isBlank()) {
                    action_saluta_noto(request, response);
                } else {
                    //se il nome non è presente, controlliamo se ci viene passato dalla form
                    //if name is not present, check if it is being passed from the form
                    String n = request.getParameter("n");
                    if (n == null || n.isBlank()) {
                        action_saluta_anonimo(request, response);
                    } else {
                        //se il nome arriva dalla form, inserimolo nell sessione
                        //if the name comes from the form, set in in the session
                        s.setAttribute("n", n);
                        action_saluta_noto(request, response);
                    }
                }
            } else {
                //se è stato richiesto il logout, choudiamo la sessione
                //if logout has been selected, close the session
                s.invalidate();
                action_saluta_anonimo(request, response);
            }
        } catch (Exception ex) {
            request.setAttribute("exception", ex);
            ServletHelpers.handleError(request, response, getServletContext());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "A kind servlet";

    }// </editor-fold>
}
