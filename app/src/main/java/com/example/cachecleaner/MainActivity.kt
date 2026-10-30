package com.example.cachecleaner

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.io.File

class MainActivity : Activity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(13, 18, 35)
        window.navigationBarColor = Color.rgb(13, 18, 35)
        window.decorView.systemUiVisibility = 0

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(34), dp(24), dp(24))
            setBackgroundColor(Color.rgb(13, 18, 35))
        }

        val title = TextView(this).apply {
            text = "CACHE CLEANER"
            textSize = 25f
            setTextColor(Color.WHITE)
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            gravity = Gravity.CENTER
        }
        root.addView(title, matchWrap())

        val subtitle = TextView(this).apply {
            text = "Perawatan penyimpanan dalam satu tempat"
            textSize = 14f
            setTextColor(Color.rgb(180, 190, 215))
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(28))
        }
        root.addView(subtitle, matchWrap())

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(20), dp(24), dp(20), dp(24))
            setBackgroundColor(Color.rgb(25, 34, 58))
        }

        val icon = TextView(this).apply {
            text = "✦"
            textSize = 54f
            setTextColor(Color.rgb(92, 170, 255))
            gravity = Gravity.CENTER
        }
        card.addView(icon, matchWrap())

        val cardTitle = TextView(this).apply {
            text = "Pembersihan cache"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(8))
        }
        card.addView(cardTitle, matchWrap())

        val info = TextView(this).apply {
            text = "Android membatasi aplikasi biasa agar tidak dapat menghapus cache semua aplikasi secara otomatis. Gunakan tombol di bawah untuk membuka alat pembersihan sistem."
            textSize = 14f
            setTextColor(Color.rgb(205, 213, 230))
            gravity = Gravity.CENTER
            setLineSpacing(dp(3).toFloat(), 1f)
        }
        card.addView(info, matchWrap())

        val cleanButton = Button(this).apply {
            text = "BERSIHKAN CACHE"
            isAllCaps = false
            textSize = 16f
            setOnClickListener {
                val removed = clearOwnCache(cacheDir)
                status.text = "Cache aplikasi ini dibersihkan: ${formatBytes(removed)}"
                Toast.makeText(this@MainActivity, "Cache aplikasi ini dibersihkan", Toast.LENGTH_SHORT).show()
                openSystemStorage()
            }
        }
        val buttonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(56)
        ).apply { topMargin = dp(22) }
        card.addView(cleanButton, buttonParams)
        root.addView(card, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        status = TextView(this).apply {
            text = "Siap digunakan"
            textSize = 14f
            setTextColor(Color.rgb(140, 210, 180))
            gravity = Gravity.CENTER
            setPadding(0, dp(20), 0, dp(8))
        }
        root.addView(status, matchWrap())

        val settingsButton = Button(this).apply {
            text = "Buka daftar aplikasi"
            isAllCaps = false
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS))
            }
        }
        root.addView(settingsButton, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(50)
        ).apply { topMargin = dp(8) })

        val foot = TextView(this).apply {
            text = "Untuk cache aplikasi lain, pilih aplikasi di Pengaturan > Aplikasi > Penyimpanan & cache > Hapus cache. Nama menu bisa berbeda menurut merek HP."
            textSize = 12f
            setTextColor(Color.rgb(155, 165, 190))
            gravity = Gravity.CENTER
            setPadding(0, dp(18), 0, 0)
        }
        root.addView(foot, matchWrap())
        setContentView(root)
    }

    private fun openSystemStorage() {
        try {
            startActivity(Intent(Settings.ACTION_MANAGE_STORAGE))
        } catch (_: Exception) {
            try {
                startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS))
            } catch (_: Exception) {
                Toast.makeText(this, "Buka Pengaturan > Penyimpanan secara manual.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clearOwnCache(file: File): Long {
        var bytes = 0L
        file.listFiles()?.forEach { child ->
            bytes += deleteRecursivelyCount(child)
        }
        return bytes
    }

    private fun deleteRecursivelyCount(file: File): Long {
        var bytes = 0L
        if (file.isDirectory) file.listFiles()?.forEach { bytes += deleteRecursivelyCount(it) }
        else bytes += file.length()
        file.deleteRecursively()
        return bytes
    }

    private fun formatBytes(bytes: Long): String = when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
        else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    private fun matchWrap() = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
}
