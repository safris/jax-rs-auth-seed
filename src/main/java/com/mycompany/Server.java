package com.mycompany;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;
import javax.ws.rs.ext.RuntimeDelegate;

import org.lib4j.cli.Options;
import org.lib4j.dbcp.DataSources;
import org.lib4j.lang.Resources;
import org.lib4j.net.mail.Mail;
import org.lib4j.xml.jaxb.JaxbUtil;
import org.libx4j.jetty.EmbeddedServletContainer;
import org.libx4j.jetty.UncaughtServletExceptionHandler;
import org.libx4j.rdb.jsql.Registry;
import org.libx4j.rdb.jsql.mycompany;
import org.libx4j.xrs.server.ext.RuntimeDelegateImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.jax_rs_auth_seed.config.Config;
import com.mycompany.jax_rs_auth_seed.config.Https;

public class Server extends EmbeddedServletContainer {
  private static final Logger logger = LoggerFactory.getLogger(Server.class);
  private static Server instance;

  static {
    System.setProperty(RuntimeDelegate.JAXRS_RUNTIME_DELEGATE_PROPERTY, RuntimeDelegateImpl.class.getName());
  }

  private final Config config;

  public static void main(final String[] args) throws Exception {
    final Options options = Options.parse(Resources.getResource("cli.xml").getURL(), Server.class, args);
    logger.info(options.toString());

    final Config config = JaxbUtil.parse(Config.class, Resources.getResourceOrFile(options.getOption("config")).getURL());
    logger.info(JaxbUtil.toXMLString(config));

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
  protected Server(final Config config, final Class<? extends HttpServlet> ... servletClasses) throws SQLException, UnsupportedEncodingException {
    super(config.getServer().getPort(), config.getServer() instanceof Https ? ((Https)config.getServer()).getKeystore().getPath() : null, config.getServer() instanceof Https ? ((Https)config.getServer()).getKeystore().getPassword() : null, !config.getDebug().isExternalResourcesAccess(), null, servletClasses);
    this.config = config;

    from = new InternetAddress(config.getMail().getServer().getCredentials().getUsername(), config.getMail().getServer().getCredentials().getUsername());
    onServiceErrorEmail = config.getDebug().getOnServiceExceptionEmail().length() > 0 ? (String)config.getDebug().getOnServiceExceptionEmail() : null;
    mailSender = new MailSender(Mail.Protocol.valueOf(config.getMail().getServer().getProtocol().toUpperCase()), config.getMail().getServer().getHost(), config.getMail().getServer().getPort(), config.getMail().getServer().getCredentials().getUsername(), config.getMail().getServer().getCredentials().getPassword());

    final DataSource dataSource = DataSources.createDataSource(config.getDbcps().getDbcp(), "mycompany");
    Registry.registerPreparedBatching(mycompany.class, dataSource);

    EmbeddedServletContainer.setUncaughtServletExceptionHandler(new UncaughtServletExceptionHandler() {
      @Override
      public void uncaughtServletException(final ServletRequest request, final ServletResponse response, final Exception e) throws Exception {
        emailException(e);
        throw e;
      }
    });
  }

  public Config getConfig() {
    return config;
  }
}