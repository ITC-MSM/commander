package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sangrite Surge
 * {4}{R}{G}
 * Sorcery
 * Target creature gets +3/+3 and gains double strike until end of turn.
 *
 * One target, two riders: [Effects.ModifyStats] and [Effects.GrantKeyword] both point at the same
 * bound target and both take their default `Duration.EndOfTurn`, which is exactly the printed
 * "until end of turn" governing the whole sentence. Composing them keeps the pump and the grant
 * as separate continuous effects, so each lands in its own layer.
 */
val SangriteSurge = card("Sangrite Surge") {
    manaCost = "{4}{R}{G}"
    colorIdentity = "GR"
    typeLine = "Sorcery"
    oracleText = "Target creature gets +3/+3 and gains double strike until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(3, 3, t),
            Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, t)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "190"
        artist = "Jarreau Wimberly"
        flavorText = "Some believe the spiky crystal called sangrite is concentrated dragon's breath. Others think it's crystallized life energy. The clans care only about the power it unleashes when crushed."
        imageUri = "https://cards.scryfall.io/normal/front/1/6/16b4527e-e449-45d0-a551-634e2ceb21c4.jpg"
    }
}
