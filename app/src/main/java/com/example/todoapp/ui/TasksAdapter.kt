package com.example.todoapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.Task
import com.example.todoapp.databinding.ItemTaskBinding

class TasksAdapter(
    private val onToggle: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : ListAdapter<Task, TasksAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding, onToggle, onDelete)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TaskViewHolder(
        private val binding: ItemTaskBinding,
        private val onToggle: (Task) -> Unit,
        private val onDelete: (Task) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.checkboxCompleted.setOnCheckedChangeListener(null)
            binding.checkboxCompleted.isChecked = task.isCompleted
            binding.textTitle.text = task.title
            binding.chipCategory.text = task.category

            binding.checkboxCompleted.setOnCheckedChangeListener { _, _ ->
                onToggle(task)
            }
            binding.imageDelete.setOnClickListener {
                onDelete(task)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem
    }
}
