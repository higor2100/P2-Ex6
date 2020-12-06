package com.example.ex6.servico

import com.example.ex6.modelo.Produto
import retrofit2.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProdutoServico {
    @GET("/s1/post")
    fun listar(): Call<List<Produto>>

    @POST("")
    fun insert(@Body produto: Produto): Call<Produto>
}