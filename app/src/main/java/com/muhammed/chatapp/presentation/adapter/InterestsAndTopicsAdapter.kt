package com.muhammed.chatapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muhammed.chatapp.data.pojo.Interest
import com.muhammed.chatapp.data.pojo.InterestAndTopic
import com.muhammed.chatapp.databinding.ListItemInterestBinding
import com.muhammed.chatapp.presentation.viewholder.InterestViewHolder

// Type is one of the static values
class InterestsAndTopicsAdapter(@IntRange(from = 0L, to = 1L) private val type: Int) :
    ListAdapter<InterestAndTopic, RecyclerView.ViewHolder>(InterestAndTopicDiff()) {
    private var onCheckedListener: ((interest: InterestAndTopic) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (type == INTEREST_TYPE) {
            return InterestViewHolder(ListItemInterestBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }else {
            return InterestViewHolder(ListItemInterestBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is InterestViewHolder -> holder.bind(getItem(position) as Interest, onCheckedListener)
        }

    }

    fun setOnCheckedChangeListener(listener: (interest: InterestAndTopic) -> Unit) {
        this.onCheckedListener = listener
    }

    companion object {
        const val INTEREST_TYPE = 0
        const val TOPIC_TYPE = 1
    }

}


private class InterestAndTopicDiff : DiffUtil.ItemCallback<InterestAndTopic>() {
    override fun areItemsTheSame(oldItem: InterestAndTopic, newItem: InterestAndTopic): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: InterestAndTopic, newItem: InterestAndTopic): Boolean {
        return oldItem == newItem
    }
}