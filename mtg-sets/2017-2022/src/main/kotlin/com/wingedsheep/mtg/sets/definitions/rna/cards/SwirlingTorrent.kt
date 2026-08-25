package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Swirling Torrent — Ravnica Allegiance #56
 * {5}{U} · Sorcery
 *
 * The same "choose one or both" count as [AppliedBiomancy]. Both modes are tempo rather than
 * removal, and choosing both on the *same* creature is legal but pointless — the second mode
 * fizzles on a creature already gone.
 */
val SwirlingTorrent = card("Swirling Torrent") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Choose one or both —\n" +
        "• Put target creature on top of its owner's library.\n" +
        "• Return target creature to its owner's hand."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Put target creature on top of its owner's library") {
                val creature = target("target", Targets.Creature)
                effect = Effects.PutOnTopOfLibrary(creature)
            }
            mode("Return target creature to its owner's hand") {
                val creature = target("target", Targets.Creature)
                effect = Effects.ReturnToHand(creature)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "56"
        artist = "Ben Wootten"
        flavorText = "\"Oops!\"\n" +
        "—Grupgrup, sluiceway technician"
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8dbb3d2f-4ee4-46e2-98aa-4aa388bd5375.jpg"
    }
}
