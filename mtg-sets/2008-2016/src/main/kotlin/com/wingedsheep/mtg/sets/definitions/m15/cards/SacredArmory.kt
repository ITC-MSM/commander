package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sacred Armory
 * {2}
 * Artifact
 * {2}: Target creature gets +1/+0 until end of turn.
 */
val SacredArmory = card("Sacred Armory") {
    manaCost = "{2}"
    typeLine = "Artifact"
    oracleText = "{2}: Target creature gets +1/+0 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{2}")
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(1, 0, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "228"
        artist = "Yeong-Hao Han"
        flavorText = "Arrive for worship. Leave for war."
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b05fde97-ab24-40c9-a1db-8844c3e62fc3.jpg?1783939155"
    }
}
