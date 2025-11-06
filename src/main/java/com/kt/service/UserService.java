package com.kt.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.domain.user.User;
import com.kt.dto.UserCreateRequest;
import com.kt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
	// private final UserJDBCRepository userJDBCRepository; 추억속으로
	private final UserRepository userRepository;

	// 트랜잭션 처리해줘
	// PSA - Portable Service Abstraction
	// 환경설정을 살짝 바꿔서 일관된 서비스를 제공하는 것
	public void create(UserCreateRequest request) {
			var newUser = new User(
				request.loginId(),
				request.password(),
				request.name(),
				request.email(),
				request.mobile(),
				request.gender(),
				request.birthday(),
				LocalDateTime.now(),
				LocalDateTime.now()
			);

			userRepository.save(newUser);
	}

	public boolean isDuplicateLoginId(String loginId) {
		return userRepository.existsByLoginId(loginId);
	}

	public void changePassword(Long id, String oldPassword, String password) {
		var user = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

		if(!user.getPassword().equals(oldPassword)) {
			throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
		}

		if(oldPassword.equals(password)) {
			throw new IllegalArgumentException("기존 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.");
		}

		user.changePassword(password);
	}

	// Pageable 인터페이스
	public Page<User> search(Pageable pageable, String keyword) {
		return userRepository.findAllByNameContaining(keyword, pageable);
	}

	public User detail(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
	}

	public void update(Long id, String name, String email, String mobile) {
		var user = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

		user.update(name, email, mobile);
	}

	public void delete(Long id) {
		userRepository.deleteById(id);
		// 삭제에는 두가지 개념 - softdelete, harddelete
		// var user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
		// userRepository.delete(user);
	}
}
