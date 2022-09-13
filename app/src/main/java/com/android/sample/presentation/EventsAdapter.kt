package com.android.sample.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.sample.R
import com.android.sample.data.EventType
import com.android.sample.databinding.ItemEventBinding

/**
 * Created by cristiandregan
 * on 17/08/2020.
 */

class EventsAdapter(private val onEventClicked: (EventType) -> Unit) :
    RecyclerView.Adapter<EventsAdapter.EventVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventVH {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventVH(itemView)
    }

    override fun getItemCount(): Int = EventType.values().size

    override fun onBindViewHolder(holder: EventVH, position: Int) {
        holder.bind(EventType.values()[position])
    }

    inner class EventVH(containerView: View) : RecyclerView.ViewHolder(containerView) {

        private var binding = ItemEventBinding.bind(containerView)

        init {
            binding.eventTV.setOnClickListener {
                onEventClicked.invoke(EventType.values()[adapterPosition])
            }
        }

        internal fun bind(eventType: EventType) {
            binding.eventTV.text = eventType.name
        }
    }
}