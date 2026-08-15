package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

// Filter for any card with the Dinosaur subtype (matches creature cards in hand by type line).
private val DinosaurCardFilter = GameObjectFilter.Any.withSubtype(Subtype.DINOSAUR)

// Filter for Dinosaur creatures on the battlefield.
private val DinosaurCreatureFilter = GameObjectFilter.Creature.withSubtype(Subtype.DINOSAUR)

/**
 * Armored Kincaller — {2}{G}
 * Creature — Dinosaur
 * 3/3
 * When this creature enters, you may reveal a Dinosaur card from your hand.
 * If you do or if you control another Dinosaur, you gain 3 life.
 *
 * Implementation notes:
 *   The "if you do or if you control another Dinosaur" is modelled via a
 *   Gather → SelectUpTo(1) → Reveal pipeline over the controller's hand, storing the
 *   result in "kincallerRevealed". A ConditionalEffect then gates GainLife(3) on an
 *   AnyCondition over two sub-checks:
 *     (1) CollectionContainsMatch("kincallerRevealed") — true iff the player actually
 *         selected a card to reveal.
 *     (2) YouControl(Creature.Dinosaur, excludeSelf = true) — true iff at least one
 *         other Dinosaur creature is on the battlefield (Kincaller itself is excluded).
 *
 *   This faithfully covers all three resolution paths:
 *     A. Player has a Dinosaur in hand and reveals it → collection non-empty → gain 3 life.
 *     B. Player has no Dinosaur in hand (or declines) but controls another Dinosaur → gain 3 life.
 *     C. Neither → no life gain.
 *
 *   When the player has no Dinosaur cards in hand, GatherCards yields an empty collection;
 *   SelectFromCollection auto-skips the prompt; RevealCollection is a no-op on the empty
 *   collection; and the AnyCondition falls through to the battlefield check.
 */
val ArmoredKincaller = card("Armored Kincaller") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    oracleText = "When this creature enters, you may reveal a Dinosaur card from your hand. " +
        "If you do or if you control another Dinosaur, you gain 3 life."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            listOf(
                // Step 1: Collect all Dinosaur cards from the controller's hand.
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        zone = Zone.HAND,
                        player = Player.You,
                        filter = DinosaurCardFilter,
                    ),
                    storeAs = "kincallerDinos",
                ),
                // Step 2: Player may pick at most one to reveal (0 = decline / no eligible cards).
                SelectFromCollectionEffect(
                    from = "kincallerDinos",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    storeSelected = "kincallerRevealed",
                    prompt = "You may reveal a Dinosaur card from your hand",
                ),
                // Step 3: Publicly reveal the selected card. No-op if empty.
                RevealCollectionEffect(from = "kincallerRevealed", fromZone = Zone.HAND),
                // Step 4: Gain 3 life if the player revealed a Dinosaur OR controls another one.
                ConditionalEffect(
                    condition = Conditions.Any(
                        Conditions.CollectionContainsMatch("kincallerRevealed"),
                        Conditions.YouControl(
                            filter = DinosaurCreatureFilter,
                            excludeSelf = true,
                        ),
                    ),
                    effect = Effects.GainLife(3),
                ),
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "174"
        artist = "John Tedrick"
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b76a46a1-a63e-460a-98c5-699dd1c827aa.jpg?1782694471"
    }
}
