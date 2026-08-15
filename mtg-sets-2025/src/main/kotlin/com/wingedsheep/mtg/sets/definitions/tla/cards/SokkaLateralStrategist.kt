package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.events.AttackPredicate

/**
 * Sokka, Lateral Strategist
 * {1}{W/U}{W/U}
 * Legendary Creature — Human Warrior Ally
 * 2/4
 *
 * Vigilance
 * Whenever Sokka and at least one other creature attack, draw a card.
 *
 * "At least one other creature" means Sokka plus one or more — a total of two or more
 * attackers — so this is the SELF-bound battalion shape `AttackerCountAtLeast(2)`.
 */
val SokkaLateralStrategist = card("Sokka, Lateral Strategist") {
    manaCost = "{1}{W/U}{W/U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Human Warrior Ally"
    power = 2
    toughness = 4
    oracleText = "Vigilance\n" +
        "Whenever Sokka and at least one other creature attack, draw a card."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.attacks(requires = setOf(AttackPredicate.AttackerCountAtLeast(2)))
        effect = Effects.DrawCards(1)
        description = "Whenever Sokka and at least one other creature attack, draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "241"
        artist = "Axel Sauerwald"
        flavorText = "\"That's called Sokka style. Learn it!\""
        imageUri = "https://cards.scryfall.io/normal/front/1/3/1326c5db-4615-495f-8e30-376243d91352.jpg?1764121782"
    }
}
