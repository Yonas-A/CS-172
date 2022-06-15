package com.example.lucene.Search;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.demo.knn.DemoEmbeddings;
import org.apache.lucene.demo.knn.KnnVectorDict;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.KnnVectorField;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.VectorSimilarityFunction;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class IndexTweets {

  // adding path independent file retrieval solution
  static final String INDEX_PATH = IndexTweets.class.getResource("/data/index/").getPath();
  static final String JSON_FILE_PATH = IndexTweets.class.getResource("/data/json/tweets_200_visited.json").getPath();
  static final String STOPWORDS_FILE_PATH = IndexTweets.class.getResource("/data/stopwords.txt").getPath();

  String indexPath;
  String jsonFilePath;
  IndexWriter indexWriter = null;

  public IndexTweets(String indexPath, String jsonFilePath) {
    this.indexPath = indexPath;
    this.jsonFilePath = jsonFilePath;
  }

  public void createIndex() throws FileNotFoundException {
    JSONArray jsonObjects = parseJSONFile(jsonFilePath);
    openIndex();
    addDocuments(jsonObjects);
    // finish();
  }

  public JSONArray parseJSONFile(String path) throws FileNotFoundException {
    InputStream jsonFile = new FileInputStream(path);
    Reader readerJson = new InputStreamReader(jsonFile);
    // Parse the json file using simple-json library
    Object fileObjects = JSONValue.parse(readerJson);
    return (JSONArray) fileObjects; // ensure that the returned object type is a JSONArray
  }

  public boolean openIndex() {
    try {
      InputStream stopWords = new FileInputStream(STOPWORDS_FILE_PATH);
      Reader readerStopWords = new InputStreamReader(stopWords);
      Directory dir = FSDirectory.open(Paths.get(indexPath));
      StandardAnalyzer analyzer = new StandardAnalyzer(readerStopWords);
      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
      iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
      iwc.setRAMBufferSizeMB(2048.0);
      indexWriter = new IndexWriter(dir, iwc);
      return true;
    } catch (Exception e) {
      System.err.println("Error opening the index... " + e.getMessage());
    }
    return false;
  }

  public void addDocuments(JSONArray jsonObjects) {
    for (JSONObject object : (List<JSONObject>) jsonObjects) {
      // Object is a HashMap of an entry, containing a key:value pair.
      // The key is "places", "id", etc.
      // Value for places is a JSONArray that contains one string, rest are the
      // correct value.
      Document doc = new Document();
      final FieldType bodyOptions = new FieldType();
      bodyOptions.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
      // used to have parameter:
      // FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
      // but that was deprecated in Lucene 5.0

      bodyOptions.setStored(true);
      bodyOptions.setStoreTermVectors(true);
      bodyOptions.setTokenized(true);

      // Create Fields for id, places, lang, title, users, text, and created_at for
      // tweet from JSON file.
      String id = (String) object.get("id");
      doc.add(new Field("id", id, bodyOptions));
      String lang = (String) object.get("lang");
      doc.add(new Field("lang", lang, bodyOptions));
      String title = "";
      if (object.get("title") != null) {
        title = (String) object.get("title");
      }
      doc.add(new Field("title", title, bodyOptions));
      String users = (String) object.get("username");
      doc.add(new Field("username", users, bodyOptions));
      String text = (String) object.get("text");
      doc.add(new Field("text", text, bodyOptions));
      String created_at = (String) object.get("created_at");
      doc.add(new Field("created_at", created_at, bodyOptions));
      String urls = "";
      if (object.get("urls") != null) {
        if (object.get("urls").getClass().getSimpleName().equals("JSONArray")) {
          JSONArray temparr = (JSONArray) object.get("urls");
          JSONObject tempobj = (JSONObject) temparr.get(0);
          urls = (String) tempobj.get("url");
        } else {
          urls = (String) object.get("urls");
        }
      }
      doc.add(new Field("urls", urls, bodyOptions));
      try {
        System.out.println("\n\nIndexTweets::addDocuments " + doc + "\n\n");

        indexWriter.addDocument(doc);
      } catch (IOException ex) {
        System.err.println("Error adding documents to the index. " + ex.getMessage());
      }
    }
  }

  public void finish() {
    try {
      indexWriter.commit();
      indexWriter.close();
    } catch (IOException ex) {
      System.err.println("We had a problem closing the index: " + ex.getMessage());
    }
  }

  public static void main(String[] args) throws FileNotFoundException {
    IndexTweets writer = new IndexTweets(INDEX_PATH, JSON_FILE_PATH);
    writer.createIndex();

    int count = 201;
    String path = "/data/json/tweets_";

    while (count <= 204) {
      System.out.println(count);
      String cnt_str = Integer.toString(count);
      String new_path = path + cnt_str + "_visited.json";
      new_path = IndexTweets.class.getResource(new_path).getPath();
      JSONArray jsonObjects = writer.parseJSONFile(new_path);
      writer.addDocuments(jsonObjects);
      count++;
    }
    writer.finish();
  }
}
