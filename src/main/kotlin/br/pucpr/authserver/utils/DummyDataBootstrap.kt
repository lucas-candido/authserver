package br.pucpr.authserver.utils

import br.pucpr.authserver.services.Service
import br.pucpr.authserver.services.ServicesRepository
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class DummyDataBootstrap(
        val servicesRepository: ServicesRepository
) : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {

        val oleo = Service(name = "Troca de Óleo", price = 250.00)
        val alinhamento = Service(name = "Alinhamento e Balanceamento", price = 250.00)
        val funilaria = Service(name = "Funilaria", price = 800.00)
        val pintura = Service(name = "Funilaria", price = 3000.00)

        if (servicesRepository.count() < 4L) {
            servicesRepository.save(oleo)
            servicesRepository.save(alinhamento)
            servicesRepository.save(funilaria)
            servicesRepository.save(pintura)
        }
    }
}