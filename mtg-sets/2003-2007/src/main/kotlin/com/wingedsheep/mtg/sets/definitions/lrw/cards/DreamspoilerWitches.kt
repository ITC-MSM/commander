package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Dreamspoiler Witches
 * {3}{B}
 * Creature — Faerie Wizard
 * 2/2
 * Flying
 * Whenever you cast a spell during an opponent's turn, you may have target creature get -1/-1
 * until end of turn.
 *
 * The Lorwyn Faerie payoff for holding up flash and instants. "During an opponent's turn" is
 * checked when the ability would trigger, so it rides on `triggerRestriction`
 * ([Conditions.IsNotYourTurn]) rather than gating the resolved effect — only players take turns,
 * so "not your turn" is exactly "an opponent's turn". Same shape as [DreamSpoilers] in Wilds of
 * Eldraine, which is a strictly-worse reprint of this idea.
 *
 * The target is mandatory (not "up to one"), so the ability is only put on the stack when there is
 * a creature to point at; the "you may" is a decline made on resolution.
 */
val DreamspoilerWitches = card("Dreamspoiler Witches") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Faerie Wizard"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "Whenever you cast a spell during an opponent's turn, you may have target creature get " +
        "-1/-1 until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastSpell
        triggerRestriction = Conditions.IsNotYourTurn
        val creature = target("target creature", Targets.Creature)
        effect = MayEffect(Effects.ModifyStats(-1, -1, creature))
        description = "Whenever you cast a spell during an opponent's turn, you may have target " +
            "creature get -1/-1 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "108"
        artist = "Jeff Easley"
        flavorText = "At night, the faeries steal dreamstuff for their queen. At daybreak, countless creatures wake weak and hollow."
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d5c2487-5da0-4d9a-beb2-598feeb068a1.jpg?1783942891"
    }
}
