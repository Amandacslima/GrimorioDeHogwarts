package com.example.grimoriohogwarts

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var ghCriatura: EditText
    private lateinit var btnGetCriatura: Button
    private lateinit var iCriatura: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var criaturaApi: CriaturaApi
    private lateinit var tvNomeCriatura: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ghCriatura = findViewById(R.id.ghCriatura)
        btnGetCriatura = findViewById(R.id.btnGetCriatura)
        iCriatura = findViewById(R.id.iCriatura)
        progressBar = findViewById(R.id.progressBar)
        tvNomeCriatura = findViewById(R.id.tvNomeCriatura)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://hogwarts-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        criaturaApi = retrofit.create(CriaturaApi::class.java)
    }

    fun getImageNome(view: View) {
        val criatura = ghCriatura.text.toString().trim()

        if (criatura.isEmpty()) {
            ghCriatura.error = "O campo não pode estar vazio"
            return
        }

        if (criatura.length < 3) {
            ghCriatura.error = "Digite pelo menos 3 caracteres"
            return
        }

        ghCriatura.error = null

        getImageCriaturaApi(criatura)
    }

    // Função para buscar a imagem da criatura usando corrotinas
    private fun getImageCriaturaApi(criatura: String) {

        lifecycleScope.launch {
            try {
                showProgressBar()
                // Faz a requisição em segundo plano
                val response = withContext(Dispatchers.IO) {
                    criaturaApi.getCriaturaImage(criatura)
                }


                val criaturas = response.data.orEmpty()

                val criaturaEncontrada = criaturas.firstOrNull {
                    it.name?.equals(criatura, ignoreCase = true) == true
                } ?: criaturas.firstOrNull()


                if (criaturaEncontrada != null && !criaturaEncontrada.image.isNullOrBlank()) {
                    // Carrega o nome da criatura
                    tvNomeCriatura.text = criaturaEncontrada.name ?: "Sem nome"
                    // Carrega a imagem no ImagemView usando Picasso
                    Picasso.get().load(criaturaEncontrada.image).into(iCriatura)

                    hideProgressBar()
                } else {
                    hideProgressBar()
                    Toast.makeText(this@MainActivity, "Criatura não encontrada", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao buscar imagem", e)
                hideProgressBar()
                Toast.makeText(
                    this@MainActivity, "Erro ao buscar a imagem. Tente novamente.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        iCriatura.visibility = View.GONE
        tvNomeCriatura.visibility = View.GONE
    }

    // Função para esconder o ProgressBar
    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        iCriatura.visibility = View.VISIBLE
        tvNomeCriatura.visibility = View.VISIBLE
    }
}