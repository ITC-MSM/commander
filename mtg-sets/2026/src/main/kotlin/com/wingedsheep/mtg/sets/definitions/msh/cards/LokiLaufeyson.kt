package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Loki Laufeyson — Marvel Super Heroes #143 (uncommon)
 * {1}{R} · Legendary Creature — God Sorcerer Villain · 2/1
 *
 * {1}, {T}: When you next cast an instant or sorcery spell with mana value less than or equal to
 * Loki's power this turn, copy that spell. You may choose new targets for the copy.
 * Power-up — {4}{R}: Put two +1/+1 counters on Loki. (Activate each power-up ability only once.
 * Reduce the cost by his mana cost if he entered this turn.)
 *
 * Ability 1 is the standard pending-rider shape ([Effects.CopyNextSpellCast], Howl of the Horde /
 * Gadwick's First Duel), the only novelty being that the mana-value cap is not a constant but
 * Loki's own power — `manaValueAtMostDynamic(DynamicAmounts.sourcePower())`. The rider's filter is
 * evaluated when the spell is actually cast, which is exactly when the delayed trigger's condition
 * is checked, so pumping Loki with the power-up ability *after* activating this one still widens
 * what the rider will copy. (`CastSpellHandler` threads the rider's own `sourceId` into the
 * predicate context so `EntityReference.Source` resolves to Loki rather than to nothing.)
 *
 * Ability 2 is the plain power-up cycle shape (CR 702.193, Brave Brawler): `isPowerUp = true`
 * desugars to `ActivationRestriction.Once` and switches on the engine's pip-wise self cost
 * reduction, so on the turn Loki enters, {4}{R} − {1}{R} = {3}.
 *
 * The two abilities read as one card: activate the copy rider, then power up to a 4/3 and the
 * rider will now catch an instant or sorcery costing up to four.
 *
 * **Last-known information for the cap.** If Loki has left the battlefield by the time the matching
 * spell is cast (armed the rider, powered up to 4/3, then got killed in response), CR 608.2h makes
 * the cap his *last known* power — 4 — because the effect reads information from an object that is
 * no longer in the zone it was expected to be in (see also CR 113.7a: the ability exists
 * independently of its source, so removing the source doesn't remove the rider). `PendingSpellCopy`
 * carries a `lastKnownSourceSnapshot` that `ZoneTransitionService` stamps at the moment the source
 * leaves the battlefield, and `PredicateEvaluator.evaluateDynamicCap` threads it into the
 * `EffectContext` it reconstructs, so `DynamicAmountEvaluator`'s existing LKI branch resolves it.
 * Stamped at departure rather than at rider creation precisely so the pump is included; a *shrunk*
 * Loki is symmetrically stricter.
 */
val LokiLaufeyson = card("Loki Laufeyson") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — God Sorcerer Villain"
    oracleText = "{1}, {T}: When you next cast an instant or sorcery spell with mana value less " +
        "than or equal to Loki's power this turn, copy that spell. You may choose new targets " +
        "for the copy.\n" +
        "Power-up — {4}{R}: Put two +1/+1 counters on Loki. (Activate each power-up ability only " +
        "once. Reduce the cost by his mana cost if he entered this turn.)"
    power = 2
    toughness = 1

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Effects.CopyNextSpellCast(
            copies = 1,
            spellFilter = GameObjectFilter.InstantOrSorcery
                .manaValueAtMostDynamic(DynamicAmounts.sourcePower())
        )
        description = "{1}, {T}: When you next cast an instant or sorcery spell with mana value " +
            "less than or equal to Loki's power this turn, copy that spell. You may choose new " +
            "targets for the copy."
    }

    activatedAbility {
        isPowerUp = true
        cost = Costs.Mana("{4}{R}")
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "143"
        artist = "Julia Vasilyeva"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53b795a7-11f8-423a-b021-f811007fc82f.jpg?1783902928"
    }
}
