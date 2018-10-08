package com.example.client;

import com.sun.javafx.font.Metrics;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class SingleThreadClient extends Thread{
//  private String url;
  private int iterationNum;// iteration times of get and post
  private Measurements report; //
  private MyClient myClient;
  private WebTarget webTarget;

  public SingleThreadClient(WebTarget webTarget, int iterationNum, Measurements report) {
    this.webTarget = webTarget;
    this.iterationNum = iterationNum;
    this.report = report;
    this.myClient = new MyClient(this.webTarget);
  }

  @Override
  public void run() {

    for (int i = 0; i < iterationNum; i++) {

      //post
      long startTime = System.currentTimeMillis();
//      Response postResponse = myClient.postText("test");
      Response postResponse = myClient.postText("\"test\"");
      long currentTime = System.currentTimeMillis();//Returns the current time in milliseconds.
      report.add(currentTime - startTime);
      report.requestAdd();
//      System.out.println(postResponse.getStatus());

      if (postResponse.getStatus() == 200) {
        report.successAdd();
      }
      postResponse.close();

      //get
      startTime = System.currentTimeMillis();
      int getResponse = myClient.getStatus();
      currentTime = System.currentTimeMillis();
      report.add(currentTime - startTime);
      report.requestAdd();
//      System.out.println(getResponse);
      if (getResponse == 200) {
        report.successAdd();
      }

    }

  }

//  public static void main(String[] args) {
//
//    String url = "http://18.219.120.174:8080";
//    Client client = ClientBuilder.newClient();
//    WebTarget webTarget = client.target(url).path("/simple-service-webapp/webapi/myresource");
//    Measurements measurements = new Measurements();
//    SingleThreadClient single = new SingleThreadClient(webTarget, 10, measurements);
//    single.run();
//    System.out.println(measurements.getRequestCount());
//
//    System.out.println(measurements.getSuccessCount());
//  }

}
