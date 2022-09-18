package com.github.ikovalyov.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class SimpleCoroutineScope : CoroutineScope {
    private val job = Job()

    override val coroutineContext = job
}
