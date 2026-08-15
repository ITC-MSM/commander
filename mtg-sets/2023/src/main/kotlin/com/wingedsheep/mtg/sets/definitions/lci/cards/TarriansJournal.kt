package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MayCastFromGraveyard
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tarrian's Journal // The Tomb of Aclazotz — The Lost Caverns of Ixalan #126
 * {1}{B} · Legendary Artifact — Book
 * //  · Legendary Land — Cave
 *
 * Front — Tarrian's Journal:
 *   {T}, Sacrifice another artifact or creature: Draw a card. Activate only as a sorcery.
 *   {2}, {T}, Discard your hand: Transform Tarrian's Journal.
 *
 * Back — The Tomb of Aclazotz:
 *   {T}: Add {B}.
 *   {T}: You may cast a creature spell from your graveyard this turn. If you do, it enters with a
 *   finality counter on it and is a Vampire in addition to its other types.
 *
 * The transform is a plain in-place flip (`TransformEffect`, CR 701.27) — not an exile-return — so
 * `CardDefinition.doubleFacedPermanent` (artifact front, non-creature land back). The back's second
 * ability grants `MayCastFromGraveyard(Creature)` for the turn (the already-wired graveyard-cast
 * permission), carrying a cast-this-way **entry rider**: a creature cast from the graveyard under
 * this grant enters with a finality counter and gains Vampire "in addition to its other types". The
 * rider is frozen onto the cast spell at cast time (`CastSpellHandler` reads the authorizing
 * `MayCastFromGraveyard` grant) and applied when it resolves onto the battlefield (`StackResolver`
 * → `EntersWithReplacements.applyCastFromGraveyardRider`): the counter is placed (the finality
 * death-replacement in `ZoneMovementUtils` then exiles it instead of dying) and the added subtype is
 * a floating `Layer.TYPE` effect that lasts while the permanent is on the battlefield.
 */

private val TarriansJournalFront = card("Tarrian's Journal") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Artifact — Book"
    oracleText = "{T}, Sacrifice another artifact or creature: Draw a card. Activate only as a " +
        "sorcery.\n" +
        "{2}, {T}, Discard your hand: Transform Tarrian's Journal."

    // {T}, Sacrifice another artifact or creature: Draw a card. Activate only as a sorcery.
    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeAnother(GameObjectFilter.CreatureOrArtifact))
        timing = TimingRule.SorcerySpeed
        effect = Effects.DrawCards(1)
    }

    // {2}, {T}, Discard your hand: Transform Tarrian's Journal.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.DiscardHand)
        effect = TransformEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "126"
        artist = "Randy Gallegos"
        imageUri = "https://cards.scryfall.io/normal/front/9/9/99255a66-b868-45fc-a2a9-0c89bd851b69.jpg?1782694509"
    }
}

private val TheTombOfAclazotz = card("The Tomb of Aclazotz") {
    manaCost = ""
    colorIdentity = "B"
    typeLine = "Legendary Land — Cave"
    oracleText = "(Transforms from Tarrian's Journal.)\n" +
        "{T}: Add {B}.\n" +
        "{T}: You may cast a creature spell from your graveyard this turn. If you do, it enters " +
        "with a finality counter on it and is a Vampire in addition to its other types. (If a " +
        "creature with a finality counter on it would die, exile it instead.)"

    // {T}: Add {B}.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
    }

    // {T}: You may cast a creature spell from your graveyard this turn (finality + Vampire rider).
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.GrantStaticAbility(
            MayCastFromGraveyard(
                filter = GameObjectFilter.Creature,
                entersWithCounter = CounterType.FINALITY,
                addedSubtypeOnEntry = "Vampire",
            ),
            EffectTarget.Self,
            Duration.EndOfTurn,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "126"
        artist = "Randy Gallegos"
        imageUri = "https://cards.scryfall.io/normal/back/9/9/99255a66-b868-45fc-a2a9-0c89bd851b69.jpg?1782694509"
    }
}

val TarriansJournal: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = TarriansJournalFront,
    backFace = TheTombOfAclazotz,
)
