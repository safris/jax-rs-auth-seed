package com.mycompany;

import com.mycompany.filter.AuthFilter;
import com.mycompany.filter.CorsFilter;
import org.libx4j.jjb.rs.JSObjectReader;
import org.libx4j.jjb.rs.JSObjectWriter;

import javax.ws.rs.ApplicationPath;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/*")
public class Application extends javax.ws.rs.core.Application {
  @Override
  public Set<Object> getSingletons() {
    final Set<Object> singletons = new HashSet<Object>();
    singletons.add(new JSObjectReader());
    singletons.add(new JSObjectWriter());
    singletons.add(new CorsFilter());
    singletons.add(new AuthFilter());
    return singletons;
  }
}