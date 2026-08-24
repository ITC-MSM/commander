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
 * Kor Bladewhirl
 * {1}{W}
 * Creature — Kor Soldier Ally
 * 2/2
 * Rally — Whenever this creature or another Ally you control enters, creatures you control gain first strike until end of turn.
 *
 * Rally is an ability word, not a keyword: the trigger is an ANY-bound enters trigger over
 * Allies you control, so the creature's own arrival fires it alongside every later Ally.
 */
val KorBladewhirl = card("Kor Bladewhirl") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Soldier Ally"
    power = 2
    toughness = 2
    oracleText = "Rally — Whenever this creature or another Ally you control enters, creatures you control gain " +
        "first strike until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype("Ally").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Patterns.Group.grantKeywordToAll(
            Keyword.FIRST_STRIKE,
            Filters.Group.creaturesYouControl,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "34"
        artist = "Steven Belledin"
        flavorText = "The high-pitched whirl of a hook is the only battle cry she needs."
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e31b12c7-df11-4450-95e1-b9a5aa97af0e.jpg?1783938218"
    }
}
