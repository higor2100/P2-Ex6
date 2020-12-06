package com.example.ex6

import android.app.ProgressDialog.show
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ex6.modelo.Produto
import com.example.ex6.servico.ProdutoServico
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.cardview.view.*
import kotlinx.android.synthetic.main.form_produto.view.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        obterProdutos()
        fab.setOnClickListener {
            val createdView = LayoutInflater.from(this).inflate(R.layout.form_produto,window.decorView as ViewGroup,false)
            AlertDialog.Builder(this)
                .setTitle("Add Teste")
                .setView(createdView)
                .setPositiveButton("Salvar", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if(createdView.etTitle2.text.toString() != "" && createdView.etContents2.text.toString() != "" && createdView.etLikes2.text.toString() != "") {
                            val id = Random(100).toString()
                            val title = createdView.etTitle2.text
                            val contents = createdView.etContents2.text
                            if (createdView.etLikes2.text.toString() == "") createdView.etLikes2.setText("0")
                            val likes = createdView.etLikes2.text.toString().toInt()

                            val produto = Produto(id, title.toString(), contents.toString(), likes)
                            inserir(produto)

                        }


                    }
                })
                .show()

        }

    }
    fun inserir(produto: Produto){
        val clienteHttp = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()
        val call = Retrofit.Builder().baseUrl("http://www.testapi.com/").client(clienteHttp).addConverterFactory(GsonConverterFactory.create())
                .build().create(ProdutoServico::class.java).insert(produto)
        call.enqueue(object: Callback<Produto?> {
            override fun onResponse(call: Call<Produto?>?, response: Response<Produto?>?) {

            }

            override fun onFailure(call: Call<Produto?>?, t: Throwable?) {

            }
        })

    }
    fun obterProdutos(){
        val clienteHttp = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl("http://www.testapi.com/")
                .client(clienteHttp)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val servico = retrofit.create(ProdutoServico::class.java)

        val call = servico.listar()

        val callback = object: Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if(response.isSuccessful){
                    atualizarTela(response.body())
                }
                else{
                    Toast.makeText(this@MainActivity,
                            "Não foi possível atualizar produtos",
                            Toast.LENGTH_LONG).show()
                    Log.e("Erro servico", response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Toast.makeText(this@MainActivity,
                        "Erro de conexão",
                        Toast.LENGTH_LONG).show()
                Log.e("MainActivity","obterProdutos",t)
            }

        }

        call.enqueue(callback)
    }

    fun atualizarTela(produtos: List<Produto>?){
        container.removeAllViews()
        val formatter = NumberFormat.getCurrencyInstance(Locale("pt","BR"))
        produtos?.let {
            for (produto in produtos) {
                val cartao = layoutInflater.inflate(R.layout.cardview, container, false)
                cartao.txtID.text = produto.id
                cartao.txtTitle.text = produto.title
                cartao.txtContents.text = produto.contents
                cartao.txtLikes.text = produto.likes.toString()


            }
        }
    }
}