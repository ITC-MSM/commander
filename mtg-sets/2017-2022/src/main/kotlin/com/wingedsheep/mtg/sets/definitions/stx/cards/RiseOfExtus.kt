package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Rise of Extus — Strixhaven: School of Mages #226 (canonical printing)
 * {4}{W/B}{W/B} · Sorcery
 *
 * Exile target creature. Exile up to one target instant or sorcery card from a graveyard.
 * Learn.
 *
 * Two independent target requirements, and the second is genuinely optional: `optional = true` on
 * the [TargetObject] is the printed "up to one", so the spell is castable — and resolves — with no
 * graveyard card chosen. When it is declined, `ContextTarget(1)` resolves to null and the second
 * exile is a no-op rather than an error.
 *
 * Exile, not destroy: indestructible and regeneration do not save the creature, and it leaves no
 * graveyard trigger behind. The graveyard half is incidental instant/sorcery hate — Strixhaven is
 * a spells set, so it is aimed at flashback and Mystical Archive recursion.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val RiseOfExtus = card("Rise of Extus") {
    manaCost = "{4}{W/B}{W/B}"
    colorIdentity = "WB"
    typeLine = "Sorcery"
    oracleText = "Exile target creature. Exile up to one target instant or sorcery card from a " +
        "graveyard.\n" +
        "Learn. (You may reveal a Lesson card you own from outside the game and put it into your " +
        "hand, or discard a card to draw a card.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        target(
            "up to one target instant or sorcery card from a graveyard",
            TargetObject(filter = TargetFilter.InstantOrSorceryInGraveyard, optional = true)
        )
        effect = Effects.Exile(creature) then
            Effects.Exile(EffectTarget.ContextTarget(1)) then
            Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "226"
        artist = "Wylie Beckert"
        flavorText = "With one lethal strike, Extus took control of the Oriq and took his first step towards vengeance."
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bbf97a71-485e-4d47-98de-bdf6f6dae0c2.jpg?1783927296"
    }
}
