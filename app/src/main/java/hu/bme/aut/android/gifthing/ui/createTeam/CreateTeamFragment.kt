package hu.bme.aut.android.gifthing.ui.createTeam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import hu.bme.aut.android.gifthing.R

class CreateTeamFragment : Fragment() {

    private lateinit var createTeamViewModel: CreateTeamViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        createTeamViewModel =
            ViewModelProviders.of(this).get(CreateTeamViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_create_team, container, false)
        val textView: TextView = root.findViewById(R.id.text_create_team)
        createTeamViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}