package com.hoangnt.controller;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hoangnt.config.TokenProvider;
import com.hoangnt.model.AccountDTO;
import com.hoangnt.model.UserDTO;
import com.hoangnt.service.UserService;

@Transactional
@RestController
public class UserController {
	@Autowired
	UserService userService;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private TokenProvider tokenProvider;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/users/all")
	public ResponseEntity<?> getAllUser() {
		if (userService.getAllUser() != null) {
			return new ResponseEntity<List<UserDTO>>(userService.getAllUser(), HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'CUSTOMER')")
	@GetMapping("users/{username}")
	public ResponseEntity<?> getUserByUserName(@PathVariable String username) {
		if (userService.findByUsername(username) != null) {
			return new ResponseEntity<UserDTO>(userService.findByUsername(username), HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
	@DeleteMapping("users/delete/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable int id) {
		userService.deleteUser(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'CUSTOMER')")
	@PutMapping("users/update")
	public ResponseEntity<Void> updateUser(@RequestBody UserDTO userDTO) {

		userService.updateUser(userDTO);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PostMapping("users/register")
	public ResponseEntity<Void> addUser(@RequestBody UserDTO userDTO) {

		userService.addUser(userDTO);
		return new ResponseEntity<Void>(HttpStatus.OK);

	}

	@PostMapping("users/login")
	public ResponseEntity<?> login(@RequestBody AccountDTO accountDTO) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(accountDTO.getUsername(), accountDTO.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		System.out.println(authentication.getAuthorities());
		return new ResponseEntity<String>(tokenProvider.generateToken(authentication), HttpStatus.OK);
	}
}
