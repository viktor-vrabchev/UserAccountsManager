package com.westnacher.uam.controllers;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.westnacher.uam.exceptions.AccountsManagerException;
import com.westnacher.uam.forms.UserForm;
import com.westnacher.uam.services.UserService;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("users")
@ApiModel(description = "Endpoints of users", value = "Users")
public class UserController {

	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@ApiOperation(produces = "application/json", value = "retrieve all users in JSON Array")
	@GetMapping(produces = "application/json")
	public ResponseEntity<?> getAllUsers() {
		return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
	}

	@ApiOperation(produces = "application/json", value = "retrieve a user as JSON object")
	@GetMapping(path = "/{userId}", produces = "application/json")
	public ResponseEntity<?> getUserById(@PathVariable Long userId) throws AccountsManagerException {
		return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
	}

	@ApiOperation(produces = "application/json", value = "create a user from JSON Object Form and return it as JSON Object. Restriction : cannot create user with email that is already registered.", consumes = "application/json")
	@PostMapping(produces = "application/json", consumes = "application/json")
	public ResponseEntity<?> createUser(@Valid @RequestBody UserForm userForm, BindingResult bindingResult)
			throws AccountsManagerException {
		if (bindingResult.hasFieldErrors()) {
			String body = convertFieldErrorsIntoString(bindingResult);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userForm));
	}

	@ApiOperation(produces = "application/json", value = "Edit user with id from the path. Takes JSON Object and returns JSON Object. Restriction : cannot edit email to already existing email.", consumes = "application/json")
	@PutMapping(path = "/{userId}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<?> editUser(@Valid @RequestBody UserForm form, BindingResult bindingResult,
			@PathVariable Long userId) throws AccountsManagerException {
		if (bindingResult.hasFieldErrors()) {
			String body = convertFieldErrorsIntoString(bindingResult);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
		}
		return ResponseEntity.status(HttpStatus.OK).body(userService.editUser(userId, form));
	}

	@ApiOperation(value = "Delete user with id from the path. No content is returned.")
	@DeleteMapping(path = "/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable Long userId) throws AccountsManagerException {
		userService.deleteUserById(userId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	private String convertFieldErrorsIntoString(BindingResult bindingResult) {
		String body = bindingResult.getFieldErrors().stream()
				.map(fe -> fe.getField().toUpperCase() + " : " + fe.getDefaultMessage())
				.collect(Collectors.joining(System.lineSeparator()));
		return body;
	}
}
