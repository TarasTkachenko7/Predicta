package com.predicta.app.data.remote

import com.predicta.app.data.remote.dto.CreateTaskRequestDto
import com.predicta.app.data.remote.dto.CreateTaskResponseDto
import com.predicta.app.data.remote.dto.EmployeeAnalyticsDto
import com.predicta.app.data.remote.dto.EmployeeDetailsDto
import com.predicta.app.data.remote.dto.HealthResponseDto
import com.predicta.app.data.remote.dto.LoginRequestDto
import com.predicta.app.data.remote.dto.LoginResponseDto
import com.predicta.app.data.remote.dto.ManagerDto
import com.predicta.app.data.remote.dto.MessageResponseDto
import com.predicta.app.data.remote.dto.ProjectStatusDto
import com.predicta.app.data.remote.dto.ReassignTaskRequestDto
import com.predicta.app.data.remote.dto.ReassignTaskResponseDto
import com.predicta.app.data.remote.dto.RegisterRequestDto
import com.predicta.app.data.remote.dto.TeamInsightsDto
import com.predicta.app.data.remote.dto.TeamVelocityEmployeeDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PredictaApi {

    @GET("health")
    suspend fun getHealth(): Response<HealthResponseDto>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto,
    ): Response<MessageResponseDto>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto,
    ): Response<LoginResponseDto>

    @GET("api/auth/me")
    suspend fun getCurrentManager(): Response<ManagerDto>

    @GET("api/project/status")
    suspend fun getProjectStatus(): Response<ProjectStatusDto>

    @GET("api/team/velocity")
    suspend fun getTeamVelocity(): Response<List<TeamVelocityEmployeeDto>>

    @GET("api/team/insights")
    suspend fun getTeamInsights(): Response<TeamInsightsDto>

    @GET("api/employee/{id}")
    suspend fun getEmployee(
        @Path("id") id: String,
    ): Response<EmployeeDetailsDto>

    @GET("api/employee/{id}/analytics")
    suspend fun getEmployeeAnalytics(
        @Path("id") id: String,
    ): Response<EmployeeAnalyticsDto>

    @POST("api/tasks/create")
    suspend fun createTask(
        @Body request: CreateTaskRequestDto,
    ): Response<CreateTaskResponseDto>

    @POST("api/tasks/reassign")
    suspend fun reassignTask(
        @Body request: ReassignTaskRequestDto,
    ): Response<ReassignTaskResponseDto>
}
