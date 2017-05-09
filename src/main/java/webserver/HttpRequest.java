package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

//	private String path;
//	private String method;
	
	
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, String> params = new HashMap<String, String>();
	
	private FirstLine firstLine;
	

	public HttpRequest(InputStream in) {

		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = br.readLine();

			
			if (line == null) {
				return;
			}

			log.debug("http Header First Line :{}", line);
			/*
			 * 여기 부분에서 라인 첫번째로 겟과 포스트 나누는 작업 하고 , 이후에는 겟일때와 포스트일때 각각 구하는 작업 진행
			 */
			
			
			firstLine = new FirstLine(line);
			
			/*
			 * get방식일땐 이미 params를 구한 상태, post방식일땐 아래 while 문에서 header랑 parmas 둘다
			 * 구해야
			 */
			line = br.readLine();
			int i = 2;
			while (!"".equals(line)) {

				if (line == null)
					return;

				log.debug("http Header Line {} :{}", i, line);

				String[] headerTokens = line.split(": ");
				if (headerTokens.length == 2) {
					headers.put(headerTokens[0], headerTokens[1]);
				}

				i++;
				line = br.readLine();
			}
			if (getMethod().equals("POST")) {
				String requestBody = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
				log.debug("Request Body : {}", requestBody);

				params = HttpRequestUtils.parseQueryString(requestBody);
			}
			else{
				params = firstLine.getParams();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
//
//		private void FirstLine(String line) {
//
//		this.method = HttpRequestUtils.getUrl(line, "method");
//
//		if (this.method.equals("POST")) {
//			this.path = HttpRequestUtils.getUrl(line, "path");
//			return;
//		} else {// get방식일경우에는 바로 params에 넣는다
//			String tmpPath = HttpRequestUtils.getUrl(line, "path");
//
//			if(tmpPath==null)
//				return;
////			
////			String[] tmp = tmpPath.split("?");
////			if(tmp.length==2){
////				this.path=tmp[0];
////				this.params=HttpRequestUtils.parseQueryString(tmp[1]);
////			}
////			
//				
//			int index = tmpPath.indexOf("?");
//
//			this.path = tmpPath.substring(0, index);
//			String queryString = tmpPath.substring(index + 1);
//			this.params = HttpRequestUtils.parseQueryString(queryString);
//
//		}
//	}

	public String getMethod() {

		return firstLine.getMethod().toString();

	}

	public String getPath() {

		return firstLine.getPath();
	}

	public String getHeader(String content) {
		return this.headers.get(content);
	}

	public String getParameter(String parameter) {
		return this.params.get(parameter);
	}

}
