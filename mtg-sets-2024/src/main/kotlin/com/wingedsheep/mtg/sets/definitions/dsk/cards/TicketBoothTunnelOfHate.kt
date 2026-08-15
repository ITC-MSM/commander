package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity

/**
 * Ticket Booth // Tunnel of Hate (DSK 158) — split-layout Room (CR 709.5).
 *
 * Ticket Booth {2}{R} — Enchantment — Room
 *   When you unlock this door, manifest dread.
 *
 * Tunnel of Hate {4}{R}{R} — Enchantment — Room
 *   Whenever you attack, target attacking creature gains double strike until end of turn.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e).
 */
val TicketBoothTunnelOfHate = card("Ticket Booth // Tunnel of Hate") {
    layout = CardLayout.SPLIT
    colorIdentity = "R"

    face("Ticket Booth") {
        manaCost = "{2}{R}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, manifest dread."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            effect = Patterns.Library.manifestDread()
        }
    }

    face("Tunnel of Hate") {
        manaCost = "{4}{R}{R}"
        typeLine = "Enchantment — Room"
        oracleText = "Whenever you attack, target attacking creature gains double strike until end of turn."

        triggeredAbility {
            trigger = Triggers.YouAttack
            val creature = target("target attacking creature", Targets.AttackingCreature)
            effect = Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, creature)
            description = "Whenever you attack, target attacking creature gains double strike until end of turn."
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "158"
        artist = "Marco Gorlei"
        imageUri = "https://cards.scryfall.io/normal/front/0/5/058ffe36-ed2d-4b67-83cd-162db8383a32.jpg?1726780790"
    }
}
