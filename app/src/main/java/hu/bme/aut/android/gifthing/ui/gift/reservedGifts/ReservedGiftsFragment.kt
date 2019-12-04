package hu.bme.aut.android.gifthing.ui.gift.reservedGifts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import hu.bme.aut.android.gifthing.R

class ReservedGiftsFragment : Fragment() {

    private lateinit var reservedGiftsViewModel: ReservedGiftsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reservedGiftsViewModel =
            ViewModelProviders.of(this).get(ReservedGiftsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_reserved_gifts, container, false)
        val textView: TextView = root.findViewById(R.id.text_reserved_gifts)
        reservedGiftsViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}