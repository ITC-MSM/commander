package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lumengrid Augur
 * {3}{U}
 * Creature — Vedalken Wizard
 * 2/2
 * {1}, {T}: Target player draws a card, then discards a card. If that player discards an
 * artifact card this way, untap this creature.
 */
val LumengridAugur = card("Lumengrid Augur") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Vedalken Wizard"
    oracleText = "{1}, {T}: Target player draws a card, then discards a card. If that player " +
        "discards an artifact card this way, untap this creature."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        val player = target("player", Targets.Player)
        effect = Effects.Pipeline {
            run(Effects.DrawCards(1, player))
            val hand = gather(CardSource.FromZone(Zone.HAND, Player.TargetPlayer))
            val discarded = chooseExactly(
                1,
                from = hand,
                chooser = Chooser.TargetPlayer,
                prompt = "Choose a card to discard",
                selectedLabel = "Discard"
            )
            move(
                discarded,
                CardDestination.ToZone(Zone.GRAVEYARD, Player.TargetPlayer),
                moveType = MoveType.Discard
            )
            ifNotEmpty(discarded, GameObjectFilter.Artifact) {
                run(Effects.Untap(EffectTarget.Self))
            }
        }
        description = "Target player draws a card, then discards a card. If that player discards " +
            "an artifact card this way, untap this creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "39"
        artist = "rk post"
        flavorText = "Information pumps like blood through vedalken society."
        imageUri = "https://cards.scryfall.io/normal/front/6/5/65704c0f-cc97-490e-a0f7-fab9a18e1e88.jpg?1783944554"
    }
}
