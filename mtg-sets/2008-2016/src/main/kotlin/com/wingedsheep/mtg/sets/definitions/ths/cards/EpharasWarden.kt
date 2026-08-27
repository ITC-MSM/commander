package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ephara's Warden
 * {3}{W}
 * Creature — Human Cleric
 * 1 / 2
 *
 * {T}: Tap target creature with power 3 or less.
 */
val EpharasWarden = card("Ephara's Warden") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 2
    oracleText = "{T}: Tap target creature with power 3 or less."

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", Targets.CreatureWithPowerAtMost(3))
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "Zack Stella"
        flavorText = "\"When you threaten the sanctity of the polis, you insult Ephara herself. If she doesn't smite you, I will.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/1/71f7f788-2795-46a7-82ae-270f1e9415ca.jpg"
    }
}
