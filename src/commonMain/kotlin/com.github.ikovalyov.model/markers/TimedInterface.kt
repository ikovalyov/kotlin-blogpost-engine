package com.github.ikovalyov.model.markers

import kotlinx.datetime.Instant

interface TimedInterface {
  val lastModified: Instant
}
