# swagger-ui-integration
Swagger core and Swagger UI integration effortless for Java EE application

---

[![Build Status](https://travis-ci.org/ptitbob/swagger-ui-integration.svg?branch=master)](https://travis-ci.org/ptitbob/swagger-ui-integration)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.shipstone/swagger-ui-integration/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.shipstone/swagger-ui-integration)

---

### swagger-ui-integration ?

swagger-ui-integration is a library that allows you to turn at the same time exposing the description of the REST API of the application via the swagger and offer access a UI (swagger-UI) using descriptive swagger thereof.

swagger lib linked : 

* swaggerUI : 2.1.4
* swagger-core : 1.5.8

demo : [examples how to use swagger-ui-integration lib on github](https://github.com/ptitbob/swagger-ui-integration-test)

Since version 0.8, you can reach the library from [maven central repository](http://mvnrepository.com/artifact/org.shipstone/swagger-ui-integration). 

###Use Swagger core and Swagger UI in your project.

The library provides a number of default values and get 3 levels of configuration.

***Configurations path - order of configuration items reading*** :

1. Annotation ```@SwaggerUIConfiguration```
2. resource Configuration file ```swagger-project.properties``` allow set configuration during package building.
3. system configuration file store in system property. The system property'name ***must be*** set in ```systemPropertyForExternalConfigurationFilename ```

***annotation properties and default values*** : 

* ```active``` : Access to the description of the REST API via Swagger and activate swagger description.
    * *default value : ```true```*
* ```configurationFilename``` : Default resource file store swagger-ui-integration configuration
    * *default value : ```swagger-project.properties```*
* ```systemPropertyForExternalConfigurationFilename``` : property system name storing the full path to the external configuration file for swagger-ui-integration.
    * *default value was empty, if you want a external configuration name, you* ***must set*** *one*.
* ```host``` : your default host. if it is not assigned, it will be determined in the first call.
* ```restApplicationPackageAsRoot``` : use the class use JAX-RS ```@ApplicationPath``` package. If false, you must set ```restApplicationPackage```
    * *default value : ```true```*
* ```restApplicationPackage``` : Base package REST application - used only if the restApplicationClass was undefined.
    * by default empty, the process take class path from ```@SwaggerUIConfiguration``` annoted class package.
* ```apiDocPath``` : API documentation sub-path
    * *default value : ```/api-docs```*
* ```apiDocIndex``` : resource fully qualified filename for replace default index.html used by.
    * *default value : not fixed - use default swagger-ui-integration index.html (hide url field)*.
    * *see below for more explaination for your own index file*

***configuration files properties and default values*** : 

All property must used prefixe ```swagger.``` (i.e. ```swagger.apiDocPath```). You can store your swagger properties in overall project configuration file.

* ```swagger.active``` : Access to the description of the REST API via Swagger and activate swagger description.
* ```swagger.systemPropertyForExternalConfigurationFilename``` : property system name storing the full path to the external configuration file for swagger-ui-integration.
* ```swagger.host``` : your default host. if it is not assigned, it will be determined in the first call.
* ```swagger.restApplicationClass``` : class wear ```@ApplicationPath``` JAX-RS annotation.
* ```swagger.restApplicationPackageAsRoot``` : use the class use JAX-RS ```@ApplicationPath``` package. If false, you must set ```swagger.restApplicationPackage```
* ```swagger.restApplicationPath``` : Base path for REST application - used only if the restApplicationClass was undefined.
* ```swagger.restApplicationPackage``` : Base package REST application - used only if the restApplicationClass was undefined.
* ```swagger.apiDocPath``` : API documentation sub-path
* ```swagger.apiDocIndex``` : resource fully qualified filename for replace default index.html used by.


***how use it***

First add dependency to your JavaEE project : 

```xml
    <dependencies>
        ...
        <dependency>
            <groupId>org.shipstone</groupId>
            <artifactId>swagger-ui-integration</artifactId>
            <version>1.0</version>
        </dependency>
        ...
    </dependencies>
```
Add empty class with the annotation **```@SwaggerUIConfiguration```**.

```java
@SwaggerUIConfiguration
public class SwaggerURLRewriter  {
}
```
Your API REST Application class, no need to do anything else :)

```java
@ApplicationPath("api")
public class RestApplication extends Application {
}
```

And finally, on all your endpoints you want to view in swagger documentation, add specific swagger annotation. Most important is ```@Api``` ;)

```java
@Path("person")
@Api(value = "Personne")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN})
public class PersonEndpoint {

  @GET
  @ApiOperation(value = "Liste des personnes")
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  public List<Person> getPersonList() {
    ...
    some stuff
    ...
  }

}
```

### Use 

If you use only default configuration, the library give you two url (i.e. use previous exemple and context (*project final name*) as ```customer``` in localhost server : 

* swagger documentation file : [http://localhost/customer/api/swagger](http://localhost/customer/api/swagger) (according the ```@ApplicationPath``` value.
* swagger UI site : [http://localhost/customer/api-docs/](http://localhost/customer/api-docs/) or [http://localhost/customer/api-docs/index.html](http://localhost/customer/api-docs/index.html)

Activation or desactivation by system configuration file need to relaunch application.

have fun !

---

####ToDo

* [x] remove org.ocpsoft.rewrite dependencies - version 0.9
* [x] use webfragment - Version 0.9
* [x] ensure compatibility java EE 6 - version 1.0-RC1
* [ ] split the library into 2 sub-module (api & core) (? *useful* ?) - version 1.0+
* [ ] allow to replace embedded site with a external site - version 1.0+
* [ ] remove support Java EE 6 and Java 1.7, switch to Java EE 7 and Java 1.8 - version 2.0+


