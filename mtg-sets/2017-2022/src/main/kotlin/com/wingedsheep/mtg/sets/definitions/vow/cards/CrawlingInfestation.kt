package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Crawling Infestation — Innistrad: Crimson Vow #193
 * {2}{G} · Enchantment · Uncommon
 *
 * At the beginning of your upkeep, you may mill two cards.
 * Whenever one or more creature cards are put into your graveyard from anywhere during your turn,
 * create a 1/1 green Insect creature token. This ability triggers only once each turn.
 *
 * The two abilities are one engine: the upkeep mill is the cheapest way to feed the second, but
 * the second reads "from anywhere", so a creature dying in combat or discarded feeds it just as
 * well — hence [Triggers.CardsPutIntoYourGraveyard] (the batching trigger, one Insect for a
 * whole board wipe) rather than a dies trigger.
 *
 * Two riders, two different knobs, and they are not interchangeable:
 *  - "during your turn" is a `triggerRestriction` ([Conditions.IsYourTurn]);
 *  - "This ability triggers only once each turn" is `oncePerTurn = true`, the cap spent by the
 *    first trigger. It is *not* `effectOncePerTurn` — nothing here is optional, so there is no
 *    decline that could leave the turn's use unspent.
 */
val CrawlingInfestation = card("Crawling Infestation") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, you may mill two cards. (You may put the top " +
        "two cards of your library into your graveyard.)\n" +
        "Whenever one or more creature cards are put into your graveyard from anywhere during " +
        "your turn, create a 1/1 green Insect creature token. This ability triggers only once " +
        "each turn."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = MayEffect(Patterns.Library.mill(2))
        description = "At the beginning of your upkeep, you may mill two cards."
    }

    triggeredAbility {
        trigger = Triggers.CardsPutIntoYourGraveyard(GameObjectFilter.Creature)
        triggerRestriction = Conditions.IsYourTurn
        oncePerTurn = true
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Insect")
        )
        description = "Whenever one or more creature cards are put into your graveyard from " +
            "anywhere during your turn, create a 1/1 green Insect creature token. This ability " +
            "triggers only once each turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "193"
        artist = "Khurrum"
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0ab55dae-5393-4356-9f81-66d6ea5d23c0.jpg?1783924816"
        ruling(
            "2021-11-19",
            "Crawling Infestation must be on the battlefield for its ability to trigger. If it's " +
                "destroyed at the same time as one or more creatures you own, its ability won't trigger."
        )
    }
}
