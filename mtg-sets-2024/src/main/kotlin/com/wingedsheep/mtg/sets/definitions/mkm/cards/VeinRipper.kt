package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Vein Ripper — Murders at Karlov Manor #110
 * {3}{B}{B}{B} · Creature — Vampire Assassin · 6/5
 *
 * Flying
 * Ward—Sacrifice a creature.
 * Whenever a creature dies, target opponent loses 2 life and you gain 2 life.
 *
 * The drain trigger is [Triggers.AnyCreatureDies] — *any* creature, either side of the table,
 * including the Ripper itself. That last case matters and falls out for free: the Ripper dying is a
 * creature dying, so it drains once on the way out (the trigger is detected from the zone-change
 * event, which is emitted whether or not the source survives to see it).
 *
 * "Target opponent" is chosen when the trigger goes on the stack, so a board wipe puts one trigger
 * per dead creature on the stack and each picks its own target. [Effects.DrainLife] is the single
 * "N from them, N to you" primitive — modelling it as separate lose/gain effects would let the gain
 * happen even when the loss was prevented or the opponent left the game.
 *
 * Ward—Sacrifice a creature ([WardCost.Sacrifice]) is the real protection: it isn't a mana tax an
 * opponent can simply pay through, it's a second creature, which usually means removal on the Ripper
 * costs them their own board. Ward triggers on becoming a target of an opponent's spell or ability
 * (CR 702.21b), so it does nothing against edicts or board wipes that never target.
 */
val VeinRipper = card("Vein Ripper") {
    manaCost = "{3}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Assassin"
    power = 6
    toughness = 5
    oracleText = "Flying\n" +
        "Ward—Sacrifice a creature.\n" +
        "Whenever a creature dies, target opponent loses 2 life and you gain 2 life."

    keywords(Keyword.FLYING)

    keywordAbility(KeywordAbility.Ward(WardCost.Sacrifice(GameObjectFilter.Creature)))

    triggeredAbility {
        trigger = Triggers.AnyCreatureDies
        target = Targets.Opponent
        effect = Effects.DrainLife(
            amount = DynamicAmount.Fixed(2),
            from = EffectTarget.ContextTarget(0),
            to = EffectTarget.Controller,
        )
        description = "Whenever a creature dies, target opponent loses 2 life and you gain 2 life."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "110"
        artist = "Bastien L. Deharme"
        flavorText = "\"I'm afraid your investigation has reached a dead end.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/7/078933b3-6d82-45f2-94e8-addf54cf1704.jpg?1783912890"
    }
}
