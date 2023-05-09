package com.codinginflow.mvvmtodo.util

// Extension property that ensures all possible values of a sealed class are covered
// to avoid when expressions that are not exhaustive
// For example, if we have a sealed class with two possible values, this extension property ensures that both cases are covered
val <T> T.exhaustive: T
    // We just return the value itself, but we use the getter to enforce the type constraint
    get() = this
