package webserver;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;

public class FirstLine {
	
	private static final Logger log = LoggerFactory.getLogger(FirstLine.class);
	
	private HttpMethod method;
	private String path;
	private Map<String,String> params = new HashMap<String , String>();
	
	public FirstLine(String firstLine){
		
		log.debug("request Line :{}" , firstLine);
		
		
		this.method = HttpMethod.valueOf(HttpRequestUtils.getUrl(firstLine, "method"));

		
		
		if (this.method==HttpMethod.POST) {
			this.path = HttpRequestUtils.getUrl(firstLine, "path");
			return;
		} else {// get방식일경우에는 바로 params에 넣는다
			String tmpPath = HttpRequestUtils.getUrl(firstLine, "path");

			log.debug("tmpPath: {}" , tmpPath);
			
			if(tmpPath==null)
				return;
//			
//			String[] tmp = tmpPath.split("?");
//			if(tmp.length==2){
//				this.path=tmp[0];
//				this.params=HttpRequestUtils.parseQueryString(tmp[1]);
//			}
//			
				
			
			int index = tmpPath.indexOf("?");
			
			if(index==-1){
				this.path = tmpPath;
				return;
			}

			this.path = tmpPath.substring(0, index);
			String queryString = tmpPath.substring(index + 1);
			this.params = HttpRequestUtils.parseQueryString(queryString);

		}

	}
	
	public HttpMethod getMethod(){
		return this.method;
	}
	public String getPath(){
		return this.path;
	}
	
	public Map<String , String> getParams(){
		return this.params;
	}
	

}
