package com.kt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.dto.UserCreateRequest;
import com.kt.dto.UserUpdatePasswordRequest;
import com.kt.service.UserService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "유저", description = "유저 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
	@ApiResponse(responseCode = "500", description = "서버 에러 - 백엔드에 바로 문의 바랍니다.")
})
public class UserController {
	// userservice를 di받아야함
	// di받는 방식이 생성자주입 씀 -> 재할당을 금지함

	private final UserService userService;


	// API 문서화는 크게 2가지의 방식이 존재
	// 1. Swagger -> 장점 UI가 이쁘다, 어노테이션 기반이라서 작성이 쉽다.
	//	단점: 프로덕션코드에 Swagger관련 어노테이션이 존재
	//	코드가 더러워지고 길어지고 그래서 유지보수가 힘듬
	// 2. RestDocs
	// 1번이랑 정반대
	// 장점 : 프로덕션 코드에 침범이 없다, 신뢰할 수 있음
	// 단점 : UI가 안이쁘다. 그리고 문서작성하는데 테스트코드 기반이라 시간이 걸림.

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	// loginId, password, name, birthday
	// json형태의 body에 담겨서 post요청으로 /users로 들어오면
	// @RequestBody를보고 jacksonObjectMapper가 동작해서 json을 읽어서 dto로 변환
	public void create(@Valid @RequestBody UserCreateRequest request) {
		userService.create(request);
	}

	// /users/duplicate-login-id?loginId=ktuser
	// IllegalArgumentException 발생 시 400에러
	// GET에서 쓰는 queryString
	// @RequestParam의 속성은 기본이 required = true
	@GetMapping("/duplicate-login-id")
	@ResponseStatus(HttpStatus.OK)
	public Boolean isDuplicateLoginId(@RequestParam String loginId) {
		return userService.isDuplicateLoginId(loginId);
	}

	//uri는 식별이 가능해야한다.
	// 유저들x , 어떤 유저?
	// /users/update-password
	// body => json으로 넣어서 보내고

	// 1. 바디에 id값을 같이 받는다
	// 2. uri에 id값을 넣는다. /users/{id}/update-password
	// 3. 인증/인가 객체에서 id값을 꺼낸다. (V)
	@PutMapping("/{id}/update-password")
	@ResponseStatus(HttpStatus.OK)
	public void updatePassword(
		@PathVariable Long id,
		@RequestBody @Valid UserUpdatePasswordRequest request
	) {
		userService.changePassword(id, request.oldPassword(), request.newPassword());
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable Long id) {
		userService.delete(id);
	}
}
