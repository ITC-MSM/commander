package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

val TheoreticalNecromancer = card("Theoretical Necromancer") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Warlock"
    power = 4
    toughness = 1
    oracleText = "{3}{B}, Exile this card from your graveyard: Return another target creature card from your graveyard to your hand."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{B}"), Costs.ExileSelf)
        activateFromZone = Zone.GRAVEYARD
        val creature = target("another creature card in your graveyard",
            TargetObject(filter = TargetFilter.CreatureInYourGraveyard.other()))
        effect = Effects.ReturnToHand(creature)
        description = "Return another target creature card from your graveyard to your hand."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "69"
        artist = "Chris Rallis"
        flavorText = "\"Mortality is just one more parameter to define and defy.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e36a7908-1e22-494b-adb4-e72ac0974d62.jpg?1789556734"
        inBooster = false
    }
}
