package com.github.ikovalyov

import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.dom.h1

class App : RComponent<Props, State>() {
    override fun RBuilder.render() {
        h1 { +"Hello, React+Kotlin/JS!" }
    }
}
