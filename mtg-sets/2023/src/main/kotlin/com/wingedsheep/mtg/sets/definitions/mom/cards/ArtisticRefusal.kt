package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Artistic Refusal
 * {4}{U}{U}
 * Instant
 * Convoke
 * Choose one or both —
 * • Counter target spell.
 * • Draw two cards, then discard a card.
 *
 * `modal(chooseCount = 2, minChooseCount = 1)` is the "choose one or both" shape (CR 700.2) — at
 * least one mode, up to both, with the counter mode's target chosen as the spell is cast. If that
 * target is illegal on resolution the whole spell is countered by game rules (CR 608.2b) and the
 * draw mode does not happen either, which is what the printed ruling describes.
 */
val ArtisticRefusal = card("Artistic Refusal") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Choose one or both —\n" +
        "• Counter target spell.\n" +
        "• Draw two cards, then discard a card."

    keywords(Keyword.CONVOKE)

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Counter target spell") {
                target = Targets.Spell
                effect = Effects.CounterSpell()
            }
            mode("Draw two cards, then discard a card") {
                effect = Effects.DrawCards(2) then Effects.Discard(1, EffectTarget.Controller)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "46"
        artist = "Olivier Bernard"
        flavorText = "Errant wouldn't allow anything to stop her from decorating the streets of " +
            "New Capenna."
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2bd05f97-ef8b-4cbe-a44b-6501aa5895b0.jpg?1783917045"
        ruling(
            "2023-04-14",
            "If you choose both modes, and the target of the first mode is an illegal target at " +
                "the time Artistic Refusal tries to resolve (probably because that spell has been " +
                "countered by something else), Artistic Refusal won't resolve and none of its " +
                "effects will happen. You won't draw or discard any cards."
        )
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional " +
                "costs, or anything else that increases or reduces the cost to cast the spell. " +
                "Convoke applies after the total cost is calculated. Convoke doesn't change a " +
                "spell's mana cost or mana value."
        )
    }
}
