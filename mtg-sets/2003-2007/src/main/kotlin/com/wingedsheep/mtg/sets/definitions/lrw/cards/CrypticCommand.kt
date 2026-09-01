package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Cryptic Command
 * {1}{U}{U}{U}
 * Instant
 * Choose two —
 * • Counter target spell.
 * • Return target permanent to its owner's hand.
 * • Tap all creatures your opponents control.
 * • Draw a card.
 *
 * The Lorwyn Command cycle's blue member, and the same `modal(chooseCount = 2)` shape as its
 * white sibling [AustereCommand]. Two of the four modes target, so those two declare their
 * requirement inside their own `mode` block and the other two declare none — the engine collects
 * the chosen modes' requirements at cast time, which is what the 2017-11-17 ruling about "look at
 * both chosen modes to determine how many targets" describes.
 */
val CrypticCommand = card("Cryptic Command") {
    manaCost = "{1}{U}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Choose two —\n" +
        "• Counter target spell.\n" +
        "• Return target permanent to its owner's hand.\n" +
        "• Tap all creatures your opponents control.\n" +
        "• Draw a card."

    spell {
        modal(chooseCount = 2) {
            mode("Counter target spell") {
                target = Targets.Spell
                effect = Effects.CounterSpell()
            }
            mode("Return target permanent to its owner's hand") {
                val permanent = target("permanent to bounce", Targets.Permanent)
                effect = Effects.ReturnToHand(permanent)
            }
            mode("Tap all creatures your opponents control") {
                effect = Patterns.Group.tapAll(GroupFilter.AllCreaturesOpponentsControl)
            }
            mode("Draw a card") {
                effect = Effects.DrawCards(1)
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "56"
        artist = "Wayne England"
        imageUri = "https://cards.scryfall.io/normal/front/8/2/829e3d6e-5d7c-4cc4-a7a6-7cbf5a7442ba.jpg?1783942904"
        ruling("2017-11-17", "You choose both modes as you cast Cryptic Command. You must choose two different modes.")
        ruling("2017-11-17", "Look at both chosen modes to determine how many targets Cryptic Command has, if any. If it has at least one target, and all its targets are illegal when it tries to resolve, then it won't resolve and none of its effects will happen. For example, if you choose the second and fourth modes, and the permanent is an illegal target when Cryptic Command tries to resolve, you won't draw a card.")
    }
}
