package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Urza's Miter
 * {3}
 * Artifact
 * Whenever an artifact you control is put into a graveyard from the battlefield, if it wasn't
 * sacrificed, you may pay {3}. If you do, draw a card.
 *
 * The "if it wasn't sacrificed" intervening-if (CR 701.21) is expressed on the trigger via
 * `excludeSacrifice = true`: `TriggerMatcher.matchesZoneChangeTrigger` reads the triggering
 * `ZoneChangeEvent.wasSacrificed` flag (stamped by the central sacrifice hook) and suppresses the
 * trigger for sacrifices. Otherwise this mirrors the no-condition sibling Tablet of Epityr: an
 * artifact-you-control dies trigger ([TriggerBinding.ANY], since this artifact itself counts) with
 * an optional pay-{3}-then-draw.
 */
val UrzasMiter = card("Urza's Miter") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever an artifact you control is put into a graveyard from the battlefield, " +
        "if it wasn't sacrificed, you may pay {3}. If you do, draw a card."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
            excludeSacrifice = true,
        )
        effect = MayPayManaEffect(ManaCost.parse("{3}"), Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "76"
        artist = "Randy Asplund-Faith"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/438f0c61-a61d-4a9e-b21f-4e86420c7913.jpg?1562908986"
    }
}
