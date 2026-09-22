package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

val AfterthoughtSentry = card("Afterthought Sentry") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Gargoyle"
    oracleText = "{2}: This creature gains flying until end of turn.\nWhenever this creature attacks, exile up to one target card from a graveyard."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        val card = target("card", TargetObject(filter = TargetFilter.CardInGraveyard, optional = true))
        effect = Effects.Exile(card)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "166"
        artist = "Andrew Mar"
        flavorText = "An eyeless guard of stone watches a forgotten annex of memory, granite feathers rustling in breezes no one thinks to remember."
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d4b3bf7-a149-4099-b97d-4e36a87dfa60.jpg?1789385875"
        inBooster = false
    }
}
