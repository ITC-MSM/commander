package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Blade Banish — Ikoria: Lair of Behemoths #4
 * {3}{W} · Instant
 *
 * Exile target creature with power 4 or greater.
 *
 * The power restriction lives in the target filter (`powerAtLeast(4)`), so legality is checked at
 * announcement and again on resolution — a creature that shrinks below 4 power in response is no
 * longer a legal target and the spell is countered for having none. Power is read from *projected*
 * state, so pumps and lords count.
 */
val BladeBanish = card("Blade Banish") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Exile target creature with power 4 or greater."

    spell {
        val creature = target("target", TargetCreature(filter = TargetFilter.Creature.powerAtLeast(4)))
        effect = Effects.Exile(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "Lie Setiawan"
        flavorText = "The Wanderer walks many paths. Her blade travels only one."
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35a30091-7f68-4a67-b47e-f44318fc93b2.jpg"
    }
}
