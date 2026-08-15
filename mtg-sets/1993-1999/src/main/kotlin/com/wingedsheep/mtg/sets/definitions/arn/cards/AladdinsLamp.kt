package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Aladdin's Lamp
 * {10}
 * Artifact
 * {X}, {T}: The next time you would draw a card this turn, instead look at the top X cards of your
 * library, put all but one of them on the bottom of your library in a random order, then draw a
 * card. X can't be 0.
 *
 * Composition:
 *  - `{X}, {T}` activated ability whose only effect is `Effects.ReplaceNextDraw(...)` — it installs a
 *    one-shot draw-replacement shield for the rest of the turn. The shield now captures the
 *    activation-time {X} (see `ReplaceDrawWithEffect.xValue`), so the replacement can read
 *    `DynamicAmount.XValue` when it fires at the next draw.
 *  - The replacement is a `lookAtTopAndKeep` dig: look at the top X, keep one on top, put the rest
 *    on the bottom in a random order, then a real `DrawCards(1)` draws the kept card (so draw
 *    triggers see a genuine draw). The shield is consumed before the inner draw, so it doesn't
 *    re-trigger itself.
 *  - "X can't be 0" is not separately enforced. With X=0 the dig looks at zero cards, so
 *    `SelectFromCollection` short-circuits on the empty collection (no prompt, empty kept/rest) and
 *    the inner `DrawCards(1)` runs as an ordinary draw. Covered by the X=0 scenario test.
 */
val AladdinsLamp = card("Aladdin's Lamp") {
    manaCost = "{10}"
    typeLine = "Artifact"
    oracleText = "{X}, {T}: The next time you would draw a card this turn, instead look at the top X " +
        "cards of your library, put all but one of them on the bottom of your library in a random " +
        "order, then draw a card. X can't be 0."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{X}"), Costs.Tap)
        effect = Effects.ReplaceNextDraw(
            Effects.Composite(
                Patterns.Library.lookAtTopAndKeep(
                    count = DynamicAmount.XValue,
                    keepCount = DynamicAmount.Fixed(1),
                    keepDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Top),
                    restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
                    restOrder = CardOrder.Random,
                    selectedLabel = "Keep on top",
                    remainderLabel = "Put on bottom at random"
                ),
                DrawCardsEffect(1)
            )
        )
        description = "{X}, {T}: The next time you would draw a card this turn, instead look at the top X " +
            "cards of your library, put all but one of them on the bottom of your library in a random " +
            "order, then draw a card. X can't be 0."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "56"
        artist = "Mark Tedin"
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8fecc5d2-5298-4d47-b085-f160603f220e.jpg?1562921727"
    }
}
