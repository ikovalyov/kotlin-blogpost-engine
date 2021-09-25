package com.github.ikovalyov.model

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.markers.IdInterface

data class Item(override val id: Uuid, val content: String) : IdInterface
