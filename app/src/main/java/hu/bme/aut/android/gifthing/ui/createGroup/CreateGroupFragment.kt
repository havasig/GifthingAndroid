package hu.bme.aut.android.gifthing.ui.createGroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.ui.about.AboutViewModel

class CreateGroupFragment : Fragment() {

    private lateinit var createGroupViewModel: CreateGroupViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        createGroupViewModel =
            ViewModelProviders.of(this).get(CreateGroupViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_create_group, container, false)
        val textView: TextView = root.findViewById(R.id.text_create_group)
        createGroupViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}