package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

val SureshotSower = card("Sureshot Sower") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Archer"
    power = 3
    toughness = 1
    oracleText = "Reach\n{3}{G}, Discard this card: Destroy target creature with flying."

    keywords(Keyword.REACH)
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{G}"), Costs.DiscardSelf)
        activateFromZone = Zone.HAND
        val creature = target("creature with flying", TargetObject(filter = TargetFilter.Creature.withKeyword(Keyword.FLYING)))
        effect = Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Zara Alfonso"
        flavorText = "Her specialized arrows turn targets to compost before they even hit the ground."
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b635389c-e286-4edb-80d1-23dbe4a18857.jpg?1789614751"
        inBooster = false
    }
}
