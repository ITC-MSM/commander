package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rolling Spoil — Ravnica: City of Guilds #179
 * {2}{G}{G} · Sorcery
 *
 * Destroy target land. If {B} was spent to cast this spell, all creatures get -1/-1 until end of turn.
 *
 * The Golgari entry in Ravnica's "if {X} was spent" rider cycle. "All creatures" is every creature on
 * the battlefield, yours included, so it is an unfiltered `GroupFilter(Creature)` sweep and not a
 * `youControl()` one — the group is snapshotted on resolution, which is also why a creature that
 * enters afterwards is unaffected.
 *
 * The rider follows the destruction rather than branching around it: the -1/-1 happens whether or not
 * the land actually died (a regenerated or indestructible land still leaves the sweep intact), and
 * both halves are lost together only if the single target is illegal on resolution (CR 608.2b).
 */
val RollingSpoil = card("Rolling Spoil") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Destroy target land. " +
        "If {B} was spent to cast this spell, all creatures get -1/-1 until end of turn."

    spell {
        val land = target("land", Targets.Land)
        effect = Effects.Move(land, Zone.GRAVEYARD, byDestruction = true)
            .then(
                ConditionalEffect(
                    condition = Conditions.ManaSpentToCastIncludes(requiredBlack = 1),
                    effect = Effects.ForEachInGroup(
                        GroupFilter(GameObjectFilter.Creature),
                        Effects.ModifyStats(-1, -1, EffectTarget.Self),
                    ),
                )
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "179"
        artist = "Ron Spencer"
        flavorText = "The shadow that fell over the grove was silent yet horribly alive, roiling with " +
            "millions of tiny minions dedicated to the process of rot."
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e6c5546f-2429-4099-a9bd-eda3f52779b7.jpg?1783943633"
    }
}
