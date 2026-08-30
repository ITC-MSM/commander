package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect

/**
 * Necrobite
 * {2}{B}
 * Instant
 *
 * Target creature gains deathtouch until end of turn. Regenerate it. (The next time that creature would be destroyed this turn, instead tap it, remove it from combat, and heal all damage on it.)
 *
 * "It" is the same creature both halves act on, so there is one named target and both effects
 * bind to it — Djeru's Resolve's shape with a regeneration shield instead of a damage one.
 * [RegenerateEffect] has no `Effects` facade entry, so the class is imported directly.
 */
val Necrobite = card("Necrobite") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gains deathtouch until end of turn. Regenerate it. (The next time that creature " +
        "would be destroyed this turn, instead tap it, remove it from combat, and heal all damage on " +
        "it.)"

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.DEATHTOUCH, creature),
            RegenerateEffect(creature)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Nils Hamm"
        flavorText = "An undead snake doesn't bite in self-defense. It hungers as any zombie, never sated."
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52e59918-cf12-4d73-a4e0-31f38e792dc4.jpg?1783940692"
    }
}
