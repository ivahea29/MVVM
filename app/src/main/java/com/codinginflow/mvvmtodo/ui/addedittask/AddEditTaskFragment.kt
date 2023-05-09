package com.codinginflow.mvvmtodo.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.databinding.FragmentAddEditTaskBinding
import com.codinginflow.mvvmtodo.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {

    // Get the viewmodel using Hilt
    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind the layout to the fragment
        val binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            // Set initial values for the UI elements
            editTextTaskName.setText(viewModel.taskName)
            checkBoxImportant.isChecked = viewModel.taskImportance
            checkBoxImportant.jumpDrawablesToCurrentState()
            textViewDateCreated.isVisible = viewModel.task != null
            textViewDateCreated.text = "Created: ${viewModel.task?.createdDateFormatted}"

            // Add listener for the task name text input field
            editTextTaskName.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            // Add listener for the important checkbox
            checkBoxImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportance = isChecked
            }

            // Add listener for the save task button
            fabSaveTask.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        // Observe the add/edit task event using the viewmodel's flow
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event ->
                // Handle the event using a when statement to check the type of the event
                when (event) {
                    is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        // Show a snackbar with the error message
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        // Clear focus from the task name text input field
                        binding.editTextTaskName.clearFocus()
                        // Set the fragment result using the result bundle
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        // Navigate back to the previous fragment in the back stack
                        findNavController().popBackStack()
                    }
                }.exhaustive // Call the 'exhaustive' extension function to ensure that all cases are handled
            }
        }
    }
}
