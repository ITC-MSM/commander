package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.EmitLibrarySearchedEventEffect
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Myr Incubator — Mirrodin #212
 * {6} · Artifact · Rare
 *
 * {6}, {T}, Sacrifice this artifact: Search your library for any number of artifact cards, exile
 * them, then create that many 1/1 colorless Myr artifact creature tokens. Then shuffle.
 *
 * One Gather → Select → Move pipeline, no new vocabulary:
 *
 *  1. `gather(FromZone(LIBRARY, You, Artifact))` snapshots the artifact cards in your library.
 *  2. `chooseAnyNumber(...)` is the literal "any number" — zero is a legal search result (CR 701.23b),
 *     so declining finds nothing, makes no tokens, and still shuffles.
 *  3. `exile(...)` moves the whole found collection to exile.
 *  4. "that many" is [DynamicAmount.DistinctEntitiesInCollections] over the found slot, evaluated
 *     after the move — the collection tracks entity ids, which survive the zone change — so the token
 *     count is the number of cards *actually* exiled rather than the number asked for.
 *  5. [ShuffleLibraryEffect] is the trailing "Then shuffle", and
 *     [EmitLibrarySearchedEventEffect] fires the "whenever a player searches their library" triggers
 *     (CR 701.23) that every other search primitive emits.
 *
 * The exile is a plain one-way exile, not a linked one: nothing on this card ever refers back to the
 * exiled cards, and the tokens are ordinary tokens rather than copies of them.
 */
val MyrIncubator = card("Myr Incubator") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{6}, {T}, Sacrifice this artifact: Search your library for any number of artifact " +
        "cards, exile them, then create that many 1/1 colorless Myr artifact creature tokens. " +
        "Then shuffle."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{6}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.Pipeline(
            descriptionOverride = "Search your library for any number of artifact cards, exile " +
                "them, then create that many 1/1 colorless Myr artifact creature tokens. Then shuffle."
        ) {
            val searchable = gather(
                CardSource.FromZone(Zone.LIBRARY, Player.You, GameObjectFilter.Artifact),
                name = "myrIncubatorSearchable"
            )

            val found = chooseAnyNumber(
                from = searchable,
                prompt = "Search your library for any number of artifact cards",
                name = "myrIncubatorFound"
            )

            exile(found)

            run(
                CreateTokenEffect(
                    count = DynamicAmount.DistinctEntitiesInCollections(listOf(found.key)),
                    power = 1,
                    toughness = 1,
                    colors = emptySet(),
                    creatureTypes = setOf("Myr"),
                    artifactToken = true,
                    // Mirrodin printed no token cards; this is the Magic Player Rewards 2004 Myr,
                    // the contemporary printing (the same series Pentavus's Pentavite comes from).
                    imageUri = "https://cards.scryfall.io/normal/front/1/8/187cf6d5-c352-48f2-b8bf-fe1bf946e2ac.jpg?1783944459"
                )
            )

            run(ShuffleLibraryEffect())
            run(EmitLibrarySearchedEventEffect)
        }
        description = "{6}, {T}, Sacrifice this artifact: Search your library for any number of " +
            "artifact cards, exile them, then create that many 1/1 colorless Myr artifact creature " +
            "tokens. Then shuffle."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "212"
        artist = "Alex Horley-Orlandelli"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31b0f9ef-7404-4e05-b759-aaf1ebcfcb31.jpg?1783944512"
    }
}
