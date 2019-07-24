package com.hoangnt.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hoangnt.entity.Account;
import com.hoangnt.entity.Role;
import com.hoangnt.entity.User;
import com.hoangnt.model.AccountDTO;
import com.hoangnt.model.UserDTO;
import com.hoangnt.repository.UserRepository;
import com.hoangnt.service.UserService;

@Transactional
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	public List<UserDTO> getAllUser() {
		List<UserDTO> userDTOs = new ArrayList<UserDTO>();
		userRepository.findAll().forEach(user -> {
			UserDTO userDTO = new UserDTO();
			userDTO.setId(user.getId());
			AccountDTO accountDTO = new AccountDTO();
			accountDTO.setUsername(user.getAccount().getUsername());
			accountDTO.setPassword(user.getAccount().getPassword());
			userDTO.setEmail(user.getEmail());
			userDTO.setAccountDTO(accountDTO);
			userDTO.setRole_id(user.getRole().getId());

			userDTOs.add(userDTO);

		});
		return userDTOs;
	}

	public UserDTO findByUsername(String username) {
		User user = userRepository.findByUsername(username);
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setUsername(user.getAccount().getUsername());
		accountDTO.setPassword(user.getAccount().getPassword());
		userDTO.setEmail(user.getEmail());
		userDTO.setAccountDTO(accountDTO);
		userDTO.setRole_id(user.getRole().getId());

		return userDTO;
	}

	public void addUser(UserDTO userDTO) {
		User user = new User();
		Account account = new Account();
		user.setEmail(userDTO.getEmail());
		account.setUsername(userDTO.getAccountDTO().getUsername());
		account.setPassword(encoder.encode(userDTO.getAccountDTO().getPassword()));
		user.setAccount(account);
		user.setRole(new Role(userDTO.getRole_id()));

		userRepository.save(user);

	}

	public void updateUser(UserDTO userDTO) {
		Optional<User> userOptional = userRepository.findById(userDTO.getId());
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			Account account = new Account();
			user.setEmail(userDTO.getEmail());
			account.setUsername(userDTO.getAccountDTO().getUsername());
			account.setPassword(encoder.encode(userDTO.getAccountDTO().getPassword()));
			user.setAccount(account);
			user.setRole(new Role(userDTO.getRole_id()));
			userRepository.save(user);
		}

	}

	public void deleteUser(int id) {
		userRepository.deleteById(id);

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username");
		}
		return new org.springframework.security.core.userdetails.User(username, user.getAccount().getPassword(),
				getAuthority(user));
	}

	private Set<SimpleGrantedAuthority> getAuthority(User user) {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
		return authorities;
	}

}
