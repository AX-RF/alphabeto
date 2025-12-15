package com.alphabeto.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alphabeto.R
import com.alphabeto.data.model.LetterWithProgress
import com.alphabeto.databinding.ItemLetterCardBinding

class LetterGridAdapter(
    private val onClick: (LetterWithProgress) -> Unit
) : ListAdapter<LetterWithProgress, LetterGridAdapter.LetterViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLetterCardBinding.inflate(inflater, parent, false)
        return LetterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LetterViewHolder(
        private val binding: ItemLetterCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LetterWithProgress) {
            val context = binding.root.context
            binding.letterText.text = item.letter.character
            val statusText = if (item.isCompleted) {
                context.getString(R.string.label_completed)
            } else {
                context.getString(R.string.label_tracing_progress)
            }
            binding.completionText.text = statusText
            binding.root.contentDescription = context.getString(
                R.string.accessibility_letter_card,
                item.letter.character,
                statusText
            )
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LetterWithProgress>() {
            override fun areItemsTheSame(
                oldItem: LetterWithProgress,
                newItem: LetterWithProgress
            ): Boolean =
                oldItem.letter.character == newItem.letter.character &&
                        oldItem.letter.language == newItem.letter.language

            override fun areContentsTheSame(
                oldItem: LetterWithProgress,
                newItem: LetterWithProgress
            ): Boolean = oldItem == newItem
        }
    }
}
