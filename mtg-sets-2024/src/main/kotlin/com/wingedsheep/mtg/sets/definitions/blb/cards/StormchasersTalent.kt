package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Stormchaser's Talent {U}
 * Enchantment — Class
 *
 * (Gain the next level as a sorcery to add its ability.)
 *
 * When this Class enters, create a 1/1 blue and red Otter creature token with prowess.
 *
 * {3}{U}: Level 2
 * When this Class becomes level 2, return target instant or sorcery card from your
 * graveyard to your hand.
 *
 * {5}{U}: Level 3
 * Whenever you cast an instant or sorcery spell, create a 1/1 blue and red Otter
 * creature token with prowess.
 */
val StormchasersTalent = card("Stormchaser's Talent") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Class"
    oracleText = "When this Class enters, create a 1/1 blue and red Otter creature token with prowess.\n{3}{U}: Level 2 — When this Class becomes level 2, return target instant or sorcery card from your graveyard to your hand.\n{5}{U}: Level 3 — Whenever you cast an instant or sorcery spell, create a 1/1 blue and red Otter creature token with prowess."

    // Level 1: ETB — create a 1/1 blue and red Otter with prowess
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE, Color.RED),
            creatureTypes = setOf("Otter"),
            keywords = setOf(Keyword.PROWESS)
        )
    }

    // Level 2: When this becomes level 2, return target instant or sorcery from YOUR graveyard to hand
    classLevel(2, "{3}{U}") {
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            val card = target(
                "instant or sorcery card in your graveyard",
                TargetObject(filter = TargetFilter.InstantOrSorceryInYourGraveyard)
            )
            effect = Effects.ReturnToHand(card)
        }
    }

    // Level 3: Whenever you cast an instant or sorcery, create a 1/1 blue and red Otter with prowess
    classLevel(3, "{5}{U}") {
        triggeredAbility {
            trigger = Triggers.YouCastInstantOrSorcery
            effect = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.BLUE, Color.RED),
                creatureTypes = setOf("Otter"),
                keywords = setOf(Keyword.PROWESS)
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "75"
        artist = "Christina Kraus"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a36e682d-b43d-4e08-bf5b-70d7e924dbe5.jpg?1739650074"
        ruling("2024-07-26", "The level 3 class ability of Stormchaser's Talent resolves before the spell or ability that caused it to trigger. It resolves even if that spell is countered.")
        ruling("2024-07-26", "Gaining a level is a normal activated ability. It uses the stack and can be responded to.")
        ruling("2024-07-26", "You can't activate the first level ability of a Class unless that Class is level 1. You can't activate the second level ability of a Class unless that Class is level 2.")
        ruling("2024-07-26", "Gaining a level won't remove abilities that a Class had at a previous level.")
    }
}
