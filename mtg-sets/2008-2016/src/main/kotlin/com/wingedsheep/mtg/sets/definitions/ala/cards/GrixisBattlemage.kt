package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Grixis Battlemage
 * {2}{B}
 * Creature — Human Wizard
 * 2 / 2
 * {U}, {T}: Draw a card, then discard a card.
 * {R}, {T}: Target creature can't block this turn.
 *
 * Two independent activated abilities, each a [Costs.Composite] of an off-colour mana pip plus
 * [Costs.Tap] — so only one can be used per untap step. The loot is literal composition,
 * [Effects.DrawCards] `then` [Effects.Discard], the latter lowering to the shared Gather → Select →
 * Move (Discard) hand pipeline; the combat half is [Effects.CantBlock] on the bound target, whose
 * default `Duration.EndOfTurn` is the printed "this turn".
 */
val GrixisBattlemage = card("Grixis Battlemage") {
    manaCost = "{2}{B}"
    colorIdentity = "BRU"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "{U}, {T}: Draw a card, then discard a card.\n" +
        "{R}, {T}: Target creature can't block this turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}"), Costs.Tap)
        effect = Effects.DrawCards(1) then Effects.Discard(1)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = Effects.CantBlock(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "78"
        artist = "Nils Hamm"
        flavorText = "Vitals of Grixis who eschew undeath must scrape and scratch to retain their mortality. The result is a breed of inventive mages."
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4dbd260c-a625-42a4-8192-27e42e18ac0f.jpg"
    }
}
