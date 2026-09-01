package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Profane Command
 * {X}{B}{B}
 * Sorcery
 * Choose two —
 * • Target player loses X life.
 * • Return target creature card with mana value X or less from your graveyard to the battlefield.
 * • Target creature gets -X/-X until end of turn.
 * • Up to X target creatures gain fear until end of turn.
 *
 * The black member of the Lorwyn Command cycle, and the only one with an {X} in its cost. The
 * 2007-10-01 ruling — "the value chosen for X applies to each X in the spell's effect. You pay {X}
 * only once" — is exactly what falls out of every mode reading the *same* [DynamicAmount.XValue]:
 * both chosen modes see the one X paid at cast time.
 *
 * X appears in three different grammatical positions, and each needs its own SDK spelling:
 *  - as an **amount** (`LoseLife`) — [DynamicAmount.XValue] straight.
 *  - as a **filter bound** ("mana value X or less") — `manaValueAtMostX()`, a card predicate that
 *    reads the paid X, not a fixed number baked in at authoring time.
 *  - as a **target count** ("up to X target creatures") — `dynamicMaxCount = XValue` with
 *    `optional = true` for the "up to", the shape Word of Binding and Icy Blast use.
 *
 * The -X/-X mode negates through [DynamicAmount.Multiply] rather than a separate "negative amount"
 * type, matching Chill Haunting.
 */
val ProfaneCommand = card("Profane Command") {
    manaCost = "{X}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Choose two —\n" +
        "• Target player loses X life.\n" +
        "• Return target creature card with mana value X or less from your graveyard to the battlefield.\n" +
        "• Target creature gets -X/-X until end of turn.\n" +
        "• Up to X target creatures gain fear until end of turn. (They can't be blocked except " +
        "by artifact creatures and/or black creatures.)"

    spell {
        modal(chooseCount = 2) {
            mode("Target player loses X life") {
                val player = target("player to lose life", Targets.Player)
                effect = Effects.LoseLife(DynamicAmount.XValue, player)
            }
            mode("Return target creature card with mana value X or less from your graveyard to the battlefield") {
                val creatureCard = target(
                    "creature card with mana value X or less in your graveyard",
                    TargetObject(filter = TargetFilter.CreatureInYourGraveyard.manaValueAtMostX())
                )
                effect = Effects.PutOntoBattlefieldFromGraveyard(creatureCard)
            }
            mode("Target creature gets -X/-X until end of turn") {
                val creature = target("creature to weaken", Targets.Creature)
                val negX = DynamicAmount.Multiply(DynamicAmount.XValue, -1)
                effect = Effects.ModifyStats(negX, negX, creature)
            }
            mode("Up to X target creatures gain fear until end of turn") {
                target(
                    "creatures to gain fear",
                    TargetCreature(optional = true, dynamicMaxCount = DynamicAmount.XValue)
                )
                effect = ForEachTargetEffect(
                    listOf(Effects.GrantKeyword(Keyword.FEAR, EffectTarget.ContextTarget(0)))
                )
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "135"
        artist = "Wayne England"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/752bf7fd-c49e-42fa-bbdd-8ca5d4c89b2b.jpg?1783942885"
        ruling("2007-10-01", "The value chosen for X applies to each X in the spell's effect. You pay {X} only once.")
    }
}
