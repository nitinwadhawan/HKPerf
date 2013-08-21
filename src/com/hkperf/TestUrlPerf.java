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
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Nitin Wadhawan
 * Date: 8/5/13
 * Time: 8:46 AM
 * To change this template use File | Settings | File Templates.
 */

public class TestUrlPerf {
	public String APIkey = "68503684ff22420497f60d22cfd2ce37";
	EnumUrl enumUrl = new EnumUrl();
	Response response = new Response();
	ResponseDetails responseDetails = new ResponseDetails();
	ResponseViewType responseType = new ResponseViewType();
	Resource resource = new ClassPathResource("applicationContext.xml");
	BeanFactory factory = new XmlBeanFactory(resource);
	Dao dao = (Dao)factory.getBean("d");
	int PRETTY_PRINT_INDENT_FACTOR = 4;

	public static String HttpGet(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}

		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();

		conn.disconnect();
		return sb.toString();
	}

	public static void checkTestStatus(String testId) throws InterruptedException, IOException, JSONException {
		String fetchTestStatus = HttpGet("http://www.webpagetest.org/testStatus.php?f=xml&test=" + testId + "&f=json");
		JSONObject obj2 = new JSONObject(fetchTestStatus);
		System.out.println("Status object" + obj2);
		JSONObject getStatus = (JSONObject) obj2.get("data");
		Integer statusCode = (Integer) getStatus.get("statusCode");
		//System.out.println("status code :"+statusCode);
		if (statusCode == 200) {
			return;
		} else {
			System.out.println("test is in process. Please wait.We will automatically resend the request in 10 secs.");
			Thread.sleep(10000);
			checkTestStatus(testId);
		}

	}

	public Date getCurrentDateTime() {
		DateFormat DF = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date todayDate = null;
		try {
			todayDate = DF.parse(DF.format(new Date()));
		} catch (ParseException e) {
			System.out.println(e);
		}

		return todayDate;
	}

	public void testPerfomance(String url) throws IOException, JSONException, InterruptedException, SQLException {
		/*1.Initialize database_Response table to log number of outgoing requests and request time.
		* 2.Submit test request to webpage test.
		* 3.retrieve testId from first response.
		* 4.Check Test status for every 10 seconds.
		* 5.Get detailed response in JSON format at completion of Test.
		* 6.Save detail response to corresponding tables*/

		response.setWebsiteId(dao.getWebSiteMappingId(url));
		response.setCreateDt(getCurrentDateTime());
		String firstResponse = HttpGet("http://www.webpagetest.org/runtest.php?url=" + url + "&k=" + APIkey + "&f=json");
		JSONObject obj = new JSONObject(firstResponse);
		System.out.println("First object :" + obj);
		JSONObject data = (JSONObject) obj.get("data");
		String testId = (String) data.get("testId");
		response.setTestId(testId);
		String fetchTestStatus = HttpGet("http://www.webpagetest.org/testStatus.php?f=xml&test=" + testId + "&f=json");
		System.out.println("Test Status: " + fetchTestStatus);
		checkTestStatus(testId);
		JSONObject obj2 = new JSONObject(fetchTestStatus);
		JSONObject getStatus = (JSONObject) obj2.get("data");
		String getXmlResult = HttpGet("http://www.webpagetest.org/xmlResult/" + testId + "/");
		try {

			JSONObject xmlJSONObj = XML.toJSONObject(getXmlResult);
			System.out.println("xmlJsonObj :" + xmlJSONObj);
			String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
			System.out.println("Formatted response :" + jsonPrettyPrintString);
			JSONObject jsonResponse = xmlJSONObj.getJSONObject("response");
			JSONObject responseData = jsonResponse.getJSONObject("data");

			response.setStatus(jsonResponse.getInt("statusCode"));
			/*response.setRuns(responseData.getInt("successfulRVRuns"));*/
			response.setCreateDt(getCurrentDateTime());
			response.setBwDown(responseData.getInt("bwDown"));
			response.setBwUp(responseData.getInt("bwUp"));
			response.setCompleteDate(getCurrentDateTime());
			response.setConnectivity(responseData.getString("connectivity"));
			response.setFrm(responseData.getString("from"));
			response.setLatency(responseData.getInt("latency"));
			response.setLocation(responseData.getString("location"));
			dao.saveResponse(response);

			JSONObject averageFirstViewDetails = responseData.getJSONObject("average").getJSONObject("firstView");
			JSONObject averageRepeatViewDetails = responseData.getJSONObject("average").getJSONObject("repeatView");

			JSONObject medianFirstViewDetails = responseData.getJSONObject("median").getJSONObject("firstView");
			JSONObject medianRepeatViewDetails = responseData.getJSONObject("median").getJSONObject("repeatView");

			JSONObject standardDeviationFirstViewDetails = responseData.getJSONObject("standardDeviation").getJSONObject("firstView");
			JSONObject standardDeviationRepeatViewDetails = responseData.getJSONObject("standardDeviation").getJSONObject("repeatView");


			responseDetails.setRequestId(dao.getRequestId(testId));
			responseDetails.setResponseViewType("3");
			responseDetails.setLoadTime(averageFirstViewDetails.getString("loadTime"));
			responseDetails.setSpeedIndex(averageFirstViewDetails.getString("SpeedIndex"));
			responseDetails.setTtfb(averageFirstViewDetails.getString("TTFB"));
			responseDetails.setUrl(averageFirstViewDetails.getString("URL"));
			responseDetails.setVisuallyCompleteDt(averageFirstViewDetails.getString("VisuallyCompleteDT"));
			responseDetails.setAdultSite(averageFirstViewDetails.getString("adult_site"));
			responseDetails.setAft(averageFirstViewDetails.getString("aft"));
			responseDetails.setAvgRun(averageFirstViewDetails.getString("avgRun"));
			responseDetails.setBasePageCdn(averageFirstViewDetails.getString("base_page_cdn"));
			responseDetails.setBrowserName(averageFirstViewDetails.getString("browser_name"));
			responseDetails.setBrowserVersion(averageFirstViewDetails.getString("browser_version"));
			responseDetails.setBytesIn(averageFirstViewDetails.getString("bytesIn"));
			responseDetails.setBytesInDoc(averageFirstViewDetails.getString("bytesInDoc"));
			responseDetails.setBytesOutDoc(averageFirstViewDetails.getString("bytesOutDoc"));
			responseDetails.setCached(averageFirstViewDetails.getString("cached"));
			responseDetails.setConnections(averageFirstViewDetails.getString("connections"));
			responseDetails.setDate(averageFirstViewDetails.getString("date"));
			responseDetails.setDocTime(averageFirstViewDetails.getString("docTime"));
			responseDetails.setDomContentLoadedEventEnd(averageFirstViewDetails.getString("domContentLoadedEventEnd"));
			responseDetails.setDomContentLoadedEventStart(averageFirstViewDetails.getString("domContentLoadedEventStart"));
			responseDetails.setDomElements(averageFirstViewDetails.getString("domElements"));
			responseDetails.setDomTime(averageFirstViewDetails.getString("domTime"));
			responseDetails.setFirstPaint(averageFirstViewDetails.getString("firstPaint"));
			responseDetails.setFixedViewport(averageFirstViewDetails.getString("fixed_viewport"));
			responseDetails.setFullyLoaded(averageFirstViewDetails.getString("fullyLoaded"));
			responseDetails.setGzipSavings(averageFirstViewDetails.getString("gzip_savings"));
			responseDetails.setGzipTotal(averageFirstViewDetails.getString("gzip_total"));
			responseDetails.setImageSavings(averageFirstViewDetails.getString("image_savings"));
			responseDetails.setImageTotal(averageFirstViewDetails.getString("image_total"));
			responseDetails.setLastVisualChange(averageFirstViewDetails.getString("lastVisualChange"));
			responseDetails.setLoadEventEnd(averageFirstViewDetails.getString("loadEventEnd"));
			responseDetails.setLoadEventStart(averageFirstViewDetails.getString("loadEventStart"));
			responseDetails.setLoadTime(averageFirstViewDetails.getString("loadTime"));
			responseDetails.setMinifySavings(averageFirstViewDetails.getString("minify_savings"));
			responseDetails.setMinifyTotal(averageFirstViewDetails.getString("minify_total"));
			responseDetails.setOptimizationChecked(averageFirstViewDetails.getString("optimization_checked"));
			responseDetails.setPageSpeedVersion(averageFirstViewDetails.getString("pageSpeedVersion"));
			responseDetails.setRender(averageFirstViewDetails.getString("render"));
			responseDetails.setRequests(averageFirstViewDetails.getString("requests"));
			responseDetails.setRequestsDoc(averageFirstViewDetails.getString("requestsDoc"));
			responseDetails.setResponses200(averageFirstViewDetails.getString("responses_200"));
			responseDetails.setResponses404(averageFirstViewDetails.getString("responses_404"));
			responseDetails.setResponsesOther(averageFirstViewDetails.getString("responses_other"));
			responseDetails.setResult(averageFirstViewDetails.getString("result"));
			responseDetails.setScoreCdn(averageFirstViewDetails.getString("score_cdn"));
			responseDetails.setScoreCombine(averageFirstViewDetails.getString("score_combine"));
			responseDetails.setScoreCompress(averageFirstViewDetails.getString("score_compress"));
			responseDetails.setScoreCookies(averageFirstViewDetails.getString("score_cookies"));
			responseDetails.setScoreEtags(averageFirstViewDetails.getString("score_etags"));
			responseDetails.setScoreGzip(averageFirstViewDetails.getString("score_gzip"));
			responseDetails.setScoreKeepAlive(averageFirstViewDetails.getString("score_keep-alive"));
			responseDetails.setScoreMinify(averageFirstViewDetails.getString("score_minify"));
			responseDetails.setScoreProgressiveJpeg(averageFirstViewDetails.getString("score_progressive_jpeg"));
			responseDetails.setServerCount(averageFirstViewDetails.getString("server_count"));
			responseDetails.setServerRtt(averageFirstViewDetails.getString("server_rtt"));
			responseDetails.setTitle(averageFirstViewDetails.getString("title"));
			responseDetails.setTitle(averageFirstViewDetails.getString("titleTime"));
			responseDetails.setVisualComplete(averageFirstViewDetails.getString("visualComplete"));
			dao.saveResponseDetails(responseDetails);

			responseDetails.setRequestId(dao.getRequestId(testId));
			responseDetails.setResponseViewType("4");
			responseDetails.setLoadTime(averageRepeatViewDetails.getString("loadTime"));
			responseDetails.setSpeedIndex(averageRepeatViewDetails.getString("SpeedIndex"));
			responseDetails.setTtfb(averageRepeatViewDetails.getString("TTFB"));
			responseDetails.setUrl(averageRepeatViewDetails.getString("URL"));
			responseDetails.setVisuallyCompleteDt(averageRepeatViewDetails.getString("VisuallyCompleteDT"));
			responseDetails.setAdultSite(averageRepeatViewDetails.getString("adult_site"));
			responseDetails.setAft(averageRepeatViewDetails.getString("aft"));
			responseDetails.setAvgRun(averageRepeatViewDetails.getString("avgRun"));
			responseDetails.setBasePageCdn(averageRepeatViewDetails.getString("base_page_cdn"));
			responseDetails.setBrowserName(averageRepeatViewDetails.getString("browser_name"));
			responseDetails.setBrowserVersion(averageRepeatViewDetails.getString("browser_version"));
			responseDetails.setBytesIn(averageRepeatViewDetails.getString("bytesIn"));
			responseDetails.setBytesInDoc(averageRepeatViewDetails.getString("bytesInDoc"));
			responseDetails.setBytesOutDoc(averageRepeatViewDetails.getString("bytesOutDoc"));
			responseDetails.setCached(averageRepeatViewDetails.getString("cached"));
			responseDetails.setConnections(averageRepeatViewDetails.getString("connections"));
			responseDetails.setDate(averageRepeatViewDetails.getString("date"));
			responseDetails.setDocTime(averageRepeatViewDetails.getString("docTime"));
			responseDetails.setDomContentLoadedEventEnd(averageRepeatViewDetails.getString("domContentLoadedEventEnd"));
			responseDetails.setDomContentLoadedEventStart(averageRepeatViewDetails.getString("domContentLoadedEventStart"));
			responseDetails.setDomElements(averageRepeatViewDetails.getString("domElements"));
			responseDetails.setDomTime(averageRepeatViewDetails.getString("domTime"));
			responseDetails.setFirstPaint(averageRepeatViewDetails.getString("firstPaint"));
			responseDetails.setFixedViewport(averageRepeatViewDetails.getString("fixed_viewport"));
			responseDetails.setFullyLoaded(averageRepeatViewDetails.getString("fullyLoaded"));
			responseDetails.setGzipSavings(averageRepeatViewDetails.getString("gzip_savings"));
			responseDetails.setGzipTotal(averageRepeatViewDetails.getString("gzip_total"));
			responseDetails.setImageSavings(averageRepeatViewDetails.getString("image_savings"));
			responseDetails.setImageTotal(averageRepeatViewDetails.getString("image_total"));
			responseDetails.setLastVisualChange(averageRepeatViewDetails.getString("lastVisualChange"));
			responseDetails.setLoadEventEnd(averageRepeatViewDetails.getString("loadEventEnd"));
			responseDetails.setLoadEventStart(averageRepeatViewDetails.getString("loadEventStart"));
			responseDetails.setLoadTime(averageRepeatViewDetails.getString("loadTime"));
			responseDetails.setMinifySavings(averageRepeatViewDetails.getString("minify_savings"));
			responseDetails.setMinifyTotal(averageRepeatViewDetails.getString("minify_total"));
			responseDetails.setOptimizationChecked(averageRepeatViewDetails.getString("optimization_checked"));
			responseDetails.setPageSpeedVersion(averageRepeatViewDetails.getString("pageSpeedVersion"));
			responseDetails.setRender(averageRepeatViewDetails.getString("render"));
			responseDetails.setRequests(averageRepeatViewDetails.getString("requests"));
			responseDetails.setRequestsDoc(averageRepeatViewDetails.getString("requestsDoc"));
			responseDetails.setResponses200(averageRepeatViewDetails.getString("responses_200"));
			responseDetails.setResponses404(averageRepeatViewDetails.getString("responses_404"));
			responseDetails.setResponsesOther(averageRepeatViewDetails.getString("responses_other"));
			responseDetails.setResult(averageRepeatViewDetails.getString("result"));
			responseDetails.setScoreCdn(averageRepeatViewDetails.getString("score_cdn"));
			responseDetails.setScoreCombine(averageRepeatViewDetails.getString("score_combine"));
			responseDetails.setScoreCompress(averageRepeatViewDetails.getString("score_compress"));
			responseDetails.setScoreCookies(averageRepeatViewDetails.getString("score_cookies"));
			responseDetails.setScoreEtags(averageRepeatViewDetails.getString("score_etags"));
			responseDetails.setScoreGzip(averageRepeatViewDetails.getString("score_gzip"));
			responseDetails.setScoreKeepAlive(averageRepeatViewDetails.getString("score_keep-alive"));
			responseDetails.setScoreMinify(averageRepeatViewDetails.getString("score_minify"));
			responseDetails.setScoreProgressiveJpeg(averageRepeatViewDetails.getString("score_progressive_jpeg"));
			responseDetails.setServerCount(averageRepeatViewDetails.getString("server_count"));
			responseDetails.setServerRtt(averageRepeatViewDetails.getString("server_rtt"));
			responseDetails.setTitle(averageRepeatViewDetails.getString("title"));
			responseDetails.setTitle(averageRepeatViewDetails.getString("titleTime"));
			responseDetails.setVisualComplete(averageRepeatViewDetails.getString("visualComplete"));
			dao.saveResponseDetails(responseDetails);

			responseDetails.setRequestId(dao.getRequestId(testId));
			responseDetails.setResponseViewType("1");
			responseDetails.setLoadTime(medianFirstViewDetails.getString("loadTime"));
			responseDetails.setSpeedIndex(medianFirstViewDetails.getString("SpeedIndex"));
			responseDetails.setTtfb(medianFirstViewDetails.getString("TTFB"));
			responseDetails.setUrl(medianFirstViewDetails.getString("URL"));
			responseDetails.setVisuallyCompleteDt(medianFirstViewDetails.getString("VisuallyCompleteDT"));
			responseDetails.setAdultSite(medianFirstViewDetails.getString("adult_site"));
			responseDetails.setAft(medianFirstViewDetails.getString("aft"));
			//responseDetails.setAvgRun(medianFirstViewDetails.getString("avgRun"));
			responseDetails.setBasePageCdn(medianFirstViewDetails.getString("base_page_cdn"));
			responseDetails.setBrowserName(medianFirstViewDetails.getString("browser_name"));
			responseDetails.setBrowserVersion(medianFirstViewDetails.getString("browser_version"));
			responseDetails.setBytesIn(medianFirstViewDetails.getString("bytesIn"));
			responseDetails.setBytesInDoc(medianFirstViewDetails.getString("bytesInDoc"));
			responseDetails.setBytesOutDoc(medianFirstViewDetails.getString("bytesOutDoc"));
			responseDetails.setCached(medianFirstViewDetails.getString("cached"));
			responseDetails.setConnections(medianFirstViewDetails.getString("connections"));
			responseDetails.setDate(medianFirstViewDetails.getString("date"));
			responseDetails.setDocTime(medianFirstViewDetails.getString("docTime"));
			responseDetails.setDomContentLoadedEventEnd(medianFirstViewDetails.getString("domContentLoadedEventEnd"));
			responseDetails.setDomContentLoadedEventStart(medianFirstViewDetails.getString("domContentLoadedEventStart"));
			responseDetails.setDomElements(medianFirstViewDetails.getString("domElements"));
			responseDetails.setDomTime(medianFirstViewDetails.getString("domTime"));
			responseDetails.setFirstPaint(medianFirstViewDetails.getString("firstPaint"));
			responseDetails.setFixedViewport(medianFirstViewDetails.getString("fixed_viewport"));
			responseDetails.setFullyLoaded(medianFirstViewDetails.getString("fullyLoaded"));
			responseDetails.setGzipSavings(medianFirstViewDetails.getString("gzip_savings"));
			responseDetails.setGzipTotal(medianFirstViewDetails.getString("gzip_total"));
			responseDetails.setImageSavings(medianFirstViewDetails.getString("image_savings"));
			responseDetails.setImageTotal(medianFirstViewDetails.getString("image_total"));
			responseDetails.setLastVisualChange(medianFirstViewDetails.getString("lastVisualChange"));
			responseDetails.setLoadEventEnd(medianFirstViewDetails.getString("loadEventEnd"));
			responseDetails.setLoadEventStart(medianFirstViewDetails.getString("loadEventStart"));
			responseDetails.setLoadTime(medianFirstViewDetails.getString("loadTime"));
			responseDetails.setMinifySavings(medianFirstViewDetails.getString("minify_savings"));
			responseDetails.setMinifyTotal(medianFirstViewDetails.getString("minify_total"));
			responseDetails.setOptimizationChecked(medianFirstViewDetails.getString("optimization_checked"));
			responseDetails.setPageSpeedVersion(medianFirstViewDetails.getString("pageSpeedVersion"));
			responseDetails.setRender(medianFirstViewDetails.getString("render"));
			responseDetails.setRequests(medianFirstViewDetails.getString("requests"));
			responseDetails.setRequestsDoc(medianFirstViewDetails.getString("requestsDoc"));
			responseDetails.setResponses200(medianFirstViewDetails.getString("responses_200"));
			responseDetails.setResponses404(medianFirstViewDetails.getString("responses_404"));
			responseDetails.setResponsesOther(medianFirstViewDetails.getString("responses_other"));
			responseDetails.setResult(medianFirstViewDetails.getString("result"));
			responseDetails.setScoreCdn(medianFirstViewDetails.getString("score_cdn"));
			responseDetails.setScoreCombine(medianFirstViewDetails.getString("score_combine"));
			responseDetails.setScoreCompress(medianFirstViewDetails.getString("score_compress"));
			responseDetails.setScoreCookies(medianFirstViewDetails.getString("score_cookies"));
			responseDetails.setScoreEtags(medianFirstViewDetails.getString("score_etags"));
			responseDetails.setScoreGzip(medianFirstViewDetails.getString("score_gzip"));
			responseDetails.setScoreKeepAlive(medianFirstViewDetails.getString("score_keep-alive"));
			responseDetails.setScoreMinify(medianFirstViewDetails.getString("score_minify"));
			responseDetails.setScoreProgressiveJpeg(medianFirstViewDetails.getString("score_progressive_jpeg"));
			responseDetails.setServerCount(medianFirstViewDetails.getString("server_count"));
			responseDetails.setServerRtt(medianFirstViewDetails.getString("server_rtt"));
			responseDetails.setTitle(medianFirstViewDetails.getString("title"));
			responseDetails.setTitle(medianFirstViewDetails.getString("titleTime"));
			responseDetails.setVisualComplete(medianFirstViewDetails.getString("visualComplete"));
			dao.saveResponseDetails(responseDetails);

			responseDetails.setRequestId(dao.getRequestId(testId));
			responseDetails.setResponseViewType("2");
			responseDetails.setLoadTime(medianRepeatViewDetails.getString("loadTime"));
			responseDetails.setSpeedIndex(medianRepeatViewDetails.getString("SpeedIndex"));
			responseDetails.setTtfb(medianRepeatViewDetails.getString("TTFB"));
			responseDetails.setUrl(medianRepeatViewDetails.getString("URL"));
			responseDetails.setVisuallyCompleteDt(medianRepeatViewDetails.getString("VisuallyCompleteDT"));
			responseDetails.setAdultSite(medianRepeatViewDetails.getString("adult_site"));
			responseDetails.setAft(medianRepeatViewDetails.getString("aft"));
			//responseDetails.setAvgRun(medianRepeatViewDetails.getString("avgRun"));
			responseDetails.setBasePageCdn(medianRepeatViewDetails.getString("base_page_cdn"));
			responseDetails.setBrowserName(medianRepeatViewDetails.getString("browser_name"));
			responseDetails.setBrowserVersion(medianRepeatViewDetails.getString("browser_version"));
			responseDetails.setBytesIn(medianRepeatViewDetails.getString("bytesIn"));
			responseDetails.setBytesInDoc(medianRepeatViewDetails.getString("bytesInDoc"));
			responseDetails.setBytesOutDoc(medianRepeatViewDetails.getString("bytesOutDoc"));
			responseDetails.setCached(medianRepeatViewDetails.getString("cached"));
			responseDetails.setConnections(medianRepeatViewDetails.getString("connections"));
			responseDetails.setDate(medianRepeatViewDetails.getString("date"));
			responseDetails.setDocTime(medianRepeatViewDetails.getString("docTime"));
			responseDetails.setDomContentLoadedEventEnd(medianRepeatViewDetails.getString("domContentLoadedEventEnd"));
			responseDetails.setDomContentLoadedEventStart(medianRepeatViewDetails.getString("domContentLoadedEventStart"));
			responseDetails.setDomElements(medianRepeatViewDetails.getString("domElements"));
			responseDetails.setDomTime(medianRepeatViewDetails.getString("domTime"));
			responseDetails.setFirstPaint(medianRepeatViewDetails.getString("firstPaint"));
			responseDetails.setFixedViewport(medianRepeatViewDetails.getString("fixed_viewport"));
			responseDetails.setFullyLoaded(medianRepeatViewDetails.getString("fullyLoaded"));
			responseDetails.setGzipSavings(medianRepeatViewDetails.getString("gzip_savings"));
			responseDetails.setGzipTotal(medianRepeatViewDetails.getString("gzip_total"));
			responseDetails.setImageSavings(medianRepeatViewDetails.getString("image_savings"));
			responseDetails.setImageTotal(medianRepeatViewDetails.getString("image_total"));
			responseDetails.setLastVisualChange(medianRepeatViewDetails.getString("lastVisualChange"));
			responseDetails.setLoadEventEnd(medianRepeatViewDetails.getString("loadEventEnd"));
			responseDetails.setLoadEventStart(medianRepeatViewDetails.getString("loadEventStart"));
			responseDetails.setLoadTime(medianRepeatViewDetails.getString("loadTime"));
			responseDetails.setMinifySavings(medianRepeatViewDetails.getString("minify_savings"));
			responseDetails.setMinifyTotal(medianRepeatViewDetails.getString("minify_total"));
			responseDetails.setOptimizationChecked(medianRepeatViewDetails.getString("optimization_checked"));
			responseDetails.setPageSpeedVersion(medianRepeatViewDetails.getString("pageSpeedVersion"));
			responseDetails.setRender(medianRepeatViewDetails.getString("render"));
			responseDetails.setRequests(medianRepeatViewDetails.getString("requests"));
			responseDetails.setRequestsDoc(medianRepeatViewDetails.getString("requestsDoc"));
			responseDetails.setResponses200(medianRepeatViewDetails.getString("responses_200"));
			responseDetails.setResponses404(medianRepeatViewDetails.getString("responses_404"));
			responseDetails.setResponsesOther(medianRepeatViewDetails.getString("responses_other"));
			responseDetails.setResult(medianRepeatViewDetails.getString("result"));
			responseDetails.setScoreCdn(medianRepeatViewDetails.getString("score_cdn"));
			responseDetails.setScoreCombine(medianRepeatViewDetails.getString("score_combine"));
			responseDetails.setScoreCompress(medianRepeatViewDetails.getString("score_compress"));
			responseDetails.setScoreCookies(medianRepeatViewDetails.getString("score_cookies"));
			responseDetails.setScoreEtags(medianRepeatViewDetails.getString("score_etags"));
			responseDetails.setScoreGzip(medianRepeatViewDetails.getString("score_gzip"));
			responseDetails.setScoreKeepAlive(medianRepeatViewDetails.getString("score_keep-alive"));
			responseDetails.setScoreMinify(medianRepeatViewDetails.getString("score_minify"));
			responseDetails.setScoreProgressiveJpeg(medianRepeatViewDetails.getString("score_progressive_jpeg"));
			responseDetails.setServerCount(medianRepeatViewDetails.getString("server_count"));
			responseDetails.setServerRtt(medianRepeatViewDetails.getString("server_rtt"));
			responseDetails.setTitle(medianRepeatViewDetails.getString("title"));
			responseDetails.setTitle(medianRepeatViewDetails.getString("titleTime"));
			responseDetails.setVisualComplete(medianRepeatViewDetails.getString("visualComplete"));
			dao.saveResponseDetails(responseDetails);

		} catch (JSONException je) {
			System.out.println(je.toString());

		}
	}

	public int getWebsiteId(String url) {
		if (url.contains("healthkart")) {
			return 1;
		} else if (url.contains("snapdeal")) {
			return 2;
		} else if (url.contains("infibeam")) {
			return 3;
		} else if (url.contains("flipkart")) {
			return 4;
		} else if (url.contains("myntra")) {
			return 5;
		} else if (url.contains("jabong")) {
			return 6;
		} else if (url.contains("shopclues")) {
			return 7;
		} else if (url.contains("amazon")) {
			return 8;
		} else return 0;

	}

	public int getViewType(String view) {
		if (view.contains("median_first")) {
			return 1;
		} else if (view.contains("median_repeat")) {
			return 2;
		} else if (view.contains("average_first")) {
			return 3;
		} else if (view.contains("average_repeat")) {
			return 4;
		} else if (view.contains("standardDeviation_first")) {
			return 5;
		} else if (view.contains("standardDeviation_repeat")) {
			return 6;
	}
		else return 0;
}

}