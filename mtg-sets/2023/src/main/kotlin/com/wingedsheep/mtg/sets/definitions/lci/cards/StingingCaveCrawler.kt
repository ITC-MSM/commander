package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stinging Cave Crawler — {2}{B}
 * Creature — Insect Horror
 * 1/3
 *
 * Deathtouch
 * Descend 4 — Whenever this creature attacks, if there are four or more permanent cards in your
 * graveyard, you draw a card and you lose 1 life.
 *
 * The "Descend 4" ability word has no rules meaning of its own (CR 207.2c) — it is purely
 * reminder framing for the intervening-if condition (four or more permanent cards in your
 * graveyard, CR 603.4). Modeled as an attack trigger with
 * an intervening-if [Conditions.CardsInGraveyardMatchingAtLeast]: the ability triggers on attack
 * only when the condition already holds, and it is rechecked at resolution — if the graveyard count
 * drops below four before it resolves, the ability is removed from the stack without effect.
 *
 * On resolution the controller draws a card and loses 1 life (both mandatory, no choice), so the
 * effect is a composite of [Effects.DrawCards] on the controller and [Effects.LoseLife] targeting
 * the controller.
 */
val StingingCaveCrawler = card("Stinging Cave Crawler") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect Horror"
    power = 1
    toughness = 3
    oracleText = "Deathtouch\n" +
        "Descend 4 — Whenever this creature attacks, if there are four or more permanent cards in " +
        "your graveyard, you draw a card and you lose 1 life."

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.CardsInGraveyardMatchingAtLeast(4, GameObjectFilter.Permanent)
        effect = Effects.DrawCards(1) then Effects.LoseLife(1, EffectTarget.Controller)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "124"
        artist = "Dan Murayama Scott"
        flavorText = "Caverns uninhabited by mycoids are often infested with worse."
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a230208-84ae-4f48-afbd-a0d50596ad27.jpg?1782694510"
    }
}
