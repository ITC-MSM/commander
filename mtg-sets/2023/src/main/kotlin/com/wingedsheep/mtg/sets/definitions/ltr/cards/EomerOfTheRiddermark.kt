package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Éomer of the Riddermark
 * {4}{R}
 * Legendary Creature — Human Knight
 * 5/4
 *
 * Haste
 * Whenever Éomer attacks, if you control a creature with the greatest power among creatures
 * on the battlefield, create a 1/1 white Human Soldier creature token.
 */
val EomerOfTheRiddermark = card("Éomer of the Riddermark") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Knight"
    power = 5
    toughness = 4
    oracleText = "Haste\nWhenever Éomer attacks, if you control a creature with the greatest power among creatures on the battlefield, create a 1/1 white Human Soldier creature token."

    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.Attacks
        // "if you control a creature with the greatest power among creatures on the battlefield"
        // ≡ you control a creature AND your max creature-power >= the global max creature-power.
        triggerCondition = Conditions.All(
            Conditions.ControlCreature,
            Compare(
                DynamicAmounts.battlefield(Player.You, GameObjectFilter.Creature).maxPower(),
                ComparisonOperator.GTE,
                DynamicAmounts.battlefield(Player.Each, GameObjectFilter.Creature).maxPower()
            )
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human", "Soldier"),
            imageUri = "https://cards.scryfall.io/normal/front/a/6/a6181330-7521-4ec6-be6c-b35487c2d2d4.jpg?1699974464"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "121"
        artist = "John Di Giovanni"
        flavorText = "But northward the white crest of Éomer led the great front of the Rohirrim which he had again gathered and marshaled."
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d920fcaf-4988-4186-962d-cdda25d79e7b.jpg?1686968869"
    }
}
