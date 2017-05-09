package webserver;

import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;
import java.net.Socket;

import java.util.Collection;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;


public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private Socket connection;
	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

			
			HttpRequest request = new HttpRequest(in);
			
			
			
			HttpResponse response = new HttpResponse(out);
			
			String path = getDefaultPath(request.getPath());
			
			log.debug("RequestHandler Path: {}" , path);
			
			
			
			
			// 이건 post 방
			if (path.startsWith("/user/create")) {

				
				User user = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"),
						request.getParameter("email"));
				log.debug("User:{}", user);

				DataBase.addUser(user);

				response.sendRedirect("/indext.html");

				
			} else if (path.startsWith("/user/login")) {
				
				

				User user = DataBase.findUserById(request.getParameter("userId"));

				if (user == null) {
					log.debug(" User Not Found!!");
					response.sendRedirect("/user/login_failed.html");
					
				} else if (user.getPassword().equals(request.getParameter("password"))) {
					log.debug(" Log in Success!!");
					
					response.addHeader("Set-Cookie", "logined=true");
					response.sendRedirect("/index.html");
					
					
				} else {
					log.debug(" PassWord missmatch!");
					
					response.sendRedirect("/user/login_failed.html");
				}

			}else if(path.startsWith("/user/list")){
				
				if(!isLogin(request.getHeader("Set-Cookie"))){
					response.sendRedirect("/user/login.html");
					return;
				}
				
				Collection<User> users = DataBase.findAll();
				StringBuilder sb = new StringBuilder();
				
				sb.append("<table border = '1'>");
				for(User user : users){
					sb.append("<tr>");
					sb.append("<td>"+user.getUserId()+"</td>");
					sb.append("<td>"+user.getName()+"</td>");
					sb.append("<td>"+user.getEmail()+"</td>");
					sb.append("</tr>");
				}
				
				sb.append("</table>");
				response.forwardBody(sb.toString());
				
			}
			 else {
				 
				 response.forward(path);
			}

			// 이건 get 방식
			// if(url.startsWith("/user/create")){
			// int index = url.indexOf("?");
			//
			// String requestPath = url.substring(0,index);
			// String queryString = url.substring(index+1);
			// Map<String, String> params =
			// HttpRequestUtils.parseQueryString(queryString);
			// User user = new User(params.get("userId"),params.get("password")
			// ,params.get("name") , params.get("email"));
			// log.debug("User:{}",user);
			//
			// url="/index.html";
			// }

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	private boolean isLogin(String line){
		String[] headerTokens = line.split(":");
		Map<String ,String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
		String value = cookies.get("logined");
		if(value == null){
			return false;
		}
		return Boolean.parseBoolean(value);
	}
	private String getDefaultPath(String path){
		if(path.equals("/")){
			return "/index.html";
		}
		return path;
	}


}
