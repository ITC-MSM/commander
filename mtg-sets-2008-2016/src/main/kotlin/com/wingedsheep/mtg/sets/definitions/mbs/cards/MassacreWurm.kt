package com.wingedsheep.mtg.sets.definitions.mbs.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Massacre Wurm
 * {3}{B}{B}{B}
 * Creature — Phyrexian Wurm
 * 6/5
 *
 * When this creature enters, creatures your opponents control get -2/-2 until end of turn.
 * Whenever a creature an opponent controls dies, that player loses 2 life.
 *
 *  - **ETB** — a per-creature -2/-2 floating effect over every creature an opponent controls at
 *    resolution ([Effects.ForEachInGroup] with the iterated creature as [EffectTarget.Self]).
 *    Creatures that enter later are unaffected; the set is fixed when the ability resolves.
 *  - **Death drain** — a [Triggers.leavesBattlefield]-to-graveyard trigger filtered to
 *    opponent-controlled creatures (fires once per death). [Player.TriggeringPlayer] resolves to
 *    the dying creature's controller, so "that player" loses 2 life.
 *
 * Canonical printing lives in Mirrodin Besieged (the card's earliest real printing); Foundations
 * contributes only a [com.wingedsheep.sdk.model.Printing] row.
 */
val MassacreWurm = card("Massacre Wurm") {
    manaCost = "{3}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Wurm"
    power = 6
    toughness = 5
    oracleText = "When this creature enters, creatures your opponents control get -2/-2 until end of turn.\n" +
        "Whenever a creature an opponent controls dies, that player loses 2 life."

    // When this creature enters, creatures your opponents control get -2/-2 until end of turn.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreaturesOpponentsControl,
            Effects.ModifyStats(power = -2, toughness = -2, target = EffectTarget.Self)
        )
    }

    // Whenever a creature an opponent controls dies, that player loses 2 life.
    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.opponentControls(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        effect = Effects.LoseLife(2, EffectTarget.PlayerRef(Player.TriggeringPlayer))
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "46"
        artist = "Jason Chan"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cdd32ec2-02a8-41fc-bf45-c9585bb2b3ee.jpg?1782715235"
    }
}
