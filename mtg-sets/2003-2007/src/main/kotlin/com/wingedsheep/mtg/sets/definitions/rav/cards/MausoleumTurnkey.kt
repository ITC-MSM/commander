package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetChooser
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Mausoleum Turnkey — Ravnica: City of Guilds #94
 * {3}{B} · Creature — Ogre Rogue · Uncommon · 3/2
 *
 * When this creature enters, return target creature card of an opponent's choice from your
 * graveyard to your hand.
 *
 * Modelling notes:
 * - The whole card is one enters trigger over the existing `Effects.ReturnToHand`. The only new
 *   thing is **who picks the target**: `TargetChooser.Opponent` existed and was honored on
 *   *activated* abilities only (Cuombajj Witches), with `CardLinter` failing any card that put it
 *   on a triggered one. This card is the printed triggered use, so the trigger path now pins the
 *   deciding opponent the same way the activated path does.
 * - The chooser is **orthogonal to legality** (see `TargetChooser`): the legal targets are still
 *   found relative to *this* card's controller, so "from your graveyard" stays your graveyard. The
 *   opponent only picks which of those cards comes back — which is the whole point of the card, and
 *   why it can't be modelled as an opponent-controlled effect.
 * - Mandatory, not "may": with a creature card in your graveyard the trigger goes on the stack and
 *   an opponent must name one. With none, no legal target exists and the trigger is removed from
 *   the stack (CR 603.3d) — nothing to decline.
 * - With three or more opponents the controller first picks *which* opponent decides, then that
 *   opponent picks the card. In a two-player game the engine skips straight to the only opponent.
 */
val MausoleumTurnkey = card("Mausoleum Turnkey") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Ogre Rogue"
    oracleText = "When this creature enters, return target creature card of an opponent's choice " +
        "from your graveyard to your hand."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "target creature card of an opponent's choice from your graveyard",
            TargetObject(
                filter = TargetFilter.CreatureInYourGraveyard,
                chooser = TargetChooser.Opponent
            )
        )
        effect = Effects.ReturnToHand(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "94"
        artist = "Darrell Riche"
        flavorText = "When he reaches for his key ring, you should be running for the exit."
        imageUri = "https://cards.scryfall.io/normal/front/5/1/51b6dc7d-fc8a-4842-bf30-3dc9fec708c4.jpg?1783943667"
    }
}
