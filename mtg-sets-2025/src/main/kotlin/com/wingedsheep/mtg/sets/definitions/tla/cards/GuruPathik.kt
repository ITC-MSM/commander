package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Guru Pathik
 * {2}{G/U}{G/U}
 * Legendary Creature — Human Monk Ally
 * 2/4
 *
 * When Guru Pathik enters, look at the top five cards of your library. You may
 * reveal a Lesson, Saga, or Shrine card from among them and put it into your hand.
 * Put the rest on the bottom of your library in a random order.
 * Whenever you cast a Lesson, Saga, or Shrine spell, put a +1/+1 counter on
 * another target creature you control.
 */
val GuruPathik = card("Guru Pathik") {
    manaCost = "{2}{G/U}{G/U}"
    colorIdentity = "GU"
    typeLine = "Legendary Creature — Human Monk Ally"
    power = 2
    toughness = 4
    oracleText = "When Guru Pathik enters, look at the top five cards of your library. You may reveal a Lesson, Saga, or Shrine card from among them and put it into your hand. Put the rest on the bottom of your library in a random order.\n" +
        "Whenever you cast a Lesson, Saga, or Shrine spell, put a +1/+1 counter on another target creature you control."

    // ETB: dig five, optionally reveal a Lesson/Saga/Shrine to hand, bottom the rest randomly.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(5),
            filter = GameObjectFilter.Any.withAnySubtype("Lesson", "Saga", "Shrine"),
            prompt = "You may reveal a Lesson, Saga, or Shrine card to put into your hand"
        )
    }

    // Whenever you cast a Lesson, Saga, or Shrine spell, put a +1/+1 counter on another target creature you control.
    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Lesson", "Saga", "Shrine")
        )
        val creature = target(
            "another target creature you control",
            TargetCreature(filter = TargetFilter.OtherCreatureYouControl)
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "223"
        artist = "Dee Nguyen"
        imageUri = "https://cards.scryfall.io/normal/front/2/2/224a2241-0dce-4008-8d9c-86db40ec5f8b.jpg?1764121623"
    }
}
