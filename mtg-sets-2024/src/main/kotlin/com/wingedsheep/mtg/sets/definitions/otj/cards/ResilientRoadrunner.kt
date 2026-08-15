package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Resilient Roadrunner
 * {1}{R}
 * Creature — Bird
 * 2/2
 * Haste, protection from Coyotes
 * {3}: This creature can't be blocked this turn except by creatures with haste.
 */
val ResilientRoadrunner = card("Resilient Roadrunner") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Bird"
    power = 2
    toughness = 2
    oracleText = "Haste, protection from Coyotes\n" +
        "{3}: This creature can't be blocked this turn except by creatures with haste."

    keywords(Keyword.HASTE)
    keywordAbility(KeywordAbility.Protection(ProtectionScope.Subtype("Coyote")))

    activatedAbility {
        cost = Costs.Mana("{3}")
        effect = Effects.GrantCantBeBlockedExceptBy(
            EffectTarget.Self,
            GameObjectFilter.Creature.withKeyword(Keyword.HASTE)
        )
        description = "This creature can't be blocked this turn except by creatures with haste."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "141"
        artist = "David Auden Nash"
        flavorText = "\"Chasing the roadrunner\"\n" +
            "—Omenport slang for wasting energy on impossible pursuits"
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e07d3ee9-d3c4-4f07-839e-ec81c2587ae0.jpg?1712355827"
    }
}
