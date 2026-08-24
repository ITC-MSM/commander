package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Welkin Guide
 * {4}{W}
 * Creature — Bird Cleric
 * 2 / 2
 * Flying
 * When this creature enters, target creature gets +2/+2 and gains flying until end of turn.
 *
 * The printed flying is a bare [Keyword.FLYING] on the card's keyword set. The enters trigger is
 * [Triggers.EntersBattlefield] with one named [Targets.Creature] slot, and its effect is the pair
 * [Effects.ModifyStats] `then` [Effects.GrantKeyword] over that same bound target — both take the
 * default [com.wingedsheep.sdk.scripting.Duration.EndOfTurn], which is the printed "until end of
 * turn" for the whole sentence.
 */
val WelkinGuide = card("Welkin Guide") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Cleric"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "When this creature enters, target creature gets +2/+2 and gains flying until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, t)
            .then(Effects.GrantKeyword(Keyword.FLYING, t))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "David Palumbo"
        flavorText = "\"Those talons really dig into your skin, but it's better than being dropped.\"\n—Rafiq of the Many"
        imageUri = "https://cards.scryfall.io/normal/front/1/9/198363f2-1a19-4954-b527-6d10ac277719.jpg"
    }
}
