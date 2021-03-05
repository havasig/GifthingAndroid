package hu.bme.aut.android.gifthing.ui.team

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.gifthing.database.models.entities.Team

class TeamEntityAdapter
    (
    private var listener: OnTeamSelectedListener,
    private var teams: MutableList<Team>
) :
    RecyclerView.Adapter<TeamEntityAdapter.TeamsViewHolder>() {

    interface OnTeamSelectedListener {
        fun onTeamSelected(team: Team)
    }

    override fun getItemCount(): Int {
        return teams.size
    }

    fun getItems(): MutableList<Team> {
        return teams
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(hu.bme.aut.android.gifthing.R.layout.item_team, parent, false)
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

    fun setTeams(teams: List<Team>) {
        this.teams = teams.toMutableList()
        notifyDataSetChanged()
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

