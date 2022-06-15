package com.example.lucene.Search;

import com.example.lucene.Query.Queryy;

import com.example.lucene.LuceneApplication;
import org.javatuples.*;

import java.io.*;
import java.util.*;


import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.apache.lucene.search.Query;

import com.example.lucene.Tweet.Tweet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;
import org.json.simple.JSONArray;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.util.function.Tuple2;

@Service
public class SearchTweets {


  public static void main(String[] args) {
  }

  @GetMapping
  public static List<Pair<Tweet, String>> getTweetsList() {

    List<Pair<Tweet, String>> tweetsList = new ArrayList<>();
    try {
      StandardAnalyzer analyzer = new StandardAnalyzer();

      Path path = Paths.get(SearchTweets.class.getResource("/data/index").getPath());

      Directory dir = FSDirectory.open(path);

      DirectoryReader indexreader = DirectoryReader.open(dir);
      IndexSearcher indexsearcher = new IndexSearcher(indexreader);

      Map<String, Float> boosts = new HashMap<String, Float>();
      boosts.put("text", 2.0f);
      boosts.put("title", 1.5f);
      boosts.put("username", 1.0f);
      boosts.put("urls", 0.5f);
      boosts.put("created_at", 0.5f);
      boosts.put("id", 0.5f);

      String query_id = "";
      String query_param = "a";
      try {
        InputStream jsonFile = new FileInputStream(
          SearchTweets.class.getResource("/data/search_term.json").getPath());
        Reader readerJson = new InputStreamReader(jsonFile);
        // Parse the json file using simple-json library
        JSONObject fileObjects = (JSONObject) JSONValue.parse(readerJson);
        query_id = fileObjects.get("id").toString();
        query_param = fileObjects.get("content").toString();
        System.out.println("\n\nSearchTweets terms: " + query_param + "\n\n");
      } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
      MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"id", "text", "lang", "created_at", "username", "title", "urls"}, analyzer, boosts);
      Query qu = parser.parse(query_param);

      ScoreDoc[] hits = indexsearcher.search(qu, 10).scoreDocs;
      String endDate = "2022-05-08 23:13:25+00:00";


      for (int i = 0; i < hits.length; i++) {
        Document hitDoc = indexsearcher.doc(hits[i].doc);

        String startDate = hitDoc.get("created_at");

        Tweet t = new Tweet(hitDoc.get("id"),
          hitDoc.get("username"),
          hitDoc.get("created_at"),
          hitDoc.get("text"),
          hitDoc.get("title"),
          hitDoc.get("lang"));

        tweetsList.add(Pair.with(t, (i + 1) + "     Score: " + hits[i].score));
      }
      indexreader.close();
      dir.close();

    } catch (Exception e) {
      e.printStackTrace();
    }


    System.out.println(tweetsList);
    return tweetsList;
  }

}

