package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Evil's Thrall — Marvel Super Heroes #128
 * {2}{R} · Sorcery
 *
 * Gain control of target creature until end of turn. If you control a Villain with greater mana
 * value than that creature, gain control of that creature until the end of your next turn instead.
 * Untap that creature. It gains haste until end of turn.
 *
 * A Threaten (Act of Treason) with one rider: the steal lasts a whole extra turn when your board
 * has a *bigger* Villain than the creature you are borrowing.
 *
 * **"Instead" is a single control change, not two.** The second sentence replaces the first's
 * duration rather than adding a second effect, so the script is one [ConditionalEffect] whose two
 * branches differ only in [Duration] — never `GainControl(EndOfTurn)` followed by a second grab.
 * One control change means one `ControlChangedEvent` and one summoning-sickness stamp either way.
 *
 * **The condition.** "You control a Villain with greater mana value than that creature" is the
 * greatest mana value among Villains you control being strictly greater than the target's mana
 * value — `AggregateBattlefield(MAX)` returns 0 for an empty set, so controlling no Villain can
 * never satisfy `> targetManaValue` (a mana-value-0 target included: 0 is not greater than 0, and
 * neither is a Villain that merely *ties* the target). Filtering by the Villain **subtype** over
 * `GameObjectFilter.Permanent` (not `Creature`) is this set's idiom for "a Villain" and matches
 * CR 205.3: "a Villain" is any permanent with that subtype. The subtype and controller halves of
 * the aggregate read projected state, so a creature that only has the Villain type from a
 * continuous effect (Ultron, Absorbing Man) counts. Mana value itself is read off the card
 * component, so a projection that only changes types does not move it.
 *
 * The target may itself be a Villain you control — the printed text does not exclude it, and the
 * comparison handles it: it can never out-value itself, so only a *different*, bigger Villain
 * extends the duration.
 *
 * **Haste is not extended.** Only the control change gets the longer duration; the printed haste
 * grant is "until end of turn" in both branches. That costs nothing on the extra turn — the
 * creature has been under your control continuously since that turn began, so CR 302.6's
 * summoning-sickness rule is already satisfied.
 *
 * Effect order follows the printed order (control, then untap, then haste), the Twisted Fealty
 * idiom, so the untap and the keyword grant both land on a creature already under your control.
 */
val EvilsThrall = card("Evil's Thrall") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Gain control of target creature until end of turn. If you control a Villain " +
        "with greater mana value than that creature, gain control of that creature until the end " +
        "of your next turn instead. Untap that creature. It gains haste until end of turn."

    spell {
        val stolen = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            ConditionalEffect(
                condition = Conditions.CompareAmounts(
                    DynamicAmounts.battlefield(
                        Player.You,
                        GameObjectFilter.Permanent.withSubtype(Subtype.VILLAIN)
                    ).maxManaValue(),
                    ComparisonOperator.GT,
                    DynamicAmounts.targetManaValue()
                ),
                effect = Effects.GainControl(stolen, Duration.EndOfYourNextTurn),
                elseEffect = Effects.GainControl(stolen, Duration.EndOfTurn)
            ),
            Effects.Untap(stolen),
            Effects.GrantKeyword(Keyword.HASTE, stolen)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "128"
        artist = "Mintautas Šukys"
        flavorText = "\"Stay your fists, Rand. You are not well!\"\n—T'Challa, the Black Panther"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/310e30cd-b8c3-40ea-9d61-57c5c5fc2a0b.jpg?1783902932"
    }
}
