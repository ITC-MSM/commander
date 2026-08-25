package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.TargetSpellOrPermanent

/**
 * Divide by Zero — Strixhaven: School of Mages #41 (canonical printing)
 * {2}{U} · Instant
 *
 * Return target spell or permanent with mana value 1 or greater to its owner's hand.
 * Learn.
 *
 * The one printed restriction applies to *both* halves of the target, so it is passed to both
 * filters of [TargetSpellOrPermanent]: a {0} spell on the stack (Memnite, a Mox) is no more a
 * legal target than a land on the battlefield is. Before this card the stack half of that
 * requirement carried no filter at all — see the SDK field's docs.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48), and it runs *after* the bounce in printed
 * order — so a card returned to your own hand is already there to be pitched to the Learn's
 * discard.
 *
 * Known limitation, narrow enough to be worth naming: mana value is read off the card, so a
 * spell on the stack whose cost is purely `{X}` reads as MV 0 rather than the value chosen for X
 * (CR 202.3b). That makes such a spell wrongly untargetable; every other `{X}` spell has a
 * non-X pip and so clears "1 or greater" either way.
 */
val DivideByZero = card("Divide by Zero") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target spell or permanent with mana value 1 or greater to its owner's hand.\n" +
        "Learn. (You may reveal a Lesson card you own from outside the game and put it into your " +
        "hand, or discard a card to draw a card.)"

    spell {
        val t = target(
            "target spell or permanent with mana value 1 or greater",
            TargetSpellOrPermanent(
                permanentFilter = GameObjectFilter.Permanent.manaValueAtLeast(1),
                spellFilter = GameObjectFilter.Any.manaValueAtLeast(1),
                // One restriction, printed once — the generated text would repeat it per half.
                descriptionOverride = "target spell or permanent with mana value 1 or greater"
            )
        )
        effect = Effects.ReturnSpellOrPermanentToOwnersHand(t) then Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "41"
        artist = "Liiga Smilshkalne"
        flavorText = "\"Misery. Inadequacy. Failure. The common denominator is you.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/9/1958d96e-ec44-48ab-80b1-5b01a24ac7b8.jpg?1783927380"
    }
}
