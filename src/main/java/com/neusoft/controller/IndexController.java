package com.neusoft.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.model.User;

@Controller
public class IndexController {
	@RequestMapping(path = { "/" })
	@ResponseBody
	public String index() {
		return "hello";
	}

	@RequestMapping(path = { "/vm" }, method = { RequestMethod.GET })
	public String template(Model model) {
		model.addAttribute("value1", "vvvv1");

		List<String> colors = Arrays.asList(new String[] { "red", "green", "black" });
		model.addAttribute("colors", colors);

		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < 4; i++) {
			map.put(String.valueOf(i), String.valueOf(i * i));
		}

		model.addAttribute("map", map);
		model.addAttribute("user", new User("lisi"));
		return "home";
	}
}
