package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenCopyOfTargetEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Doppelgang — Murders at Karlov Manor #198
 * {X}{X}{X}{G}{U} · Sorcery
 *
 * For each of X target permanents, create X tokens that are copies of that permanent.
 *
 * X squared, for three mana each. The single X is spent three times over: it prices the spell, it
 * caps how many permanents you may target, and it sets how many copies each of them gets — so the
 * card's whole shape is one number read in three places.
 *
 * Both quantity reads are the same [DynamicAmount.XValue], resolved from the X locked in at cast
 * time:
 *  - `TargetPermanent.dynamicMaxCount` clamps the *target count* to X. This surfaces as
 *    `xConstrainsTargetCount` on the legal action, so the client's targeting overlay stops the
 *    player at X and `TargetValidator` rejects a cast that exceeded it. `optional = true` follows
 *    the [com.wingedsheep.mtg.sets.definitions.mir.cards.BuildersBane] precedent for the "X target"
 *    shape: X=0 is legal but does nothing, and a board with fewer than X legal permanents doesn't
 *    make the spell uncastable.
 *  - `CreateTokenCopyOfTargetEffect.count` is the *copies per target*.
 *
 * [ForEachTargetEffect] is what turns one into the other: it re-runs its body once per surviving
 * target with that target bound to `ContextTarget(0)`, so a target that became illegal in response
 * is skipped without costing the others their copies.
 *
 * Copying is the standard copiable-values path, which is exactly what the rulings ask for: tokens
 * take printed characteristics (plus whatever the original was itself copying), not counters,
 * Auras, damage, or tap state, and their enters-the-battlefield abilities trigger normally. Nothing
 * strips legendary — copying your own legend is a real, deliberate way to lose it to the legend
 * rule, and copying an opponent's is a real way to make them lose theirs.
 */
val Doppelgang = card("Doppelgang") {
    manaCost = "{X}{X}{X}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Sorcery"
    oracleText = "For each of X target permanents, create X tokens that are copies of that permanent."

    spell {
        target = TargetPermanent(
            optional = true,
            filter = TargetFilter.Permanent,
            dynamicMaxCount = DynamicAmount.XValue
        )
        effect = ForEachTargetEffect(
            listOf(
                CreateTokenCopyOfTargetEffect(
                    target = EffectTarget.ContextTarget(0),
                    count = DynamicAmount.XValue
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "198"
        artist = "Chris Rallis"
        flavorText = "\"Suspect spotted at the Tin Street Market, Yescu Street entrance. And the " +
            "Oak Street entrance. And somehow also in Lobar Alley!\"\n" +
            "—Avult of the Foundway Associates"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2daec58-78ed-4da5-b3b0-b04f12b0acbd.jpg?1783912852"

        ruling(
            "2024-02-02",
            "The tokens copy exactly what was printed on the original permanent and nothing else " +
                "(unless that permanent is itself copying something else; see below). They don't " +
                "copy whether that permanent is tapped or untapped, whether it has any counters " +
                "on it or Auras attached to it, or any non-copy effects that have changed its " +
                "power, toughness, types, color, and so on."
        )
        ruling(
            "2024-02-02",
            "If the copied permanent is copying something else, then the tokens enter the " +
                "battlefield as whatever that permanent copied."
        )
        ruling("2024-02-02", "If the copied permanent has {X} in its mana cost, X is 0.")
        ruling(
            "2024-02-02",
            "Any enters-the-battlefield abilities of the copied permanent will trigger when the " +
                "tokens enter the battlefield. Any \"as [this permanent] enters the battlefield\" " +
                "or \"[this permanent] enters the battlefield with\" abilities of the permanent " +
                "will also work."
        )
    }
}
