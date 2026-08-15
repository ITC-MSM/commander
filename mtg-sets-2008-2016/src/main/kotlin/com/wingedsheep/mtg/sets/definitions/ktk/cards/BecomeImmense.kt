package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Become Immense
 * {5}{G}
 * Instant
 * Delve
 * Target creature gets +6/+6 until end of turn.
 */
val BecomeImmense = card("Become Immense") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Delve (Each card you exile from your graveyard while casting this spell pays for {1}.)\nTarget creature gets +6/+6 until end of turn."

    keywords(Keyword.DELVE)

    spell {
        val t = target("target", TargetCreature())
        effect = Effects.ModifyStats(6, 6, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "130"
        artist = "Jaime Jones"
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e17b65e-56c3-4e12-a774-780514dfd8ba.jpg?1562787250"
    }
}
