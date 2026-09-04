package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rottenheart Ghoul (Shadows over Innistrad #132)
 * {3}{B}
 * Creature — Zombie
 * 2 / 4
 *
 * When this creature dies, target player discards a card.
 *
 * [Effects.Discard] against a bound target player is the Gather → Select → Move pipeline, so the
 * discarding player is the one who chooses which card leaves their hand.
 */
val RottenheartGhoul = card("Rottenheart Ghoul") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 2
    toughness = 4
    oracleText = "When this creature dies, target player discards a card."

    triggeredAbility {
        trigger = Triggers.Dies
        val player = target("target", Targets.Player)
        effect = Effects.Discard(1, player)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "132"
        artist = "Dave Kendall"
        flavorText = "\"To die failing to save a loved one is just so sad—or, more to the point, pathetic.\"\n—Liliana Vess"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/933f0504-c611-4557-b1e6-f5be72154805.jpg?1783937765"
    }
}
