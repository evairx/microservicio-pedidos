package com.huertabeja.pedidos

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Docs del microservicio Pedidos - API Huertabeja")
                    .version("1.0.2")
                    .description("""
                        API para los pedidos de Huertabeja.
                        
                        ## Características
                        - Consulta de pedidos
                        - Integración con Supabase
                        - Respuestas en tiempo real
                    """.trimIndent())
                    .contact(
                        Contact()
                            .name("Huertabeja")
                            .email("contact@huertabeja.com")
                            .url("https://huertabejafs.vercel.app/")
                    )
            )
    }
}