package com.github.ikovalyov

import react.*
import react.dom.h1

class App : RComponent<RProps, RState>() {
    override fun RBuilder.render() {
        h1 { +"Hello, React+Kotlin/JS!" }
    }
}
