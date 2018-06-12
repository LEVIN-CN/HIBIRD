/**
 * 
 */
package netwk;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import local.vo.HBall;

/**
 * @author Administrator
 *
 */
public class ZcwDataClient {

	private static final String ZCW_HIS_LAST30_URL = "http://www.zhcw.com/kaijiang/kjData/2012/zhcw_ssq_index_last30.js";
	private JSONObject kjData = null;
	private JSONObject kjNum = null;
	private Set<String> kjIssData = null;

	private ZcwDataClient() {
	}

	private static enum ZcwEnum {
		INSTANCE;
		private ZcwDataClient client;

		private ZcwEnum() {
			client = new ZcwDataClient();
		}

		public ZcwDataClient getClient() {
			return client;
		}
	}

	private String getZCWKJData() throws ClientProtocolException, IOException {
		String jsData = "";
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(ZCW_HIS_LAST30_URL);
		CloseableHttpResponse response = client.execute(get);
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			jsData = EntityUtils.toString(entity, "utf-8");
		}
		response.close();
		client.close();
		return jsData;
	}

	public static ZcwDataClient getClient() {
		return ZcwEnum.INSTANCE.getClient();
	}

	public ZcwDataClient parse() {
		String jsData = null;
		try {
			jsData = getZCWKJData();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (jsData != null) {
			String[] needData = jsData.split(System.getProperty("line.separator"));

			String kjD = needData[2];
			int index_5 = kjD.indexOf("{");
			int index_6 = kjD.lastIndexOf("}");
			kjNum = JSON.parseObject(kjD.substring(index_5, index_6 + 1));

			String myKj = needData[3];
			int index_1 = myKj.indexOf("{");
			int index_2 = myKj.lastIndexOf("}");
			kjData = JSON.parseObject(myKj.substring(index_1, index_2 + 1));

			String isNos = needData[4];
			kjIssData = new TreeSet<String>();
			int index_3 = isNos.indexOf("\"");
			int index_4 = isNos.lastIndexOf("\"");
			String isNos2 = isNos.substring(index_3 + 1, index_4);
			for (String iss : isNos2.split(",")) {
				kjIssData.add(iss);
			}

		}
		return this;
	}

	public Set<String> getLast_30_issue_no() throws Exception {
		if (kjIssData == null) {
			throw new Exception("issue no not inited... .");
		}
		return kjIssData;
	}

	public JSONObject getKJDetailByIssue(String issue) throws Exception {
		if (kjData == null) {
			throw new Exception("issue no not inited... .");
		}
		return kjData.getJSONObject(issue);
	}

	public HBall getKjNumByIssue(String issue) throws Exception {
		HBall ball = null;
		if (kjNum == null) {
			throw new Exception("issue no not inited... .");
		}
		JSONObject kjN = kjNum.getJSONObject(issue);
		if (kjN != null) {
			ball = new HBall();
			ball.setqNum(kjData.getJSONObject(issue).getIntValue("QNum"));
			this.setBallValue(ball, kjN);
		}
		return ball;
	}

	private void setBallValue(HBall ball, JSONObject kjN) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ball.setIssueDate(kjN.getString("kjDate"));
		ball.setIssueno(kjN.getString("kjIssue"));
		int b_num = Integer.parseInt(kjN.getString("kjTNum"));
		Class<? extends HBall> ballClazz = ball.getClass();
		Method m = ballClazz.getMethod("setB_" + b_num, int.class);
		m.invoke(ball, b_num);
		String[] r_nums = kjN.getString("kjZNum").split(" ");
		for (String st : r_nums) {
			int r_num = Integer.parseInt(st);
			Method mR = ballClazz.getMethod("setR_" + r_num, int.class);
			mR.invoke(ball, r_num);
		}

	}
}
