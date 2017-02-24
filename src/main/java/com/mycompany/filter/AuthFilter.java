package com.mycompany.filter;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.safris.commons.net.AuthSchemes;
import org.safris.commons.security.Credentials;
import org.safris.dbx.jsql.mycompany;
import org.safris.xrs.server.DefaultRESTServlet;

import com.mycompany.AccountPrincipal;
import com.mycompany.data.AccountData;

@Provider
public class AuthFilter implements ContainerRequestFilter {
  private static final String AUTHORIZED_USER = DefaultRESTServlet.class.getPackage().getName() + ".AUTHORIZED_USER";
  private static final String AUTHORIZED_USER_CHECKED = AUTHORIZED_USER + "_CHECKED";

  private static mycompany.Account getAccount(final ContainerRequestContext requestContext) {
    if (requestContext.getProperty(AUTHORIZED_USER_CHECKED) != null)
      return (mycompany.Account)requestContext.getProperty(AUTHORIZED_USER);

    requestContext.setProperty(AUTHORIZED_USER_CHECKED, Boolean.TRUE);
    final String authorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
    if (authorization == null)
      return null;

    final Credentials credentials = AuthSchemes.parseBasicAuthHeader(authorization);
    if (credentials == null)
      return null;

    try {
      final mycompany.Account a = AccountData.findAccount(credentials.username, credentials.password);
      requestContext.setProperty(AUTHORIZED_USER, a);
      return a;
    }
    catch (final IOException | SQLException e) {
      throw new WebApplicationException(e);
    }
  }

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    final boolean secure = requestContext.getSecurityContext().isSecure();
    final mycompany.Account a = getAccount(requestContext);
    if (a != null) {
      requestContext.setSecurityContext(new SecurityContext() {
        private final AccountPrincipal principal = new AccountPrincipal(a);

        @Override
        public Principal getUserPrincipal() {
          return principal;
        }

        @Override
        public boolean isUserInRole(final String role) {
          return "user".equals(role);
        }

        @Override
        public boolean isSecure() {
          return secure;
        }

        @Override
        public String getAuthenticationScheme() {
          return requestContext.getHeaderString(HttpHeaders.AUTHORIZATION).startsWith("Basic ") ? BASIC_AUTH : null;
        }
      });
    }
  }
}