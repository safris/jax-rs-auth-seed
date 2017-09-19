package com.mycompany.service;

import java.io.IOException;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.libx4j.rdb.jsql.mycompany;

import com.mycompany.data.AccountData;

import jjb.api;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(api.mimeType)
public class RegistrationService {
  @POST
  @Path("/register")
  public api.Account register(final api.Account account) throws IOException, SQLException {
    mycompany.Account a = new mycompany.Account();
    a.firstName.set(account.firstName.get());
    a.lastName.set(account.lastName.get());
    a.email.set(account.email.get());
    a.password.set(account.password.get());
    a.authRole.set(mycompany.Account.AuthRole.USER);

    a = AccountData.saveAccount(a);
    if (a == null)
      throw new WebApplicationException("email is already registered", Response.Status.CONFLICT); // FIXME: What if some other error occurs? Seems like there should be a special catch for SC_BAD_REQUEST too

    account.id.set(a.id.get());
    account.password.clear();
    return account;
  }
}