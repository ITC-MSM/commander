package com.wingedsheep.sdk.scripting.effects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Pipeline collection holding the permanents the champion ability *offered* as legal choices —
 * "another [quality] you control" (CR 702.72a).
 */
const val CHAMPION_CANDIDATES = "championCandidates"

/** Pipeline collection holding the permanent the controller picked, before it moves. */
const val CHAMPION_CHOICE = "championChoice"

/**
 * Pipeline collection holding the cards the champion ability actually *exiled*. Written by the
 * move step's `storeMovedAs`, so it is empty both when the controller declined and when the pick
 * failed to reach exile — which is exactly the "unless" of CR 702.72a, and what the ability's
 * [SuccessCriterion.CollectionNonEmpty] gate reads to decide between "championed" and "sacrifice".
 */
const val CHAMPIONED_CARDS = "championedCards"

/**
 * Emit a `ChampionedEvent` (CR 702.72c) for each permanent the source just exiled as its champion
 * ability resolved. Appended internally by [com.wingedsheep.sdk.dsl.champion] as the `then` branch
 * of the ability's "exile … unless" gate, so "When a [quality] is championed with this creature"
 * payoffs ([com.wingedsheep.sdk.scripting.EventPattern.ChampionedEvent], i.e. Mistbind Clique) fire.
 *
 * CR 702.72c: "A permanent is 'championed' by another permanent if the latter exiles the former as
 * the direct result of a champion ability." The event is therefore emitted from *inside* the champion
 * ability's own resolution rather than derived from the `ZoneChangeEvent` — a permanent exiled by any
 * other effect, including another linked-exile ability on the same source, is not championed.
 *
 * The championed permanents are read out of the pipeline collection [from] (the champion pipeline's
 * `storeMovedAs`), so nothing is emitted when the controller declined and the source was sacrificed
 * instead. The champion is always the ability's source.
 *
 * Card authors should not use this directly; it is wired into the `champion()` helper.
 *
 * @property from Pipeline collection holding the cards actually exiled by this champion ability.
 */
@SerialName("EmitChampionedEvent")
@Serializable
data class EmitChampionedEventEffect(
    val from: String = CHAMPIONED_CARDS
) : Effect {
    // Intentionally blank: this is an internal pipeline tail with no player-facing text.
    override val description: String = ""
}
