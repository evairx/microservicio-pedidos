package com.huertabeja.pedidos

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class Pedidos {

    // GET / - Listar todos los pedidos
    @GetMapping
    suspend fun getPedidos(): ResponseEntity<List<PedidosSchema>> {
        return try {
            val pedidos = SupabaseClient.client
                .from("pedidos")
                .select()
                .decodeList<PedidosSchema>()
            ResponseEntity.ok(pedidos)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    // POST / - Crear nuevo pedido
    @PostMapping
    suspend fun crearPedido(@RequestBody pedido: PedidoRequest): ResponseEntity<PedidosSchema> {
        return try {
            val nuevoPedido = SupabaseClient.client
                .from("pedidos")
                .insert(pedido) {
                    select()
                }
                .decodeSingle<PedidosSchema>()
            ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    // GET /{id} - Obtener pedido por ID
    @GetMapping("/{id}")
    suspend fun getPedidoById(@PathVariable id: Int): ResponseEntity<PedidosSchema> {
        return try {
            val pedido = SupabaseClient.client
                .from("pedidos")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<PedidosSchema>()
            
            if (pedido != null) {
                ResponseEntity.ok(pedido)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    // GET /cliente/{clienteId} - Obtener pedidos de un cliente
    @GetMapping("/cliente/{clienteId}")
    suspend fun getPedidosByCliente(@PathVariable clienteId: String): ResponseEntity<List<PedidosSchema>> {
        return try {
            val pedidos = SupabaseClient.client
                .from("pedidos")
                .select {
                    filter {
                        eq("cliente_id", clienteId)
                    }
                }
                .decodeList<PedidosSchema>()
            ResponseEntity.ok(pedidos)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    // PUT /{id} - Actualizar estado del pedido
    @PutMapping("/{id}")
    suspend fun actualizarPedido(
        @PathVariable id: Int,
        @RequestBody actualizacion: PedidoUpdateRequest
    ): ResponseEntity<PedidosSchema> {
        return try {
            val pedidoActualizado = SupabaseClient.client
                .from("pedidos")
                .update(actualizacion) {
                    filter {
                        eq("id", id)
                    }
                    select()
                }
                .decodeSingleOrNull<PedidosSchema>()
            
            if (pedidoActualizado != null) {
                ResponseEntity.ok(pedidoActualizado)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    // DELETE /{id} - Eliminar pedido
    @DeleteMapping("/{id}")
    suspend fun eliminarPedido(@PathVariable id: Int): ResponseEntity<Unit> {
        return try {
            SupabaseClient.client
                .from("pedidos")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}

// Schema completo para respuesta (incluye id y created_at que genera Supabase)
@Serializable
data class PedidosSchema(
    val id: Int? = null,
    val cliente_id: String? = null,
    val status: String? = null,
    val direccion: String? = null,
    val total: Int? = null,
    val created_at: String? = null
)

// DTO para crear pedido (sin id ni created_at)
@Serializable
data class PedidoRequest(
    val cliente_id: String,
    val status: String = "pendiente",
    val direccion: String,
    val total: Int
)

// DTO para actualizar pedido (campos opcionales)
@Serializable
data class PedidoUpdateRequest(
    val status: String? = null,
    val direccion: String? = null,
    val total: Int? = null
)