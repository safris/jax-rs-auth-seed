package com.mycompany.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class CorsFilter implements ContainerResponseFilter {
  private static final List<String> allowMethods = Arrays.asList(new String[] {HttpMethod.HEAD, HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.OPTIONS});
  private static final List<String> allowHeaders = Arrays.asList(new String[] {"Access-Control-Allow-Origin", "Authorization", "Origin", "x-requested-with", "Content-Type", "Content-Range", "Content-Disposition", "Content-Description"});

  @Override
  public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
    responseContext.getStringHeaders().putSingle("Access-Control-Allow-Origin", "*");
    if (HttpMethod.OPTIONS.equals(requestContext.getMethod())) {
      responseContext.getStringHeaders().put(HttpHeaders.ALLOW, allowMethods);
      responseContext.getStringHeaders().put("Access-Control-Allow-Methods", allowMethods);
      responseContext.getStringHeaders().put("Access-Control-Allow-Headers", allowHeaders);

      requestContext.abortWith(Response.ok().build());
    }
  }
}