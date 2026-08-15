package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.craft
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.DonorCards
import com.wingedsheep.sdk.scripting.HasAllActivatedAbilitiesOfCards
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * The Enigma Jewel // Locus of Enlightenment (CR 702.167, The Lost Caverns of Ixalan, #55)
 * {U}
 * Legendary Artifact // Legendary Artifact
 *
 * Front face — The Enigma Jewel ({U}, Legendary Artifact)
 *   The Enigma Jewel enters tapped.
 *   {T}: Add {C}{C}. Spend this mana only to activate abilities.
 *   Craft with four or more nonlands with activated abilities {8}{U}
 *
 * Back face — Locus of Enlightenment (Legendary Artifact)
 *   Locus of Enlightenment has each activated ability of the exiled cards used to craft it.
 *   You may activate each of those abilities only once each turn.
 *   Whenever you activate an ability that isn't a mana ability, copy it. You may choose new
 *   targets for the copy.
 *
 * Implementation (fully built from composable primitives — no card-specific engine code):
 *  - "Enters tapped" is the [EntersTapped] enters-the-battlefield replacement effect.
 *  - The mana ability is [Effects.AddColorlessMana] (2) carrying
 *    [ManaRestriction.AbilityActivationOnly] — the pool-level spend check accepts any ability
 *    activation (of any source) and rejects spell casts and special actions.
 *  - The **craft** ability is the `craft(...)` DSL over the material filter
 *    `GameObjectFilter.Nonland.withCardPredicate(CardPredicate.HasActivatedAbility)` — "four or
 *    more nonlands with activated abilities" (CR 702.167a/b; the material pool spans battlefield
 *    permanents you control and cards in your graveyard, and `HasActivatedAbility` counts mana
 *    abilities too, so a mana rock/dork qualifies).
 *  - The back face's **ability grant** is the [HasAllActivatedAbilitiesOfCards] static with
 *    `source = CRAFTED` and `oncePerTurnEach = true` (CR 702.167c): the Locus has each activated
 *    ability of the cards exiled to craft it, each usable only once each turn (tracked per exiled
 *    card — two exiled copies of one card each get their own budget). `{T}` costs tap the Locus and
 *    self-references bind to it (CR 113.7 — a granted ability's source is the object that has it).
 *  - The back face's **copy clause** is [Triggers.YouActivateAbility] (fires for every activated
 *    ability you activate that isn't a mana ability, CR 605.1a) paired with
 *    [Effects.CopyTargetSpellOrAbility] against [EffectTarget.TriggeringEntity]: the triggering
 *    entity of an `AbilityActivatedEvent` is the activated ability already on the stack, and the
 *    copy executor reprompts for new targets (CR 707.10c) or copies a no-target ability without a
 *    prompt. The copy is pushed with `emitActivationEvent = false`, so it doesn't re-trigger the
 *    clause and doesn't consume another once-each-turn activation.
 */

/** "Four or more nonlands with activated abilities" — the craft material filter (CR 702.167a/b). */
private val CraftMaterialFilter: GameObjectFilter =
    GameObjectFilter.Nonland.withCardPredicate(CardPredicate.HasActivatedAbility)

private val TheEnigmaJewelFront = card("The Enigma Jewel") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Legendary Artifact"
    oracleText = "The Enigma Jewel enters tapped.\n" +
        "{T}: Add {C}{C}. Spend this mana only to activate abilities.\n" +
        "Craft with four or more nonlands with activated abilities {8}{U} ({8}{U}, Exile this artifact, Exile the four or more from among other permanents you control and/or cards in your graveyard: Return this card transformed under its owner's control. Craft only as a sorcery.)"

    // The Enigma Jewel enters tapped.
    replacementEffect(EntersTapped())

    // {T}: Add {C}{C}. Spend this mana only to activate abilities.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(2, ManaRestriction.AbilityActivationOnly)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // Craft with four or more nonlands with activated abilities {8}{U}
    craft(
        filter = CraftMaterialFilter,
        cost = "{8}{U}",
        materialDescription = "four or more nonlands with activated abilities",
        minCount = 4
    )

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "55"
        artist = "Martin de Diego Sádaba"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e98970d-06a8-4c91-ba47-4a02c5b949f2.jpg?1782694566"
    }
}

private val LocusOfEnlightenment = card("Locus of Enlightenment") {
    manaCost = ""
    colorIdentity = "U"
    typeLine = "Legendary Artifact"
    oracleText = "Locus of Enlightenment has each activated ability of the exiled cards used to craft it. You may activate each of those abilities only once each turn.\n" +
        "Whenever you activate an ability that isn't a mana ability, copy it. You may choose new targets for the copy."

    // Locus of Enlightenment has each activated ability of the exiled cards used to craft it.
    // You may activate each of those abilities only once each turn.
    staticAbility {
        ability = HasAllActivatedAbilitiesOfCards(
            donors = DonorCards.CRAFT_MATERIALS,
            oncePerTurnEach = true
        )
    }

    // Whenever you activate an ability that isn't a mana ability, copy it. You may choose
    // new targets for the copy. (CR 605.1a excludes mana abilities from the trigger event;
    // the copy executor handles targeted and untargeted abilities — CR 707.10c.)
    triggeredAbility {
        trigger = Triggers.YouActivateAbility
        effect = Effects.CopyTargetSpellOrAbility(EffectTarget.TriggeringEntity)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "55"
        artist = "Martin de Diego Sádaba"
        imageUri = "https://cards.scryfall.io/normal/back/2/e/2e98970d-06a8-4c91-ba47-4a02c5b949f2.jpg?1782694566"
    }
}

val TheEnigmaJewel: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = TheEnigmaJewelFront,
    backFace = LocusOfEnlightenment
)
