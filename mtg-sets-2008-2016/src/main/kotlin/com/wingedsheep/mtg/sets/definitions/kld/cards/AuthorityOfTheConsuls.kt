package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.PermanentsEnterTapped
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Authority of the Consuls
 * {W}
 * Enchantment
 *
 * Creatures your opponents control enter tapped.
 * Whenever a creature an opponent controls enters, you gain 1 life.
 *
 * - "Creatures your opponents control enter tapped" is a global [PermanentsEnterTapped] runtime
 *   replacement (the group counterpart of the self-only `EntersTapped`) whose `appliesTo` filter
 *   describes the *affected* permanents — creatures an opponent of Authority's controller controls.
 *   The controller-relative `opponentControls()` predicate is resolved against Authority's own
 *   controller at entry time.
 * - The gain-life clause is an ANY-binding enters trigger over the same opponent-creature filter;
 *   it fires once per opponent creature that enters (tokens included).
 */
val AuthorityOfTheConsuls = card("Authority of the Consuls") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Creatures your opponents control enter tapped.\n" +
        "Whenever a creature an opponent controls enters, you gain 1 life."

    // Creatures your opponents control enter tapped.
    replacementEffect(
        PermanentsEnterTapped(
            appliesTo = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Creature.opponentControls(),
                to = Zone.BATTLEFIELD,
            )
        )
    )

    // Whenever a creature an opponent controls enters, you gain 1 life.
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.opponentControls(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "5"
        artist = "Lake Hurwitz"
        flavorText = "Citizens are free to do as they wish, within the confines of the Consulate's laws."
        imageUri = "https://cards.scryfall.io/normal/front/3/2/324b2f55-1e09-490e-8f7e-bfde85a91ac4.jpg?1782711627"
    }
}
