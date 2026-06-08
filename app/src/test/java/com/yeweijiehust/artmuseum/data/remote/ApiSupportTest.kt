package com.yeweijiehust.artmuseum.data.remote

import com.google.common.truth.Truth.assertThat
import com.yeweijiehust.artmuseum.domain.model.AppFailure
import org.junit.Test
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ApiSupportTest {
    @Test
    fun invalidCredentialsRemainDistinctFromExpiredSession() {
        val invalidCredentials = mapApiFailure(
            401,
            ApiErrorDto("INVALID_CREDENTIALS", "Invalid email or password"),
            "Unauthorized"
        )
        val expiredSession = mapApiFailure(
            401,
            ApiErrorDto("UNAUTHORIZED", "Authentication required"),
            "Unauthorized"
        )

        assertThat(invalidCredentials).isEqualTo(
            AppFailure.Api("INVALID_CREDENTIALS", "Invalid email or password")
        )
        assertThat(expiredSession).isEqualTo(AppFailure.Unauthorized)
    }

    @Test
    fun actionableApiCodesRemainAvailableToThePresentationLayer() {
        val emailExists = mapApiFailure(409, ApiErrorDto("EMAIL_EXISTS", "Already exists"), "Conflict")
        val storageFailure = mapApiFailure(502, ApiErrorDto("STORAGE_FAILURE", "Upload failed"), "Bad Gateway")

        assertThat(emailExists).isEqualTo(AppFailure.Api("EMAIL_EXISTS", "Already exists"))
        assertThat(storageFailure).isEqualTo(AppFailure.Api("STORAGE_FAILURE", "Upload failed"))
    }

    @Test
    fun statusOnlyFailuresUseActionableCategories() {
        assertThat(mapApiFailure(429, null, "Too Many Requests")).isEqualTo(AppFailure.RateLimited)
        assertThat(mapApiFailure(503, null, "Unavailable")).isEqualTo(AppFailure.ServerUnavailable)
        assertThat(mapApiFailure(504, null, "Gateway Timeout")).isEqualTo(AppFailure.Timeout)
    }

    @Test
    fun networkExceptionsUseActionableCategories() {
        assertThat(mapNetworkFailure(SocketTimeoutException())).isEqualTo(AppFailure.Timeout)
        assertThat(mapNetworkFailure(UnknownHostException())).isEqualTo(AppFailure.Unreachable)
        assertThat(mapNetworkFailure(ConnectException())).isEqualTo(AppFailure.Unreachable)
        assertThat(mapNetworkFailure(IOException())).isEqualTo(AppFailure.Offline)
    }
}
