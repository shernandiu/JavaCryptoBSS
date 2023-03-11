package util;

import java.util.regex.Pattern;

public class PasswordStrength {
	private static final double LOWER_POINTS = 10;
	private static final double UPPER_POINTS = 15;
	private static final double NUMBER_POINTS = 15;
	private static final double PUNCT_POINTS = 20;

	private static final Pattern upper = Pattern.compile("\\p{Upper}");
	private static final Pattern lower = Pattern.compile("\\p{Lower}");
	private static final Pattern number = Pattern.compile("\\d");
	private static final Pattern punct = Pattern.compile("\\p{Punct}");

	public static int check_strength(char[] password_array) {
		String password = new String(password_array);

		int size = password_array.length;

		int number_upper = (int) upper.matcher(password).results().count();
		int number_lower = (int) lower.matcher(password).results().count();
		int number_number = (int) number.matcher(password).results().count();
		int number_punct = (int) punct.matcher(password).results().count();

		return (int) (number_lower * LOWER_POINTS / 10 + (number_lower > 0 ? LOWER_POINTS : 0) +
				number_upper * UPPER_POINTS / 10 + (number_upper > 0 ? UPPER_POINTS : 0) +
				number_number * NUMBER_POINTS / 10 + (number_number > 0 ? NUMBER_POINTS : 0) +
				number_punct * PUNCT_POINTS / 10 + (number_punct > 0 ? PUNCT_POINTS : 0));
	}
}
