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
 * Resolute Blademaster
 * {3}{R}{W}
 * Creature — Human Soldier Ally
 * 2/2
 * Rally — Whenever this creature or another Ally you control enters, creatures you control gain double strike until end of turn.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so the creature's own arrival fires it alongside every later Ally.
 */
val ResoluteBlademaster = card("Resolute Blademaster") {
    manaCost = "{3}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Creature — Human Soldier Ally"
    power = 2
    toughness = 2
    oracleText = "Rally — Whenever this creature or another Ally you control enters, creatures you control gain " +
        "double strike until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Patterns.Group.grantKeywordToAll(
            Keyword.DOUBLE_STRIKE,
            Filters.Group.creaturesYouControl,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "218"
        artist = "Joseph Meehan"
        flavorText = "\"Great steel is born in the hottest forges. Great soldiers are born in war.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/e/bea96f8d-0c57-448d-8145-51f9cca04432.jpg?1783938178"
    }
}
