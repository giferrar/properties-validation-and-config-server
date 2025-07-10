# Advanced Properties Configuration

In this Workshop we will see some techniques for a better management of the properties inside an application, including validation and the possibility to change them at runtime.

## 1. Validation, optimization and logging

### 1.1. @Value Annotation vs @ConfigurationProperties Annotation

Often in an application the properties are injected using the `@Value` annotation. This annotation is used to inject a value from a property source directly in a field.

```java
    @Value("${my.property:default_value}")
    private String myProperty;
```

The problem with this approach is:
- Lack of scalability: the more the application grows, the more injected values are scattered in the codebase.
- There is no validation of the injected values, possibly leading to errors in the application during runtime.
- The use of default values can lead to unexpected behavior if the property is not set.
- If the values of properties comes from the CI/CD pipeline, a mistake could mean the need for a new deployment.
- It is not possible to change the values at runtime, for every change we will incur in downtime.

To solve these problems we can use the `@ConfigurationProperties` annotation. This annotation is used to inject a value from a configuration file or environment variable directly in a bean.

```java
@Configuration // Or otherwise @EnableConfigurationProperties(MyProperties.class) in the main class to bind the properties in the POJO
@ConfigurationProperties(prefix = "my")
public class MyProperties {
    private String property;
    
    public String getProperty() {
        return property;
    }
    
    public void setProperty(String property) {
        this.property = property;
    }
}
```

In this way all properties with the prefix `my.` will be injected in the `MyProperties` bean. 
It is possible to create multiple classes with different prefixes, but it is recommended to keep them in the same package.

#### ✍️ Exercise 1
- Create a class using the `@ConfigurationProperties` annotation.
- Modify the class `ClientController` to use the newly created class for the properties instead of the `@Value` annotation.
- Run the application and call the `/info` endpoint and see if it works as expected.

### 1.2 Using Record and Jakarta Validation annotations

Now our properties for the application are organized in a central place, solving the problem of scalability, but we still have to solve the problem of validation and the boilerplate code from the getters and setters.
To achieve this we will first modify the class we created and transform it in a record.

Moreover, it is possible to use the JSR-303 Annotations for bean validation.
First of all we will need the following dependencies in the pom.xml:

```xml
    <!-- https://mvnrepository.com/artifact/jakarta.validation/jakarta.validation-api -->
    <dependency>
        <groupId>jakarta.validation</groupId>
        <artifactId>jakarta.validation-api</artifactId>
        <version>3.1.1</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.hibernate.validator/hibernate-validator -->
    <dependency>
        <groupId>org.hibernate.validator</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>9.0.1.Final</version>
    </dependency>
```

The resulting class will look something like this:

```java
@Validated // The object will be validated at creation
@ConfigurationProperties(prefix = "my")
public record MyProperties(
        @NotEmpty
        String property
) {
}
```

#### ✍️ Exercise 2
- Add the required dependencies for validation.
- Modify the class you created for the properties and transform it in a record.
- Add the `@Validated` annotation to the class. 
- The parameter `number` must be greater than 0 and smaller than 100. The parameter `name` must not be empty or null. `tags` must have a size between 0 and 5.
- Run the application and see if it works as expected.
- Try to assign an invalid value to a property and see what happens during application startup.

### 1.3 Logging properties at Application Ready Event

To be able to identify mistakes in the application properties that cannot be identified by the validation, we want to be able to log the current values at startup to check them manually.

We will create a Component with an EventListener that will log our properties record.

```java
    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        log.info("Application is ready. Properties are: {}", myProperties.toString());
    }
```

#### ✍️ Exercise 3
- Create the Component `CustomEventListeners` with an EventListener that will log our properties record as shown below.
- Run the application and see if the log entry is displayed.

## 2. Spring Cloud Config Server

Spring Cloud Config Server is used in distributed systems to create a composite environment repository. 
This allows to have a centralized logical repository to manage all properties for all client applications and for all environments.

<img src="https://jaehun2841.github.io/2022/03/10/2022-03-11-spring-cloud-config/spring-cloud-config1.png" alt="drawing" style="width:600px;"/>

