package com.example.myapplication.data.models

import com.google.gson.annotations.SerializedName

// ==================== REQUEST MODELS ====================

data class LoginRequest(
    val email: String,
    val password: String,
    @SerializedName("device_fingerprint")
    val deviceFingerprint: String? = null,
    @SerializedName("device_name")
    val deviceName: String? = null
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    @SerializedName("company_id")
    val companyId: String? = null,
    val phone: String? = null
)

data class Verify2FARequest(
    val email: String,
    val code: String,
    @SerializedName("device_fingerprint")
    val deviceFingerprint: String? = null
)

data class VerifyEmailRequest(
    val token: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val token: String,
    @SerializedName("new_password")
    val newPassword: String
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)

data class VerifyDeviceRequest(
    val token: String
)

data class RevokeDeviceRequest(
    @SerializedName("device_id")
    val deviceId: String
)

data class ResendVerificationRequest(
    val email: String,
    val type: String // "email_verification" o "device_verification"
)

// ==================== RESPONSE MODELS ====================

data class LoginResponse(
    val success: Boolean = false,
    @SerializedName("requires_2fa")
    val requires2FA: Boolean = false,
    @SerializedName("requires_device_verification")
    val requiresDeviceVerification: Boolean = false,
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("refresh_token")
    val refreshToken: String? = null,
    @SerializedName("token_type")
    val tokenType: String? = null,
    @SerializedName("expires_in")
    val expiresIn: Int? = null,
    val user: UserInfo? = null,
    val message: String? = null,
    val email: String? = null,
    @SerializedName("verification_token")
    val verificationToken: String? = null,
    @SerializedName("temp_user_id")
    val tempUserId: String? = null
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val user: UserInfo? = null,
    @SerializedName("verification_token")
    val verificationToken: String? = null
)

data class Enable2FAResponse(
    val success: Boolean,
    @SerializedName("secret_key")
    val secretKey: String,
    @SerializedName("qr_code_url")
    val qrCodeUrl: String,
    @SerializedName("backup_codes")
    val backupCodes: List<String>
)

data class RefreshTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String? = null,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("user")
    val user: UserInfo? = null
)

data class UserInfo(
    val id: String,
    val email: String,
    val name: String,
    val role: String? = null,
    @SerializedName("company_id")
    val companyId: String? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("email_verified")
    val emailVerified: Boolean = false,
    @SerializedName("two_factor_enabled")
    val twoFactorEnabled: Boolean = false
)

data class LoginAttempt(
    val id: String,
    val email: String,
    @SerializedName("ip_address")
    val ipAddress: String,
    @SerializedName("user_agent")
    val userAgent: String,
    val success: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    val location: String? = null
)

data class TrustedDevice(
    val id: String,
    @SerializedName("device_name")
    val deviceName: String,
    @SerializedName("device_fingerprint")
    val deviceFingerprint: String,
    @SerializedName("last_used")
    val lastUsed: String,
    @SerializedName("ip_address")
    val ipAddress: String,
    @SerializedName("user_agent")
    val userAgent: String,
    @SerializedName("created_at")
    val createdAt: String
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null
)

data class ErrorResponse(
    val detail: String
)

