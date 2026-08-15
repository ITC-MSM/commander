package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Gate Colossus — RNA #232
 * Artifact Creature — Construct (8/8), Uncommon
 *
 * Affinity for Gates (This spell costs {1} less to cast for each Gate you control.)
 * This creature can't be blocked by creatures with power 2 or less.
 * Whenever a Gate you control enters, you may put this card from your graveyard on top of
 *   your library.
 *
 * The recursion trigger functions from the graveyard (`triggerZone = Zone.GRAVEYARD`) — it is
 * the card in the graveyard that sees a Gate enter and offers to go on top of the library.
 * Binding is ANY rather than OTHER because the source is never itself the entering Gate.
 */
val GateColossus = card("Gate Colossus") {
    manaCost = "{8}"
    typeLine = "Artifact Creature — Construct"
    power = 8
    toughness = 8
    oracleText = "Affinity for Gates (This spell costs {1} less to cast for each Gate you control.)\n" +
        "This creature can't be blocked by creatures with power 2 or less.\n" +
        "Whenever a Gate you control enters, you may put this card from your graveyard on top of your library."

    keywordAbility(KeywordAbility.AffinityForSubtype(Subtype.GATE))

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtMost(2))
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.GATE).youControl(),
            binding = TriggerBinding.ANY,
        )
        triggerZone = Zone.GRAVEYARD
        effect = MayEffect(
            effect = Effects.Move(
                target = EffectTarget.Self,
                destination = Zone.LIBRARY,
                placement = ZonePlacement.Top,
                fromZone = Zone.GRAVEYARD,
            ),
        )
        description = "Whenever a Gate you control enters, you may put this card from your " +
            "graveyard on top of your library."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "232"
        artist = "Izzy"
        imageUri = "https://cards.scryfall.io/normal/front/9/9/99767e2f-a558-4d63-b9b6-923d15b433e1.jpg?1783933626"

        ruling(
            "2024-11-08",
            "To determine the total cost of a spell, start with the mana cost or alternative cost " +
                "you're paying, add any cost increases, then apply any cost reductions. The mana value " +
                "of the spell remains unchanged, no matter what the total cost to cast it was.",
        )
        ruling(
            "2024-11-08",
            "Once a creature with power 3 or greater has blocked Gate Colossus, changing the power of " +
                "the blocking creature won't cause Gate Colossus to become unblocked.",
        )
    }
}
