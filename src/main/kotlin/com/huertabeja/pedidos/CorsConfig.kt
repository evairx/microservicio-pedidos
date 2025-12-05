package com.huertabeja.pedidos

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig {

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**") // todas las rutas
                    .allowedOrigins("*")   // cualquiera puede acceder
                    .allowedMethods("*")   // GET, POST, DELETE, PUT, PATCH, etc.
                    .allowedHeaders("*")   // cualquier header
                    .allowCredentials(false)
            }
        }
    }
}
