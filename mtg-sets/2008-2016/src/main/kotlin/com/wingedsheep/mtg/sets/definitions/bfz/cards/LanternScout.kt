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
 * Lantern Scout
 * {2}{W}
 * Creature — Human Scout Ally
 * 3/2
 * Rally — Whenever this creature or another Ally you control enters, creatures you control gain lifelink until end of turn.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so the creature's own arrival fires it alongside every later Ally.
 */
val LanternScout = card("Lantern Scout") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Scout Ally"
    power = 3
    toughness = 2
    oracleText = "Rally — Whenever this creature or another Ally you control enters, creatures you control gain " +
        "lifelink until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Patterns.Group.grantKeywordToAll(
            Keyword.LIFELINK,
            Filters.Group.creaturesYouControl,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "37"
        artist = "Steven Belledin"
        flavorText = "Hedron lanterns fend off more than just the darkness."
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fea3b271-226d-4223-8be8-51b5b2b7cae8.jpg?1783938218"
    }
}
