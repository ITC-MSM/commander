package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Gift of Growth
 * {1}{G}
 * Instant
 * Kicker {2}
 * Untap target creature. It gets +2/+2 until end of turn.
 * If this spell was kicked, that creature gets +4/+4 until end of turn instead.
 */
val GiftOfGrowth = card("Gift of Growth") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Kicker {2}\nUntap target creature. It gets +2/+2 until end of turn. If this spell was kicked, that creature gets +4/+4 until end of turn instead."

    keywordAbility(KeywordAbility.kicker("{2}"))

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Untap(t)
            .then(
                ConditionalEffect(
                    condition = WasKicked,
                    effect = Effects.ModifyStats(4, 4, t),
                    elseEffect = Effects.ModifyStats(2, 2, t)
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "163"
        artist = "YW Tang"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a3f8878-5928-47ec-bb31-f4ee24ad982b.jpg?1562738151"
    }
}
