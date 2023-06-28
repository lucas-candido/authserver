package br.pucpr.authserver.orders.requests

import jakarta.validation.constraints.NotNull

data class OrdersRequest(
        @field:NotNull
        val userId: Long?,

        @field:NotNull
        val serviceId: Long?,
)