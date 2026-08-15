package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Voracious Cobra
 * {2}{R}{G}
 * Creature — Snake
 * 2/2
 * First strike
 * Whenever this creature deals combat damage to a creature, destroy that creature.
 *
 * Oracle errata: original printing read "bury that creature" (Destroy + can't be regenerated).
 * Modern oracle text drops the regeneration clause.
 */
val VoraciousCobra = card("Voracious Cobra") {
    manaCost = "{2}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Snake"
    power = 2
    toughness = 2
    oracleText = "First strike\nWhenever this creature deals combat damage to a creature, destroy that creature."

    keywords(Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToCreature
        effect = Effects.Destroy(EffectTarget.TriggeringEntity)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "288"
        artist = "Terese Nielsen"
        flavorText = "There's no known antidote for the cobra's venom . . . or its appetite."
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d8c5669-11a9-4d95-8431-7065037f1fb6.jpg?1562926724"
    }
}
