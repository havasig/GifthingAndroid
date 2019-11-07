package hu.bme.aut.android.gifthing.ui.myGifts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import hu.bme.aut.android.gifthing.R

class MyGiftsFragment : Fragment() {

    private lateinit var myGiftsViewModel: MyGiftsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myGiftsViewModel =
            ViewModelProviders.of(this).get(MyGiftsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_my_gifts, container, false)
        val textView: TextView = root.findViewById(R.id.text_my_gifts)
        myGiftsViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}