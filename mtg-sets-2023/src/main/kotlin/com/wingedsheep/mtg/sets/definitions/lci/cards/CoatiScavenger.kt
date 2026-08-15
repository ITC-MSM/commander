package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Coati Scavenger — {2}{G}
 * Creature — Raccoon
 * 3/2
 *
 * Descend 4 — When this creature enters, if there are four or more permanent cards in your
 * graveyard, return target permanent card from your graveyard to your hand.
 *
 * "Descend 4" is an ability word (CR 207.2c) with no rules meaning of its own; it merely names the
 * intervening-if condition (four or more permanent cards in your graveyard). The trigger fires on ETB
 * only when the condition is already true at that moment and is rechecked at resolution — if the
 * graveyard count drops below four before resolution the ability fizzles without effect (no valid
 * target for the return effect in any case).
 *
 * Targeting is mandatory (no "may") — the controller must choose a permanent card from their
 * graveyard to return if at least one exists. If none exists, the ability fizzles.
 */
val CoatiScavenger = card("Coati Scavenger") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Raccoon"
    power = 3
    toughness = 2
    oracleText = "Descend 4 — When this creature enters, if there are four or more permanent cards in your graveyard, return target permanent card from your graveyard to your hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.CardsInGraveyardMatchingAtLeast(4, GameObjectFilter.Permanent)
        val card = target(
            "target permanent card from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    baseFilter = GameObjectFilter.Permanent.ownedByYou(),
                    zone = Zone.GRAVEYARD,
                ),
            ),
        )
        effect = Effects.ReturnToHand(card)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "179"
        artist = "Alessandra Pisano"
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2c70d86-1764-487d-a415-15ae79ba570c.jpg?1782694467"
    }
}
