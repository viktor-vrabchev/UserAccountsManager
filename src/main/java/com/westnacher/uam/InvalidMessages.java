package com.westnacher.uam;

public class InvalidMessages {
	
	public static final String NOT_BLANK_MESSAGE = "'${validatedValue}' must not be null or blank";
	public static final String NOT_NULL_MESSAGE = "'${validatedValue}' must not be null";
	public static final String SIZE_MESSAGE_MIN = "The following String : '${validatedValue}' must be at least  {min} characters long";
	public static final String SIZE_MESSAGE_MAX = "The following String: '${validatedValue}' must be up to {max} characters long";
	public static final String SIZE_MESSAGE_MIN_AND_MAX = "The following String :'${validatedValue}' must be between {min} and {max} characters long";
	public static final String PAST_OR_PRESENT_MESSAGE = "'${validatedValue}' must be a date in the past or in the present";
	public static final String PATTERN_MESSAGE ="'${validatedValue}' must match {regexp}";
	public static final String INVALID_EMAIL_MESSAGE = "Invalid email :'${validatedValue}'.Email can contain DIGITS(0 - 9), lowercase and uppercase LATIN LETTERS(a-z and A-Z),  PRINTABLE CHARACTERS(-!#$%&’*+-/=?^_`{|}~) and DOT('.') as long as is not inital or final character and is NOT used consecutively. Domain of the email can contain : DIGITS(0-9), lowercase and uppercase LATIN LETTERS(a-z and A-Z) and HYPHEN or DOT as long as they are not inital or final character";

}
