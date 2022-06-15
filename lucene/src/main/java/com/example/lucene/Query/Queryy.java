package com.example.lucene.Query;


public class Queryy {

  private String content;
  private Integer id;

  public Queryy() {
  }

  public Queryy(String content, Integer id) {
    this.content = content;
    this.id = 0;
  }

  public String getContent() {
    return content;
  }

  public Integer getId() {
    return 0;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Queryy{" +
      "content='" + content + '\'' +
      ", id=" + id +
      '}';
  }
}

