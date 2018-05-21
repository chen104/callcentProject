package com.example.vmac.callCenter;

import com.example.vmac.callCenter.util.IflyUtil;
import com.example.vmac.callCenter.util.TulIngRobotUtil;
import com.example.vmac.callCenter.watson.conversation.MessageFilter;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class TestURL {
	public static void main(String[] args) throws Exception {
		URL reqURL = new URL(
				"https://www.ibm.com/support/knowledgecenter/en/POWER8/p8ecs/p8ecs_87x_88x_parts.htm?view=embed"); // 创建URL对象
		HttpsURLConnection httpsConn = (HttpsURLConnection) reqURL
				.openConnection();

		/*
		 * 下面这段代码实现向Web页面发送数据，实现与网页的交互访问 httpsConn.setDoOutput(true);
		 * OutputStreamWriter out = new
		 * OutputStreamWriter(huc.getOutputStream(), "8859_1"); out.write( "……"
		 * ); out.flush(); out.close();
		 */

		// 取得该连接的输入流，以读取响应内容
		InputStreamReader insr = new InputStreamReader(
				httpsConn.getInputStream());
		BufferedReader read =new BufferedReader(insr);
	
		String rs = read.readLine();
		System.out.println(rs);
		rs = read.readLine();
		while (null!=rs) {
			System.out.println(rs);
			rs = read.readLine();
		}
		System.out.println(rs);
		
	}


	public  void testString(){
		MessageFilter messageFilter =new MessageFilter();
		MessageFilter.MatcherResulter re = messageFilter.isMatcherCallInfoPhone("123-12312412");
		// STOPSHIP: 2018/1/19
		System.out.println(re.isMatcher+" "+re.matcherString);
		 re = messageFilter.isMatcherCallInfoPhone("12312312412");
		System.out.println(re.isMatcher+" "+re.matcherString);
		 re = messageFilter.isMatcherCallInfoPhone("1231-2312412");
		System.out.println(re.isMatcher+" "+re.matcherString);

		re = messageFilter.isMatcherCallInfoPhone("12zdszhognwe ");
		System.out.println(re.isMatcher+" "+re.matcherString);


		re = messageFilter.isMatcherCallInfoPhone("12123");
		System.out.println(re.isMatcher+" "+re.matcherString);
	}

	public void testP(){
		String re = IflyUtil.convertText("sdf你好2134-234中文序列号");
		System.out.println(re);

	}
	public void TestTuling() throws Exception {
		String ste =TulIngRobotUtil.sendMessage("我爱你");
		System.out.println(ste);
	}

	@Test
	public void TestNumber() throws Exception {
		System.out.println(" "+MessageFilter.isNumber("1"));
		System.out.println(" "+MessageFilter.isNumber("12"));
		System.out.println(" "+MessageFilter.isNumber("11a"));
		System.out.println(" "+MessageFilter.isNumber("a1"));
	}
}
