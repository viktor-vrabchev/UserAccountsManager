package com.westnacher.uam.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.westnacher.uam.models.User;

public class UserDto {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private LocalDate dateOfBirth;
	private LocalDateTime registeredAt;
	private LocalDateTime modifiedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public LocalDateTime getRegisteredAt() {
		return registeredAt;
	}

	public void setRegisteredAt(LocalDateTime registeredAt) {
		this.registeredAt = registeredAt;
	}

	public LocalDateTime getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(LocalDateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public static UserDto of(User user) {
		UserDto result = new UserDto();

		result.setId(user.getId());
		result.setFirstName(user.getFirstName());
		result.setLastName(user.getLastName());
		result.setEmail(user.getEmail());
		result.setDateOfBirth(user.getDateOfBirth());
		result.setRegisteredAt(user.getRegisteredAt());
		result.setModifiedAt(user.getModifiedAt());

		return result;
	}
}
