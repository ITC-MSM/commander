package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerExpiry
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.model.Rarity

/**
 * Rediscover the Way — Tarkir: Dragonstorm #215
 * {U}{R}{W} · Enchantment — Saga · Rare
 *
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 * I, II — Look at the top three cards of your library. Put one of them into your hand and the
 *         rest on the bottom of your library in any order.
 * III — Whenever you cast a noncreature spell this turn, target creature you control gains
 *       double strike until end of turn.
 *
 * Chapters I and II are the standard "look at top three, keep one to hand, bottom the rest in any
 * order" dig — [Patterns.Library.lookAtTopAndKeep] with the remainder going to the bottom of the
 * library, ordered by the controller ([CardOrder.ControllerChooses]).
 *
 * Chapter III installs a turn-bounded, event-based delayed triggered ability:
 * [CreateDelayedTriggerEffect] on [Triggers.YouCastNoncreature] with `fireOnce = false` (it fires
 * for *every* noncreature spell cast this turn, not just the first) and `expiry = EndOfTurn`. Its
 * [CreateDelayedTriggerEffect.targetRequirement] is "target creature you control", chosen each
 * time the trigger fires; the chosen creature is granted double strike until end of turn via
 * [Effects.GrantKeyword] reading `ContextTarget(0)`.
 */
val RediscoverTheWay = card("Rediscover the Way") {
    manaCost = "{U}{R}{W}"
    colorIdentity = "URW"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I, II — Look at the top three cards of your library. Put one of them into your hand and the rest " +
        "on the bottom of your library in any order.\n" +
        "III — Whenever you cast a noncreature spell this turn, target creature you control gains double " +
        "strike until end of turn."

    sagaChapter(1) {
        effect = rediscoverDig()
    }
    sagaChapter(2) {
        effect = rediscoverDig()
    }
    sagaChapter(3) {
        effect = CreateDelayedTriggerEffect(
            trigger = Triggers.YouCastNoncreature,
            fireOnce = false,
            expiry = DelayedTriggerExpiry.EndOfTurn,
            targetRequirement = Targets.CreatureYouControl,
            effect = Effects.GrantKeyword(Keyword.DOUBLE_STRIKE)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "215"
        artist = "Clint Lockwood"
        imageUri = "https://cards.scryfall.io/normal/front/7/9/79d6decf-afd5-4e96-b87e-fd7ab7e3c068.jpg?1743204851"
    }
}

/**
 * "Look at the top three cards of your library. Put one of them into your hand and the rest on the
 * bottom of your library in any order." A fresh instance backs each of chapters I and II.
 */
private fun rediscoverDig() = Patterns.Library.lookAtTopAndKeep(
    count = DynamicAmount.Fixed(3),
    keepCount = DynamicAmount.Fixed(1),
    keepDestination = CardDestination.ToZone(Zone.HAND),
    restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
    restOrder = CardOrder.ControllerChooses,
    selectedLabel = "Put into hand",
    remainderLabel = "Put on bottom"
)
