package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Lammastide Weave — Lorwyn #226
 * {1}{G} · Instant
 *
 * Choose a card name, then target player mills a card. If a card with the chosen name was milled
 * this way, you gain life equal to its mana value.
 * Draw a card.
 *
 * The Aberrant Researcher / Loafing Giant idiom: `Patterns.Library.mill(1, …)` publishes the
 * milled card to the standard `"milled"` collection, and [Conditions.CollectionContainsMatch]
 * reads it back — here against [GameObjectFilter.namedFromVariable], the name the controller just
 * chose with [Effects.ChooseCardName]. Because the check is against the *gathered card* rather
 * than against the graveyard, a replacement effect that sends the milled card elsewhere still
 * pays off, which is the same reason that idiom was chosen there.
 *
 * The life gained is [DynamicAmount.ManaValueSumOfCollection] over that one-card collection.
 * A sum is exact here precisely because the mill count is one: "its mana value" has a single
 * referent, and an empty library mills nothing, matches nothing, and gains nothing.
 *
 * The name is chosen *before* the mill (printed "choose a card name, **then**"), so the chooser
 * cannot see the card first. The draw is a separate sentence and happens either way.
 */
val LammastideWeave = card("Lammastide Weave") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Choose a card name, then target player mills a card. If a card with the chosen " +
        "name was milled this way, you gain life equal to its mana value.\n" +
        "Draw a card."

    spell {
        target("target player", Targets.Player)
        effect = Effects.Composite(
            listOf(
                Effects.ChooseCardName(
                    storeAs = "weaveChosenName",
                    prompt = "Choose a card name",
                ),
                Patterns.Library.mill(1, EffectTarget.ContextTarget(0)),
                ConditionalEffect(
                    condition = Conditions.CollectionContainsMatch(
                        "milled",
                        GameObjectFilter.Any.namedFromVariable("weaveChosenName"),
                    ),
                    effect = Effects.GainLife(DynamicAmount.ManaValueSumOfCollection("milled")),
                ),
                Effects.DrawCards(1),
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "226"
        artist = "Howard Lyon"
        flavorText = "\"A ribbon torn will ward away dark dreams.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/6/667fd7ab-de75-48e7-8d4b-a96130ae4666.jpg?1783942861"
    }
}
