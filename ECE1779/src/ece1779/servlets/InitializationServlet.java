package ece1779.servlets;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.BasicAWSCredentials;

import ece1779.GlobalValues;

public class InitializationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InitializationServlet() {
		super();
	}
	
	public void init(){
		initAWS(this.getServletConfig());
		initJDBC();
	}


	private void initAWS(ServletConfig config) {

		String accessKey = config.getInitParameter("AWSaccessKey");
		String secretKey = config.getInitParameter("AWSsecretKey");
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey,
				secretKey);
		config.getServletContext().setAttribute(GlobalValues.AWS_CREDENTIALS,
				awsCredentials);
	}

	private void initJDBC() {

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
