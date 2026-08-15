package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Cheeky House-Mouse // Squeak By
 * {W}
 * Creature — Mouse
 * 2/1
 *
 * Adventure: Squeak By — {W}, Sorcery — Adventure
 * Target creature you control gets +1/+1 until end of turn. It can't be blocked by creatures
 * with power 3 or greater this turn.
 *
 * (CR 715: Adventure cards. Casting the Adventure exiles the card on resolution and lets the
 * caster cast it as the creature spell while it remains in exile.)
 */
val CheekyHouseMouse = card("Cheeky House-Mouse") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Mouse"
    oracleText = ""
    power = 2
    toughness = 1

    adventure("Squeak By") {
        manaCost = "{W}"
        typeLine = "Sorcery — Adventure"
        oracleText = "Target creature you control gets +1/+1 until end of turn. It can't be blocked by " +
            "creatures with power 3 or greater this turn. (Then exile this card. You may cast the creature later from exile.)"
        spell {
            val t = target("target", Targets.CreatureYouControl)
            effect = Effects.Composite(
                Effects.ModifyStats(1, 1, t),
                Effects.GrantStaticAbility(
                    CantBeBlockedBy(GameObjectFilter.Creature.powerAtLeast(3)),
                    t
                )
            )
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "7"
        artist = "Uriah Voth"
        flavorText = "\"Get back here, vile rodent! I was almost done cursing that ring!\""
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f3013bf-9647-4bdb-a638-d299ae00f88e.jpg"
    }
}
