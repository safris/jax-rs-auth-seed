package com.mycompany;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

import org.safris.jjb.rs.JSObjectBodyReader;
import org.safris.jjb.rs.JSObjectBodyWriter;

@ApplicationPath("/*")
public class Application extends javax.ws.rs.core.Application {
  @Override
  public Set<Object> getSingletons() {
    final Set<Object> singletons = new HashSet<Object>();
    singletons.add(new JSObjectBodyReader());
    singletons.add(new JSObjectBodyWriter());
    return singletons;
  }
}