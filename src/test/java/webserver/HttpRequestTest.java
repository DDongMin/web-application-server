package webserver;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;

public class HttpRequestTest {

	
	private String testDirectory = "./src/test/resources/";
	@Test
	public void request_GET() throws Exception {
		System.out.println(testDirectory+"Http_GET.txt");
		InputStream in = new FileInputStream(new File(testDirectory+"Http_GET.txt"));
		
		HttpRequest request = new HttpRequest(in);
		
		
		
		assertEquals("GET", request.getMethod());
		assertEquals("/user/create" , request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("javajigi", request.getParameter("userId"));
	}
	@Test
	public void request_POST() throws FileNotFoundException{
		InputStream in = new FileInputStream(new File(testDirectory+"Http_POST.txt"));
		
		HttpRequest request = new HttpRequest(in);
		
		assertEquals("POST", request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("javajigi", request.getParameter("userId"));
	}
	

}
