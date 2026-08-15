package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Sidequest: Catch a Fish // Cooking Campsite — Final Fantasy #31
 * {2}{W} · Enchantment // Land
 *
 * Front — Sidequest: Catch a Fish:
 *   At the beginning of your upkeep, look at the top card of your library. If it's an
 *   artifact or creature card, you may reveal it and put it into your hand. If you put a
 *   card into your hand this way, create a Food token and transform this enchantment.
 *
 * Back — Cooking Campsite:
 *   {T}: Add {W}.
 *   {3}, {T}, Sacrifice an artifact: Put a +1/+1 counter on each creature you control.
 *   Activate only as a sorcery.
 *
 * The upkeep ability is a look-top → may-take → reflexive pipeline: gather the top card,
 * offer only an artifact/creature card for the hand, and gate the Food + transform on a card
 * actually being taken (the reveal is a "may", so declining leaves the card on top and the
 * enchantment un-transformed — see the 2025-06-06 ruling). The transform side lives on the
 * land back face's activated abilities.
 */
private val CookingCampsite = card("Cooking Campsite") {
    manaCost = ""
    colorIdentity = "W"
    typeLine = "Land"
    oracleText = "{T}: Add {W}.\n" +
        "{3}, {T}, Sacrifice an artifact: Put a +1/+1 counter on each creature you control. " +
        "Activate only as a sorcery."

    // {T}: Add {W}.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {3}, {T}, Sacrifice an artifact: Put a +1/+1 counter on each creature you control.
    // Activate only as a sorcery.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{3}"),
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.Artifact)
        )
        timing = TimingRule.SorcerySpeed
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "31"
        artist = "Gal Or"
        flavorText = "\"Seeing how you enjoy fishing, you should learn how to prepare your catch.\"\n" +
            "—Ignis Scientia"
        imageUri = "https://cards.scryfall.io/normal/back/b/d/bdb5452e-d97f-409b-91d0-2664f39b09b8.jpg?1782686574"
    }
}

private val SidequestCatchAFishFront = card("Sidequest: Catch a Fish") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, look at the top card of your library. If it's " +
        "an artifact or creature card, you may reveal it and put it into your hand. If you put a " +
        "card into your hand this way, create a Food token and transform this enchantment."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Pipeline {
            // Look at the top card of your library.
            val looked = gather(CardSource.TopOfLibrary(DynamicAmount.Fixed(1), player = Player.You))
            // If it's an artifact or creature card, you may reveal it and put it into your hand.
            val kept = chooseUpTo(
                count = 1,
                from = looked,
                filter = GameObjectFilter.Artifact or GameObjectFilter.Creature,
                showAllCards = true,
                prompt = "You may reveal this card and put it into your hand",
                selectedLabel = "Put into your hand",
            )
            // If you put a card into your hand this way, create a Food token and transform.
            ifNotEmpty(kept) {
                toHand(kept, revealed = true)
                run(Effects.CreateFood())
                run(TransformEffect(EffectTarget.Self))
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "31"
        artist = "Gal Or"
        flavorText = "\"I am Noctis, Prince of Lucis and King of Fishing!\""
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bdb5452e-d97f-409b-91d0-2664f39b09b8.jpg?1782686574"

        ruling(
            "2025-06-06",
            "You don't have to reveal the card even if it's an artifact or creature card. You may " +
                "choose not to reveal it and instead leave it on top of your library."
        )
    }
}

val SidequestCatchAFish: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = SidequestCatchAFishFront,
    backFace = CookingCampsite,
)
