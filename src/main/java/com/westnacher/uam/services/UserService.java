package com.westnacher.uam.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.westnacher.uam.dtos.UserDto;
import com.westnacher.uam.exceptions.AccountsManagerException;
import com.westnacher.uam.forms.UserForm;
import com.westnacher.uam.models.User;
import com.westnacher.uam.repositories.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<UserDto> getAllUsers() {
		return userRepository.findAll().stream().map(UserDto::of).collect(Collectors.toList());
	}

	public UserDto getUserById(Long id) throws AccountsManagerException {
		return UserDto.of(userRepository.findById(id)
				.orElseThrow(() -> new AccountsManagerException(
						"Attempting to get User with id :" + id.toString() + " failed. User was not found.",
						HttpStatus.BAD_REQUEST)));
	}

	public UserDto createUser(UserForm form) throws AccountsManagerException {
		if (userRepository.findByEmail(form.getEmail()).isPresent()) {
			throw new AccountsManagerException("This email (" + form.getEmail() + ") is already registered",
					HttpStatus.BAD_REQUEST);
		}
		User newUser = User.of(form);
		User savedUser = userRepository.save(newUser);
		return UserDto.of(savedUser);
	}

	public UserDto editUser(Long id, UserForm form) throws AccountsManagerException {
		User foundUser = userRepository.findById(id)
				.orElseThrow(() -> new AccountsManagerException(
						"Attempting to edit User with id :" + id.toString() + " failed. User was not found.",
						HttpStatus.BAD_REQUEST));
		if (userRepository.findByEmail(form.getEmail()).isPresent()
				&& !userRepository.findByEmail(form.getEmail()).get().equals(foundUser)) {
			throw new AccountsManagerException("User with email " + form.getEmail() + " is already registered",
					HttpStatus.BAD_REQUEST);
		}
		foundUser.setFirstName(form.getFirstName());
		foundUser.setLastName(form.getLastName());
		foundUser.setEmail(form.getEmail());
		foundUser.setDateOfBirth(form.getDateOfBirth());

		return UserDto.of(userRepository.saveAndFlush(foundUser));
	}

	public void deleteUserById(Long id) throws AccountsManagerException {
		userRepository.findById(id)
				.orElseThrow(() -> new AccountsManagerException(
						"Attempting to delete User with id :" + id.toString() + " failed. User was not found.",
						HttpStatus.BAD_REQUEST));
		userRepository.deleteById(id);
	}

}
