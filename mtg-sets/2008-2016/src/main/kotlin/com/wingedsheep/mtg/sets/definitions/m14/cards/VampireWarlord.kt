package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vampire Warlord
 * {4}{B}
 * Creature — Vampire Warrior
 * 4 / 2
 * Sacrifice another creature: Regenerate this creature. (The next time this creature would be
 * destroyed this turn, instead tap it, remove it from combat, and heal all damage on it.)
 */
val VampireWarlord = card("Vampire Warlord") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Warrior"
    power = 4
    toughness = 2
    oracleText = "Sacrifice another creature: Regenerate this creature. (The next time this creature would be destroyed this turn, instead tap it, remove it from combat, and heal all damage on it.)"

    activatedAbility {
        cost = Costs.SacrificeAnother(GameObjectFilter.Creature)
        effect = RegenerateEffect(EffectTarget.Self)
        description = "Sacrifice another creature: Regenerate this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "120"
        artist = "Wesley Burt"
        flavorText = "\"How can you serve me? By dying.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e07929b-450c-45b0-85e6-512ad280a122.jpg"
    }
}
