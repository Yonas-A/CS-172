package com.example.lucene.Tweet;

import com.example.lucene.Search.SearchTweets;
import org.javatuples.Pair;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "api/v1/tweet")
public class TweetController {

  private final SearchTweets tweetService;


  @Autowired
  public TweetController(SearchTweets tweetService) {
    this.tweetService = tweetService;
  }

  @GetMapping
  public List<Pair<Tweet, String>> getTweets() {
    return SearchTweets.getTweetsList();
  }
}
