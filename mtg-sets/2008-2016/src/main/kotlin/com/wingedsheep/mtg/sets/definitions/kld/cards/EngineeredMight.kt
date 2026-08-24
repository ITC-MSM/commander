package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Engineered Might
 * {3}{G}{W}
 * Sorcery
 *
 * Choose one —
 * • Target creature gets +5/+5 and gains trample until end of turn.
 * • Creatures you control get +2/+2 and gain vigilance until end of turn.
 *
 * Two shapes of the same sentence, so they are written two different ways. The first mode names a
 * single target once and says two things about it, which is a plain composite of
 * [Effects.ModifyStats] and [Effects.GrantKeyword] over that one bound target. The second names a
 * *group* once and says two things about it, which is [Patterns.Group.pumpAndGrantToAll] — the
 * group is gathered a single time on resolution, so creatures that arrive later in the turn get
 * neither half.
 */
val EngineeredMight = card("Engineered Might") {
    manaCost = "{3}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Target creature gets +5/+5 and gains trample until end of turn.\n" +
        "• Creatures you control get +2/+2 and gain vigilance until end of turn."

    spell {
        modal {
            mode("Target creature gets +5/+5 and gains trample until end of turn") {
                val t = target("target", Targets.Creature)
                effect = Effects.Composite(
                    Effects.ModifyStats(5, 5, t),
                    Effects.GrantKeyword(Keyword.TRAMPLE, t),
                )
            }
            mode("Creatures you control get +2/+2 and gain vigilance until end of turn") {
                effect = Patterns.Group.pumpAndGrantToAll(
                    power = 2,
                    toughness = 2,
                    keyword = Keyword.VIGILANCE,
                    filter = Filters.Group.creaturesYouControl,
                )
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "181"
        artist = "Lake Hurwitz"
        imageUri = "https://cards.scryfall.io/normal/front/6/7/675b5fc7-51b2-4425-b053-a5d19c1595e0.jpg?1783937168"
    }
}
