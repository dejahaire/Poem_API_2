package com.example.pinkapi

import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var poemTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var authorTextView: TextView

    private lateinit var newPoemButton: Button
    private lateinit var poemScrollView: ScrollView
    private val client = AsyncHttpClient()
    private val url = "https://poetrydb.org/random"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        poemTextView = findViewById(R.id.poemTextView)
        titleTextView = findViewById(R.id.titleTextView)
        authorTextView = findViewById(R.id.authorTextView)
        newPoemButton = findViewById(R.id.newPoemButton)
        poemScrollView = findViewById(R.id.poemScrollView)

        newPoemButton.setOnClickListener {
            fetchPoem()
        }
    }

    private fun fetchPoem() {
        poemTextView.text = "Loading a new poem... 💭"

        client.get(url, object : JsonHttpResponseHandler() {

            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JsonHttpResponseHandler.JSON
            ) {
                val jsonArray: JSONArray = json.jsonArray ?: return
                val poemObj = jsonArray.getJSONObject(0)
                val title = poemObj.getString("title")
                val author = poemObj.optString("author")

                val linesArray = poemObj.getJSONArray("lines")
                val content = (0 until linesArray.length())
                    .joinToString("\n") { linesArray.getString(it) }


                poemTextView.animate().alpha(0f).setDuration(200).withEndAction {

                   titleTextView.text = title
                    authorTextView.text = "— $author"
                    poemTextView.text = content


                    titleTextView.animate().alpha(1f).setDuration(300).start()
                    authorTextView.animate().alpha(1f).setDuration(300).start()
                    poemTextView.animate().alpha(1f).setDuration(300).start()
                    poemScrollView.post {
                        poemScrollView.fullScroll(ScrollView.FOCUS_UP)
                    }
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                poemTextView.text = "Oops! Couldn’t load a poem 💔"
            }
        })
    }
}
