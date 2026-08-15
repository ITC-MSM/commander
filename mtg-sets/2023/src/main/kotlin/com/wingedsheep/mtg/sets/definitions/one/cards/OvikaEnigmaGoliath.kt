package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.ForEachInCollectionEffect
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Ovika, Enigma Goliath
 * {5}{U}{R}
 * Legendary Creature — Phyrexian Nightmare
 * 6/6
 *
 * Flying
 * Ward—{3}, Pay 3 life.
 * Whenever you cast a noncreature spell, create X 1/1 red Phyrexian Goblin creature
 * tokens, where X is the mana value of that spell. They gain haste until end of turn.
 *
 * Implementation notes:
 * - Ward—{3}, Pay 3 life is a single composite ward cost ([WardCost.Composite] of a mana
 *   part and a life part), the Gisa, the Hellraiser shape (CR 702.21a).
 * - The token count reads the triggering spell's mana value via
 *   [EntityReference.Triggering]; for {X} spells CR 202.3e fixes the X portion to the
 *   chosen value while the spell is on the stack, matching the 2024-11-08 ruling.
 * - "They gain haste" iterates the [CREATED_TOKENS] pipeline collection the create-token
 *   executor publishes, so only the tokens made by this trigger are granted haste — and
 *   only until end of turn, so a later control change doesn't inherit permanent haste.
 */
val OvikaEnigmaGoliath = card("Ovika, Enigma Goliath") {
    manaCost = "{5}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Legendary Creature — Phyrexian Nightmare"
    power = 6
    toughness = 6
    oracleText = "Flying\n" +
        "Ward—{3}, Pay 3 life.\n" +
        "Whenever you cast a noncreature spell, create X 1/1 red Phyrexian Goblin creature " +
        "tokens, where X is the mana value of that spell. They gain haste until end of turn."

    keywords(Keyword.FLYING)
    keywordAbility(
        KeywordAbility.wardComposite(WardCost.Mana("{3}"), WardCost.Life(3))
    )

    // Whenever you cast a noncreature spell, create X 1/1 red Phyrexian Goblin tokens,
    // where X is the mana value of that spell. They gain haste until end of turn.
    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.Composite(listOf(
            Effects.CreateToken(
                count = DynamicAmount.EntityProperty(
                    EntityReference.Triggering,
                    EntityNumericProperty.ManaValue,
                ),
                power = 1,
                toughness = 1,
                colors = setOf(Color.RED),
                creatureTypes = setOf("Phyrexian", "Goblin"),
                imageUri = "https://cards.scryfall.io/normal/front/3/6/3663e79b-2bf9-44af-a638-c0ad9067d8d4.jpg?1783918169",
            ),
            ForEachInCollectionEffect(
                CREATED_TOKENS,
                Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self),
            ),
        ))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "213"
        artist = "Antonio José Manzanedo"
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b298cf34-7aa5-4f97-a86c-7f28d2113b87.jpg?1783917997"
        ruling(
            "2024-11-08",
            "If a spell on the stack has {X} in its cost, use the value chosen for X to determine its mana value."
        )
    }
}
