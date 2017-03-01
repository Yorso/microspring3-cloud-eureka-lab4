##Lab 4 - Create a Spring Cloud Eureka Server and Client 
We will create several client applications that will work together to compose a sentence. The sentence will contain a subject, verb, article, adjective and noun such as “I saw a leaky boat” or “You have the reasonable book”. 5 services will randomly generate the word components, and a 6th service will assemble them into a sentence.

Several servers
primary, secondary, and tertiary illustrate running 3 intercommunicating instances.  This example has them running
side-by-side on localhost -- which is unrealistic in production -- but does illustrate how multiple instances collaborate.
Run by opening 3 separate command prompts:
```
java -jar -Dspring.profiles.active=primary lab-4-eureka-server-solution-1.jar
java -jar -Dspring.profiles.active=secondary lab-4-eureka-server-solution-1.jar
java -jar -Dspring.profiles.active=tertiary lab-4-eureka-server-solution-1.jar
```

If you run server as always, you are running a default server (hhtp://localhost:8010/eureka)

Add in /etc/hosts:
```
START section for Microservices with Spring Course
  127.0.0.1       eureka-primary
  127.0.0.1       eureka-secondary
  127.0.0.1       eureka-tertiary
END section for Microservices with Spring Course
```

**Part 1, create server**

1. Create a new Spring Boot application.  Name the project "lab-4-eureka-server”, and use this value for the Artifact.  Use JAR packaging and the latest versions of Java.  Use a version of Boot < 1.3.x.  No need to select any dependencies.

2. Edit the POM (or gradle) file.  Alter the parent group Id to be "org.springframework.cloud" and artifact to be "spring-cloud-starter-parent".  Version Camden.SR2 is the most recent stable version at the time of this writing. 

3. Add a dependency for group "org.springframework.cloud" and artifact "spring-cloud-starter-eureka-server".  You do not need to specify a version -- this is already defined in the parent project.  

4. Save an application.yml (or properties) file in the root of your classpath (src/main/resources recommended).  Add the following key / values (use correct YAML formatting):
  - server.port: 8010

5. (optional) Save a bootstrap.yml (or properties) file in the root of your classpath.  Add the following key / values (use correct YAML formatting):
  - spring.application.name=lab-4-eureka-server

6. Add @EnableEurekaServer to the Application class.  Save.  Start the server.  Temporarily ignore the warnings about running a single instance (i.e. connection refused, unable to refresh cache, backup registry not implemented, etc.).  Open a browser to [http://localhost:8010](http://localhost:8010) to see the server running.

    **Part 2, create clients**  
    
    In this next section we will create several client applications that will work together to compose a sentence.  The sentence will contain a subject, verb, article, adjective and noun such as “I saw a leaky boat” or “You have the reasonable book”.  5 services will randomly generate the word components, and a 6th service will assemble them into a sentence.

7. Create a new Spring Boot web application.  Use a version of Boot < 1.3.x.  Name the application “lab-4-subject”, and use this value for the Artifact.  Use JAR packaging and the latest versions of Java and Boot.  Add actuator and web as a dependencies.

8. Modify the POM (or Gradle) file:  
  - Alter the parent group Id to be "org.springframework.cloud" and artifact to be "spring-cloud-starter-parent".  Version Camden.SR2 is the most recent stable version at the time of this writing. 
  - Add a dependency for group "org.springframework.cloud" and artifact "spring-cloud-starter-eureka".

9. Modify the Application class.  Add @EnableDiscoveryClient.

10. Save an application.yml (or properties) file in the root of your classpath (src/main/resources recommended).  Add the following key / values (use correct YAML formatting):
  - eureka.client.serviceUrl.defaultZone=http://localhost:8010/eureka/
  - words=I,You,He,She,It
  - server.port=${PORT:${SERVER_PORT:0}}
(this will cause a random, unused port to be assigned if none is specified)

11. Save a bootstrap.yml (or properties) file in the root of your classpath.  Add the following key / values (use correct YAML formatting):
  - spring.application.name=lab-4-subject

12. Add a Controller class
  - Place it in the 'demo' package or a subpackage of your choice.
  - Name the class anything you like.  Annotate it with @RestController.
  - Add a String member variable named “words”.  Annotate it with @Value("${words}”).
  - Add the following method to serve the resource (optimize this code if you like):
  ```
    @RequestMapping("/")
    public @ResponseBody String getWord() {
      String[] wordArray = words.split(",");
      int i = (int)Math.round(Math.random() * (wordArray.length - 1));
      return wordArray[i];
    }
  ```

13. Repeat steps 7 thru 12 (copy the entire project if it is easier), except use the following values:
  - Name of application: “lab-4-verb”
  - spring.application.name: “lab-4-verb”
  - words: “ran,knew,had,saw,bought”

14. Repeat steps 7 thru 12, except use the following values:
  - Name of application: “lab-4-article”
  - spring.application.name: “lab-4-article”
  - words: “a,the”

15. Repeat steps 7 thru 12, except use the following values:
  - Name of application: “lab-4-adjective”
  - spring.application.name: “lab-4-adjective”
  - words: “reasonable,leaky,suspicious,ordinary,unlikely”

16. Repeat steps 7 thru 12, except use the following values:
  - Name of application: “lab-4-noun”
  - spring.application.name: “lab-4-noun”
  - words: “boat,book,vote,seat,backpack,partition,groundhog”

17. Create a new Spring Boot web application.  Name the application “lab-4-sentence”, and use this value for the Artifact.  Use JAR packaging and the latest versions of Java and Boot.  Add actuator and web as a dependencies.  Alter the POM (or Gradle) dependencies just as you did in step 8. 

18. Add @EnableDiscoveryClient to the Application class.  

19. Save an application.yml (or properties) file in the root of your classpath (src/main/resources recommended).  Add the following key / values (use correct YAML formatting):
  - eureka.client.serviceUrl.defaultZone=http://localhost:8010/eureka/
  - server.port: 8020

20. Add a Controller class to assemble and return the sentence:
  - Name the class anything you like.  Annotate it with @RestController.
  - Use @Autowired to obtain a DiscoveryClient (import from Spring Cloud).
  - Add the following methods to serve the sentence based on the words obtained from the client services. (feel free to optimize / refactor this code as you like:
  ```
    @RequestMapping("/sentence")
    public @ResponseBody String getSentence() {
      return 
        getWord("LAB-4-SUBJECT") + " "
        + getWord("LAB-4-VERB") + " "
        + getWord("LAB-4-ARTICLE") + " "
        + getWord("LAB-4-ADJECTIVE") + " "
        + getWord("LAB-4-NOUN") + "."
        ;
    }
    
    public String getWord(String service) {
      List<ServiceInstance> list = client.getInstances(service);
      if (list != null && list.size() > 0 ) {
        URI uri = list.get(0).getUri();
	if (uri !=null ) {
	  return (new RestTemplate()).getForObject(uri,String.class);
	}
      }
      return null;
    }
  ```

21. Run all of the word services and sentence service.  (Run within your IDE, or build JARs for each one (mvn clean package) and run from the command line (java -jar name-of-jar.jar), whichever you find easiest).  (If running from STS, uncheck “Enable Live Bean support” in the run configurations).  Since each service uses a separate port, they should be able to run side-by-side on the same computer.  Open [http://localhost:8020/sentence](http://localhost:8020/sentence) to see the completed sentence.  Refresh the URL and watch the sentence change.

**BONUS - Refactor to use multiple Eureka Servers**  
    
  To make the application more fault tolerant, we can run multiple Eureka servers.  Ordinarily we would run copies on different racks / data centers, but to simulate this locally do the following:

29.  Stop all of the running applications.

30.  Edit your computer's /etc/hosts file (c:\WINDOWS\system32\drivers\etc\hosts on Windows).  Add the following lines and save your work:

  ```
  # START section for Microservices with Spring Course
  127.0.0.1       eureka-primary
  127.0.0.1       eureka-secondary
  127.0.0.1       eureka-tertiary
  # END section for Microservices with Spring Course
  ```

31.  Within the lab-4-server project, add application.yml with multiple profiles:
primary, secondary, tertiary.  The server.port value should be 8011, 8012, and 8013 respectively.  The eureka.client.serviceUrl.defaultZone for each profile should point to the "eureka-*" URLs of the other two; for example the primary value should be: http://eureka-secondary:8012/eureka/,http://eureka-tertiary:8013/eureka/

32.  Run the application 3 times, using -Dspring.profiles.active=primary (and secondary, and tertiary) to activate the relevant profile.  The result should be 3 Eureka servers which communicate with each other.

33.  In your GitHub project, modify the application.properties eureka.client.serviceUrl.defaultZone to include the URIs of all three Eureka servers (comma-separated, no spaces).

34.  Start all clients.  Open [http://localhost:8020/sentence](http://localhost:8020/sentence) to see the completed sentence.

35.  To test Eureka’s fault tolerance, stop 1 or 2 of the Eureka instances.  Restart 1 or 2 of the clients to ensure they have no difficulty finding Eureka.  Note that it may take several seconds for the clients and servers to become fully aware of which services are up / down.  Make sure the sentence still displays.

##Info

- [x] **[Microservices with Spring Cloud (Udemy)](https://www.udemy.com/microservices-with-spring-cloud/learn/v4/overview)** :link:

- [x] **Instructor: [Ken Krueger, Technical Instructor in Software Development topics](https://linkedin.com/in/ken-krueger-43670111)** :link:
