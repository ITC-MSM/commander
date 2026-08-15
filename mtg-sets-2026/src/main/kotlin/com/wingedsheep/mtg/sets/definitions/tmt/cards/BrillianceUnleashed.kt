package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Brilliance Unleashed
 * {4}{U}{R}
 * Sorcery
 *
 * Choose one or both —
 * • Brilliance Unleashed deals 5 damage to target creature.
 * • Choose target artifact card in your graveyard. Return it to the
 *   battlefield if it's an artifact creature card. Otherwise, return
 *   it to the battlefield and it's a 3/3 Robot artifact creature with
 *   flying.
 */
val BrillianceUnleashed = card("Brilliance Unleashed") {
    manaCost = "{4}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Sorcery"
    oracleText = "Choose one or both —\n• Brilliance Unleashed deals 5 damage to target creature.\n• Choose target artifact card in your graveyard. Return it to the battlefield if it's an artifact creature card. Otherwise, return it to the battlefield and it's a 3/3 Robot artifact creature with flying."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Brilliance Unleashed deals 5 damage to target creature") {
                val creature = target("target creature", Targets.Creature)
                effect = Effects.DealDamage(5, creature)
            }
            mode("Return target artifact card in your graveyard to the battlefield. If it isn't an artifact creature card, it's a 3/3 Robot artifact creature with flying") {
                target = TargetObject(
                    filter = TargetFilter(
                        baseFilter = GameObjectFilter.Artifact.ownedByYou(),
                        zone = Zone.GRAVEYARD,
                    )
                )
                effect = Effects.Move(
                    target = com.wingedsheep.sdk.scripting.targets.EffectTarget.ContextTarget(0),
                    destination = Zone.BATTLEFIELD,
                    fromZone = Zone.GRAVEYARD,
                ).then(
                    ConditionalEffect(
                        condition = Conditions.Not(
                            Conditions.TargetMatchesFilter(GameObjectFilter.Creature)
                        ),
                        effect = Effects.BecomeCreature(
                            target = com.wingedsheep.sdk.scripting.targets.EffectTarget.ContextTarget(0),
                            power = 3,
                            toughness = 3,
                            keywords = setOf(Keyword.FLYING),
                            creatureTypes = setOf("Robot"),
                            duration = Duration.Permanent,
                        )
                    )
                )
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "141"
        artist = "Hokyoung Kim"
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b7ab2110-5aad-46c9-8dc4-1eac24b6f46b.jpg?1771587000"
    }
}
