package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Ossification
 * {1}{W}
 * Enchantment — Aura
 * Enchant basic land you control
 * When this Aura enters, exile target creature or planeswalker an opponent controls until this Aura
 * leaves the battlefield.
 *
 * Enchants a *basic* land specifically — [GameObjectFilter.BasicLand] is the supertype check, not
 * `LandWithBasicLandType`, so a shockland or Dryad Arbor is not a legal host.
 *
 */
val Ossification = card("Ossification") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant basic land you control\n" +
        "When this Aura enters, exile target creature or planeswalker an opponent controls until this Aura leaves the battlefield."

    auraTarget = TargetPermanent(filter = TargetFilter(GameObjectFilter.BasicLand.youControl()))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val victim = target(
            "creature or planeswalker an opponent controls",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker.opponentControls())),
        )
        effect = Effects.MoveUntilSourceLeaves(victim, Zone.EXILE)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "26"
        artist = "Nino Vecia"
        flavorText = "Only Elesh Norn's most loyal servants are granted the honor of becoming part of her throne."
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0da03224-c1af-438f-96c2-b0e41e1070b7.jpg?1783918077"
        ruling("2023-02-04", "If Ossification leaves the battlefield before its triggered ability resolves, the target permanent won't be exiled.")
        ruling("2023-02-04", "Auras attached to the exiled permanent will be put into their owners' graveyards. Any Equipment will become unattached and remain on the battlefield. Any counters on the exiled permanent will cease to exist. When the card returns to the battlefield, it will be a new object with no connection to the card that was exiled.")
        ruling("2023-02-04", "If a token is exiled this way, it will cease to exist and won't return to the battlefield.")
    }
}
