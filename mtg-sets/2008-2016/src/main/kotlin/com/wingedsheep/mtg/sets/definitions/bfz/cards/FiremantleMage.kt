package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Firemantle Mage
 * {2}{R}
 * Creature — Human Shaman Ally
 * 2/2
 * Rally — Whenever this creature or another Ally you control enters, creatures you control gain menace until end of turn. (A creature with menace can't be blocked except by two or more creatures.)
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so the creature's own arrival fires it alongside every later Ally.
 */
val FiremantleMage = card("Firemantle Mage") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Shaman Ally"
    power = 2
    toughness = 2
    oracleText = "Rally — Whenever this creature or another Ally you control enters, creatures you control gain " +
        "menace until end of turn. (A creature with menace can't be blocked except by two or more " +
        "creatures.)"

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Patterns.Group.grantKeywordToAll(
            Keyword.MENACE,
            Filters.Group.creaturesYouControl,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "145"
        artist = "Chris Rahn"
        flavorText = "\"Come on, you twisted things! Don't you want to get acquainted?\""
        imageUri = "https://cards.scryfall.io/normal/front/1/9/197411bf-3307-4056-a7d4-31db0d4b8f9a.jpg?1783938195"
    }
}
