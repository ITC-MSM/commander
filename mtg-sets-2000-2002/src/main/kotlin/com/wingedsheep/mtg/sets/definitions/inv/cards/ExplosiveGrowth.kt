package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Explosive Growth
 * {G}
 * Instant
 * Kicker {5}
 * Target creature gets +2/+2 until end of turn. If this spell was kicked,
 * that creature gets +5/+5 until end of turn instead.
 */
val ExplosiveGrowth = card("Explosive Growth") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Kicker {5} (You may pay an additional {5} as you cast this spell.)\n" +
        "Target creature gets +2/+2 until end of turn. If this spell was kicked, " +
        "that creature gets +5/+5 until end of turn instead."

    keywordAbility(KeywordAbility.kicker("{5}"))

    spell {
        val t = target("target", Targets.Creature)
        effect = ConditionalEffect(
            condition = WasKicked,
            effect = Effects.ModifyStats(5, 5, t),
            elseEffect = Effects.ModifyStats(2, 2, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "187"
        artist = "Arnie Swekel"
        imageUri = "https://cards.scryfall.io/normal/front/e/a/eabc1e77-404c-436b-bde1-be1b21d00584.jpg?1562942177"
    }
}
