package com.jorge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WordController {

	@Value("${words}")
	String words;

	@RequestMapping("/")
	public @ResponseBody String getWord() {
		String[] wordArray = words.split(",");
		int i = (int) Math.round(Math.random() * (wordArray.length - 1));
		return wordArray[i];
	}
}
