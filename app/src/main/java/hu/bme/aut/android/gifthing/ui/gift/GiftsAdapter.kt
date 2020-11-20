package hu.bme.aut.android.gifthing.ui.gift

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.gifthing.database.models.entities.Gift

class GiftsAdapter
    (
    private var listener: OnGiftSelectedListener,
    private var gifts: MutableList<Gift>
) :
    RecyclerView.Adapter<GiftsAdapter.GiftsViewHolder>() {

    interface OnGiftSelectedListener {
        fun onGiftSelected(gift: Gift)
    }

    override fun getItemCount(): Int {
        return gifts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(hu.bme.aut.android.gifthing.R.layout.item_gift, parent, false)
        return GiftsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GiftsViewHolder, position: Int) {
        val item = gifts[position]
        holder.giftName.text = item.name
        item.price?.let { holder.giftPrice.text = it.toString() }
        holder.item = item
    }

    fun addGift(newGift: Gift) {
        gifts.add(newGift)
        notifyItemInserted(gifts.size - 1)
    }

    fun removeGift(position: Int) {
        gifts.removeAt(position)
        notifyItemRemoved(position)
        if (position < gifts.size) {
            notifyItemRangeChanged(position, gifts.size - position)
        }
    }

    fun setGifts(gifts: List<Gift>) {
        this.gifts = gifts.toMutableList()
        notifyDataSetChanged()
    }

    inner class GiftsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var giftName: TextView = itemView.findViewById(hu.bme.aut.android.gifthing.R.id.giftName)
        var giftPrice: TextView = itemView.findViewById(hu.bme.aut.android.gifthing.R.id.giftPrice)

        lateinit var item: Gift

        init {
            itemView.setOnClickListener {
                listener.onGiftSelected(item)
            }
        }
    }
}

