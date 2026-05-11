package com.unad.agenda

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si el usuario no está logueado, enviarlo a LoginActivity
        if (!SessionManager(this).estaLogueado()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main); // Enlaza con el archivo acticity_main.xml

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)


        viewPager.adapter = MainPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager){tab, position ->
            tab.text = if (position == 0) "Contactos" else "Perfil"
        }.attach()

    }
}