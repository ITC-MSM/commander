package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity

/**
 * Defiled Crypt // Cadaver Lab (DSK 91) — split-layout Room (CR 709.5).
 *
 * Defiled Crypt {3}{B} — Enchantment — Room
 *   Whenever one or more cards leave your graveyard, create a 2/2 black Horror enchantment
 *   creature token. This ability triggers only once each turn.
 *
 * Cadaver Lab {B} — Enchantment — Room
 *   When you unlock this door, return target creature card from your graveyard to your hand.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e).
 *
 * Defiled Crypt reuses the batching [Triggers.CardsLeaveYourGraveyard] leave-graveyard trigger
 * with `oncePerTurn = true` for "This ability triggers only once each turn" (the tracker is
 * cleared at end of turn). The token is an enchantment creature, modeled via
 * `enchantmentToken = true`. Cadaver Lab is a "when you unlock this door" trigger (CR 709.5h)
 * returning a target creature card from the controller's own graveyard.
 */
val DefiledCryptCadaverLab = card("Defiled Crypt // Cadaver Lab") {
    layout = CardLayout.SPLIT
    colorIdentity = "B"

    face("Defiled Crypt") {
        manaCost = "{3}{B}"
        typeLine = "Enchantment — Room"
        oracleText = "Whenever one or more cards leave your graveyard, create a 2/2 black Horror " +
            "enchantment creature token. This ability triggers only once each turn."

        triggeredAbility {
            trigger = Triggers.CardsLeaveYourGraveyard()
            oncePerTurn = true
            effect = Effects.CreateToken(
                power = 2,
                toughness = 2,
                colors = setOf(Color.BLACK),
                creatureTypes = setOf("Horror"),
                enchantmentToken = true,
                imageUri = "https://cards.scryfall.io/normal/front/3/6/36bb6907-e7d7-4f99-966a-ab09ca130fb8.jpg?1754931076"
            )
        }
    }

    face("Cadaver Lab") {
        manaCost = "{B}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, return target creature card from your graveyard to your hand."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            val creatureCard = target(
                "target creature card from your graveyard",
                Targets.CreatureCardInYourGraveyard
            )
            effect = Effects.ReturnToHand(creatureCard)
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "91"
        artist = "Martin de Diego Sádaba"
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d94fb4da-0d3f-4d84-966b-914b84b23289.jpg?1726780590"
    }
}
