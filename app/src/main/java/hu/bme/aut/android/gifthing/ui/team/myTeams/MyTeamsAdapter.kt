package hu.bme.aut.android.gifthing.ui.team.myTeams

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.gifthing.models.Team

class MyTeamsAdapter
    (private var listener: OnTeamSelectedListener,
     private var teams : MutableList<Team>
) :
    RecyclerView.Adapter<MyTeamsAdapter.TeamsViewHolder>() {

    interface OnTeamSelectedListener {
        fun onTeamSelected(team: Team)
    }

    override fun getItemCount(): Int {
        return teams.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(hu.bme.aut.android.gifthing.R.layout.item_team, parent, false)
        return TeamsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamsViewHolder, position: Int) {
        val item = teams[position]
        holder.teamName.text = item.name.toString()
        holder.item = item

    }

    fun addTeam(newTeam: Team) {
        teams.add(newTeam)
        notifyItemInserted(teams.size - 1)
    }

    fun removeTeam(position: Int) {
        teams.removeAt(position)
        notifyItemRemoved(position)
        if (position < teams.size) {
            notifyItemRangeChanged(position, teams.size - position)
        }
    }

    inner class TeamsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var teamName: TextView = itemView.findViewById(hu.bme.aut.android.gifthing.R.id.teamName)

        lateinit var item: Team

        init {
            itemView.setOnClickListener {
                listener.onTeamSelected(item)
            }
        }
    }
}

