package com.jorge;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/*
 * Try: http://localhost:8020/sentence and refresh to view the changes
 */

@RestController
public class SentenceController{

	//Obtaining a DiscoveryClient (import from Spring Cloud).
	@Autowired 
	private DiscoveryClient client;
	
	
	/*
	 * Add the following methods to serve the sentence based on the words obtained from the client services. 
	 * 
	 */
	@RequestMapping("/sentence") 
	public @ResponseBody String getSentence() {
		return 
			
			getWord("MICROSPRING3-EUREKA-DISCOVERY-CLIENT-SUBJECT-LAB4", "/getSubject") + " "
			+ getWord("MICROSPRING3-EUREKA-DISCOVERY-CLIENT-VERB-LAB4") + " "
			+ getWord("MICROSPRING3-EUREKA-DISCOVERY-CLIENT-ARTICLE-LAB4") + " "
			+ getWord("MICROSPRING3-EUREKA-DISCOVERY-CLIENT-ADJECTIVE-LAB4") + " "
			+ getWord("MICROSPRING3-EUREKA-DISCOVERY-CLIENT-NOUN-LAB4") + "."
			;
	}

	public String getWord(String... service) {
		List<ServiceInstance> list = client.getInstances(service[0]);
		if (list != null && list.size() > 0) {
			URI uri = list.get(0).getUri();
			if (uri != null) {
				
				String url = uri.toString();
				//Si le pasamos también el nombre del método del controlador del cliente, debemos añadírselo a la url
				//Es sólo para demostrar que se puede especificar un @RequestMapping("/getSubject") distinto
				//al @RequestMapping("/") de los demás clientes
				if(service.length > 1)
					url = url.concat(service[1]);
				
				return (new RestTemplate()).getForObject(url, String.class);
			}
		}
		return null;
	}
}
