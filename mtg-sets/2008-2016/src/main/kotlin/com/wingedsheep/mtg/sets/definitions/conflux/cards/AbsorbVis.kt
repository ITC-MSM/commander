package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Absorb Vis
 * {6}{B}
 * Sorcery
 * Target player loses 4 life and you gain 4 life.
 * Basic landcycling {1}{B}
 *
 * Two life clauses sharing one target slot: [Effects.LoseLife] aimed at the bound player and
 * [Effects.GainLife] left on its default controller recipient, joined by [Effects.Composite].
 * Only the loss is targeted — the gain names "you", so no second requirement is declared.
 *
 * "Basic landcycling" is [KeywordAbility.basicLandcycling], the typecycling machinery narrowed to
 * *basic* land cards (not merely lands carrying a basic land type), which is the `IsBasicLand`
 * search filter the printed reminder text spells out.
 */
val AbsorbVis = card("Absorb Vis") {
    manaCost = "{6}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Target player loses 4 life and you gain 4 life.\n" +
        "Basic landcycling {1}{B} ({1}{B}, Discard this card: Search your library for a basic land " +
        "card, reveal it, put it into your hand, then shuffle.)"

    spell {
        val t = target("target", TargetPlayer())
        effect = Effects.Composite(
            Effects.LoseLife(4, t),
            Effects.GainLife(4)
        )
    }

    keywordAbility(KeywordAbility.basicLandcycling("{1}{B}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Brandon Kitkouski"
        imageUri = "https://cards.scryfall.io/normal/front/5/5/5528886e-3198-48b1-a3b0-6d41ba87bfd6.jpg"
    }
}
