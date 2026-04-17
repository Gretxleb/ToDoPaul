package com.example.todoapp.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.data.TaskDatabase
import com.example.todoapp.data.TaskRepository
import com.example.todoapp.databinding.FragmentTasksBinding
import com.example.todoapp.ui.TasksAdapter
import com.example.todoapp.ui.TasksViewModel
import com.example.todoapp.ui.dialogs.AddTaskDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TasksViewModel
    private lateinit var adapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = TaskDatabase.getDatabase(requireContext()).taskDao()
        val repository = TaskRepository(dao)
        viewModel = ViewModelProvider(this, TasksViewModelFactory(repository))
            .get(TasksViewModel::class.java)

        setupRecyclerView()
        setupFab()
        observeTasks()
        setHasOptionsMenu(true)
    }

    private fun setupRecyclerView() {
        adapter = TasksAdapter(
            onToggle = { task -> viewModel.toggleCompletion(task) },
            onDelete = { task -> viewModel.deleteTask(task) }
        )
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TasksFragment.adapter
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            AddTaskDialog { title, category ->
                viewModel.addTask(title, category)
                Snackbar.make(binding.root, R.string.task_added, Snackbar.LENGTH_SHORT).show()
            }.show(parentFragmentManager, "AddTaskDialog")
        }
    }

    private fun observeTasks() {
        lifecycleScope.launch {
            viewModel.allTasks.collect { tasks ->
                adapter.submitList(tasks)
                updateEmptyView(tasks.isEmpty())
            }
        }
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        binding.textEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerViewTasks.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_tasks, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.submitList(filterTasks(newText))
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_completed -> {
                viewModel.clearCompleted()
                Snackbar.make(binding.root, R.string.completed_cleared, Snackbar.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filterTasks(query: String?): List<com.example.todoapp.data.Task> {
        val currentList = adapter.currentList
        return if (query.isNullOrBlank()) {
            currentList
        } else {
            currentList.filter { task ->
                task.title.contains(query, ignoreCase = true) ||
                    task.category.contains(query, ignoreCase = true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class TasksViewModelFactory(private val repository: TaskRepository) :
        ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TasksViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
