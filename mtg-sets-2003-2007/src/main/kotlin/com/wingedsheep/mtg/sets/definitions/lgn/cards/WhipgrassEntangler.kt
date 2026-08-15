package com.wingedsheep.mtg.sets.definitions.lgn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration

/**
 * Whipgrass Entangler
 * {2}{W}
 * Creature — Human Cleric
 * 1/3
 * {1}{W}: Until end of turn, target creature gains "This creature can't attack or block unless
 * its controller pays {1} for each Cleric on the battlefield."
 */
val WhipgrassEntangler = card("Whipgrass Entangler") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 3
    oracleText = "{1}{W}: Until end of turn, target creature gains \"This creature can't attack or block unless its controller pays {1} for each Cleric on the battlefield.\""

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        val creature = target("creature", Targets.Creature)
        effect = Effects.GrantAttackBlockTaxPerCreatureType(
            target = creature,
            creatureType = "Cleric",
            manaCostPer = "{1}",
            duration = Duration.EndOfTurn
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "26"
        artist = "Ben Thompson"
        flavorText = "\"Now that I have your attention, perhaps I can tell you of the Order.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c0b18b09-b1ff-479d-bd1c-cb8620a34fe4.jpg?1562933704"
    }
}
