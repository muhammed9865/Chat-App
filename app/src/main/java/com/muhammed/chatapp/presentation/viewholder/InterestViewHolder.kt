package com.muhammed.chatapp.presentation.viewholder

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.muhammed.chatapp.R
import com.muhammed.chatapp.data.pojo.Interest
import com.muhammed.chatapp.databinding.ListItemInterestBinding

class InterestViewHolder(private val binding: ListItemInterestBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(interest: Interest, onCheckedListener: ((interest: Interest) -> Unit)?) {
        with(binding) {
            interestTitle.text = interest.title
            if (interest.hasImage()) {
                interestIcon.load(interest.imagePath!!)
            }
            interestCard.setOnClickListener {
                interest.isChecked = !interest.isChecked
                setInterestState(interest.isChecked)
                onCheckedListener?.let {
                    it(interest)
                }
            }
        }
    }

    private fun setInterestState(isChecked: Boolean) {
        binding.apply {
            val cardColor =
                if (isChecked) itemView.context.getColor(R.color.interest_selected) else itemView.context.getColor(
                    R.color.interest_unselected
                )
            val textColor =
                if (isChecked) cardColor else itemView.context.getColor(R.color.interest_title_unselected)
            interestCard.setCardBackgroundColor(cardColor)
            interestTitle.setTextColor(textColor)

        }
    }
}