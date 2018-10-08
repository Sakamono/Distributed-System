package com.example.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadClient extends Thread {
  private String url;
  private int threadNum;
  private int iterationNum;
  private List<Measurements> reports;

  public MultiThreadClient(String url, int threadNum, int iterationNum, List<Measurements> reports) {
    this.url = url;
    this.threadNum = threadNum;
    this.iterationNum = iterationNum;
    this.reports = reports;
  }

  public List<Long> runMultiThread() {
    List<Long> times = new ArrayList<>();
    ExecutorService executor = Executors.newFixedThreadPool(threadNum);
    Long startTime = System.currentTimeMillis();

    //create and start client threads
    Client client = ClientBuilder.newClient();
//    WebTarget webTarget = client.target(url).path("simple-service-webapp/webapi/myresource");
    WebTarget webTarget = client.target(url).path("LambdaTest");
    for (int i = 0; i < threadNum; i++) {
      Measurements mt = new Measurements();
      reports.add(mt);
      SingleThreadClient worker = new SingleThreadClient(webTarget, iterationNum, mt);
      executor.submit(worker);
    }

    executor.shutdown();
    while(!executor.isTerminated());

    Long endTime = System.currentTimeMillis();
    Long wallTime = endTime - startTime;
    times.add(startTime);
    times.add(endTime);
    times.add(wallTime);
    return times;
  }

}
