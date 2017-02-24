package com.mycompany;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;
import javax.ws.rs.ext.RuntimeDelegate;

import org.safris.commons.cli.Options;
import org.safris.commons.dbcp.DataSources;
import org.safris.commons.jetty.EmbeddedServletContainer;
import org.safris.commons.jetty.UncaughtServletExceptionHandler;
import org.safris.commons.lang.Resources;
import org.safris.commons.logging.Logging;
import org.safris.commons.net.mail.Mail;
import org.safris.commons.sql.ConnectionProxy;
import org.safris.commons.xml.dom.DOMStyle;
import org.safris.commons.xml.dom.DOMs;
import org.safris.dbx.jsql.EntityDataSource;
import org.safris.dbx.jsql.EntityRegistry;
import org.safris.dbx.jsql.mycompany;
import org.safris.xrs.server.ext.RuntimeDelegateImpl;
import org.safris.xsb.runtime.Bindings;
import org.xml.sax.InputSource;

import com.mycompany.jaxrsauthseed.config.xe.$cf_https;
import com.mycompany.jaxrsauthseed.config.xe.cf_config;

public class Server extends EmbeddedServletContainer {
  private static final Logger logger = Logger.getLogger(Server.class.getName());
  private static Server instance;

  static {
    System.setProperty(RuntimeDelegate.JAXRS_RUNTIME_DELEGATE_PROPERTY, RuntimeDelegateImpl.class.getName());
  }

  private final cf_config config;

  public static void main(final String[] args) throws Exception {
    final Options options = Options.parse(Resources.getResource("cli.xml").getURL(), Server.class, args);
    logger.info(options.toString());

    final String configOption = options.getOption("config");
    final cf_config config = (cf_config)Bindings.parse(new InputSource(Resources.getResourceOrFile(configOption != null ? configOption : "config.xml").getURL().openStream()));
    logger.info(DOMs.domToString(Bindings.marshal(config), DOMStyle.INDENT));

    instance = new Server(config, RESTServlet.class);

    instance.start();
    instance.join();
  }

  public static Server instance() {
    return instance;
  }

  private final InternetAddress from;
  private final String onServiceErrorEmail;
  private final MailSender mailSender;

  public void email(final String to, final String subject, final String message) {
    mailSender.send(from, to, subject, message);
  }

  public void emailException(final Throwable e) {
    if (onServiceErrorEmail != null)
      mailSender.send(from, onServiceErrorEmail, e);
  }

  @SafeVarargs
  protected Server(final cf_config config, final Class<? extends HttpServlet> ... servletClasses) throws SQLException, UnsupportedEncodingException {
    super(config._server(0)._port$().text(), config._server(0) instanceof $cf_https ? (($cf_https)config._server(0))._keystore(0)._path$().text() : null, config._server(0) instanceof $cf_https ? (($cf_https)config._server(0))._keystore(0)._password$().text() : null, !config._debug(0)._externalResourcesAccess$().isNull() && config._debug(0)._externalResourcesAccess$().text(), config._realm(0), servletClasses);
    this.config = config;
    Logging.setLevel(Level.parse(config._debug(0)._logging(0)._global$().text()), config._debug(0)._logging(0)._logger());

    from = new InternetAddress(config._mail(0)._server(0)._credentials(0)._username$().text(), config._mail(0)._server(0)._credentials(0)._username$().text());
    onServiceErrorEmail = ((String)config._debug(0)._onServiceExceptionEmail$().text()).length() > 0 ? (String)config._debug(0)._onServiceExceptionEmail$().text() : null;
    mailSender = new MailSender(Mail.Protocol.valueOf(config._mail(0)._server(0)._protocol$().text().toUpperCase()), config._mail(0)._server(0)._host$().text(), config._mail(0)._server(0)._port$().text(), config._mail(0)._server(0)._credentials(0)._username$().text(), config._mail(0)._server(0)._credentials(0)._password$().text());

    final DataSource dataSource = DataSources.createDataSource(config._dbcps(0).dbcp_dbcp(), "mycompany");
    EntityRegistry.register(mycompany.class, PreparedStatement.class, new EntityDataSource() {
      @Override
      public Connection getConnection() throws SQLException {
        return new ConnectionProxy(dataSource.getConnection());
      }
    });

    EmbeddedServletContainer.setUncaughtServletExceptionHandler(new UncaughtServletExceptionHandler() {
      @Override
      public void uncaughtServletException(final ServletRequest request, final ServletResponse response, final Exception e) throws Exception {
        emailException(e);
        throw e;
      }
    });
  }

  public cf_config getConfig() {
    return config;
  }
}