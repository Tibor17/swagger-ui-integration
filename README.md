# swagger-ui-integration
Swagger core and Swagger UI integration effortless for JavaEE application

---

[![Build Status](https://travis-ci.org/ptitbob/swagger-ui-integration.svg?branch=master)](https://travis-ci.org/ptitbob/swagger-ui-integration)

---

###swagger-ui-integration ?

swagger-ui-integration is a library that allows you to turn at the same time exposing the description of the REST API of the application via the swagger and offer access a UI (swagger-UI) using descriptive swagger thereof.

The library provides a number of default values and get 3 levels of configuration.

***annotation/configuration files properties and default values*** : 

* ```restApplication``` : classname for the class use JAS-RS ```@ApplicationPath``` : ```Void.class``` (not assigned)
* ```restApplicationPath``` : API REST path root : ```/api``` - first seek the ```@ApplicationPath``` given by ```restApplication```, if not exist, use the property ```restApplicationPath```
* ```apiDocPath``` : API documentation site : ```/api-docs```
* ```active``` : Access to the description of the REST API via Swagger : ```true```
* ```externalConfigurationFile``` Name of the property system that stores the path to the system configuration file : ```[EMPTY]``` (*need to set*)

***Configurations level*** :

1. Annotation ```@SwaggerUIConfiguration```
2. resource Configuration file ```swagger-project.properties``` allow set configuration during build
3. system configuration file store in system property. The system property'name ***must be*** set in ```externalConfigurationFile```

###Use Swagger core and Swagger UI in your project.

First add dependency to your JavaEE project : 

```xml
    <dependencies>
    	 <dependency>
            <groupId>javax</groupId>
            <artifactId>javaee-api</artifactId>
            <version>7.0</version>
        </dependency>
        <dependency>
            <groupId>org.shipstone.swagger</groupId>
            <artifactId>swagger-ui-integration</artifactId>
            <version>0.5</version>
        </dependency>
    </dependencies>
```
Add empty class with the annotations ```@RewriteConfiguration``` **and** **```@SwaggerUIConfiguration```**. Your class **must** extends ```AbstractSwaggerURLRewriter```.


```java
@RewriteConfiguration
@SwaggerUIConfiguration(
    restApplication = RestApplication.class
)
public class SwaggerURLRewriter extends AbstractSwaggerURLRewriter {
}
```
Your API REST Application class, no need to do anything else :)

```java
@ApplicationPath("api")
public class RestApplication extends Application {
}
```

And finally, on all your endpooints you want to view in swagger documentation, add specific swagger annotation. Most important is ```@Api``` ;)

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

* swagger documentation file : [http://localhost/customer/api/swagger](http://localhost/customer/api/swagger)
* swagger UI site : [http://localhost/customer/api-docs/](http://localhost/customer/api-docs/) or [http://localhost/customer/api-docs/index.html](http://localhost/customer/api-docs/index.html)

Activation or desactivation by system configuration file need to relaunch application.

have fun !
