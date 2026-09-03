package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Angrath's Rampage — War of the Spark #185 (canonical printing)
 * {B}{R}
 * Sorcery
 * Choose one —
 * • Target player sacrifices an artifact of their choice.
 * • Target player sacrifices a creature of their choice.
 * • Target player sacrifices a planeswalker of their choice.
 *
 * Three copies of the Diabolic Edict shape behind one modal choice: each mode binds its own
 * player target and differs only in the [GameObjectFilter] handed to [Effects.Sacrifice].
 * The sacrifice is the targeted player's own choice, which is what makes an edict an edict —
 * the spell never targets the permanent, so hexproof and shroud on the board do not protect it.
 */
val AngrathsRampage = card("Angrath's Rampage") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Target player sacrifices an artifact of their choice.\n" +
        "• Target player sacrifices a creature of their choice.\n" +
        "• Target player sacrifices a planeswalker of their choice."

    spell {
        modal {
            mode("Target player sacrifices an artifact of their choice.") {
                val player = target("target", Targets.Player)
                effect = Effects.Sacrifice(GameObjectFilter.Artifact, 1, player)
            }
            mode("Target player sacrifices a creature of their choice.") {
                val player = target("target", Targets.Player)
                effect = Effects.Sacrifice(GameObjectFilter.Creature, 1, player)
            }
            mode("Target player sacrifices a planeswalker of their choice.") {
                val player = target("target", Targets.Player)
                effect = Effects.Sacrifice(GameObjectFilter.Planeswalker, 1, player)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "185"
        artist = "Victor Adame Minguez"
        flavorText = "\"Grand city? No. This is nothing but another squalid cage.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f5a39379-7313-463a-9a02-e5157b9557f4.jpg"
    }
}
