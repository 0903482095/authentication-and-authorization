package com.hoangnt.service;

import java.util.List;

import com.hoangnt.model.UserDTO;

public interface UserService {

	List<UserDTO> getAllUser();

	UserDTO findByUsername(String username);

	void addUser(UserDTO userDTO);

	void updateUser(UserDTO userDTO);

	void deleteUser(int id);

}
