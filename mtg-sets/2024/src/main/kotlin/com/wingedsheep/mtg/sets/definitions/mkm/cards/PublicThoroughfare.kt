package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Public Thoroughfare — Murders at Karlov Manor #265
 * Land · Common
 *
 * This land enters tapped.
 * When this land enters, sacrifice it unless you tap an untapped artifact or land you control.
 * {T}: Add one mana of any color.
 *
 * A five-color land whose price is a second permanent's turn. All three lines are existing rails:
 * [EntersTapped] as a replacement effect, [PayOrSufferEffect] for the "unless" clause, and
 * [Effects.AddAnyColorMana] behind a `manaAbility` activation.
 *
 * The "unless" is a **cost, paid on resolution of the trigger** — not a targeted choice and not a
 * may-gate. [PayOrSufferEffect] models exactly that asymmetry: pay [Costs.pay.Tap] and keep the
 * land, decline (or be unable to pay) and [SacrificeSelfEffect] fires. `Costs.pay.Tap` already
 * means "tap N *untapped* permanents matching the filter that *you control*", so the printed
 * "untapped … you control" needs no further narrowing; the filter carries only the type union,
 * [GameObjectFilter.ArtifactOrLand].
 *
 * There is deliberately **no self-exclusion** on that filter. Public Thoroughfare normally enters
 * tapped and so can't pay for itself, but the 2024-02-02 ruling calls out the unusual case where it
 * is untapped as the trigger resolves — an effect that untapped it in response — and in that case
 * tapping itself is a legal payment. `CostAtom.TapPermanents.excludeSelf` defaults to false and is
 * left that way; the printed text says "an untapped artifact or land you control", not "another".
 *
 * That line did not work before this card: `PayOrSufferExecutor` used to drop the source from the
 * candidate set unconditionally, disagreeing with `CostPaymentService`, which has always read the
 * atom's flag. Fixed in the same change — the executor now honours `excludeSelf` on both its
 * sacrifice and tap costs, which also repairs Command Bridge's identical ruling.
 *
 * Ordering note: the land enters tapped by replacement, so the enters trigger sees a tapped
 * permanent. The mana ability is a real mana ability ([TimingRule.ManaAbility], `manaAbility =
 * true`) — it never uses the stack, so it can be activated while paying for something else.
 */
val PublicThoroughfare = card("Public Thoroughfare") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "This land enters tapped.\n" +
        "When this land enters, sacrifice it unless you tap an untapped artifact or land you " +
        "control.\n" +
        "{T}: Add one mana of any color."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = PayOrSufferEffect(
            cost = Costs.pay.Tap(GameObjectFilter.ArtifactOrLand),
            suffer = SacrificeSelfEffect
        )
        description = "When this land enters, sacrifice it unless you tap an untapped artifact " +
            "or land you control."
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "265"
        artist = "Anthony Devine"
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f8b915f-3e82-4b05-b963-01ebff7a8f7b.jpg?1783912824"

        ruling(
            "2024-02-02",
            "In the unusual case where Public Thoroughfare is untapped as its triggered ability " +
                "is resolving, you can tap it for its own triggered ability."
        )
    }
}
