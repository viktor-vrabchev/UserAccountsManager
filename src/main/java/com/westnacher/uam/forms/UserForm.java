package com.westnacher.uam.forms;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.westnacher.uam.InvalidMessages;

public class UserForm {
	
	
	
	private static final String FIRST_NAME_REGEX = "[A-Z][a-zA-Z]*";

	private static final String LAST_NAME_REGEX = "[a-zA-Z]+([ '-][a-zA-Z]+)*";

	private final static String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

	@NotBlank(message = InvalidMessages.NOT_BLANK_MESSAGE)
	@Size(min = 2, max = 30)
	@Pattern(regexp = FIRST_NAME_REGEX)
	private String firstName;

	@NotBlank
	@Size(min = 2, max = 30)
	@Pattern(regexp = LAST_NAME_REGEX)
	private String lastName;

	@NotBlank(message = InvalidMessages.NOT_BLANK_MESSAGE)
	@Size(min = 3, max = 30, message = InvalidMessages.SIZE_MESSAGE_MIN_AND_MAX)
	@Email(regexp = EMAIL_REGEX, message = InvalidMessages.INVALID_EMAIL_MESSAGE)
	private String email;

	@NotNull(message = InvalidMessages.NOT_NULL_MESSAGE)
	@PastOrPresent(message = InvalidMessages.PAST_OR_PRESENT_MESSAGE)
	private LocalDate dateOfBirth;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

}
