package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Master Piandao
 * {4}{W}
 * Legendary Creature — Human Warrior Ally
 * 4/4
 *
 * First strike
 * Whenever Master Piandao attacks, look at the top four cards of your library. You may reveal
 * an Ally, Equipment, or Lesson card from among them and put it into your hand. Put the rest on
 * the bottom of your library in a random order.
 *
 * The attack trigger reuses the shared [Patterns.Library.lookAtTopRevealMatchingToHand] recipe
 * (Guru Pathik shape): dig four, optionally reveal one card matching the subtype predicate to
 * hand, then bottom the remainder in a random order. "Ally, Equipment, or Lesson" is a
 * type-agnostic subtype union via [GameObjectFilter.Any.withAnySubtype], since the three subtypes
 * span creatures, artifacts, and enchantments/sorceries respectively.
 */
val MasterPiandao = card("Master Piandao") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Warrior Ally"
    power = 4
    toughness = 4
    oracleText = "First strike\n" +
        "Whenever Master Piandao attacks, look at the top four cards of your library. You may reveal " +
        "an Ally, Equipment, or Lesson card from among them and put it into your hand. Put the rest on " +
        "the bottom of your library in a random order."

    keywords(Keyword.FIRST_STRIKE)

    // Whenever Master Piandao attacks, dig four, optionally reveal an Ally/Equipment/Lesson to hand,
    // bottom the rest randomly.
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(4),
            filter = GameObjectFilter.Any.withAnySubtype("Ally", "Equipment", "Lesson"),
            prompt = "You may reveal an Ally, Equipment, or Lesson card to put into your hand"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "28"
        artist = "Brian Yuen"
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3e4a042c-f2a6-45e7-9444-acd1ca838b87.jpg?1764120075"
    }
}
