package com.example.lucene.Tweet;

public class Tweet {
  private String id;
  private String username;
  private String createdAt;
  private String text;
  private String title;
  private String lang;


  public Tweet(String id,
               String username,
               String createdAt,
               String text,
               String title,
               String lang) {
    this.id = id;
    this.username = username;
    this.createdAt = createdAt;
    this.text = text;
    this.title = title;
    this.lang = lang;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return username;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public String getText() {
    return text;
  }

  public String getTitle() {
    return title;
  }

  public String getLanguage() {
    return lang;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String username) {
    this.username = username;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setLanguage(String lang) {
    this.lang = lang;
  }


  @Override
  public String toString() {
    return "Tweet{" +
      "id='" + id + '\'' +
      ", username='" + username + '\'' +
      ", createdAt='" + createdAt + '\'' +
      ", text='" + text + '\'' +
      ", title='" + title + '\'' +
      ", lang='" + lang + '\'' +
      '}';
  }
}