The Config Server can manage the properties from different sources, like:
- Local properties file
- Environment variables
- Git repositories
- Other Remote/Cloud repositories
- Databases

The communication between clients and server could be also secured, but this is out of the scope of this workshop.

In our case the module `config-server` will be used to manage the properties for our application `client-app`.
We will use properties files to manage the values, that is called `native` configuration.

Take some time to look the code of the module `config-server`. Simple, isn't it?

When we will start the Client application, it will communicate with the Config Server to get the values of the properties.
In this call it will be passing the application name and profile. 
The config server will search for the properties in a file with name `{applicationName}-{profile}.properties`.

### 2.1. Configuring the client application

To configure the client application to use the config server, we will set the following dependencies: 

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-context</artifactId>
            <version>4.3.0</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-client</artifactId>
            <version>4.3.0</version>
        </dependency>
```

And this property:

    spring.config.import=configserver:http://localhost:8888/

The URL of the config server is often set in tutorials as optional. In our case not. The Client app will not work without a running Config Server.

⚠️ The properties refresh we will implement later on, will not work if the properties Bean is a record, since records are immutable. Change it back to a class!

#### ✍️ Exercise 4
- Modify the client app to use the config server.
- Change the record to a class and eliminate boilerplate code with Lombok annotations.
- Run the Config Server and the Client App and see if there is a communication with the config server and if the values from the server are logged.

### 2.2. Properties refresh during runtime

To communicate with the Config Server at startup is not enough for us. 
Now, to be able to refresh the properties Bean during runtime we will have to:
1) Add the annotation `@RefreshScope` to the class Bean. 
2) Enable the actuator with the dependency:
   
       <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
       </dependency>
3) Add the following properties to the client application:
   
       management.endpoint.refresh.enabled=true
       management.endpoints.web.exposure.include=refresh

This will expose the `/actuator/refresh` endpoint. By calling it the client application will perform a call to the Config Server to refresh the properties.
The Bean with the `@RefreshScope` annotation will be cleared and a new instance will be created.

#### ✍️ Exercise 5
- Add the annotation `@RefreshScope` to the class Bean and the properties as described above.
- Start the Config Server and the Client App.
- Modify the value of the properties in the Config Server and restart it.
- Create a new HTTP client that perform the POST call to the `/actuator/refresh` endpoint.
- Call the actuator endpoint and verify that the values are updated.

⚠️ The annotation `@RefreshScope` is not necessary if the class includes the annotations `@ConfigurationProperties`!

### 2.3. Logging properties at Refresh Scope Event
During a refresh it is not displayed in log the which values the new properties possess. 
To be able to log the value of the properties during a refresh, we will add the following listener:

```java
    @EventListener(RefreshScopeRefreshedEvent.class)
    public void runAfterScopeRefresh() {
        log.info("Scope got refreshed. Properties changed: {}", myProperties.toString());
    }
```

#### ✍️ Exercise 6
- Addd the listener as described above.
- Repeat the steps from Exercise 5.
- Verify the new properties are logged during the refresh.

💡 Lastly the validation of properties should be replicated or moved to the Config Server, in order to reduce the rish of a client application gracefully shutting down due to an invalid configuration.

## 3. 💬 Summary
In this lab we have seen how to use and validate Spring Boot Properties and how to use Spring Cloud Config Server to manage the properties of a distributed system.
We can refresh the properties of a client application at runtime and log the values of the properties.

Questions? Observations? Comments?

Thank you for attending the workshop!

## 4. 📚 Resources
- [Stop Using @Value In Spring Boot 3](https://medium.com/hacking-hunter/stop-using-value-in-spring-boot-3-cb9041c2edc5)
- [Jakarta Validation](https://jakarta.ee/specifications/bean-validation/3.1/apidocs/jakarta/validation/constraints/package-summary)
- [Application Events and Listeners](https://docs.spring.io/spring-boot/docs/2.1.6.RELEASE/reference/html/boot-features-spring-application.html#boot-features-application-events-and-listeners)
- [Spring Cloud Config](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#_quick_start)
- [Dynamic Configuration Properties in Spring Boot and Spring Cloud](https://gist.github.com/dsyer/a43fe5f74427b371519af68c5c4904c7)