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
 * Makindi Patrol
 * {2}{W}
 * Creature — Human Knight Ally
 * 2/3
 * Rally — Whenever this creature or another Ally you control enters, creatures you control gain vigilance until end of turn.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so the creature's own arrival fires it alongside every later Ally.
 */
val MakindiPatrol = card("Makindi Patrol") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight Ally"
    power = 2
    toughness = 3
    oracleText = "Rally — Whenever this creature or another Ally you control enters, creatures you control gain " +
        "vigilance until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Patterns.Group.grantKeywordToAll(
            Keyword.VIGILANCE,
            Filters.Group.creaturesYouControl,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "39"
        artist = "David Palumbo"
        flavorText = "Working with his noble mount, he notices every form on the horizon, every scent in the air, " +
            "every tremor in the earth."
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae03f018-3062-4b1c-99b8-13fcffd70b49.jpg?1783938218"
    }
}
