package com.example.todoapp.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.todoapp.databinding.DialogAddTaskBinding

class AddTaskDialog(
    private val onAdd: (title: String, category: String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAddTaskBinding.inflate(layoutInflater)

        return AlertDialog.Builder(requireContext())
            .setTitle("New Task")
            .setView(binding.root)
            .setPositiveButton("Add") { _, _ ->
                val title = binding.editTextTitle.text.toString().trim()
                val category = when (binding.radioGroupCategory.checkedRadioButtonId) {
                    binding.radioWork.id -> "Work"
                    binding.radioPersonal.id -> "Personal"
                    else -> "Work"
                }
                if (title.isNotEmpty()) {
                    onAdd(title, category)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
