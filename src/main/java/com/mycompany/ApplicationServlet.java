package com.mycompany;

import org.libx4j.xrs.server.RestApplicationServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(initParams={@WebInitParam(name="javax.ws.rs.Application", value="com.mycompany.Application")})
public class ApplicationServlet extends RestApplicationServlet {
  private static final long serialVersionUID = -826395783690371140L;
}