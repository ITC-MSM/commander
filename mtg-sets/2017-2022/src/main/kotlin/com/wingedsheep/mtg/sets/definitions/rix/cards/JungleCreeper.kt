package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Jungle Creeper
 * {1}{B}{G}
 * Creature — Elemental
 * 3/3
 * {3}{B}{G}: Return this card from your graveyard to your hand.
 */
val JungleCreeper = card("Jungle Creeper") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Elemental"
    oracleText = "{3}{B}{G}: Return this card from your graveyard to your hand."
    power = 3
    toughness = 3

    activatedAbility {
        cost = Costs.Mana("{3}{B}{G}")
        effect = Effects.ReturnToHandFromGraveyard(EffectTarget.Self)
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "161"
        artist = "Matt Stewart"
        flavorText = "\"You cannot kill it, not for long. It ripens where the ground is soaked " +
            "with the blood of the slaughtered.\"\n—Atiuru, priest of the Verdant Sun"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d71282d-021c-4028-9ab7-f10e43e92c80.jpg?1783935274"
    }
}
