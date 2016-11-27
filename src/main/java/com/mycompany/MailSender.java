package com.mycompany;

import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.safris.commons.lang.Throwables;
import org.safris.commons.net.mail.Mail;
import org.safris.commons.net.mail.Mail.Protocol;
import org.safris.commons.net.mail.MimeContent;
import org.safris.commons.security.Credentials;

public class MailSender {
  private static final Logger logger = Logger.getLogger(MailSender.class.getName());

  private final Mail.Sender sender;
  private final Credentials credentials;

  public MailSender(final Protocol protocol, final String host, final int port, final String username, final String password) {
    sender = Mail.Sender.instance(protocol, host, port);
    credentials = new Credentials(username, password);
  }

  private void send(final String type, final InternetAddress from, final String to, final String subject, final String message) {
    try {
      sender.send(credentials, new Mail.Message(subject, new MimeContent(message, type), from, to));
    }
    catch (final MessagingException e) {
      logger.throwing(getClass().getName(), e.getMessage(), e);
    }
  }

  public void send(final InternetAddress from, final String to, final String subject, final String message) {
    send("text/html", from, to, subject, message);
  }

  public void send(final InternetAddress from, final String to, final Throwable t) {
    send("text/plain", from, to, t.getClass().getSimpleName(), Throwables.toString(t));
  }
}