package com.muhammed.chatapp.presentation.viewholder

import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.muhammed.chatapp.data.pojo.InterestAndTopic
import com.muhammed.chatapp.data.pojo.InterestWithTopics
import com.muhammed.chatapp.data.pojo.Topic
import com.muhammed.chatapp.databinding.ListItemInterestWithTopicsBinding
import com.muhammed.chatapp.databinding.ListItemTopicBinding
import com.muhammed.chatapp.presentation.common.loadImage

class InterestWithTopicsViewHolder(private val binding: ListItemInterestWithTopicsBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val chipWithTopicList = hashMapOf<Chip, Topic>()
    fun bind(item: InterestWithTopics, onCheckedListener: ((topic: InterestAndTopic) -> Unit)?) {
        with(binding) {
            interestTitle.text = item.interest.title
            if (item.interest.hasImage()) {
                interestIcon.loadImage(item.interest.imagePath!!)
            }

            // inflating topics as Chips
            item.topics.forEach {
                val chip =
                    ListItemTopicBinding.inflate(LayoutInflater.from(itemView.context)).topicChip
                chip.text = it.title
                topicsCg.addView(chip)
                chipWithTopicList[chip] = it
                setOnChipCheckedListener(chip, onCheckedListener)
            }


        }
    }

    private fun setOnChipCheckedListener(
        chip: Chip,
        listener: ((topic: InterestAndTopic) -> Unit)?
    ) {
        chip.setOnCheckedChangeListener { _, _ ->
            chipWithTopicList[chip]?.let {
                if (listener != null) {
                    Log.d("IntAndTopViewHolder", "setOnChipCheckedListener: ${it.title}")
                    it.isChecked = chip.isChecked
                    listener(it)
                }
            }
        }
    }
}