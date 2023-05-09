package com.codinginflow.mvvmtodo.util

import androidx.appcompat.widget.SearchView

inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }
        /**
         * Called when the user changes the query text.
         * We invoke the listener with the new query text or an empty string if it is null.
         * We return true to indicate that we have handled the event.
         */
        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}