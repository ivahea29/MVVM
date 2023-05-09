package com.codinginflow.mvvmtodo.ui.deleteallcompleted

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompletedDialogFragment : DialogFragment() {

    // The view model instance, created using the by viewModels delegate
    private val viewModel: DeleteAllCompletedViewModel by viewModels()

    // This method is called when the dialog fragment is created
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =

        // Create an alert dialog using the AlertDialog.Builder class
        AlertDialog.Builder(requireContext())

            // Set the title of the dialog
            .setTitle("Confirm deletion")

            // Set the message of the dialog
            .setMessage("Do you really want to delete all completed tasks?")

            // Add a "Cancel" button to the dialog, which does nothing when clicked
            .setNegativeButton("Cancel", null)

            // Add a "Yes" button to the dialog, which calls the onConfirmClick method of the view model when clicked
            .setPositiveButton("Yes") { _, _ ->
                viewModel.onConfirmClick()
            }

            // Create the dialog
            .create()
}
