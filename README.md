<img src="https://www.cohesionfirst.org/logo.png" align="right"/>
## jax-rs-auth-seed<br>![java-enterprise][java-enterprise] <a href="https://www.cohesionfirst.org/"><img src="https://img.shields.io/badge/CohesionFirst%E2%84%A2--blue.svg"></a>
> The complete basic auth pattern, and seed for JAX-RS.

**Why do we keep reinventing the wheel? The authentication patten has been done and done again 1,000,000 times. Instead of spending the first two weeks of development on the authentication requirement, get a jump start on implementing your business logic immediately.**

This project is countepart (the back-end component) of the [`angular-auth-seed`][angular-auth-seed] project. Together, these two projects provide a developer with a fully functional platform for authenticated applications based on AngularJS and Java's [JAX-RS v2.0 Specification][jax-rs-spec].

**Created with the highest standard of development in mind, this project uses best practices and patterns to empower the developer to write code that is clear, cohesive, and easily testable.**

This project is a server implementation of the Basic authentication methodology for RESTful applications. It is written in Java, and is 100% JAX-RS-compliant. It can be run in servers that implement the JAX-RS 2.0 specification, which includes: [Jersey][jersey], [JBoss RESTEasy][RESTeasy], [Restlet][restlet], [Apache CXF][apache-cxf], [Apache Wink][apache-wink], and more. Though the developer is free to choose his JAX-RS server vendor of choice, I recommend the [XRS][xrs] server, which is a CohesionFirst™ implementation alternative of the JAX-RS 2.0 spec. The [XRS][xrs] server is designed to be ultra light-weight, and is configured in this project to run in the [Jetty][jetty] servlet container.

The `angular-auth-seed` project is a complete solution, intended to be bug-free and an instant "plug-and-play" base to get you started fast and easy. Preconfigured to install the Angular framework, development prerequisites, and testing tools for instant web deployment gratification, this solution can be used to quickly bootstrap your Angular project and dev environment.

### Getting Started

To get you started, you can simply clone the `jax-rs-auth-seed` repository. A detailed walkthrough is described below.

#### Prerequisites

1. [git][git] - The version control system client needed to clone the `jax-rs-auth-seed` repository.
1. [Java JDK][jdk-download] - A minimum JDK and JRE of version 7 is required.
1. SMTP Server - A SMTP server is needed for `jax-rs-auth-seed` to drive the "Forgot Password" process. This requirement can be satisfied with something as simple as a GMail account, but it is recommended that a dedicated server is used in production.

#### Clone `jax-rs-auth-seed`

Clone the `jax-rs-auth-seed` repository using git:

```tcsh
git clone git@github.com:SevaSafris/jax-rs-auth-seed.git
cd jax-rs-auth-seed
```

If you want to start a new project without the `jax-rs-auth-seed` commit history, then you can do:

```tcsh
git clone --depth=1 git@github.com:SevaSafris/jax-rs-auth-seed.git <your-project-name>
```

The `depth=1` tells git to only pull down one commit worth of historical data.

##### Tools for development

1. [Maven][maven] - The workflow and dependency management system.
1. IDE - [Eclipse][eclipse] is the recommended IDE, but this project is IDE-agnostic.

##### Database

The `jax-rs-auth-seed` project uses a SQL DRBSM system for its data storage. You are free to choose a database vendor that provides a JDBC driver. In this project, we recommend [PostgreSQL][postgresql] as the database. The database must be installed, running, and initialized for the application to run. After installing your RDBMS server, you must create a database. For PostgreSQL, the following commands can be used:

```tcsh
CREATE USER mycompany WITH PASSWORD 'mycompany';
CREATE DATABASE mycompany;
GRANT ALL PRIVILEGES ON DATABASE mycompany TO mycompany;
```

**NOTE:** The name `mycompany` will be used in this guide as the generic name that you should customize yourself. To change the database name, login credentials, or server location, please refer to the `/config/dbcps/dbcp` XPath path in `src/main/resources/config.xml`.

Ensure the database is running by logging in with `psql`:

```tcsh
psql -U mycompany
```

##### Email Server

The `jax-rs-auth-seed` application relies on email to drive the "forgot password" process. It also uses email to send exception occurrences to developers, if so chosen. To allow `jax-rs-auth-seed` to send email, you must provide credential information for a SMTP server. This information is configured in `src/main/resources/config.xml` in the `/config/mail/server` XPath path. This can be your GMail account credentials, or any other dedicated SMTP server.

```xml
<config>
...
  <mail>
    <server host="smtp.gmail.com" port="465" protocol="smtps">
      <credentials username="mycompany@gmail.com" password="mypassword"/>
    </server>
  </mail>
...
</config>
```

#### Build the Application

Before starting, ensure you have satisfied the Database and Email Server requirements above.

The build system for this application is [Maven][maven]. To build a standalone executable jar:

```tcsh
mvn package
```

This will execute the lifecycle phases of the Maven build to download and install all dependencies. Upon completion, the application will be compiled and packaged to `target/server-standalone.jar`.

#### Run the Application

To run the application, simply execute the standalone server jar:

```tcsh
java -jar target/server-standalone.jar
```

This will launch the Jetty servlet container and inilialize the JAX-RS servlet with the application.

To run with remote debugging turned on, you can execute:

```tcsh
java -agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=n -jar target/server-standalone.jar 
```

Once the server is successfully running, it will be listening to REST requests at the configured service paths. For a guide on how to create the client-side application counterpart, please refer to [`angular-auth-seed`][angular-auth-seed], an AngularJS application.

### Contact

[Comments and Issues][jax-rs-auth-seed-issues]

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.

[angular-auth-seed]: https://github.com/SevaSafris/angular-auth-seed/
[angular-seed-issues]: https://github.com/SevaSafris/angular-auth-seed/issues
[apache-cxf]: http://cxf.apache.org/
[apache-wink]: https://wink.apache.org/
[eclipse]: https://eclipse.org/
[git]: https://git-scm.com/
[java-enterprise]: https://img.shields.io/badge/java-enterprise-blue.svg
[jax-rs-auth-seed-issues]: https://github.com/SevaSafris/jax-rs-auth-seed/issues
[jax-rs-spec]: http://download.oracle.com/otn-pub/jcp/jaxrs-2_0_rev_A-mrel-eval-spec/jsr339-jaxrs-2.0-final-spec.pdf
[jdk-download]: http://www.oracle.com/technetwork/java/javase/downloads
[jersey]: https://jersey.java.net/
[jetty]: http://www.eclipse.org/jetty/
[maven]: https://maven.apache.org/
[postgresql]: https://www.postgresql.org/
[RESTeasy]: http://resteasy.jboss.org/
[restlet]: https://restlet.com/
[web-container]: https://en.wikipedia.org/wiki/Web_container
[xrs]: https://github.com/SevaSafris/xrs/