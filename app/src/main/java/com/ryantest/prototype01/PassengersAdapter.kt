package com.ryantest.prototype01

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ryantest.prototype01.databinding.ItemPassengerBinding

class PassengersAdapter(var passengerItems: List<PassengerItem>,private val onItemClicked: (position: Int) -> Unit): RecyclerView.Adapter<PassengersAdapter.PassengersViewHolder>() {

    inner class PassengersViewHolder(val binding: ItemPassengerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengersViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPassengerBinding.inflate(layoutInflater, parent, false)
        return PassengersViewHolder(binding)
    }


    override fun onBindViewHolder(holder: PassengersViewHolder, position: Int) {
        holder.binding.apply {

            passengerNameTv.text = passengerItems[position].username

            passengerNameTv.setOnClickListener {
                onItemClicked(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return  passengerItems.size
    }

}