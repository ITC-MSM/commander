package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostZone
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Fear of Abduction
 * {4}{W}{W}
 * Enchantment Creature — Nightmare
 * 5/5
 * As an additional cost to cast this spell, exile a creature you control.
 * Flying
 * When this creature enters, exile target creature an opponent controls.
 * When this creature leaves the battlefield, put each card exiled with it into its owner's hand.
 *
 * The ETB exile is linked to the source (CR 603.6e-style "until ~ leaves" exile pile), so the
 * leaves trigger returns exactly the card this creature exiled — to its owner's HAND, not the
 * battlefield. The additional-cost exile of your own creature is a permanent exile (not linked),
 * so it is never returned.
 */
val FearOfAbduction = card("Fear of Abduction") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment Creature — Nightmare"
    power = 5
    toughness = 5
    oracleText = "As an additional cost to cast this spell, exile a creature you control.\n" +
        "Flying\n" +
        "When this creature enters, exile target creature an opponent controls.\n" +
        "When this creature leaves the battlefield, put each card exiled with it into its owner's hand."

    keywords(Keyword.FLYING)

    additionalCost(
        Costs.additional.ExileCards(
            count = 1,
            filter = GameObjectFilter.Creature.youControl(),
            fromZone = CostZone.BATTLEFIELD
        )
    )

    // ETB: exile target creature an opponent controls until this creature leaves the battlefield.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target(
            "creature an opponent controls",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.opponentControls()))
        )
        effect = Effects.ExileUntilLeaves(creature)
    }

    // LTB: return each linked-exiled card to its owner's hand.
    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileToHand()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "9"
        artist = "Fernando Falcone"
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc9374be-5e4b-4c23-8b6e-94c03d4f5ef1.jpg?1726285889"

        ruling("2024-09-20", "If Fear of Abduction leaves the battlefield before its third ability resolves, the ability still exiles the target creature.")
        ruling("2024-09-20", "If there are no exiled cards when Fear of Abduction's last ability resolves (most likely because its third ability hasn't resolved yet), the ability won't do anything.")
    }
}
