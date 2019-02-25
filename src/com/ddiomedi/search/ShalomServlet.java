package com.ddiomedi.search;

import com.ddiomedi.utils.SearchWikiDataIndex;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ShalomServlet", urlPatterns = {"/mySearch"})
public class ShalomServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // http://prisma:8080/wikidataSearch/mySearch?query=chile
        String query = "", searchType = "";
        int topResults = 10;
        if(request.getParameter("query") != null)
            query = request.getParameter("query");
        if(request.getParameter("top-results") != null)
            try {
                topResults = Integer.parseInt(request.getParameter("top-results"));
            } catch (NumberFormatException e) {
                System.out.println(request.getParameter("top-results") + " is not a valid integer number");
            }
        if(request.getParameter("search-type") != null)
            searchType = request.getParameter("search-type");

        String relativePath = "/wikidataIndex/output-all";
        // String inDirectory = "C:\\Users\\Daniel\\Documents\\Semestre2018-2\\trabajo dirigido\\mdp-lab06-sol\\data\\output-all";
        if(searchType.equals("image")) {
            // inDirectory = "C:\\Users\\Daniel\\Documents\\Semestre2018-2\\trabajo dirigido\\mdp-lab06-sol\\data\\output-simple";
            relativePath = "/wikidataIndex/output-simple";
        }
        String inDirectory = getServletContext().getRealPath(relativePath);

        System.out.println(inDirectory);

        SearchWikiDataIndex myIndex = new SearchWikiDataIndex(inDirectory, searchType);

        System.out.println(query);
        System.out.println(topResults);
        System.out.println(searchType);
        // JsonConverter converter = new JsonConverter();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        //String output = converter.getJson();
        String output = myIndex.oneSearchAppWikidata(request.getParameter("query"), topResults);

        request.setAttribute("message", output); // This will be available as ${message}
        request.getRequestDispatcher("index.jsp").forward(request, response);

        // System.out.println(output);
        // out.println(output);
        // out.flush();
/*
        String res = "{ \"key1\": \"value1\", \"key2\": \"value2\" }";
        //response.setContentType("application/json");
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.println("<h1>shalom</h1>");
        // out.println(res);
        out.flush();
*/
    }
}
