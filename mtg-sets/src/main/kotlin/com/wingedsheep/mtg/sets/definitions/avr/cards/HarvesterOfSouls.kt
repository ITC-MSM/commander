package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.predicates.CardPredicate

/**
 * Harvester of Souls
 * {4}{B}{B}
 * Creature — Demon
 * 5/5
 * Deathtouch
 * Whenever another nontoken creature dies, you may draw a card.
 */
val HarvesterOfSouls = card("Harvester of Souls") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    oracleText =
        "Deathtouch\nWhenever another nontoken creature dies, you may draw a card."
    power = 5
    toughness = 5

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = TriggerSpec(
            event = ZoneChangeEvent(
                filter = GameObjectFilter(
                    cardPredicates = listOf(CardPredicate.IsCreature, CardPredicate.IsNontoken),
                ),
                from = Zone.BATTLEFIELD,
                to = Zone.GRAVEYARD,
            ),
            binding = TriggerBinding.OTHER,
        )
        effect = MayEffect(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "107"
        artist = "Slawomir Maniak"
        flavorText =
            "\"He is judge, jury, and executioner because he killed them all to claim those positions.\""
        imageUri =
            "https://cards.scryfall.io/normal/front/5/0/505c0d25-dc1f-402e-9183-01c273efe0e1.jpg?1592708993"
    }
}
