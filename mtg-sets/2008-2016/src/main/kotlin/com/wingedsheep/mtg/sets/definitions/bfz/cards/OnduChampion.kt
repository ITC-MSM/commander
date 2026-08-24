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
 * Ondu Champion
 * {2}{R}{R}
 * Creature — Minotaur Warrior Ally
 * 4/3
 * Rally — Whenever this creature or another Ally you control enters, creatures you control gain trample until end of turn.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so the creature's own arrival fires it alongside every later Ally.
 */
val OnduChampion = card("Ondu Champion") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Minotaur Warrior Ally"
    power = 4
    toughness = 3
    oracleText = "Rally — Whenever this creature or another Ally you control enters, creatures you control gain " +
        "trample until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Patterns.Group.grantKeywordToAll(
            Keyword.TRAMPLE,
            Filters.Group.creaturesYouControl,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "149"
        artist = "Volkan Baǵa"
        flavorText = "\"Put a minotaur at the head of an army and suddenly all the soldiers act like they have " +
            "horns.\"\n" +
            "—Gideon Jura"
        imageUri = "https://cards.scryfall.io/normal/front/7/0/708e1979-902b-410a-ae46-3fd1d2acc31d.jpg?1783938194"
    }
}
