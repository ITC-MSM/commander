package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stonerise Spirit — Strixhaven: School of Mages #32 (canonical printing)
 * {1}{W} · Creature — Spirit Bird · 1/2
 *
 * Flying
 * {4}, Exile a card from your graveyard: Target creature gains flying until end of turn.
 *
 * Flying is the bare keyword. The activation's cost is a [Costs.Composite] of {4} and
 * [Costs.ExileFromGraveyard] (one card, any kind, chosen by the player); its effect is
 * [Effects.GrantKeyword] flying until end of turn on [Targets.Creature].
 */
val StoneriseSpirit = card("Stonerise Spirit") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Bird"
    oracleText =
        "Flying\n" +
        "{4}, Exile a card from your graveyard: Target creature gains flying until end of turn."
    power = 1
    toughness = 2

    keywords(Keyword.FLYING)

    // {4}, Exile a card from your graveyard: Target creature gains flying until end of turn.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.ExileFromGraveyard(1))
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Uriah Voth"
        flavorText = "The carved cliffs of Pillardrop thrum with the sounds of ancient spirits rising from the past."
        imageUri = "https://cards.scryfall.io/normal/front/3/8/388f2e45-570f-4a35-b205-37e1345b5d06.jpg?1783927384"
    }
}
