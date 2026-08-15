package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MustBeBlockedEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Gaea's Protector
 * {3}{G}
 * Creature — Elemental Warrior
 * 4/2
 * Gaea's Protector must be blocked if able.
 */
val GaeasProtector = card("Gaea's Protector") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental Warrior"
    power = 4
    toughness = 2
    oracleText = "Gaea's Protector must be blocked if able."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = MustBeBlockedEffect(EffectTarget.Self, allCreatures = false)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "162"
        artist = "Grzegorz Rutkowski"
        flavorText = "Fallen Phyrexians were transmuted into elementals by Gaea long ago, but Yavimaya's other inhabitants still regard them with unease."
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7bc5ce71-282c-43f9-b12a-edd8f4ab6006.jpg?1562738294"
    }
}
