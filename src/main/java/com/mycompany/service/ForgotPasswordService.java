package com.mycompany.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.safris.commons.util.Random;

import com.mycompany.Server;
import com.mycompany.Template;
import com.mycompany.data.AccountData;

import xdb.ddl.mycompany;
import xjb.api;

public class ForgotPasswordService {
  private static void sendEmail(final mycompany.Account a, final String subject, final String template) throws BadLocationException, ParseException {
    final Map<String,String> properties = new HashMap<String,String>();
    properties.put("firstName", a.firstName.get());
    properties.put("token", a.forgotToken.get());
    final String content = Template.getTemplate(template, properties);
    Server.instance().email(a.email.get(), subject, content);
  }

  @POST
  @Path("/forgot")
  @Consumes(MediaType.APPLICATION_JSON)
  public void forgot(final api.Credentials credentials) throws BadLocationException, ParseException, SQLException {
    final mycompany.Account a = AccountData.findAccount(credentials.email());
    if (a == null)
      throw new NotAcceptableException();

    a.forgotToken.set(Random.alphaNumeric(6));
    AccountData.saveAccount(a);
    sendEmail(a, "Reset Your Password", "forgot.html");
  }

  @GET
  @Path("/forgot/reset/{token}")
  public void reset(@PathParam("token") final String token) throws SQLException {
    final mycompany.Account a = AccountData.findAccountByToken(token);
    if (a == null)
      throw new NotAcceptableException();
  }

  @POST
  @Path("/forgot/reset/{token}")
  @Consumes(MediaType.TEXT_PLAIN)
  public void reset(@PathParam("token") final String token, final String password) throws BadLocationException, ParseException, SQLException {
    final mycompany.Account a = AccountData.findAccountByToken(token);
    if (a == null)
      throw new NotAcceptableException();

    a.password.set(password);
    a.forgotToken.set(null);
    AccountData.saveAccount(a);
    sendEmail(a, "Password Reset", "reset.html");
  }

  @POST
  @Path("/forgot/report/{token}")
  public void report(@PathParam("token") final String token) throws SQLException {
    if (AccountData.removeAccountToken(token) == 0)
      throw new NotAcceptableException();
  }
}