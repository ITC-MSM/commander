package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Umbral Juke — Strixhaven: School of Mages #89 (canonical printing)
 * {2}{B} · Instant
 *
 * Choose one —
 * • Target player sacrifices a creature or planeswalker of their choice.
 * • Create a 2/1 white and black Inkling creature token with flying.
 *
 * A choose-one modal whose first mode is the only one with a target: an edict —
 * [Effects.Sacrifice] over [GameObjectFilter.CreatureOrPlaneswalker] naming the targeted player,
 * who picks what to give up. The second mode is a plain [Effects.CreateToken]; token art resolves
 * through STX's synced token printings, so none is declared here.
 */
val UmbralJuke = card("Umbral Juke") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText =
        "Choose one —\n" +
        "• Target player sacrifices a creature or planeswalker of their choice.\n" +
        "• Create a 2/1 white and black Inkling creature token with flying."

    spell {
        modal(chooseCount = 1) {
            mode("Target player sacrifices a creature or planeswalker of their choice.") {
                val player = target("target", Targets.Player)
                effect = Effects.Sacrifice(GameObjectFilter.CreatureOrPlaneswalker, target = player)
            }
            mode("Create a 2/1 white and black Inkling creature token with flying.") {
                effect = Effects.CreateToken(
                    power = 2,
                    toughness = 1,
                    colors = setOf(Color.WHITE, Color.BLACK),
                    creatureTypes = setOf("Inkling"),
                    keywords = setOf(Keyword.FLYING)
                )
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "89"
        artist = "Bram Sels"
        flavorText = "The sport of Mage Tower was conceived as a playful testing ground for young spellcasters."
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3fbd0921-e953-492b-ad73-c8a8bfaa750b.jpg?1783927361"
    }
}
