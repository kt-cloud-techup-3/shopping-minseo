package com.kt.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.domain.user.User;
import com.kt.dto.UserUpdateRequest;
import com.kt.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
	private final UserService userService;
	// 유저 리스트 조회

	//?key=value&page=1&keyword=asdasd
	// 이름에
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Page<User> search(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String keyword
	) {

		// pageable -> interface -> 구현체 : PageRequest
		// 인터페이스가 존재하면 반드시 구현체(클래스)가 있다고 약속이 되어있다.

		return userService.search(PageRequest.of(page - 1, size), keyword);

	}
	// 유저 상세 조회

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public User detail(@PathVariable Long id) {
		return userService.detail(id);
	}

	// 유저 정보 수정
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void update(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
		userService.update(id, request.name(), request.email(), request.mobile());
	}
	// 유저 삭제
	// DELETE FROM MEMBER WHERE id = ?
	// 유저 비밀번호 초기화
}
