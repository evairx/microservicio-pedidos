package com.huertabeja.pedidos

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class Pedidos {

    // Health check endpoint
    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("status" to "ok", "service" to "pedidos"))
    }

    // Test Supabase connection
    @GetMapping("/test")
    fun testSupabase(): ResponseEntity<Map<String, Any>> = runBlocking {
        return@runBlocking try {
            val result = SupabaseClient.client
                .from("pedidos")
                .select()
                .decodeList<PedidosSchema>()
            ResponseEntity.ok(mapOf(
                "status" to "success",
                "count" to result.size,
                "message" to "Conexión exitosa con Supabase"
            ))
        } catch (e: Exception) {
            println("Error en test Supabase: ${e.message}")
            e.printStackTrace()
            ResponseEntity.ok(mapOf(
                "status" to "error",
                "error" to (e.message ?: "Error desconocido"),
                "type" to e::class.simpleName!!
            ))
        }
    }

    // GET / - Listar todos los pedidos
    @GetMapping
    fun getPedidos(): ResponseEntity<List<PedidosSchema>> = runBlocking {
        return@runBlocking try {
            val pedidos = SupabaseClient.client
                .from("orders")
                .select()
                .decodeList<PedidosSchema>()
            ResponseEntity.ok(pedidos)
        } catch (e: Exception) {
            println("Error al obtener pedidos: ${e.message}")
            e.printStackTrace()
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    // POST / - Crear nuevo pedido
    @PostMapping
    fun crearPedido(@RequestBody pedido: PedidoRequest): ResponseEntity<PedidosSchema> = runBlocking {
        return@runBlocking try {
            val nuevoPedido = SupabaseClient.client
                .from("orders")
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
    fun getPedidoById(@PathVariable id: Int): ResponseEntity<PedidosSchema> = runBlocking {
        return@runBlocking try {
            val pedido = SupabaseClient.client
                .from("orders")
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

    // GET /user/{userId} - Obtener pedidos de un usuario
    @GetMapping("/user/{userId}")
    fun getPedidosByUser(@PathVariable userId: Int): ResponseEntity<List<PedidosSchema>> = runBlocking {
        return@runBlocking try {
            val pedidos = SupabaseClient.client
                .from("orders")
                .select {
                    filter {
                        eq("user_id", userId)
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
    fun actualizarPedido(
        @PathVariable id: Int,
        @RequestBody actualizacion: PedidoUpdateRequest
    ): ResponseEntity<PedidosSchema> = runBlocking {
        return@runBlocking try {
            val pedidoActualizado = SupabaseClient.client
                .from("orders")
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
    fun eliminarPedido(@PathVariable id: Int): ResponseEntity<Unit> = runBlocking {
        return@runBlocking try {
            SupabaseClient.client
                .from("orders")
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
    val user_id: Int? = null,
    val status: String? = null,
    val total: Int? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

// DTO para crear pedido (sin id ni created_at)
@Serializable
data class PedidoRequest(
    val user_id: Int,
    val status: String = "pendiente",
    val total: Int
)

// DTO para actualizar pedido (campos opcionales)
@Serializable
data class PedidoUpdateRequest(
    val status: String? = null,
    val total: Int? = null
)