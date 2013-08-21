package com.hkperf;

import org.json.JSONException;

import java.io.IOException;
import java.sql.SQLException;

public class PagePerformance{


	public static final String WWW_INFIBEAM_COM = "www.infibeam.com";
	public static final String WWW_HEALTHKART_COM = "www.healthkart.com";
	public static final String WWW_SNAPDEAL_COM = "www.snapdeal.com";
	public static final String WWW_FLIPKART_COM = "www.flipkart.com";

	public static void main(String[] args) throws IOException, JSONException, InterruptedException, SQLException {
		TestUrlPerf testUrlPerf= new TestUrlPerf();
		testUrlPerf.testPerfomance(WWW_INFIBEAM_COM);
		testUrlPerf.testPerfomance(WWW_HEALTHKART_COM);
		testUrlPerf.testPerfomance(WWW_SNAPDEAL_COM);
		testUrlPerf.testPerfomance(WWW_FLIPKART_COM);


	}
}