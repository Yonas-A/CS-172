package com.example.lucene;


import com.example.lucene.Query.Queryy;
import com.example.lucene.Search.SearchTweets;


import com.example.lucene.Tweet.Tweet;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.javatuples.Pair;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Controller
@Service
public class ViewController {

  private final SearchTweets tweetService;

  @Autowired
  public ViewController(SearchTweets tweetService) {
    this.tweetService = tweetService;
  }

  @GetMapping("/")
  public String getQueryForm(Model model) {
    System.out.print("\n\n\n\nViewController::getQueryForm" + "\n\n\n\n\n");
    model.addAttribute("query", new Queryy());
    return "index";
  }

  @PostMapping("/")
  public String SendQuery(@ModelAttribute Queryy query, Model model) {
    model.addAttribute("query", query);

    HashMap<String, String> searchTerm = new HashMap<String, String>();
    searchTerm.put("id", query.getId().toString());
    searchTerm.put("content", query.getContent());

    JSONObject jsonObject = new JSONObject(searchTerm);

    //JSON
    try {
      FileWriter file = new FileWriter(
        ViewController.class.getResource("/data/search_term.json").getPath());

      file.write(jsonObject.toJSONString());
      file.close();

      System.out.println("Successfully wrote to the file.");
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }

    System.out.print("\n\n\n\nViewController::SendQuery" + query + "\n\n\n\n\n");
    return "result";
  }

}


