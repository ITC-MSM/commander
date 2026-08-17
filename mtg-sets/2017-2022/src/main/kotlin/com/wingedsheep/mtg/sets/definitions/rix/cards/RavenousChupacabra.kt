package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ravenous Chupacabra
 * {2}{B}{B}
 * Creature — Beast Horror
 * 2/2
 * When this creature enters, destroy target creature an opponent controls.
 *
 * Unconditional removal stapled to a body: [Effects.Destroy] on a cast-time target. The "an
 * opponent controls" restriction is carried entirely by [Targets.CreatureOpponentControls], whose
 * filter adds the controller predicate — so no gating condition or extra effect is needed.
 */
val RavenousChupacabra = card("Ravenous Chupacabra") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Beast Horror"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, destroy target creature an opponent controls."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val victim = target("target", Targets.CreatureOpponentControls)
        effect = Effects.Destroy(victim)
        description = "When this creature enters, destroy target creature an opponent controls."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "82"
        artist = "Daarken"
        flavorText = "Opening Orazca unleashed more horrors than just the Immortal Sun."
        imageUri = "https://cards.scryfall.io/normal/front/0/2/02551196-ecea-472f-9547-3c9658d0489e.jpg?1783935306"
    }
}
