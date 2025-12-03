package com.huertabeja.pedidos

import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class Pedidos {

    @GetMapping
    suspend fun getPedidos(): List<PedidosSchema> {
        return SupabaseClient.client
            .from("pedidos")
            .select()
            .decodeList<PedidosSchema>()
    }
}

@Serializable
data class PedidosSchema(
    val cliente_id: String? = null,
    val status: String? = null,   // ← era Int, debe ser String
    val direccion: String? = null,
    val total: Int? = null
)