package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CreateTokenCopyOfTargetEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * "for each Construct you control" — the bare tribal noun, so every Construct *permanent* you
 * control counts, not just the creatures among them (CR 301.5c's Equipment precedent).
 */
private val ConstructsYouControl =
    DynamicAmounts.battlefield(Player.You, GameObjectFilter.Permanent.withSubtype(Subtype.CONSTRUCT))
        .count()

/**
 * Dollhouse of Horrors
 * {5}
 * Artifact
 *
 * {1}, {T}, Exile a creature card from your graveyard: Create a token that's a copy of the exiled
 * card, except it's a 0/0 Construct artifact in addition to its other types and it has "This token
 * gets +1/+1 for each Construct you control." It gains haste until end of turn. Activate only as a
 * sorcery.
 *
 * Implementation notes:
 * - The exile is a **cost** (CR 601.2h), so by resolution the card is already in exile and can't be
 *   read off the graveyard. [CardSource.ExiledAsCost] names exactly the cards *this activation's*
 *   payment exiled, and gathering them lets [EffectTarget.PipelineTarget] point the copy effect at
 *   the one card that was paid — Necropolis' shape, one step further along. Nothing targets, so
 *   nothing fizzles.
 * - The "except …" riders map 1:1 onto [CreateTokenCopyOfTargetEffect]'s copy-exception fields:
 *   `overridePower`/`overrideToughness` for the 0/0, `addCardTypes` for "artifact **in addition to**
 *   its other types", `addedSubtypes` for the added Construct, and `addedStaticAbilities` for the
 *   quoted "+1/+1 for each Construct you control". Setting the P/T outright is also what the second
 *   ruling requires: a copied characteristic-defining ability that would define power/toughness is
 *   not copied, so the token really is a 0/0 base even off a Lhurgoyf.
 * - The granted static counts *permanents* with the Construct subtype you control — the bare tribal
 *   noun, not "Construct creatures" — and the token counts itself (first ruling), so a lone token is
 *   a 1/1.
 * - Haste rides as an added keyword on the copy rather than a separate until-end-of-turn grant. A
 *   token created this turn can only ever care about haste this turn (summoning sickness ends at its
 *   controller's next untap), so the two agree everywhere except the corner where the token changes
 *   controller later; the separate grant would have to reach the just-created token through a
 *   pipeline handle, which is a strictly worse trade for that corner.
 */
val DollhouseOfHorrors = card("Dollhouse of Horrors") {
    manaCost = "{5}"
    typeLine = "Artifact"
    oracleText = "{1}, {T}, Exile a creature card from your graveyard: Create a token that's a " +
        "copy of the exiled card, except it's a 0/0 Construct artifact in addition to its other " +
        "types and it has \"This token gets +1/+1 for each Construct you control.\" It gains haste " +
        "until end of turn. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Tap,
            Costs.ExileFromGraveyard(1, GameObjectFilter.Creature),
        )
        timing = TimingRule.SorcerySpeed
        effect = Effects.Composite(
            GatherCardsEffect(source = CardSource.ExiledAsCost, storeAs = "dollhouseExiled"),
            CreateTokenCopyOfTargetEffect(
                target = EffectTarget.PipelineTarget("dollhouseExiled"),
                overridePower = 0,
                overrideToughness = 0,
                addCardTypes = setOf(CardType.ARTIFACT.name),
                addedSubtypes = setOf(Subtype.CONSTRUCT),
                addedKeywords = setOf(Keyword.HASTE),
                addedStaticAbilities = listOf(
                    GrantDynamicStatsEffect(
                        filter = GroupFilter.source(),
                        powerBonus = ConstructsYouControl,
                        toughnessBonus = ConstructsYouControl,
                    )
                ),
            ),
        )
        description = "Create a token that's a copy of the exiled card, except it's a 0/0 " +
            "Construct artifact in addition to its other types and it has \"This token gets +1/+1 " +
            "for each Construct you control.\" It gains haste until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "255"
        artist = "Muhammad Firdaus"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/396abc9e-a738-430d-85cc-448ace2548f9.jpg?1783924786"

        ruling("2021-11-19", "The token creature counts itself when counting Constructs you control.")
        ruling(
            "2021-11-19",
            "If the exiled creature card has any characteristic-defining abilities that define its " +
                "power and/or toughness, those abilities are not copied."
        )
    }
}
