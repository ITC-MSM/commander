package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * River's Rebuke
 * {4}{U}{U}
 * Sorcery
 * Return all nonland permanents target player controls to their owner's hand.
 *
 * Targets a player ([Targets.Player] — either player, including the caster), then bounces every
 * nonland permanent that player *controls* back to its owner's hand. The controller-axis match is
 * [GameObjectFilter.targetPlayerControls], reading `context.targetPlayerId`; because
 * [Patterns.Group.returnAllToHand] gathers with `BattlefieldMatching(player = Player.Each)` and the
 * move routes each card to its owner's hand, a permanent the target controls but another player owns
 * is still returned to its rightful owner (not the target).
 */
val RiversRebuke = card("River's Rebuke") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Return all nonland permanents target player controls to their owner's hand."

    spell {
        target("target player", Targets.Player)
        effect = Patterns.Group.returnAllToHand(
            GroupFilter(GameObjectFilter.NonlandPermanent.targetPlayerControls())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "71"
        artist = "Raymond Swanland"
        flavorText = "Carefully following the thaumatic compass Bolas had given her, Vraska blundered straight into the River Heralds' trap."
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fda8ef30-bbfa-4857-9750-0dd0def8b13f.jpg?1782710457"
    }
}
