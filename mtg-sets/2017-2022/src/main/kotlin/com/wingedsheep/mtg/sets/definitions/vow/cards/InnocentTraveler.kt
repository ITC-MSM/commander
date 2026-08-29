package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.AnyPlayerMayPayEffect
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Innocent Traveler // Malicious Invader (Innistrad: Crimson Vow)
 * {2}{B}{B}
 * Creature — Human // Creature — Vampire
 *
 * Front — Innocent Traveler (1/3)
 *   At the beginning of your upkeep, any opponent may sacrifice a creature of their choice.
 *   If no one does, transform this creature.
 *
 * Back — Malicious Invader (3/3)
 *   Flying
 *   This creature gets +2/+0 as long as an opponent controls a Human.
 *
 * The upkeep ability is Desecration Demon's shape run the other way round: an
 * [AnyPlayerMayPayEffect] scoped to [Player.EachOpponent], but with the transform hanging off
 * `consequenceIfNonePaid` rather than `consequence`. That placement is the whole card — per the
 * release-note ruling, each opponent decides in turn order knowing the earlier answers, every
 * chosen creature is sacrificed simultaneously, and the Traveler flips **only** if that set came
 * back empty. One opponent paying is enough to keep it face up, no matter how many others declined.
 *
 * The back's pump is a [ConditionalStaticAbility] over [Filters.Self] recomputed at projection, so
 * the +2/+0 comes and goes with the opposing Human rather than latching. "A Human" is a permanent
 * predicate, not a creature one — a noncreature permanent with the Human subtype turns it on too.
 */

private val InnocentTravelerFront = card("Innocent Traveler") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human"
    power = 1
    toughness = 3
    oracleText = "At the beginning of your upkeep, any opponent may sacrifice a creature of their " +
        "choice. If no one does, transform this creature."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = AnyPlayerMayPayEffect(
            cost = Costs.pay.Sacrifice(GameObjectFilter.Creature, count = 1),
            consequenceIfNonePaid = TransformEffect(EffectTarget.Self),
            eligiblePlayers = Player.EachOpponent
        )
        description = "At the beginning of your upkeep, any opponent may sacrifice a creature of " +
            "their choice. If no one does, transform this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "121"
        artist = "Cristi Balanescu"
        flavorText = "\"Aren't you going to invite me in?\""
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13a5e5fd-a67a-4c0e-97ae-923bdbc1be20.jpg?1783924866"

        ruling(
            "2021-11-19",
            "When Innocent Traveler's ability resolves, the next opponent in turn order chooses " +
                "whether they want to sacrifice a creature at all and which creature to sacrifice " +
                "if so, then each other opponent in turn order does the same, knowing the choices " +
                "made before them. Then all the chosen creatures are sacrificed at the same time. " +
                "If no creatures are sacrificed this way, Innocent Traveler is transformed."
        )
    }
}

private val MaliciousInvader = card("Malicious Invader") {
    manaCost = ""
    colorIdentity = "B"
    colorIndicator = "B" // Transformed back face, no mana cost (CR 204).
    typeLine = "Creature — Vampire"
    power = 3
    toughness = 3
    oracleText = "Flying\nThis creature gets +2/+0 as long as an opponent controls a Human."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(2, 0, Filters.Self),
            condition = Exists(
                Player.EachOpponent,
                Zone.BATTLEFIELD,
                GameObjectFilter.Permanent.withSubtype("Human")
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "121"
        artist = "Cristi Balanescu"
        flavorText = "\"Aren't you going to offer me a drink?\""
        imageUri = "https://cards.scryfall.io/normal/back/1/3/13a5e5fd-a67a-4c0e-97ae-923bdbc1be20.jpg?1783924866"
    }
}

val InnocentTraveler: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = InnocentTravelerFront,
    backFace = MaliciousInvader,
)
