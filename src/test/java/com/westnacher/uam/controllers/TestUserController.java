package com.westnacher.uam.controllers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.westnacher.uam.dtos.UserDto;
import com.westnacher.uam.exceptions.AccountsManagerException;
import com.westnacher.uam.forms.UserForm;
import com.westnacher.uam.models.User;
import com.westnacher.uam.services.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TestUserController {
	
	@Autowired
    private MockMvc mockMvc;
	
	@MockBean
	private UserService userService;
	
	private ObjectMapper mapper;
	
	@Before
	public void setup() {
		this.mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
	
	@Test
	public void testGetAllUsers() throws Exception {
		
		User user1 = createRandomUser();
		User user2 = createRandomUser();
		
		List<UserDto> users = new ArrayList();
		Collections.addAll(users, UserDto.of(user1), UserDto.of(user2));
		
		given(this.userService.getAllUsers()).willReturn(users);
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/users")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}
	
	@Test
	public void testGetUserById_Success() throws Exception {
		
		Long userId = RandomUtils.nextLong();
		UserDto user = UserDto.of(createRandomUser());
		user.setId(userId);
		
		given(this.userService.getUserById(Mockito.anyLong())).willReturn(user);
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/users/" + userId)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", is(userId)));
	}
	
	@Test
	public void testGetUserById_InvalidId_Fail() throws Exception {
		
		String message = RandomStringUtils.random(10, true, false);
		given(userService.getUserById(Mockito.anyLong())).willThrow(new AccountsManagerException(message, HttpStatus.BAD_REQUEST));

		mockMvc.perform(MockMvcRequestBuilders
				.get("/users/{userId}", Mockito.anyLong())
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message", is(message)));
	}
	
	@Test
	public void testCreateUser_Success() throws Exception {
		UserForm form = createValidUserForm();
		
		String json = mapper.writeValueAsString(form);
		
		UserDto expected = UserDto.of(User.of(form));
		expected.setId(RandomUtils.nextLong());
		expected.setRegisteredAt(LocalDateTime.now());
		expected.setModifiedAt(LocalDateTime.now());
		
		given(userService.createUser(Mockito.any())).willReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.firstName", is(form.getFirstName())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.lastName", is(form.getLastName())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.email", is(form.getEmail())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.dateOfBirth", startsWith(form.getDateOfBirth().toString())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.registeredAt").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.modifiedAt").isNotEmpty());
	}
	
	
	@Test
	public void testCreateUser_NullEmail_Fail() throws Exception {
		UserForm form = createValidUserForm();
		form.setEmail(null);
		
		String json = mapper.writeValueAsString(form);
		
		UserDto expected = UserDto.of(User.of(form));
		expected.setId(RandomUtils.nextLong());
		expected.setRegisteredAt(LocalDateTime.now());
		expected.setModifiedAt(LocalDateTime.now());
		
		given(userService.createUser(Mockito.any())).willReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$", containsString("EMAIL : '' must not be null or blank")));
	}
	
	@Test
	public void testCreateUser_OutOfSizeEmail_Fail() throws Exception {
		UserForm form = createValidUserForm();
		form.setEmail(RandomStringUtils.random(30, true, true) + "@test.com");
		
		String json = mapper.writeValueAsString(form);
		
		UserDto expected = UserDto.of(User.of(form));
		expected.setId(RandomUtils.nextLong());
		expected.setRegisteredAt(LocalDateTime.now());
		expected.setModifiedAt(LocalDateTime.now());
		
		given(userService.createUser(Mockito.any())).willReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$", is("EMAIL : The following String :'" + form.getEmail() +"' must be between 3 and 30 characters long")));
	}
	
	@Test
	public void testCreateUser_Email_RegexWillFail() throws Exception {
		UserForm form = createValidUserForm();
		final String invalidEmail = "invalidemail.com";
		form.setEmail(invalidEmail);

		String json = mapper.writeValueAsString(form);
		
		UserDto expected = UserDto.of(User.of(form));
		expected.setId(RandomUtils.nextLong());
		expected.setRegisteredAt(LocalDateTime.now());
		expected.setModifiedAt(LocalDateTime.now());
		
		given(userService.createUser(Mockito.any())).willReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$", containsString("EMAIL")))
				.andExpect(jsonPath("$", containsString("Invalid email")));
	}
	
	@Test
	public void testCreateUser_NullOrRegexFailNames_Fail() throws Exception {
		UserForm form = createValidUserForm();
		form.setFirstName(null);
		form.setLastName("Invalid*Name");
		
		String json = mapper.writeValueAsString(form);
		
		UserDto expected = UserDto.of(User.of(form));
		
		given(userService.createUser(Mockito.any())).willReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$", containsString("FIRSTNAME : '' must not be null or blank")))
				.andExpect(jsonPath("$", containsString("LASTNAME : must match \"[a-zA-Z]+([ '-][a-zA-Z]+)*\"")));
	}

	@Test
	public void testCreateUser_OutOfSizeNames_Fail() throws Exception {
		UserForm form = createValidUserForm();
		form.setFirstName("A");
		final String invalidName = StringUtils.capitalize(RandomStringUtils.random(35, true, false).toLowerCase());
		form.setLastName(invalidName);
		
		String json = mapper.writeValueAsString(form);
		
		UserDto expected = UserDto.of(User.of(form));
		
		given(userService.createUser(Mockito.any())).willReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$", containsString("FIRSTNAME : size must be between 2 and 30")))
				.andExpect(jsonPath("$", containsString("LASTNAME : size must be between 2 and 30")));
	}
	
	@Test
	public void testCreateUser_FutureDateOfBirth_Fail() throws Exception {
		UserForm form = createValidUserForm();
		form.setDateOfBirth(LocalDate.MAX);
		
		String json = mapper.writeValueAsString(form);
		
		UserDto expected = UserDto.of(User.of(form));
		
		given(userService.createUser(Mockito.any())).willReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$", containsString("must be a date in the past or in the present")));
	}	
	
	@Test
	public void testCreateUser_NullDateOfBirth_Fail() throws Exception {
		UserForm form = createValidUserForm();
		form.setDateOfBirth(null);
		
		String json = mapper.writeValueAsString(form);
		
		UserDto expected = UserDto.of(User.of(form));
		
		given(userService.createUser(Mockito.any())).willReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/users")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$", containsString("DATEOFBIRTH : '' must not be null")));
	}
	
	@Test
	public void testEditUser_Success() throws Exception {
		UserForm form = createValidUserForm();
		Long userId = RandomUtils.nextLong();
		
		String json = mapper.writeValueAsString(form);
		
		UserDto expected = UserDto.of(User.of(form));
		
		given(userService.editUser(Mockito.anyLong(), Mockito.any())).willReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.put("/users/{userId}", userId)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.firstName", is(form.getFirstName())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.lastName", is(form.getLastName())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.email", is(form.getEmail())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.dateOfBirth", startsWith(form.getDateOfBirth().toString())));				
	}
	
	@Test
	public void testEditUser_InvalidEmail_Fail() throws Exception {
		UserForm form = createValidUserForm();
		form.setEmail("THIS_STRING_WONT_PASS_THE_EMAIL_VALIDATION");
		Long userId = RandomUtils.nextLong();
		
		String json = mapper.writeValueAsString(form);
		
		UserDto expected = UserDto.of(User.of(form));
		
		given(userService.editUser(userId, form)).willReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.put("/users/{userId}", userId)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$", containsString("EMAIL")));
	}
	
	@Test
	public void testEditUser_OutOfSizeNames_Fail() throws Exception {
		UserForm form = createValidUserForm();
		String firstName = StringUtils.capitalize(RandomStringUtils.random(35, true, false));
		form.setFirstName(firstName);
		form.setLastName("A");
		Long userId = RandomUtils.nextLong();
		
		String json = mapper.writeValueAsString(form);
		
		UserDto expected = UserDto.of(User.of(form));
		
		given(userService.editUser(userId, form)).willReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.put("/users/{userId}", userId)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$", containsString("FIRSTNAME : size must be between 2 and 30")))
				.andExpect(jsonPath("$", containsString("LASTNAME : size must be between 2 and 30")));
	}
	
	@Test
	public void testEditUser_NullOrRegexFailNames_Fail() throws Exception {
		UserForm form = createValidUserForm();
		form.setFirstName(null);
		final String invalidName = "invalid*name";
		form.setLastName(invalidName);
		Long userId = RandomUtils.nextLong();
		
		String json = mapper.writeValueAsString(form);
		
		UserDto expected = UserDto.of(User.of(form));
		
		given(userService.editUser(userId, form)).willReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.put("/users/{userId}", userId)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$", containsString("FIRSTNAME : '' must not be null or blank")))
				.andExpect(jsonPath("$", containsString("LASTNAME : must match \"[a-zA-Z]+([ '-][a-zA-Z]+)*\"")));
	}
	
	@Test
	public void testEditUser_FutureDateOfBirth_Fail() throws Exception {
		UserForm form = createValidUserForm();
		form.setDateOfBirth(LocalDate.MAX);
		Long userId = RandomUtils.nextLong();
		
		String json = mapper.writeValueAsString(form);
		
		UserDto expected = UserDto.of(User.of(form));
		
		given(userService.editUser(userId, form)).willReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.put("/users/{userId}", userId)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$", containsString("must be a date in the past or in the present")));
	}
	
	@Test
	public void testEditUser_NullDateOfBirth_Fail() throws Exception {
		UserForm form = createValidUserForm();
		form.setDateOfBirth(null);
		Long userId = RandomUtils.nextLong();
		
		String json = mapper.writeValueAsString(form);
		
		UserDto expected = UserDto.of(User.of(form));
		
		given(userService.editUser(userId, form)).willReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.put("/users/{userId}", userId)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$", containsString("DATEOFBIRTH : '' must not be null")));
	}
	
	@Test
	public void testEditUser_InvalidId_Fail() throws Exception {
		UserForm form = createValidUserForm();
		
		String json = mapper.writeValueAsString(form);
		
		String message = RandomStringUtils.random(10, true, false);
		given(userService.editUser(Mockito.anyLong(), Mockito.any())).willThrow(new AccountsManagerException(message, HttpStatus.BAD_REQUEST));
		
		Long randomId = RandomUtils.nextLong();
		mockMvc.perform(MockMvcRequestBuilders
				.put("/users/{userId}", randomId.toString())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message", is(message)));
	}
	
	@Test
	public void testDeleteUser_Success() throws Exception {
		
		Long userId = RandomUtils.nextLong();
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/users/{userId}", userId.toString()))
					.andDo(print())
					.andExpect(status().isNoContent());
	}
	
	@Test
	public void testDeleteUser_InvalidId_Fail() throws Exception {
		
		Long userId = RandomUtils.nextLong();
		String message = RandomStringUtils.random(10, true, false);
		
		doThrow(new AccountsManagerException(message, HttpStatus.BAD_REQUEST)).when(userService).deleteUserById(Mockito.anyLong());
		
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/users/{userId}", userId.toString()))
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message", is(message)));
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
