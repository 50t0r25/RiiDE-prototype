package com.ryantest.prototype01

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ryantest.prototype01.databinding.ItemTripBinding

class TripsAdapter(var tripItems: List<TripItem>,private val onItemClicked: (tripItems: List<TripItem>,position: Int) -> Unit): RecyclerView.Adapter<TripsAdapter.TripsViewHolder>() {

    // I mainly DO NOT HAVE A SINGLE CLUE what is happening in this class
    // All i know is that i passed the onItemClick function as a parameter to be able to execute code when items are clicked
    inner class TripsViewHolder(val binding: ItemTripBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTripBinding.inflate(layoutInflater, parent, false)
        return TripsViewHolder(binding)
    }


    override fun onBindViewHolder(holder: TripsViewHolder, position: Int) {
        holder.binding.apply {
            itemTripCard.setOnClickListener {
                onItemClicked(tripItems,position)
            }
            itemTripFromToTv.text = tripItems[position].fromTo
            itemTripPriceTv.text = tripItems[position].price
            itemTripSeatsTv.text = tripItems[position].seats
            itemTripDateTv.text = tripItems[position].date
        }
    }

    override fun getItemCount(): Int {
        return  tripItems.size
    }

}