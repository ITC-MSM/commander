package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Vial of Poison
 * {1}
 * Artifact
 * {1}, Sacrifice this artifact: Target creature gains deathtouch until end of turn.
 */
val VialOfPoison = card("Vial of Poison") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{1}, Sacrifice this artifact: Target creature gains deathtouch until end of turn. (Any amount of damage it deals to a creature is enough to destroy it.)"

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        val recipient = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, recipient)
        description = "{1}, Sacrifice this artifact: Target creature gains deathtouch until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "226"
        artist = "Franz Vohwinkel"
        flavorText = "There are worse ways to die, but not many."
        imageUri = "https://cards.scryfall.io/normal/front/7/7/7769159b-5a6a-45e5-b69b-8db2a6ef5418.jpg"
    }
}
