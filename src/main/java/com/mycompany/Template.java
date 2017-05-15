package com.mycompany;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.text.BadLocationException;

import org.safris.commons.io.Streams;
import org.safris.commons.lang.Resource;
import org.safris.commons.lang.Resources;
import org.safris.commons.lang.Strings;
import org.safris.commons.net.URLs;

import com.mycompany.jaxrsauthseed.config.xe.$cf_config;
import com.mycompany.jaxrsauthseed.config.xe.cf_config;

public final class Template {
  private static final Map<String,String> defaultProperties = new HashMap<String,String>();

  private static void loadDirectory(final List<String> entries, final File directory) {
    final File[] files = directory.listFiles();
    for (final File file : files) {
      if (file.isDirectory())
        loadDirectory(entries, file);
      else
        entries.add(file.getName());
    }
  }

  private static final Map<String,String> nameToTemplate = new HashMap<String,String>();

  static {
    final String templateRoot = "template";
    final Resource templates = Resources.getResource(templateRoot);
    final URL url = templates.getURL();
    final String decodedUrl = URLs.decode(url);
    final File directory = new File(decodedUrl);
    final List<String> entries = new ArrayList<String>();
    if (directory.exists()) {
      loadDirectory(entries, directory);
    }
    else {
      final JarURLConnection jarURLConnection;
      final JarFile jarFile;
      try {
        jarURLConnection = (JarURLConnection)url.openConnection();
        jarFile = jarURLConnection.getJarFile();
      }
      catch (final IOException e) {
        throw new ExceptionInInitializerError(e);
      }

      final Enumeration<JarEntry> enumeration = jarFile.entries();
      while (enumeration.hasMoreElements()) {
        final String entry = enumeration.nextElement().getName();
        if (entry.startsWith(templateRoot + "/"))
          entries.add(entry);
      }
    }

    if (entries.size() != 0) {
      try {
        for (final String entry : entries) {
          try (final InputStream in = Resources.getResource(entry).getURL().openStream()) {
            final String data = new String(Streams.readBytes(in));
            nameToTemplate.put(entry.substring(templateRoot.length() + 1), data);
          }
        }

        Strings.interpolate(nameToTemplate, "<include template=\"", "\"></include>");
      }
      catch (final BadLocationException | IOException | ParseException e) {
        throw new ExceptionInInitializerError(e);
      }
    }

    final cf_config config = Server.instance().getConfig();
    if (!config._properties(0).isNull())
      for (final $cf_config._properties._property property : config._properties(0)._property())
        defaultProperties.put(property._name$().text(), property._value$().text());

    try {
      Strings.interpolate(defaultProperties, "{{", "}}");
    }
    catch (final BadLocationException | ParseException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  public static String getTemplate(final String name, final Map<String,String> properties) throws BadLocationException, ParseException {
    properties.putAll(defaultProperties);
    Strings.interpolate(properties, "{{", "}}");
    final String template = nameToTemplate.get(name);
    return template == null ? null : Strings.interpolate(template, properties, "{{", "}}");
  }

  public static String getTemplate(final String name) throws BadLocationException, ParseException {
    return getTemplate(name, null);
  }

  private Template() {
  }
}