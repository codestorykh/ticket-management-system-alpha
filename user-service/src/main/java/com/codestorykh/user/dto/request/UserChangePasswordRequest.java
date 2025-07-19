package com.codestorykh.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserChangePasswordRequest(
		@NotBlank(message = "Current password cannot be blank")
		@Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
		String password,

		@NotBlank(message = "New password cannot be blank")
		@Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
		@Pattern(
				regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
				message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace"
		)
		String newPassword,

		@NotBlank(message = "Confirm password cannot be blank")
		@Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
		String confirmPassword
) {
	public boolean passwordsMatch() {
		return newPassword != null && newPassword.equals(confirmPassword);
	}

	public boolean isNewPasswordDifferent() {
		return !password.equals(newPassword);
	}
}
