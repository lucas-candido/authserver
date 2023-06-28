package br.pucpr.authserver.utils

import br.pucpr.authserver.orders.OrdersController
import br.pucpr.authserver.services.ServicesController
import br.pucpr.authserver.users.UsersController
import org.slf4j.LoggerFactory

class Logger {
    companion object {
        val users = LoggerFactory.getLogger(UsersController::class.java)
        val orders = LoggerFactory.getLogger(OrdersController::class.java)
        val services = LoggerFactory.getLogger(ServicesController::class.java)
    }
}