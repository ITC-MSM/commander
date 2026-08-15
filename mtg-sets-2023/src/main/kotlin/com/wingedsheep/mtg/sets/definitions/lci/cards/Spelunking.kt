package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersUntapped
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalOnCollectionEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Spelunking — {2}{G} Enchantment.
 *
 * - ETB: "draw a card, then you may put a land card from your hand onto the battlefield. If you put
 *   a Cave onto the battlefield this way, you gain 4 life." Composed as
 *   Draw → [Patterns.Hand.putFromHand] (Gather hand lands → Select up to 1 → Move to battlefield;
 *   the "up to 1" models the "you may" — the player can decline or have no land) → a
 *   [ConditionalOnCollectionEffect] gate over the pipeline's `"putting"` collection restricted to
 *   the Cave subtype, the exact "if the moved card is a X" shape used by connive / Cauldron Dance.
 *   The life gain only fires when a Cave was actually put this way.
 * - "Lands you control enter untapped." — a static [EntersUntapped] replacement consulted from the
 *   battlefield whenever a land its controller controls would enter tapped (the inverse of
 *   [com.wingedsheep.sdk.scripting.EntersTapped], per The Wandering Minstrel). Per CR 616.1 the
 *   affected land's controller chooses the replacement order — and since this affects only "lands
 *   you control", that chooser is Spelunking's controller, who always applies "enters untapped"
 *   last, so it wins over a land's own "enters tapped" replacement. A land merely put onto the
 *   battlefield tapped (no replacement) enters untapped instead.
 */
val Spelunking = card("Spelunking") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, draw a card, then you may put a land card from " +
        "your hand onto the battlefield. If you put a Cave onto the battlefield this way, you " +
        "gain 4 life.\n" +
        "Lands you control enter untapped."

    // When this enchantment enters, draw a card, then you may put a land card from your hand onto
    // the battlefield. If you put a Cave onto the battlefield this way, you gain 4 life.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = DrawCardsEffect(1, EffectTarget.Controller) then
            Patterns.Hand.putFromHand(filter = GameObjectFilter.Land, count = 1).then(
                ConditionalOnCollectionEffect(
                    collection = "putting",
                    filter = GameObjectFilter.Land.withSubtype("Cave"),
                    ifNotEmpty = Effects.GainLife(4)
                )
            )
        description = "When this enchantment enters, draw a card, then you may put a land card " +
            "from your hand onto the battlefield. If you put a Cave onto the battlefield this " +
            "way, you gain 4 life."
    }

    // Lands you control enter untapped.
    replacementEffect(
        EntersUntapped(
            appliesTo = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Land.youControl(),
                to = Zone.BATTLEFIELD,
            )
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "213"
        artist = "Ernanda Souza"
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d3be4257-2316-4a2e-b347-f71c0368a947.jpg?1782694439"
    }
}
