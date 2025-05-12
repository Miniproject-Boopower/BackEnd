package likelion.mini.team1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import likelion.mini.team1.service.CrawlingService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1/crawling")
@RequiredArgsConstructor
public class CrawlingController {

	@Autowired
	private final CrawlingService crawlingService;

	@PostMapping("assignment/after-today/{studentNum}")
	ResponseEntity<?> crawlAssignmentAftertoday(@PathVariable String studentNum) throws Exception {
		crawlingService.saveSubjectAndAssignmentFirst(studentNum);
		return null;
	}

	@PostMapping("assignment/all/{studentNum}")
	ResponseEntity<?> crawlAssignmentAll(@PathVariable String studentNum) throws Exception {
		crawlingService.saveSubjectAndAssignmentAll(studentNum);
		return null;
	}

	@PostMapping("/course/{studentNum}")
	ResponseEntity<?> crawlCourse(@PathVariable String studentNum) throws Exception {
		crawlingService.crawlCourse(studentNum);
		return null;
	}

}
