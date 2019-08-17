package com.example.procrastinationgame


import android.os.AsyncTask
import java.net.HttpURLConnection
import java.net.URL
import java.io.*
import java.net.HttpURLConnection.HTTP_OK
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.HttpsURLConnection.*


class CallAPI : AsyncTask<String, String, Unit>() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String?) {
        val urlString = "http://192.168.212.122:25000/send_token/" // URL to call
        val data = params[0] //data to post
        var out: OutputStream? = null

        try {
            val url = URL(urlString)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            out = BufferedOutputStream(urlConnection.outputStream)

            val writer = BufferedWriter(OutputStreamWriter(out, "UTF-8"))
            writer.write(data)
            writer.flush()
            writer.close()
            out.close()

            urlConnection.connect()

            val responseCode = urlConnection.getResponseCode()
            var response = ""
            if (responseCode == HttpsURLConnection.HTTP_OK) {

                val br = BufferedReader(InputStreamReader(urlConnection.getInputStream()))
                var line =  br.readLine()

                while (line != null) {
                    response += line
                    line = br.readLine()
                }
            } else {
                response = ""

            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}//set context variables if required