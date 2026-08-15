package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Ostrich-Horse
 * {2}{G}
 * Creature — Bird Horse
 * 3/1
 *
 * When this creature enters, mill three cards. You may put a land card from among them into
 * your hand. If you don't, put a +1/+1 counter on this creature. (To mill three cards, put the
 * top three cards of your library into your graveyard.)
 *
 * Modeled as an inline Gather → Move (mill) → optional-choice pipeline: the top three cards go
 * to the graveyard, the milled collection is partitioned on `Land`, and the player may choose up
 * to one land among them to put into their hand. If a land was chosen the chosen slot is
 * non-empty and goes to hand; otherwise (the player declined, or no land was milled) the slot is
 * empty and a +1/+1 counter is placed on this creature — matching "if you don't". The gathered
 * collection tracks entity refs, so selecting from it after the mill move still points at exactly
 * those three cards now in the graveyard.
 */
val OstrichHorse = card("Ostrich-Horse") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Bird Horse"
    power = 3
    toughness = 1
    oracleText = "When this creature enters, mill three cards. You may put a land card from among " +
        "them into your hand. If you don't, put a +1/+1 counter on this creature. (To mill three " +
        "cards, put the top three cards of your library into your graveyard.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Pipeline {
            val milled = gather(CardSource.TopOfLibrary(DynamicAmount.Fixed(3), Player.You))
            toGraveyard(milled)
            val lands = filter(milled, GameObjectFilter.Land)
            val chosen = chooseUpTo(1, from = lands)
            ifNotEmpty(chosen) {
                toHand(chosen)
            } orElse {
                run(Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self))
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "188"
        artist = "Pablo Rivera"
        flavorText = "Ostrich-horses are renowned in the Earth Kingdom as reliable steeds for " +
            "couriers, soldiers, and merchants."
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5ca3fd45-9301-44ef-afc0-4d7999d66d36.jpg?1764121280"
    }
}
