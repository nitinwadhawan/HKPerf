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
			System.out.println("Test is in process. Please wait.We will automatically resend the request in 10 secs.");
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
		/*1.'1' and '2' for median first and repeat view.
		* 2.'3' and '4' for average first and repeat view.
		* 3.'5' and '6' for standard deviation first and repeat view.*/


			setResponseAndSaveDao(medianFirstViewDetails,testId,"1");
			setResponseAndSaveDao(medianRepeatViewDetails,testId,"2");
			setResponseAndSaveDao(averageRepeatViewDetails,testId,"4");
			setResponseAndSaveDao(averageFirstViewDetails,testId,"3");
			setResponseAndSaveDao(standardDeviationFirstViewDetails,testId,"5");
			setResponseAndSaveDao(standardDeviationRepeatViewDetails,testId,"6");

		} catch (JSONException je) {
			System.out.println(je.toString());

		}
	}
	public void setResponseAndSaveDao(JSONObject viewTypeObject,String testId,String responseViewType)throws JSONException{
		responseDetails.setRequestId(dao.getRequestId(testId));
		responseDetails.setResponseViewType(responseViewType);
		responseDetails.setLoadTime(viewTypeObject.getString("loadTime"));
		responseDetails.setSpeedIndex(viewTypeObject.getString("SpeedIndex"));
		responseDetails.setTtfb(viewTypeObject.getString("TTFB"));
		responseDetails.setUrl(viewTypeObject.getString("URL"));
		responseDetails.setVisuallyCompleteDt(viewTypeObject.getString("VisuallyCompleteDT"));
		responseDetails.setAdultSite(viewTypeObject.getString("adult_site"));
		responseDetails.setAft(viewTypeObject.getString("aft"));
		if(viewTypeObject.toString().contains("Avg")){
		  responseDetails.setAvgRun(viewTypeObject.getString("avgRun"));
		}
		else
		System.out.println("No Avg run in response");
		responseDetails.setBasePageCdn(viewTypeObject.getString("base_page_cdn"));
		responseDetails.setBrowserName(viewTypeObject.getString("browser_name"));
		responseDetails.setBrowserVersion(viewTypeObject.getString("browser_version"));
		responseDetails.setBytesIn(viewTypeObject.getString("bytesIn"));
		responseDetails.setBytesInDoc(viewTypeObject.getString("bytesInDoc"));
		responseDetails.setBytesOutDoc(viewTypeObject.getString("bytesOutDoc"));
		responseDetails.setCached(viewTypeObject.getString("cached"));
		responseDetails.setConnections(viewTypeObject.getString("connections"));
		responseDetails.setDate(viewTypeObject.getString("date"));
		responseDetails.setDocTime(viewTypeObject.getString("docTime"));
		responseDetails.setDomContentLoadedEventEnd(viewTypeObject.getString("domContentLoadedEventEnd"));
		responseDetails.setDomContentLoadedEventStart(viewTypeObject.getString("domContentLoadedEventStart"));
		responseDetails.setDomElements(viewTypeObject.getString("domElements"));
		responseDetails.setDomTime(viewTypeObject.getString("domTime"));
		responseDetails.setFirstPaint(viewTypeObject.getString("firstPaint"));
		responseDetails.setFixedViewport(viewTypeObject.getString("fixed_viewport"));
		responseDetails.setFullyLoaded(viewTypeObject.getString("fullyLoaded"));
		responseDetails.setGzipSavings(viewTypeObject.getString("gzip_savings"));
		responseDetails.setGzipTotal(viewTypeObject.getString("gzip_total"));
		responseDetails.setImageSavings(viewTypeObject.getString("image_savings"));
		responseDetails.setImageTotal(viewTypeObject.getString("image_total"));
		responseDetails.setLastVisualChange(viewTypeObject.getString("lastVisualChange"));
		responseDetails.setLoadEventEnd(viewTypeObject.getString("loadEventEnd"));
		responseDetails.setLoadEventStart(viewTypeObject.getString("loadEventStart"));
		responseDetails.setLoadTime(viewTypeObject.getString("loadTime"));
		responseDetails.setMinifySavings(viewTypeObject.getString("minify_savings"));
		responseDetails.setMinifyTotal(viewTypeObject.getString("minify_total"));
		responseDetails.setOptimizationChecked(viewTypeObject.getString("optimization_checked"));
		responseDetails.setPageSpeedVersion(viewTypeObject.getString("pageSpeedVersion"));
		responseDetails.setRender(viewTypeObject.getString("render"));
		responseDetails.setRequests(viewTypeObject.getString("requests"));
		responseDetails.setRequestsDoc(viewTypeObject.getString("requestsDoc"));
		responseDetails.setResponses200(viewTypeObject.getString("responses_200"));
		responseDetails.setResponses404(viewTypeObject.getString("responses_404"));
		responseDetails.setResponsesOther(viewTypeObject.getString("responses_other"));
		responseDetails.setResult(viewTypeObject.getString("result"));
		responseDetails.setScoreCdn(viewTypeObject.getString("score_cdn"));
		responseDetails.setScoreCombine(viewTypeObject.getString("score_combine"));
		responseDetails.setScoreCompress(viewTypeObject.getString("score_compress"));
		responseDetails.setScoreCookies(viewTypeObject.getString("score_cookies"));
		responseDetails.setScoreEtags(viewTypeObject.getString("score_etags"));
		responseDetails.setScoreGzip(viewTypeObject.getString("score_gzip"));
		responseDetails.setScoreKeepAlive(viewTypeObject.getString("score_keep-alive"));
		responseDetails.setScoreMinify(viewTypeObject.getString("score_minify"));
		responseDetails.setScoreProgressiveJpeg(viewTypeObject.getString("score_progressive_jpeg"));
		responseDetails.setServerCount(viewTypeObject.getString("server_count"));
		responseDetails.setServerRtt(viewTypeObject.getString("server_rtt"));
		responseDetails.setTitle(viewTypeObject.getString("title"));
		responseDetails.setTitle(viewTypeObject.getString("titleTime"));
		responseDetails.setVisualComplete(viewTypeObject.getString("visualComplete"));
		dao.saveResponseDetails(responseDetails);

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
		if (view.contains("medianFirst")) {
			return 1;
		} else if (view.contains("medianRepeat")) {
			return 2;
		} else if (view.contains("averageFirst")) {
			return 3;
		} else if (view.contains("averageRepeat")) {
			return 4;
		} else if (view.contains("standardDeviationFirst")) {
			return 5;
		} else if (view.contains("standardDeviationRepeat")) {
			return 6;
	}
		else return 0;
}

}