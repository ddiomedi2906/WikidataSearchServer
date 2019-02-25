package com.server.utils;

import com.server.search.ImageRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class RenderTools {

    public static void renderTable(StringBuilder sb, JsonObject data) {
        RenderTools.renderTableDescription(sb, data);
        sb.append("<table class=\"table table-hover\">");
        RenderTools.renderTableHeader(sb);
        String modalString = RenderTools.renderTableBody(sb, data.get("documents").getAsJsonArray());
        sb.append("</table>");
        sb.append(modalString);
    }

    private static void renderTableDescription(StringBuilder sb, JsonObject data) {
        int matches = data.get("matching").getAsInt(), top = data.get("topResults").getAsInt();
        sb.append("<h2>").append(data.get("query").getAsString()).append("</h2>");
        sb.append("<p>").append("Total matches: ")
                .append("<b>").append(matches).append("</b>")
                .append(", displaying Top ")
                .append("<b>").append(Math.min(matches, top)).append("</b>")
                .append(" results.").append("</p>");
    }

    private static void renderTableHeader(StringBuilder sb) {
        sb.append("<thead>").append("<tr>")
                .append("<th>Number</th>")
                .append("<th>Entity</th>")
                .append("<th>PrefLabel</th>")
                // .append("<th>AltLabel</th>")
                .append("<th>Images</th>")
                .append("</tr>")
                .append("</thead>");
    }

    private static String renderTableBody(StringBuilder sb, JsonArray documents) {
        StringBuilder modalSb = new StringBuilder();
        sb.append("<tbody>");
        for(JsonElement document: documents) {
            JsonObject doc = document.getAsJsonObject();
            sb.append("<tr>");
            // Add number Value
            sb.append("<td>").append(doc.get("number").getAsString().replaceAll("\"", "")).append("</td>");
            // Add entity Value
            String entityId = doc.get("entity").getAsString().replaceAll("\"", "");
            sb.append("<td>").append(entityId).append("</td>");
            // Add prefLabel value
            String prefLabel = "";
            if (!doc.get("prefLabel").isJsonNull())
                prefLabel = doc.get("prefLabel").getAsString();
            sb.append("<td>").append(prefLabel.replaceAll("\"", "")).append("</td>");
            // Add altLabels and images
            //String altLabelModal = renderAltLabelsColumns(sb, entityId, doc.get("altLabels"));
            String imagesModal = renderImagesColumns(sb, entityId, doc.get("images"));
            modalSb
                    //.append(altLabelModal)
                    .append(imagesModal);
            sb.append("</tr>");
        }
        sb.append("</tbody>");
        return modalSb.toString();
    }

    private static String renderImagesColumns (StringBuilder sb, String entityId, JsonElement images) {
        if (images.isJsonNull()) {
            sb.append("<td>")
                    .append("<button type=\"button\" class=\"btn btn-danger\">")
                    .append("<i class=\"material-icons\">clear</i>")
                    .append("</button>")
                    .append("</td>");
            return "";
        }
        String modalId = "image" + entityId;
        sb.append("<td>")
                .append("<button type=\"button\" class=\"btn btn-info\" data-toggle=\"modal\" data-target=\"#").append(modalId).append("\">")
                .append("<i class=\"material-icons\">image</i>")
                .append("</button>")
                .append("</td>");
        // Modal Creation
        StringBuilder modalSbAux = new StringBuilder();
        modalSbAux
                .append("<div class=\"modal fade\" id=\"").append(modalId).append("\">")
                .append("<div class=\"modal-dialog modal-sm\">")
                .append("<div class=\"modal-content\">");
        // Modal Header
        renderModalHeader(modalSbAux, entityId);
        // Modal Body
        modalSbAux.append("<div class=\"modal-body\">");
        //renderListImageNames(modalSbAux, images);
        renderListImageCarousel(modalSbAux, entityId, images);
        modalSbAux.append("</div>");
        // Modal Footer
        renderModalFooter(modalSbAux);
        modalSbAux.append("</div>").append("</div>").append("</div>");
        return modalSbAux.toString();
    }

    private static void renderListImageCarousel(StringBuilder modalSbAux, String header, JsonElement images) {
        String carouselId = "car-" + header;
        modalSbAux
                .append("<div id=\"").append(carouselId).append("\" class=\"carousel slide\" data-ride=\"carousel\"  data-interval=\"false\">");
        // Create list of alt labels
        ArrayList<String> imageNames = new ArrayList<>(Arrays.asList(images.getAsString().split(" ")));
        ImageRequest imgRequest = new ImageRequest(imageNames);
        StringBuilder modalCarInd = new StringBuilder("<ul class=\"carousel-indicators\">"),
                modalCarInn = new StringBuilder("<div class=\"carousel-inner\">");
        try {
            imgRequest.sendGET();
            if(imgRequest.thumbUrlList.size() > 0) {
                modalCarInd.append("<li data-target=\"#").append(carouselId).append("\" data-slide-to=\"0\" class=\"active\"></li>");
                modalCarInn.append("<div class=\"carousel-item active\">")
                        .append("<img class=\"img-thumbnail d-block w-100\" src=\"").append(imgRequest.thumbUrlList.get(0)).append("\" alt=\"img0\">")
                        .append("</div>");
                for (int i = 1; i < imgRequest.thumbUrlList.size(); i++) {
                    String url = imgRequest.thumbUrlList.get(i);
                    modalCarInd.append("<li data-target=\"#").append(carouselId).append("\" data-slide-to=\"").append(i).append("\"></li>");
                    modalCarInn.append("<div class=\"carousel-item\">")
                            .append("<img class=\"img-thumbnail d-block w-100\" src=\"").append(url).append("\"alt=\"img").append(i).append("\">")
                            .append("</div>");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        modalCarInd.append("</ul>");
        modalCarInn.append("</div>");
        modalSbAux.append(modalCarInd.toString()).append(modalCarInn.toString());
        modalSbAux.append("<a class=\"carousel-control-prev \" href=\"#").append(carouselId).append("\" data-slide=\"prev\">")
                .append("<span class=\"carousel-control-prev-icon\"></span>")
                .append("</a>")
                .append("<a class=\"carousel-control-next\" href=\"#").append(carouselId).append("\" data-slide=\"next\">")
                .append("<span class=\"carousel-control-next-icon\"></span>")
                .append("</a>")
                .append("</div>");
    }

    private static void renderListImageNames(StringBuilder modalSbAux, JsonElement images) {
        modalSbAux
                .append("<ul class=\"list-group\">");
        // Create list of alt labels
        ArrayList<String> imageNames = new ArrayList<>(Arrays.asList(images.getAsString().split(" ")));
        for(String name: imageNames) {
            if(!name.trim().equals(""))
                modalSbAux.append("<li class=\"list-group-item\">").append(name).append("</li>");
        }
        modalSbAux.append("</ul>");
    }

    private static void renderModalHeader(StringBuilder msb, String header) {
        msb.append("<div class=\"modal-header\">")
                .append("<h4 class=\"modal-title\">").append(header).append("</h4>")
                .append("<button type=\"button\" class=\"close\" data-dismiss=\"modal\">&times;</button>")
                .append("</div>");
    }

    private static void renderModalFooter(StringBuilder msb) {
        msb.append("<div class=\"modal-footer\">")
                .append("<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">Close</button>")
                .append("</div>");
    }


    private static String renderAltLabelsColumns (StringBuilder sb, String entityId, JsonElement altLabels) {
        // entity doesn't have alt labels
        if (altLabels.isJsonNull()) {
            sb.append("<td>")
                    .append("<button type=\"button\" class=\"btn btn-danger\">")
                    .append("<i class=\"material-icons\">clear</i>")
                    .append("</button>")
                    .append("</td>");
            return "";
        }
        // entity does have alt labels
        String modalId = "alt" + entityId;
        sb.append("<td>")
                .append("<button type=\"button\" class=\"btn btn-info\" data-toggle=\"modal\" data-target=\"#").append(modalId).append("\">")
                .append("<i class=\"material-icons\">list</i>")
                .append("</button>")
                .append("</td>");
        // Modal Creation
        StringBuilder modalSbAux = new StringBuilder();
        modalSbAux.append("<div class=\"modal\" id=\"").append(modalId).append("\">")
                .append("<div class=\"modal-dialog\">")
                .append("<div class=\"modal-content\">");
        // Modal Header
        renderModalHeader(modalSbAux, entityId);
        // Modal Body
        modalSbAux.append("<div class=\"modal-body\">")
                .append("<ul class=\"list-group\">");
        // Create list of alt labels
        ArrayList<String> labels = new ArrayList<>(Arrays.asList(altLabels.getAsString().split("\"")));
        for(String label: labels) {
            if(!label.trim().equals(""))
                modalSbAux.append("<li class=\"list-group-item\">").append(label).append("</li>");
        }
        modalSbAux.append("</ul>").append("</div>");
        // Modal Footer
        renderModalFooter(modalSbAux);
        modalSbAux.append("</div>").append("</div>").append("</div>");
        return modalSbAux.toString();
    }

}
