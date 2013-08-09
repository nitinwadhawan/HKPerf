package com.hkperf;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PagePerformance{


	public static void main(String[] args) throws IOException, JSONException, InterruptedException, SQLException {
		TestUrlPerf perf= new TestUrlPerf();
		perf.testPerfomance("www.infibeam.com");
		perf.testPerfomance("www.healthkart.com");
		perf.testPerfomance("www.snapdeal.com");
		perf.testPerfomance("www.flipkart.com");


	}
}