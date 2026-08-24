package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Enthusiastic Study — Strixhaven: School of Mages #99 (canonical printing)
 * {2}{R} · Instant
 *
 * Target creature gets +3/+1 and gains trample until end of turn.
 * Learn.
 *
 * Both riders land on the same targeted creature and both are until-end-of-turn, so they are two
 * effects over one target rather than a single compound one.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val EnthusiasticStudy = card("Enthusiastic Study") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+1 and gains trample until end of turn.\n" +
        "Learn. (You may reveal a Lesson card you own from outside the game and put it into your " +
        "hand, or discard a card to draw a card.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(3, 1, creature) then
            Effects.GrantKeyword(Keyword.TRAMPLE, creature) then
            Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "99"
        artist = "Jesper Ejsing"
        flavorText = "\"If the pen is mightier than the sword, just think what a giant tome could do!\""
        imageUri = "https://cards.scryfall.io/normal/front/5/4/543c64ff-2c51-4a63-a940-dc8645717c85.jpg?1783927356"
    }
}
