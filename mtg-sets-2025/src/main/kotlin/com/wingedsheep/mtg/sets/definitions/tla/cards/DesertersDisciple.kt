package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Deserter's Disciple
 * {1}{R}
 * Creature — Human Rebel Ally
 * 2/2
 * {T}: Another target creature you control with power 2 or less can't be blocked this turn.
 */
val DesertersDisciple = card("Deserter's Disciple") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Rebel Ally"
    power = 2
    toughness = 2
    oracleText = "{T}: Another target creature you control with power 2 or less can't be blocked this turn."

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", TargetCreature(filter = TargetFilter.OtherCreatureYouControl.powerAtMost(2)))
        effect = GrantKeywordEffect(AbilityFlag.CANT_BE_BLOCKED.name, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "131"
        artist = "HAIKEI"
        flavorText = "\"Follow me! I can get you outta here!\""
        imageUri = "https://cards.scryfall.io/normal/front/1/4/14f2ed5a-042b-4cce-82ad-cfb4bd511d98.jpg?1764120901"
    }
}
