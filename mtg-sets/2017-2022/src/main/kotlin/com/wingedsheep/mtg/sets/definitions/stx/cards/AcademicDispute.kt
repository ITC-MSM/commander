package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Academic Dispute — Strixhaven: School of Mages #91 (canonical printing)
 * {R} · Instant
 *
 * Target creature blocks this turn if able. You may have it gain reach until end of turn.
 * Learn. (You may reveal a Lesson card you own from outside the game and put it into your hand,
 * or discard a card to draw a card.)
 *
 * A combat trick that forces a block rather than preventing one — the reach rider exists so the
 * forced blocker can be a flier's blocker, which is why it is optional ([MayEffect]) and why it
 * lands on the same targeted creature.
 *
 * "Blocks this turn if able" is [Effects.MarkMustBlockThisTurn]: a *requirement*, not a guarantee
 * (CR 509.1c) — a tapped creature, or one whose every block would be illegal, simply doesn't
 * block. The three clauses resolve in printed order, so the Learn happens whether or not the
 * creature ends up blocking.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val AcademicDispute = card("Academic Dispute") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature blocks this turn if able. You may have it gain reach until end " +
        "of turn.\n" +
        "Learn. (You may reveal a Lesson card you own from outside the game and put it into your " +
        "hand, or discard a card to draw a card.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.MarkMustBlockThisTurn(creature) then
            MayEffect(
                effect = Effects.GrantKeyword(Keyword.REACH, creature),
                descriptionOverride = "You may have it gain reach until end of turn."
            ) then
            Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "91"
        artist = "Manuel Castañón"
        flavorText = "\"I'll show you original research, you hack!\""
        imageUri = "https://cards.scryfall.io/normal/front/4/6/4620cc3b-e401-4096-b310-fed080806344.jpg?1783927359"
    }
}
