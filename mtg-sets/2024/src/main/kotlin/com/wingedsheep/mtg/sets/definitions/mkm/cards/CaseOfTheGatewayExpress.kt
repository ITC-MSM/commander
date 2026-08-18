package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.solvedStaticAbility
import com.wingedsheep.sdk.dsl.toSolve
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Case of the Gateway Express — Murders at Karlov Manor #8
 * {1}{W} · Enchantment — Case · Uncommon
 *
 * When this Case enters, choose target creature you don't control. Each creature you control deals
 * 1 damage to that creature.
 * To solve — Three or more creatures attacked this turn.
 * Solved — Creatures you control get +1/+0.
 *
 * The ETB is a group ping, and *who* deals the damage matters: each creature you control is its own
 * damage source, so a deathtouch or lifelink creature in the group applies its own keyword to its
 * own point. That is [Effects.ForEachInGroup] with `damageSource = EffectTarget.Self` — inside a
 * group iteration `Self` binds to the iterated creature, not to the Case — rather than one lump of
 * damage from the enchantment.
 *
 * "Three or more creatures attacked this turn" names no player, so it is
 * [Conditions.CreaturesAttackedThisTurn] (the union of every player's attack record) rather than
 * the controller-scoped `YouAttackedWithCreaturesThisTurn`. The two agree in practice — only the
 * active player declares attackers, and the trigger checks at the Case's controller's own end step
 * — but the printed text is the player-agnostic one, and a creature whose controller changed after
 * it attacked still counts either way, because the record is keyed to the player who declared it.
 */
val CaseOfTheGatewayExpress = card("Case of the Gateway Express") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Case"
    oracleText = "When this Case enters, choose target creature you don't control. Each creature " +
        "you control deals 1 damage to that creature.\n" +
        "To solve — Three or more creatures attacked this turn. (If unsolved, solve at the " +
        "beginning of your end step.)\n" +
        "Solved — Creatures you control get +1/+0."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target = TargetCreature(filter = TargetFilter.Creature.opponentControls())
        effect = Effects.ForEachInGroup(
            filter = Filters.AllControlledCreatures,
            effect = Effects.DealDamage(
                amount = 1,
                target = EffectTarget.ContextTarget(0),
                damageSource = EffectTarget.Self
            )
        )
    }

    toSolve(Conditions.CreaturesAttackedThisTurn(3))

    solvedStaticAbility {
        ability = ModifyStats(1, 0, Filters.AllControlledCreatures)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "8"
        artist = "Edgar Sánchez Hidalgo"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/0862bf07-8a76-4e80-bba2-20d22f8eee30.jpg?1783912927"
    }
}
