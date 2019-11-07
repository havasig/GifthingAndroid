package hu.bme.aut.android.gifthing.ui.myGroups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import hu.bme.aut.android.gifthing.R

class MyGroupsFragment : Fragment() {

    private lateinit var myGroupsViewModel: MyGroupsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myGroupsViewModel =
            ViewModelProviders.of(this).get(MyGroupsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_my_groups, container, false)
        val textView: TextView = root.findViewById(R.id.text_my_groups)
        myGroupsViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}