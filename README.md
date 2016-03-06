# swagger-ui-integration
Swagger core and Swagger UI integration effortless for JavaEE application

---

[![Build Status](https://travis-ci.org/ptitbob/swagger-ui-integration.svg?branch=master)](https://travis-ci.org/ptitbob/swagger-ui-integration)

---

###swagger-ui-integration ?

swagger-ui-integration is a library that allows you to turn at the same time exposing the description of the REST API of the application via the swagger and offer access a UI (swagger-UI) using descriptive swagger thereof.

swagger lib linked : 

* swaggerUI : 2.1.8-M1
* swagger-core : 1.5.7

###Use Swagger core and Swagger UI in your project.

The library provides a number of default values and get 3 levels of configuration.

***Configurations path*** :

1. Annotation ```@SwaggerUIConfiguration```
2. resource Configuration file ```swagger-project.properties``` allow set configuration during build
3. system configuration file store in system property. The system property'name ***must be*** set in ```externalConfigurationFile```

***annotation files properties and default values*** : 

* ```configurationFilename``` : Default resource file store swagger-ui-integration configuration
    * *default value : ```swagger-project.properties```*
* ```systemPropertyForExternalConfigurationFilename``` : property system name storing the full path to the external configuration file for swagger-ui-integration.
    * *default value was empty, if you want a external configuration name, you* ***must set*** *one*.
* ```restApplicationClass``` : classname for the class use JAS-RS ```@ApplicationPath```
    * *default value : ```Void.class``` (not assigned)*
* ```restApplicationPath``` : API REST path root, first seek the ```@ApplicationPath``` given by ```restApplication```, if not exist, use the property ```restApplicationPath```.
    * *default value : ```/api```*
* ```apiDocPath``` : API documentation sub-path
    * *default value : ```/api-docs```*
* ```apiDocIndex``` : resource fully qualified filename for replace default index.html used by.
    * *default value : not fixed - use default swagger-ui-integration index.html (hide url field)*.
    * *see below for more explaination for your own index file*
* ```active``` : Access to the description of the REST API via Swagger and activate swagger description.
    * *default value : ```true```*

***configuration files properties and default values*** : 

All property must used prefixe ```swagger.``` (i.e. ```swagger.apiDocPath```). You can store your swagger properties in overall project configuration file.

***how use it***

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

* swagger documentation file : [http://localhost/customer/api/swagger](http://localhost/customer/api/swagger) (according the ```@ApplicationPath``` value.
* swagger UI site : [http://localhost/customer/api-docs/](http://localhost/customer/api-docs/) or [http://localhost/customer/api-docs/index.html](http://localhost/customer/api-docs/index.html)

Activation or desactivation by system configuration file need to relaunch application.

have fun !
