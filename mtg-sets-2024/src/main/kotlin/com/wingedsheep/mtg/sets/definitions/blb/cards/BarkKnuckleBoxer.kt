package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bark-Knuckle Boxer
 * {1}{G}
 * Creature — Raccoon Berserker
 * 3/2
 *
 * Whenever you expend 4, this creature gains indestructible until end of turn.
 * (You expend 4 as you spend your fourth total mana to cast spells during a turn.)
 */
val BarkKnuckleBoxer = card("Bark-Knuckle Boxer") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Raccoon Berserker"
    power = 3
    toughness = 2
    oracleText = "Whenever you expend 4, this creature gains indestructible until end of turn. " +
        "(You expend 4 as you spend your fourth total mana to cast spells during a turn.)"

    triggeredAbility {
        trigger = Triggers.Expend(4)
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "164"
        artist = "Filip Burburan"
        flavorText = "May your bark be worse than your bite.\n—Raccoonfolk war blessing"
        imageUri = "https://cards.scryfall.io/normal/front/5/8/582637a9-6aa0-4824-bed7-d5fc91bda35e.jpg?1721426764"
    }
}
