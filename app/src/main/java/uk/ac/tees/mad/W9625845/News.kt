package uk.ac.tees.mad.W9625845

import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class News : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loader: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loader = findViewById(R.id.progressBar2)

        fetchArticles()
    }

    private fun fetchArticles() {
        val url = "https://medical-articles-live.p.rapidapi.com/journals"

        val requestQueue = Volley.newRequestQueue(this)


        val stringRequest = object : StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                val articlesType = object : TypeToken<List<Article>>() {}.type
                val articles: List<Article> = Gson().fromJson(response, articlesType)
                recyclerView.adapter = ArticlesAdapter(articles)
                loader.visibility = View.GONE

            },
            Response.ErrorListener { error ->

                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["X-RapidAPI-Key"] = "f6d96b246cmsh9f60d5199ed066dp178283jsn2441bcb10c02"
                    headers["X-RapidAPI-Host"] = "medical-articles-live.p.rapidapi.com"
                    return headers }
            override fun getParams(): Map<String, String> {
                return params
            }

        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            10000, // 10 seconds timeout
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue.add(stringRequest)
    }
}

data class Article(
    val title: String,
    val url: String,
    val source: String
)


class ArticlesAdapter(private val articles: List<Article>) : RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val sourceTextView: TextView = view.findViewById(R.id.sourceTextView)
        val urlTextView: TextView = view.findViewById(R.id.urlTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.article_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.titleTextView.text = article.title
        holder.sourceTextView.text = article.source
        holder.urlTextView.text = article.url

        // Make the URL clickable and open in a web browser
        holder.urlTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = articles.size
}
