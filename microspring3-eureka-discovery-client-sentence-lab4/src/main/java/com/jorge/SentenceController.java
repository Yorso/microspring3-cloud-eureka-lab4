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

	@Autowired 
	private DiscoveryClient client;
	
	@RequestMapping("/sentence") 
	public @ResponseBody String getSentence() {
		return 
			
			getWord("MICROSPRING3-EUREKA-DISCOVERY-CLIENT-SUBJECT-LAB4") + " "
			+ getWord("MICROSPRING3-EUREKA-DISCOVERY-CLIENT-VERB-LAB4") + " "
			+ getWord("MICROSPRING3-EUREKA-DISCOVERY-CLIENT-ARTICLE-LAB4") + " "
			+ getWord("MICROSPRING3-EUREKA-DISCOVERY-CLIENT-ADJECTIVE-LAB4") + " "
			+ getWord("MICROSPRING3-EUREKA-DISCOVERY-CLIENT-NOUN-LAB4") + "."
			;
	}

	public String getWord(String service) {
		List<ServiceInstance> list = client.getInstances(service);
		if (list != null && list.size() > 0) {
			URI uri = list.get(0).getUri();
			if (uri != null) {
				return (new RestTemplate()).getForObject(uri, String.class);
			}
		}
		return null;
	}
}
