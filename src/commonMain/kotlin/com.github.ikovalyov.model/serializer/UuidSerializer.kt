package com.github.ikovalyov.model.serializer

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

//@ExperimentalSerializationApi
//@Serializer(forClass = Uuid::class)
object UuidSerializer : KSerializer<Uuid> {
    override fun serialize(encoder: Encoder, value: Uuid) {
        encoder.encodeString(value = value.toString())
    }

    override fun deserialize(decoder: Decoder): Uuid {
        val string = decoder.decodeString()
        return uuidFrom(string)
    }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Uuid", PrimitiveKind.STRING)
}
