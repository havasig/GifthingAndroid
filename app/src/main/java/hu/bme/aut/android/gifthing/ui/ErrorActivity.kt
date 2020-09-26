package hu.bme.aut.android.gifthing.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.gifthing.R
import kotlinx.android.synthetic.main.activity_error.*

class ErrorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)

        tvErrorMessage.text = intent.getStringExtra("ERROR_MESSAGE")

        okBtn.setOnClickListener {
            finish()
        }
    }
}
