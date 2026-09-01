package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Azorius Charm
 * {W}{U}
 * Instant
 *
 * Choose one —
 * • Creatures you control gain lifelink until end of turn.
 * • Draw a card.
 * • Put target attacking or blocking creature on top of its owner's library.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * A plain choose-one `modal(chooseCount = 1)`. The mass grant is [Effects.ForEachInGroup] over
 * [GroupFilter.AllCreaturesYouControl] with [EffectTarget.Self] as the per-iteration subject —
 * the group iteration's "self", not the spell. Only the third mode targets, so only it
 * declares one; a mode's targets are chosen when the mode is (CR 601.2c).
 */
val AzoriusCharm = card("Azorius Charm") {
    manaCost = "{W}{U}"
    colorIdentity = "UW"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Creatures you control gain lifelink until end of turn.\n" +
        "• Draw a card.\n" +
        "• Put target attacking or blocking creature on top of its owner's library."

    spell {
        modal(chooseCount = 1) {
            mode("Creatures you control gain lifelink until end of turn") {
                effect = Effects.ForEachInGroup(
                    GroupFilter.AllCreaturesYouControl,
                    Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self),
                )
            }
            mode("Draw a card") {
                effect = Effects.DrawCards(1)
            }
            mode("Put target attacking or blocking creature on top of its owner's library") {
                val t = target(
                    "target attacking or blocking creature",
                    TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature)
                )
                effect = Effects.PutOnTopOfLibrary(t)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "145"
        artist = "Zoltan Boros"
        flavorText = "\"The rules of logic and order have already made the choice for you.\"\n" +
            "—Isperia"
        imageUri = "https://cards.scryfall.io/normal/front/2/6/26adc211-d089-4102-91e5-225bbeb5f382.jpg?1783940345"
    }
}
