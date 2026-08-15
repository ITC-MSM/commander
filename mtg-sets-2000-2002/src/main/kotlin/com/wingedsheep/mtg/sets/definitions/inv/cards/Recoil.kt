package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Recoil
 * {1}{U}{B}
 * Instant
 * Return target permanent to its owner's hand. Then that player discards a card.
 */
val Recoil = card("Recoil") {
    manaCost = "{1}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Instant"
    oracleText = "Return target permanent to its owner's hand. Then that player discards a card."

    spell {
        val t = target("target", Targets.Permanent)
        effect = Effects.ReturnToHand(t) then
                Effects.Discard(1, EffectTarget.PlayerRef(Player.OwnerOf("target permanent")))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "264"
        artist = "Alan Pollack"
        flavorText = "Anything sent into a plagued world is bound to come back infected."
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6a77be3-e3b0-40f5-a470-414bac49da60.jpg?1562931727"
    }
}
