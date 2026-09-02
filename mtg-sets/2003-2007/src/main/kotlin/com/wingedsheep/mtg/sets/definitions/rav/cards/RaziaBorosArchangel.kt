package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RedirectNextDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetOther

/**
 * Razia, Boros Archangel — Ravnica: City of Guilds #223
 * {4}{R}{R}{W}{W} · Legendary Creature — Angel 6/3 · Rare
 *
 * Flying, vigilance, haste
 * {T}: The next 3 damage that would be dealt to target creature you control this turn is dealt
 * to another target creature instead.
 *
 * Modelling notes:
 * - A capacity-limited redirection shield (CR 615.7 / CR 614.9): `amount = 3` is what makes the
 *   two rulings fall out of the engine's own bookkeeping — the 3 damage need not come from one
 *   source or all at once, because the shield decrements per instance and survives until its
 *   capacity is spent.
 * - Two independent single-target requirements, the second wrapped in [TargetOther] so it cannot
 *   be the same creature as the first (CR 601.2c "another"). Both are creatures — the recipient
 *   may be any creature, including one Razia's controller owns.
 * - "If either target creature leaves the battlefield before damage is dealt, that damage won't
 *   be redirected": the protected creature leaving means nothing is dealt to it; the recipient
 *   leaving is handled by the redirection check, which skips a shield whose recipient is gone so
 *   the original creature takes the damage. Razia herself leaving doesn't matter — the shield is
 *   a floating effect, not a static of hers.
 */
val RaziaBorosArchangel = card("Razia, Boros Archangel") {
    manaCost = "{4}{R}{R}{W}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Angel"
    power = 6
    toughness = 3
    oracleText = "Flying, vigilance, haste\n" +
        "{T}: The next 3 damage that would be dealt to target creature you control this turn is " +
        "dealt to another target creature instead."

    keywords(Keyword.FLYING, Keyword.VIGILANCE, Keyword.HASTE)

    activatedAbility {
        cost = Costs.Tap
        val shielded = target(
            "target creature you control",
            TargetCreature(filter = TargetFilter.CreatureYouControl)
        )
        val recipient = target("another target creature", TargetOther(TargetCreature()))
        effect = RedirectNextDamageEffect(
            protectedTargets = listOf(shielded),
            redirectTo = recipient,
            amount = 3
        )
        description = "The next 3 damage that would be dealt to target creature you control this " +
            "turn is dealt to another target creature instead."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "223"
        artist = "Donato Giancola"
        flavorText = "Her sword burns with such brightness that foes avert their eyes and arrows " +
            "divert their paths."
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4580cc4-ee26-41d9-a81c-91505c9e2a99.jpg?1783943615"
        ruling(
            "2005-10-01",
            "The 3 damage doesn't have to be from the same source, and it doesn't have to be all " +
                "dealt at once. If only 2 damage is redirected, the next 1 damage will also be " +
                "redirected."
        )
        ruling(
            "2005-10-01",
            "If either target creature leaves the battlefield before damage is dealt, that damage " +
                "won't be redirected. It doesn't matter if Razia leaves the battlefield."
        )
    }
}
