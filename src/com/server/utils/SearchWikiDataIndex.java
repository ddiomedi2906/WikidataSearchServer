package com.server.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.HashMap;

/**
 * Main method to search articles using Lucene.
 * 
 * @author Aidan
 */
public class SearchWikiDataIndex {

    // public static String inDirectory = "C:\\Users\\Daniel\\Desktop\\testServer\\wikidataIndex\\output-all";

    public enum FieldNames3 {
        ENTITY_ID, PREF_LABEL, ALT_LABELS, IMAGE_NAMES, RANK
    }

	private final HashMap<String,Float> BOOSTS3 = new HashMap<String,Float>();

	private final int DOCS_PER_PAGE  = 10;

	private MultiFieldQueryParser queryParser;

    private IndexSearcher searcher;

	public SearchWikiDataIndex(String inDirectory, String searchType) throws IOException {
        System.err.println("Opening directory at  "+inDirectory);

        // BOOSTS.put(FieldNames3.ENTITY_ID.name(), 5f); //<- default
        if(searchType.equals("label")) {
            BOOSTS3.put(FieldNames3.PREF_LABEL.name(), 5f);
            BOOSTS3.put(FieldNames3.ALT_LABELS.name(), 1f);
            BOOSTS3.put(FieldNames3.IMAGE_NAMES.name(), 1f);
        } else {
            BOOSTS3.put(FieldNames3.PREF_LABEL.name(), 0f);
            BOOSTS3.put(FieldNames3.ALT_LABELS.name(), 0f);
            BOOSTS3.put(FieldNames3.IMAGE_NAMES.name(), 5f);
        }

        // open a reader for the directory
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(inDirectory)));
        // open a searcher over the reader
        this.searcher = new IndexSearcher(reader);
        // use the same analyser as when building the index
        Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_48);

        // this accepts queries/searches and parses them into
        // searches over the index
        this.queryParser = new MultiFieldQueryParser(
                Version.LUCENE_48,
                new String[]{FieldNames3.PREF_LABEL.name(), FieldNames3.ALT_LABELS.name(), FieldNames3.IMAGE_NAMES.name()},
                analyzer, BOOSTS3);
    }

	/**
	 *
     * @param queryLine: query line
	 * @throws IOException
	 */
	public String oneSearchAppWikidata(String queryLine, int topNResults) throws IOException {

        // BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject json = new JsonObject();
        json.addProperty("type", "document");

        queryLine = queryLine.trim();
        if (!queryLine.isEmpty()) {
            try {
                // parse query
                Query query = this.queryParser.parse(queryLine);

                int nDocs = Math.max(DOCS_PER_PAGE, topNResults);

                // get hits
                TopDocs results = this.searcher.search(query, nDocs);
                ScoreDoc[] hits = results.scoreDocs;

                System.err.println("Running query: " + queryLine);
                System.err.println("Parsed query: " + query);
                System.err.println("Matching documents: " + results.totalHits);
                System.err.println("Showing top " + nDocs + " results");

                // Create new JSON Object
                JsonObject resultsJson = new JsonObject();
                resultsJson.addProperty("query", queryLine);
                resultsJson.addProperty("matching", results.totalHits);
                resultsJson.addProperty("topResults", nDocs);
                JsonArray topDocuments = new JsonArray();

                for (int i = 0; i < hits.length; i++) {
                    Document doc = this.searcher.doc(hits[i].doc);
                    String entity = doc.get(FieldNames3.ENTITY_ID.name()),
                            prefLabel = doc.get(FieldNames3.PREF_LABEL.name()),
                            altLabels = doc.get(FieldNames3.ALT_LABELS.name()),
                            images = doc.get(FieldNames3.IMAGE_NAMES.name());

                    JsonObject topResult = new JsonObject();
                    topResult.addProperty("number", i + 1);
                    topResult.addProperty("entity", entity);
                    topResult.addProperty("prefLabel", prefLabel);
                    topResult.addProperty("altLabels", altLabels);
                    topResult.addProperty("images", images);

                    String sline = (i + 1) + "\t" + entity + "\t" + prefLabel + "\t" + altLabels + "\t" + images;

                    topDocuments.add(topResult);
                    System.out.println(sline);
                }
                resultsJson.add("documents", topDocuments);

                // File file = new File("data/write.json");
                // Writer writer = new BufferedWriter(new FileWriter(file));

                // writer.write(gson.toJson(resultsJson));
                // writer.close();

                json.add("data", resultsJson);
                return json.toString();
            } catch (Exception e) {
                System.err.println("Error with query '" + queryLine + "'");
                e.printStackTrace();
                return "{\"type\": \"error\"}";
            }
        }
        return "{\"type\": \"empty\"}";
	}

}