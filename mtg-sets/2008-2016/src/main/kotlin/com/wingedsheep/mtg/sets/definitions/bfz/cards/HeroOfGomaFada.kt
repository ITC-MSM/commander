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
 * Hero of Goma Fada
 * {4}{W}
 * Creature — Human Knight Ally
 * 4/3
 * Rally — Whenever this creature or another Ally you control enters, creatures you control gain indestructible until end of turn.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so the creature's own arrival fires it alongside every later Ally.
 */
val HeroOfGomaFada = card("Hero of Goma Fada") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight Ally"
    power = 4
    toughness = 3
    oracleText = "Rally — Whenever this creature or another Ally you control enters, creatures you control gain " +
        "indestructible until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Patterns.Group.grantKeywordToAll(
            Keyword.INDESTRUCTIBLE,
            Filters.Group.creaturesYouControl,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "31"
        artist = "Lake Hurwitz"
        flavorText = "\"They seek to break us. Let us show them our strength!\""
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4ca3400c-0687-4a92-9236-f5a0d7eae337.jpg?1783938218"
    }
}
