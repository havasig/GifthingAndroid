package hu.bme.aut.android.gifthing.ui.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.gifthing.models.User

class UserListAdapter
    (private var listener: OnUserSelectedListener,
     private var users : MutableList<User>
) :
    RecyclerView.Adapter<UserListAdapter.UsersViewHolder>() {

    interface OnUserSelectedListener {
        fun onUserSelected(user: User)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(hu.bme.aut.android.gifthing.R.layout.item_user, parent, false)
        return UsersViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val item = users[position]
        if(item.nickName != null)
            holder.userName.text = item.nickName
        else
            holder.userName.text = item.firstName + " " + item.lastName

        holder.item = item

    }

    fun addUser(newUser: User) {
        users.add(newUser)
        notifyItemInserted(users.size - 1)
    }

    fun getUser(): MutableList<User> {
        return users
    }

    fun removeUser(position: Int) {
        users.removeAt(position)
        notifyItemRemoved(position)
        if (position < users.size) {
            notifyItemRangeChanged(position, users.size - position)
        }
    }

    inner class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userName: TextView = itemView.findViewById(hu.bme.aut.android.gifthing.R.id.userName)

        lateinit var item: User

        init {
            itemView.setOnClickListener {
                listener.onUserSelected(item)
            }
        }
    }
}

