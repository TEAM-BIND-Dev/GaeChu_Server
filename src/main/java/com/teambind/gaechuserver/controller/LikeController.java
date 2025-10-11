package com.teambind.gaechuserver.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@Slf4j
@RequiredArgsConstructor
public class LikeController {
	
	//TODO impl this
	@PostMapping("/{categoryId}/{referenceId}")
	public void like(@PathVariable(required = true) Long categoryId, @PathVariable(required = true) String referenceId, @RequestParam(required = true) String likerId, @RequestParam(required = true) boolean isLike) {
	}
	
	//TODO impl this
	@GetMapping("/detail/{categoryId}/{referenceId}")
	public void getLikeAccount(@PathVariable(required = true) Long categoryId, @PathVariable(required = true) String referenceId) {
	}
	
	//TODO impl this
	@GetMapping("/count/{categoryId}")
	public void getLikeAccount(@PathVariable(required = true) Long categoryId, @RequestParam(required = true) List<String> referenceIds) {
	}
	
	
}
