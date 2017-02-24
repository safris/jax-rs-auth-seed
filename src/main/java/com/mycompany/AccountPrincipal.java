package com.mycompany;

import java.security.Principal;

import org.safris.dbx.jsql.mycompany;

public class AccountPrincipal implements Principal {
  private final mycompany.Account account;

  public AccountPrincipal(final mycompany.Account account) {
    this.account = account;
  }

  public mycompany.Account getAccount() {
    return account;
  }

  @Override
  public String getName() {
    return account.email.get();
  }
}