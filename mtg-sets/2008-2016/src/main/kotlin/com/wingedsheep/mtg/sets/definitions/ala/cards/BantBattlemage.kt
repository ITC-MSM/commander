package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bant Battlemage
 * {2}{W}
 * Creature — Human Wizard
 * 2 / 2
 * {G}, {T}: Target creature gains trample until end of turn.
 * {U}, {T}: Target creature gains flying until end of turn.
 *
 * Two independent activated abilities rather than one modal one — the printed card prices them
 * separately and each taps the Battlemage, so they can't both be used in a turn without an untapper.
 * Each cost is [Costs.Composite] of a coloured [Costs.Mana] and [Costs.Tap], and each effect is
 * [Effects.GrantKeyword] on the ability's own bound target, whose default `Duration.EndOfTurn` is
 * the printed "until end of turn". The off-colour activation costs are what put G and U into the
 * card's colour identity.
 */
val BantBattlemage = card("Bant Battlemage") {
    manaCost = "{2}{W}"
    colorIdentity = "GUW"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "{G}, {T}: Target creature gains trample until end of turn.\n" +
        "{U}, {T}: Target creature gains flying until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}"), Costs.Tap)
        val creature = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.TRAMPLE, creature)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}"), Costs.Tap)
        val creature = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "5"
        artist = "Donato Giancola"
        flavorText = "\"A night attack will be easy. We'll make an air raid over the Akrasan border. Just get me some flint to light the war torches.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2c597b1d-d8b5-4922-a3f2-1f173a73ea2a.jpg"
    }
}
