package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ovalchase Daredevil
 * {3}{B}
 * Creature — Human Pilot
 * 4/2
 *
 * Whenever an artifact you control enters, you may return this card from your graveyard to your
 * hand.
 *
 * The ability functions from the graveyard, not the battlefield (CR 113.6b), so the trigger is
 * zone-scoped with `triggerZone = Zone.GRAVEYARD`; without it the trigger would only be indexed
 * while the Daredevil is on the battlefield, where the ability can never do anything.
 * `binding = TriggerBinding.ANY` is what makes the entering artifact — rather than the source —
 * the triggering entity.
 */
val OvalchaseDaredevil = card("Ovalchase Daredevil") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Pilot"
    oracleText = "Whenever an artifact you control enters, you may return this card from your graveyard to your hand."
    power = 4
    toughness = 2

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.ANY
        )
        triggerZone = Zone.GRAVEYARD
        optional = true
        effect = Effects.Move(EffectTarget.Self, Zone.HAND, fromZone = Zone.GRAVEYARD)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "97"
        artist = "Winona Nelson"
        flavorText = "\"Let me guess. You thought I was dead.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a323a495-e154-4541-ba4e-25b66b84d692.jpg?1783937202"
    }
}
