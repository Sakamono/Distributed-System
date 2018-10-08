package com.example.client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientEngine {
  public static void main(String[] args) {
    String ipAddress = "52.15.211.97";
    String port = "8080";
    int threadNum = 50;
    int iterationNum = 100;

    if (args.length > 0) {
      threadNum = Integer.parseInt(args[0]);
      iterationNum = Integer.parseInt(args[1]);
      ipAddress = args[2];
      port = args[3];
    }

//    System.out.println("ip address is: " + ipAddress);
//    System.out.println("port is: " + port);
    String url = "https://kgorgk01w0.execute-api.us-east-2.amazonaws.com";
    System.out.println("URL is: " + url);
    System.out.println("thread number is: " + threadNum);
    System.out.println("iteration number is: " + iterationNum);
//    String url = "http://" + ipAddress + ":" + port;

    outputPhases(url, threadNum, iterationNum);

  }

  private static void outputPhases(String url, int threadNum, int iterationNum) {
    // output start time
    Long startTime = System.currentTimeMillis();
    System.out.println("Client starts at: Time "
        + String.format(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(startTime)));

    List<List<Measurements>> allReports = new ArrayList<>();
    String[] phaseName = {"Warmup phase", "Loading phase", "Peak phase", "Cooldown phase"};
    double[] ratio = {0.1, 0.5, 1, 0.25};

    for (int i = 0; i < 4; i++) {
      List<Measurements> reports = new ArrayList<Measurements>();
      MultiThreadClient mc = new MultiThreadClient(url, (int)(ratio[i] * threadNum), iterationNum, reports);
      List<Long> result = mc.runMultiThread();
      allReports.add(reports);
      getPhases(result, phaseName[i]);
    }

    Long endTime = System.currentTimeMillis();
    System.out.println("Client ends at: "
        + String.format(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(endTime)));

    System.out.println("==========================================================");

    //output measurements for all four phases
    int numOfRequest = 0;
    int numOfResponses = 0;
    int latencySum = 0;
    List<Long> latencyList = new ArrayList<Long>();
    for (List<Measurements> reports : allReports) {
      for (Measurements report : reports) {
        numOfRequest += report.getRequestCount();
        numOfResponses += report.getSuccessCount();
        latencyList.addAll(report.getLatencyList());
        latencySum += report.getLatencySum();
      }
    }
    System.out.println("Total number of request sent: " + numOfRequest);
    System.out.println("Total number of successful responses: " + numOfResponses);
    long wallTime = endTime - startTime;
    System.out.println("Test Wall time: " + wallTime / 1000 + "s");
    // 3 new measurements:
    System.out.println("Overall throughput for all phases (total number of requests/total wall time) : "
        + numOfRequest / (wallTime / 1000) + " times/s");
    Long[] latencyArray = latencyList.toArray(new Long[latencyList.size()]);
    int size = latencyArray.length;

    double meanLatency = latencySum / size;
    System.out.println("Mean latencies for all requests is: " + meanLatency + " ms");

    Arrays.sort(latencyArray);
    double mediaLatency;
    if (size %2 == 0) {
      mediaLatency = (latencyArray[size / 2 - 1] + latencyArray[size / 2 ]) / 2;
    } else {
      mediaLatency = latencyArray[size / 2];
    }
    System.out.println("Median latencies for all request is: " + mediaLatency + " ms");

    long lt99 = latencyArray[(int) (0.99 * size - 1)];
    long lt95 = latencyArray[(int) (0.95 * size - 1)];

    System.out.println("95th percentile latency is: " + lt95 + " ms");
    System.out.println("99th percentile latency is: " + lt99 + " ms");
    System.out.println("Finished.");

  }

  private static void getPhases(List<Long> result, String phaseName) {
    System.out.println(phaseName + ": All threads running...");
    System.out.println(phaseName + ": Start at: Time: " +
        String.format(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(result.get(0))));
    System.out.println(phaseName + ": Complete at: Time: " +
        String.format(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(result.get(1))));
    System.out.println(phaseName + ": Wall time: " + result.get(2) / 1000 + "s");
  }


}
