package com.example.myapplication.data.network

import com.example.myapplication.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<ApiResponse<Unit>>

    @POST("auth/verify-2fa")
    suspend fun verify2FA(@Body request: Verify2FARequest): Response<LoginResponse>

    @POST("auth/verify-device")
    suspend fun verifyDevice(@Body request: VerifyDeviceRequest): Response<ApiResponse<Unit>>

    @POST("auth/resend-verification")
    suspend fun resendVerification(@Body request: ResendVerificationRequest): Response<ApiResponse<Unit>>

    @POST("auth/enable-2fa")
    suspend fun enable2FA(@Header("Authorization") token: String): Response<Enable2FAResponse>

    @POST("auth/disable-2fa")
    suspend fun disable2FA(@Header("Authorization") token: String): Response<ApiResponse<Unit>>

    @POST("auth/request-password-reset")
    suspend fun requestPasswordReset(@Body request: ForgotPasswordRequest): Response<ApiResponse<Unit>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<Unit>>

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<ApiResponse<Unit>>

    @GET("auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<UserInfo>

    @GET("auth/login-history")
    suspend fun getLoginHistory(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<LoginAttempt>>>

    @GET("auth/trusted-devices")
    suspend fun getTrustedDevices(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<TrustedDevice>>>

    @DELETE("auth/trusted-devices/{device_id}")
    suspend fun revokeTrustedDevice(
        @Path("device_id") deviceId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>
}

