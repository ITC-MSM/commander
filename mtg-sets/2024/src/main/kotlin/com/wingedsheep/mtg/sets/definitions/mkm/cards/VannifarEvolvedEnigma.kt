package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Vannifar, Evolved Enigma — Murders at Karlov Manor #241
 * {2}{G}{U} · Legendary Creature — Elf Ooze Wizard · 3/4
 *
 * At the beginning of combat on your turn, choose one —
 * • Cloak a card from your hand.
 * • Put a +1/+1 counter on each colorless creature you control.
 *
 * The two modes are one engine: cloaking makes colorless 2/2s, and the counter mode grows exactly
 * those. Nothing links them in the SDK — the link is that a face-down permanent is colorless
 * (CR 708.2), and [CardPredicate.IsColorless] reads *projected* colors, so a cloaked card counts
 * regardless of what it is on the front.
 *
 * The cloak mode is the hand-side twin of Hide in Plain Sight's library pipeline: gather the hand,
 * select one, and move it to the battlefield with [FaceDownMode.CLOAK]. There is no
 * cloak-from-hand facade because this is the first card that needs one — `Patterns.Hand.putFromHand`
 * has no face-down mode, and widening it for a single caller would be speculative. An empty hand
 * simply selects nothing and the mode does nothing; the mode is still legal to choose (CR 601.2b
 * — modes aren't checked for effect).
 *
 * The counter mode is `ForEachInGroup` rather than a targeted effect: "each colorless creature you
 * control" targets nothing, so it reaches hexproof creatures and can't be fizzled.
 */
val VannifarEvolvedEnigma = card("Vannifar, Evolved Enigma") {
    manaCost = "{2}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Legendary Creature — Elf Ooze Wizard"
    oracleText = "At the beginning of combat on your turn, choose one —\n" +
        "• Cloak a card from your hand. (Put it onto the battlefield face down as a 2/2 creature " +
        "with ward {2}. Turn it face up any time for its mana cost if it's a creature card.)\n" +
        "• Put a +1/+1 counter on each colorless creature you control."
    power = 3
    toughness = 4

    triggeredAbility {
        trigger = Triggers.BeginCombat
        effect = ModalEffect(
            modes = listOf(
                Mode.noTarget(
                    Effects.Composite(
                        GatherCardsEffect(
                            source = CardSource.FromZone(Zone.HAND),
                            storeAs = "vannifarHand",
                        ),
                        SelectFromCollectionEffect(
                            from = "vannifarHand",
                            selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                            storeSelected = "vannifarCloaking",
                            prompt = "Choose a card to cloak",
                        ),
                        MoveCollectionEffect(
                            from = "vannifarCloaking",
                            destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                            faceDown = FaceDownMode.CLOAK,
                        ),
                    ),
                    "Cloak a card from your hand",
                ),
                Mode.noTarget(
                    Effects.ForEachInGroup(
                        GroupFilter(
                            GameObjectFilter.Creature
                                .withCardPredicate(CardPredicate.IsColorless)
                                .youControl(),
                        ),
                        Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
                    ),
                    "Put a +1/+1 counter on each colorless creature you control",
                ),
            ),
            chooseCount = 1,
            countsAsModalSpell = false,
        )
        description = "At the beginning of combat on your turn, choose one — Cloak a card from " +
            "your hand; or put a +1/+1 counter on each colorless creature you control."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "241"
        artist = "Uriah Voth"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6d381eb-6cb6-4505-aebe-995c1ddc8527.jpg?1783912833"
    }
}
