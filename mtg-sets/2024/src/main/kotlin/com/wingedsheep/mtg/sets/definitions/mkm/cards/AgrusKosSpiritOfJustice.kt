package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.TriggeredAbilityBuilder
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Agrus Kos, Spirit of Justice — Murders at Karlov Manor #184
 * {2}{R}{W} · Legendary Creature — Spirit Detective · 2/4
 *
 * Double strike, vigilance
 * Whenever Agrus Kos enters or attacks, choose up to one target creature. If it's suspected, exile
 * it. Otherwise, suspect it.
 *
 * A two-stage removal engine: the first hit hangs a suspect designation on a blocker (menace plus
 * "can't block", CR 701.60a), and any later hit — the same Agrus Kos attacking again, or a second
 * copy entering — cashes that designation in for an exile. Because Agrus Kos has vigilance, the
 * attack trigger costs nothing defensively, and double strike means the body it just disarmed is
 * exactly the one it wanted out of the way.
 *
 * **"Enters or attacks" is two triggered abilities, not one.** The SDK has no combined
 * enters-or-attacks trigger, so the ability is written twice over a shared effect factory — the same
 * shape [BenthicCriminologists] uses for the identical clause — one `TriggeredAbilityBuilder`
 * extension declaring the target and the rider, invoked from both blocks. That is also the rules-correct
 * reading: they are separate abilities that each trigger and target independently, so a turn where
 * Agrus Kos enters *and* attacks puts two abilities on the stack, each choosing its own target.
 *
 * **The branch reads the target at resolution, not at announcement.** `TargetMatchesFilter` over
 * `Creature.suspected()` is evaluated when the ability resolves, so a creature that was suspected on
 * announcement but had the designation stripped in between (Absolving Lammasu, Airtight Alibi) gets
 * suspected again rather than exiled — and vice versa. Modelling this as two modes chosen up front
 * would get that backwards. The condition is keyed on the *target index* rather than on the handle
 * `target(…)` returns: `ConditionEvaluator` resolves `ContextTarget` but not a bound variable, so a
 * handle-based condition would silently evaluate false and always take the suspect branch.
 *
 * `optional = true` carries the printed "up to one": declining is legal even with legal targets on
 * the board, which matters because suspecting an opponent's creature *helps* it get through as an
 * attacker. With no target chosen the ability resolves and does nothing.
 *
 * Suspect is deliberately permanent (`Effects.Suspect` defaults to `Duration.Permanent`) — per the
 * rulings a creature stays suspected until it leaves the battlefield or an effect un-suspects it.
 * Nothing about the exile branch refers to Agrus Kos, so it still happens if Agrus Kos dies to the
 * blocker's damage before the ability resolves.
 */
val AgrusKosSpiritOfJustice = card("Agrus Kos, Spirit of Justice") {
    manaCost = "{2}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Spirit Detective"
    power = 2
    toughness = 4
    oracleText = "Double strike, vigilance\n" +
        "Whenever Agrus Kos enters or attacks, choose up to one target creature. If it's suspected, " +
        "exile it. Otherwise, suspect it. (A suspected creature has menace and can't block.)"

    keywords(Keyword.DOUBLE_STRIKE, Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        exileIfSuspectedOtherwiseSuspect()
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        exileIfSuspectedOtherwiseSuspect()
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "184"
        artist = "Jason A. Engle"
        flavorText = "\"Eventually I'll give retirement another shot. But not today.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/8/58aeac7c-1275-49d4-9915-7604ae4bfdff.jpg?1783912857"

        ruling(
            "2024-02-02",
            "When an effect suspects a creature, it becomes suspected. It gains menace and \"This " +
                "creature can't block\" for as long as it's suspected. It stays suspected until it " +
                "leaves the battlefield or another effect causes it to no longer be suspected."
        )
        ruling(
            "2024-02-02",
            "If a suspected creature loses all abilities, it will lose menace and \"This creature " +
                "can't block\", but it won't stop being suspected."
        )
        ruling(
            "2024-02-02",
            "Being suspected isn't a copiable value. If a permanent becomes a copy of a suspected " +
                "creature, it won't be suspected."
        )
        ruling(
            "2024-02-02",
            "If a creature is already suspected, suspecting it again won't have any effect."
        )
        ruling(
            "2024-02-02",
            "There's no limit to the number of creatures that can be suspected simultaneously. " +
                "Suspecting a new creature doesn't cause other creatures to stop being suspected."
        )
    }
}

/** The rider shared by the enters and attacks triggers. */
private fun TriggeredAbilityBuilder.exileIfSuspectedOtherwiseSuspect() {
    val suspect = target("up to one target creature", TargetCreature(optional = true))
    effect = ConditionalEffect(
        condition = Conditions.TargetMatchesFilter(GameObjectFilter.Creature.suspected()),
        effect = Effects.Exile(suspect),
        elseEffect = Effects.Suspect(suspect)
    )
    description = "Choose up to one target creature. If it's suspected, exile it. Otherwise, " +
        "suspect it."
}
