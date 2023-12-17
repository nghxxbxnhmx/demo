package com.ftel.demo

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ftel.demo.adapter.PingAdapter
import com.ftel.demo.dto.PingResult
import com.ftel.demo.util.PingUtil
import java.io.IOException

import java.net.InetSocketAddress

import java.net.Socket
import kotlin.math.floor


class MainActivity : AppCompatActivity() {
    private lateinit var editTextIPAddress: EditText
    private lateinit var buttonPing: Button
    private lateinit var recyclerView: RecyclerView

    private val avgResponseTimeTextView: TextView by lazy {
        findViewById<TextView>(R.id.avgResponseTimeTextView) ?: throw IllegalStateException("avgResponseTimeTextView not found")
    }
    private val buttonActivity_2: Button by lazy {
        findViewById<Button>(R.id.activity_2Button)
    }
    private lateinit var pingAdapter: PingAdapter

    private val responses: ArrayList<String> = ArrayList()
    private val pingResults : ArrayList<PingResult> = ArrayList()

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextIPAddress = findViewById(R.id.editTextIPAddress)
        buttonPing = findViewById(R.id.buttonPing)
        recyclerView = findViewById(R.id.recyclerViewResult)


        pingAdapter = PingAdapter()

        recyclerView.adapter = pingAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        buttonPing.setOnClickListener {
            val ipAddress = editTextIPAddress.text.toString().trim()
            handler.postDelayed(pingTaskRunnable(ipAddress), 0)
        }

        buttonActivity_2.setOnClickListener {
            val intent = Intent(this, Activity_2::class.java)
            startActivity(intent)
        }
    }

    private fun pingTaskRunnable(ipAddress: String): Runnable {
        return Runnable {
            PingTask().execute(ipAddress)
        }
    }

    private inner class PingTask : AsyncTask<String, Void, PingResult>() {
        override fun doInBackground(vararg params: String?): PingResult {
            val ipAddress = params[0] ?: ""
            val pingUtil = PingUtil(ipAddress)
            val ttl = 59
            val timeout = 10000
            val packetSize = 32
            var result : PingResult = pingUtil.sendPing(ttl, timeout, packetSize)
            pingResults.add(result)
            return result
        }

        override fun onPostExecute(pingResult: PingResult) {
            val resultString = """
            Ping #${responses.size + 1}
            Status: ${pingResult.replyStatus}  -  IpAddress: ${pingResult.ipAddress}
            Response Time: ${pingResult.responseTime}ms
        """.trimIndent()

            responses.add(0, resultString)

            val avgResponseTime = pingResults.map { it.responseTime }.average()
            avgResponseTimeTextView.text = "Avg: ${(floor(avgResponseTime * 100)/100).toString()}"
            pingAdapter.clearData()
            pingAdapter.setData(responses)

            // Schedule the next ping after a delay of 1000 milliseconds
            handler.postDelayed(pingTaskRunnable(editTextIPAddress.text.toString().trim()), 1000)
        }


    }

}
