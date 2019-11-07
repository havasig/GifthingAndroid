package hu.bme.aut.android.gifthing.ui.myInvites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import hu.bme.aut.android.gifthing.R


class MyInvitesFragment : Fragment() {

    private lateinit var myInvitesViewModel: MyInvitesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myInvitesViewModel =
            ViewModelProviders.of(this).get(MyInvitesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_my_invites, container, false)
        val textView: TextView = root.findViewById(R.id.text_my_invites)
        myInvitesViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}