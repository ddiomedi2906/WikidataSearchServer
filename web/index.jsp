<%@ page import="com.google.gson.*" %>
<%@ page import="com.server.utils.RenderTools" %>
<%--
  Created by IntelliJ IDEA.
  User: Daniel
  Date: 12/1/2018
  Time: 6:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>IMGpedia Search</title>
    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg bg-info navbar-dark sticky-top">
    <a class="navbar-brand" href="http://imgpedia.dcc.uchile.cl/">
        <img src="images/imgpedia-min.png" alt="Logo" style="width:100px;height:50px;">
        <!--h4>IMGpedia Lucene Search</h4-->
    </a>
    <form class="form-inline pt-2" action="${pageContext.request.contextPath}/mySearch" method="get">
        <div class="input-group">
            <div class="input-group-prepend">
                <span class="input-group-text"><b>Search by</b></span>
            </div>
            <div class="input-group-text">
                <div class="form-check-inline">
                    <label class="form-check-label">
                        <input type="radio" class="form-check-input" name="search-type" value="label" checked>Labels
                    </label>
                </div>
                <div class="form-check-inline">
                    <label class="form-check-label">
                        <input type="radio" class="form-check-input" name="search-type" value="image">Images
                    </label>
                </div>
            </div>
        </div>
        <div class="input-group row">
            <input type="text" class="form-control col-6 pl-2 pr-0" name="query" id="query" placeholder="Word to look up">
            <input type="number" class="form-control col-2 pl-2 pr-0" name="top-results" id="top-results" min="10" max="100" value="10">
            <div class="input-group-append col-4 pl-0">
                <button type="submit" class="btn btn-light">Search</button>
            </div>
        </div>
    </form>
</nav>
<div class="container">
    <%
        // If request has results to show...
        if (request.getAttribute("message") != null) {
            String result = (String) request.getAttribute("message");
            Gson gson = new GsonBuilder().create();
            JsonObject json = gson.fromJson(result, JsonObject.class);
            StringBuilder sb = new StringBuilder();
            String resultType = json.get("type").getAsString();
            if(resultType.equals("document")) {
                // sb.append(json.toString());
                JsonObject data = json.get("data").getAsJsonObject();
                RenderTools.renderTable(sb, data);
            } else if(resultType.equals("error"))
                sb.append("<h1>Error</h1>");
            out.print(sb.toString());
        } else
            out.print("<div class=\"col m-3\"><h6>Here you will see the results...</h6></div>");
    %>
</div>
</body>
</html>
