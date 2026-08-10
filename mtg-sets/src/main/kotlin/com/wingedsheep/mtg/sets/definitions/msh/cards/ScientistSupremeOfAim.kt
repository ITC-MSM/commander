package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Scientist Supreme of A.I.M. — Marvel Super Heroes #225 (rare)
 * {U}{B} · Legendary Creature — Human Scientist Villain · 2/2
 *
 * Pay 2 life: Copy target activated or triggered ability you control from an artifact source. You
 * may choose new targets for the copy. Activate only during your turn and only once each turn.
 * (Mana abilities can't be targeted.)
 *
 * The artifact-source twin of Echo, Perceptive Prodigy, built from the same pieces:
 *  - [Targets.ActivatedOrTriggeredAbilityYouControlFrom]`(Artifact)` — the existing ability-on-stack
 *    target narrowed by `CardPredicate.AbilitySourceMatches(Artifact)`, which matches the ability's
 *    *source* (CR 113.7) rather than the ability object. An artifact that was animated into a
 *    creature still counts (the source's projected types are read while it is on the battlefield),
 *    and an artifact that has already been sacrificed to its own ability still counts too, because
 *    the source is read with last known information (CR 113.7a).
 *  - [Effects.CopyTargetSpellOrAbility] — the copy, with the CR 707.10c retarget prompt.
 *  - Cost is a bare [Costs.PayLife] — no mana, no tap, so it works the turn Scientist Supreme
 *    enters. The two activation clauses are the existing restrictions
 *    [ActivationRestriction.OnlyDuringYourTurn] and [ActivationRestriction.OncePerTurn].
 *  - `holdPriority` keeps auto-pass from resolving your own artifact's ability before you can copy it.
 */
val ScientistSupremeOfAim = card("Scientist Supreme of A.I.M.") {
    manaCost = "{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Human Scientist Villain"
    power = 2
    toughness = 2
    oracleText = "Pay 2 life: Copy target activated or triggered ability you control from an " +
        "artifact source. You may choose new targets for the copy. Activate only during your turn " +
        "and only once each turn. (Mana abilities can't be targeted.)"

    activatedAbility {
        cost = Costs.PayLife(2)
        val ability = target(
            "activated or triggered ability you control from an artifact source",
            Targets.ActivatedOrTriggeredAbilityYouControlFrom(GameObjectFilter.Artifact)
        )
        effect = Effects.CopyTargetSpellOrAbility(ability)
        restrictions = listOf(
            ActivationRestriction.OnlyDuringYourTurn,
            ActivationRestriction.OncePerTurn
        )
        holdPriority = true
        description = "Pay 2 life: Copy target activated or triggered ability you control from an " +
            "artifact source. You may choose new targets for the copy. Activate only during your " +
            "turn and only once each turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "225"
        artist = "Gal Or"
        flavorText = "\"A weapon to kill a god? A.I.M. can do that, if you have the money.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/4/0473a990-88c4-4921-a492-377d9171318a.jpg?1783902899"
    }
}
