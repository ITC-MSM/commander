package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Larger Than Life
 * {1}{G}
 * Sorcery
 *
 * Target creature gets +4/+4 and gains trample until end of turn.
 *
 * One target shared by both halves: the bound `target` handle is passed to
 * [Effects.ModifyStats] and [Effects.GrantKeyword] so the pump and the trample land on the same
 * creature.
 */
val LargerThanLife = card("Larger Than Life") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Target creature gets +4/+4 and gains trample until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(4, 4, t),
            Effects.GrantKeyword(Keyword.TRAMPLE, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "160"
        artist = "Jaime Jones"
        flavorText = "\"They say Head Judge Tezzeret will be reviewing every invention at the Fair. I made sure to build something that will stand out.\"\n—Jehvani, Vahadar artificer"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d0da994-d3e7-41b9-ae8f-6f1a3b779f23.jpg?1783937177"
    }
}
