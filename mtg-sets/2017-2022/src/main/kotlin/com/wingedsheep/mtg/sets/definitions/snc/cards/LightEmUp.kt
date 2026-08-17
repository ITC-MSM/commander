package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Light 'Em Up
 * {1}{R}
 * Sorcery
 * Casualty 2 (As you cast this spell, you may sacrifice a creature with power 2 or greater. When you do, copy this spell and you may choose a new target for the copy.)
 * Light 'Em Up deals 2 damage to target creature or planeswalker.
 *
 * Casualty 2 (CR 702.153) is the printed [KeywordAbility.Casualty]; the reflexive copy it queues
 * may choose a new target.
 */
val LightEmUp = card("Light 'Em Up") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Casualty 2 (As you cast this spell, you may sacrifice a creature with power 2 or greater. When you do, copy this spell and you may choose a new target for the copy.)\nLight 'Em Up deals 2 damage to target creature or planeswalker."

    keywordAbility(KeywordAbility.casualty(2))

    spell {
        val t = target("target", Targets.CreatureOrPlaneswalker)
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "113"
        artist = "Tony Foti"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee31d56a-9b7e-4de0-81cb-3b7c76b6fbd5.jpg?1783923117"
    }
}
