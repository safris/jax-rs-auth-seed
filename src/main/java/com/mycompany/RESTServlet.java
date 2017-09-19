package com.mycompany;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import org.libx4j.xrs.server.DefaultRESTServlet;

@WebServlet(initParams={@WebInitParam(name="javax.ws.rs.Application", value="com.mycompany.Application")})
public class RESTServlet extends DefaultRESTServlet {
  private static final long serialVersionUID = -826395783690371140L;
}