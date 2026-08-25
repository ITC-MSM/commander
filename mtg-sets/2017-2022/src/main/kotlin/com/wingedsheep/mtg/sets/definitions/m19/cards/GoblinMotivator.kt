package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Motivator
 * {R}
 * Creature — Goblin Warrior
 * 1/1
 * {T}: Target creature gains haste until end of turn. (It can attack and {T} this turn.)
 */
val GoblinMotivator = card("Goblin Motivator") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 1
    toughness = 1
    oracleText = "{T}: Target creature gains haste until end of turn. (It can attack and {T} this turn.)"

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.HASTE, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "143"
        artist = "Johann Bodin"
        flavorText = "Small words stoke large flames."
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94b3a4fb-9024-45ef-a54b-cf3a9fa5b9c2.jpg"
    }
}
