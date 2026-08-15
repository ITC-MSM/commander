package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Beluna's Gatekeeper // Entry Denied
 * {5}{U}
 * Creature — Giant Soldier
 * 6/5
 *
 * Adventure: Entry Denied — {1}{U}, Sorcery — Adventure
 * Return target creature you don't control with mana value 3 or less to its owner's hand.
 *
 * (CR 715: Adventure cards. Casting the Adventure exiles the card on resolution and lets the
 * caster cast it as the creature spell while it remains in exile.)
 */
val BelunasGatekeeper = card("Beluna's Gatekeeper") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Giant Soldier"
    oracleText = ""
    power = 6
    toughness = 5

    adventure("Entry Denied") {
        manaCost = "{1}{U}"
        typeLine = "Sorcery — Adventure"
        oracleText = "Return target creature you don't control with mana value 3 or less to its owner's hand. " +
            "(Then exile this card. You may cast the creature later from exile.)"
        spell {
            val t = target(
                "target",
                TargetCreature(filter = TargetFilter.Creature.opponentControls().manaValueAtMost(3))
            )
            effect = Effects.ReturnToHand(t)
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Kai Carpenter"
        flavorText = "\"And what makes you think Lady Grandsquall wants to see you, little one?\""
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c1d410e-4237-4963-b015-54d26730e63d.jpg"
    }
}
