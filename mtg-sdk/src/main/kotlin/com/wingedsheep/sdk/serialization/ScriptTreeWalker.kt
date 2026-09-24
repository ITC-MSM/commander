package com.wingedsheep.sdk.serialization

import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CharacteristicValue
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.SerializersModule

/**
 * Visits every typed node of a card's data tree — each ability, effect, condition, dynamic amount,
 * filter, and so on, down to the leaves — without encoding anything.
 *
 * "Is there an X anywhere in this card?" questions (does any clause gate on delirium?) can't be
 * answered by a hand-written walk: the SDK's sealed hierarchies run to hundreds of subtypes, and a
 * condition can sit inside an effect inside a mode inside a triggered ability on a back face. A
 * hand-written walk with a catch-all `else` silently misses every container added after it was
 * written. So instead this drives the card's own generated serializers with an [AbstractEncoder]
 * that throws the output away and hands each object to [visit] on the way down. Everything a card
 * *is* — everything that round-trips through its JSON — is therefore visited, and a new SDK type is
 * covered the moment it is `@Serializable`, which it has to be to exist on a card at all.
 *
 * Defaults are visited too (the encoder never skips a default-valued element), so `player = You`
 * and `filter = Any` are seen as real nodes. Each object is visited once: a polymorphic wrapper
 * hands its value on to the concrete subtype's serializer, and only that second call visits it.
 */
object ScriptTreeWalker {

    /** Visit every node of [definition], including its back face and card faces. */
    fun forEachNode(definition: CardDefinition, visit: (Any) -> Unit) =
        forEachNode(CardDefinition.serializer(), definition, visit)

    /** Visit every node of [root] as its [serializer] sees it. */
    fun <T> forEachNode(serializer: SerializationStrategy<T>, root: T, visit: (Any) -> Unit) {
        NodeVisitingEncoder(visit).encodeSerializableValue(serializer, root)
    }
}

@OptIn(ExperimentalSerializationApi::class)
private class NodeVisitingEncoder(private val visit: (Any) -> Unit) : AbstractEncoder() {

    override val serializersModule: SerializersModule = CardSerialization.module

    /** Primitives, strings and enums are leaves; there is nothing below them to visit. */
    override fun encodeValue(value: Any) = Unit

    override fun encodeNull() = Unit

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        if (value == null) return
        if (serializer.descriptor.kind !is PolymorphicKind) visit(value)
        when (value) {
            // Its serializer writes JSON directly (it casts the encoder to JsonEncoder for the
            // compact `3` form), so walk the one child it carries by hand.
            is CharacteristicValue -> when (value) {
                is CharacteristicValue.Fixed -> Unit
                is CharacteristicValue.Dynamic ->
                    encodeSerializableValue(DynamicAmount.serializer(), value.source)
                is CharacteristicValue.DynamicWithOffset ->
                    encodeSerializableValue(DynamicAmount.serializer(), value.source)
            }
            else -> serializer.serialize(this, value)
        }
    }
}
