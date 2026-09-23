package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.grantedLoyaltyAbility
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * The granted −10 is an ordinary loyalty ability on each planeswalker you control — the Jace token
 * included — so it keeps sorcery timing and the one-loyalty-activation-per-turn limit.
 */
val AvatarOfBurgeoningEchoes = card("Avatar of Burgeoning Echoes") {
    manaCost = "{G}{U}"
    colorIdentity = "GU"
    typeLine = "Creature — Avatar"
    oracleText = "Landfall — Whenever a land you control enters, empower Jace 2. (Put two loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")\n" +
        "Planeswalkers you control have \"[−10]: Put a +1/+1 counter on target creature for each land you control.\""
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Patterns.Mechanic.empowerJace(2)
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = grantedLoyaltyAbility(-10) {
                val creature = target("target creature", Targets.Creature)
                effect = Effects.AddDynamicCounters(
                    Counters.PLUS_ONE_PLUS_ONE,
                    DynamicAmounts.landsYouControl(),
                    creature
                )
                description = "Put a +1/+1 counter on target creature for each land you control."
            },
            filter = GroupFilter(GameObjectFilter.Planeswalker.youControl())
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "122"
        artist = "Eli Minaya"
        imageUri = "https://cards.scryfall.io/normal/front/5/9/5905995b-7a20-4602-a7cc-90aa5089a082.jpg?1788878208"
        inBooster = false
    }
}
