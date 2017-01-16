package com.jorge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WordController {

	@Value("${words}")//These words are got from application.yml (or application.properties)
	String words;

	@RequestMapping("/getSubject") //We are calling this method from SentenceController.java, getSentence() method
	public @ResponseBody String getWord() {
		String[] wordArray = words.split(",");
		int i = (int) Math.round(Math.random() * (wordArray.length - 1));
		return wordArray[i];
	}
	

	@RequestMapping("/")
	public @ResponseBody String getWordFail() {
		return "Sentence Client will never get this method"; //Check SentenceController.java, getSentence() method to know why
	}
}
