package com.westnacher.uam.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.westnacher.uam.dtos.UserDto;
import com.westnacher.uam.exceptions.AccountsManagerException;
import com.westnacher.uam.forms.UserForm;
import com.westnacher.uam.models.User;
import com.westnacher.uam.repositories.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserServiceTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Test
	public void getAllUsers_Success() {

		List<User> users = new ArrayList();
		User user1 = createRandomUser();
		User user2 = createRandomUser();
		Collections.addAll(users, user1, user2);

		List<UserDto> expected = users.stream().map(UserDto::of).collect(Collectors.toList());

		given(userRepository.findAll()).willReturn(users);

		assertThat(userService.getAllUsers()).isInstanceOf(List.class).hasSize(2);

	}

	@Test
	public void getUserById_Success() {
		User user = createRandomUser();
		Long userId = 1l;
		user.setId(userId);
		Optional<User> opt = Optional.of(user);

		given(userRepository.findById(userId)).willReturn(opt);

		assertThat(userService.getUserById(userId)).isNotNull().isInstanceOf(UserDto.class);
		assertThat(userService.getUserById(userId).getId()).isEqualTo(userId);

	}

	@Test
	public void getUserById_Fail() {
		Long id = RandomUtils.nextLong();
		given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());

		try {
			userService.getUserById(id);
		} catch (AccountsManagerException ex) {
			assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(ex.getMessage())
					.isEqualTo("Attempting to get User with id :" + id.toString() + " failed. User was not found.");
			return;
		}
		Assertions.fail();
	}

	@Test
	public void testCreateUser_Success() {
		UserForm form = createValidUserForm();
		User createdUser = User.of(form);
		Long userId = RandomUtils.nextLong();
		createdUser.setId(userId);

		given(userRepository.findByEmail(Mockito.anyString())).willReturn(Optional.empty());
		given(userRepository.save(Mockito.any())).willReturn(createdUser);

		assertThat(userService.createUser(form).getId()).isEqualTo(userId);
		assertThat(userService.createUser(form).getEmail()).isEqualTo(form.getEmail());
		assertThat(userService.createUser(form).getFirstName()).isEqualTo(form.getFirstName());
		assertThat(userService.createUser(form).getLastName()).isEqualTo(form.getLastName());
		assertThat(userService.createUser(form).getDateOfBirth()).isEqualTo(form.getDateOfBirth());
	}

	@Test
	public void testCreateUser_AlreadyRegisteredEmail_Fail() {
		UserForm form = createValidUserForm();

		Optional<User> opt = Optional.of(createRandomUser());

		given(userRepository.findByEmail(Mockito.anyString())).willReturn(opt);

		try {
			userService.createUser(form);
		} catch (AccountsManagerException ex) {
			assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(ex.getMessage()).isEqualTo("This email (" + form.getEmail() + ") is already registered");
			return;
		}
		Assertions.fail();
	}

	@Test
	public void testEditUser_Success() {
		UserForm form = createValidUserForm();
		User foundUser = User.of(form);
		Long userId = RandomUtils.nextLong();
		foundUser.setId(userId);

		given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.of(foundUser));
		given(userRepository.findByEmail(Mockito.anyString())).willReturn(Optional.empty());
		given(userRepository.saveAndFlush(Mockito.any())).willReturn(foundUser);

		assertThat(userService.editUser(userId, form).getId()).isEqualTo(userId);
		assertThat(userService.editUser(userId, form).getEmail()).isEqualTo(form.getEmail());
		assertThat(userService.editUser(userId, form).getFirstName()).isEqualTo(form.getFirstName());
		assertThat(userService.editUser(userId, form).getLastName()).isEqualTo(form.getLastName());
		assertThat(userService.editUser(userId, form).getDateOfBirth()).isEqualTo(form.getDateOfBirth());
	}

	@Test
	public void testEditUser_AlreadyRegisteredEmail_Fail() {
		UserForm form = createValidUserForm();
		Long userId = RandomUtils.nextLong();

		given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.of(createRandomUser()));
		given(userRepository.findByEmail(Mockito.anyString())).willReturn(Optional.of(createRandomUser()));

		try {
			userService.editUser(userId, form);
		} catch (AccountsManagerException ex) {
			assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(ex.getMessage()).isEqualTo("User with email " + form.getEmail() + " is already registered");
			return;
		}
		Assertions.fail();
	}

	@Test
	public void testEditUser_CannotFindId_Fail() {
		UserForm form = createValidUserForm();
		Long userId = RandomUtils.nextLong();

		given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());

		try {
			userService.editUser(userId, form);
		} catch (AccountsManagerException ex) {
			assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(ex.getMessage()).isEqualTo(
					"Attempting to edit User with id :" + userId.toString() + " failed. User was not found.");
			return;
		}
		Assertions.fail();
	}

	@Test
	public void testDeleteUserById_CannotFindId_Fail() {
		Long userId = RandomUtils.nextLong();

		given(userRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());

		try {
			userService.deleteUserById(userId);
		} catch (AccountsManagerException ex) {
			assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(ex.getMessage()).isEqualTo(
					"Attempting to delete User with id :" + userId.toString() + " failed. User was not found.");
			return;
		}
		Assertions.fail();
	}

	private User createRandomUser() {
		User user = new User();
		user.setDateOfBirth(generateRandomPastLocalDate());
		user.setFirstName(generateRandomName());
		user.setLastName(generateRandomName());
		user.setEmail(generateRandomEmail());
		return user;
	}

	private UserForm createValidUserForm() {
		UserForm form = new UserForm();
		form.setDateOfBirth(generateRandomPastLocalDate());
		form.setFirstName(generateRandomName());
		form.setLastName(generateRandomName());
		form.setEmail(generateRandomEmail());
		return form;
	}

	private String generateRandomName() {
		String randomNameString = RandomStringUtils.random(8, true, false).toLowerCase();
		return StringUtils.capitalize(randomNameString);
	}

	private String generateRandomEmail() {
		String randomEmailString = RandomStringUtils.random(6, true, true);
		String randomDomainString = RandomStringUtils.random(3, true, false).toLowerCase() + "."
				+ RandomStringUtils.random(3, true, false).toLowerCase();
		return randomEmailString + "@" + randomDomainString;
	}

	private LocalDate generateRandomPastLocalDate() {
		int randomYear = RandomUtils.nextInt(1900, LocalDate.now().getYear());
		int randomMonth = RandomUtils.nextInt(1, 12);
		int randomDay = RandomUtils.nextInt(1, Month.of(randomMonth).maxLength());

		return LocalDate.of(randomYear, randomMonth, randomDay);
	}

}
