package com.github.ikovalyov.model.security

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.markers.IdInterface

data class UserRole(override val id: Uuid, val name: String): IdInterface {
    companion object
}
