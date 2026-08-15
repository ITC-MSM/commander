package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Powerleech
 * {G}{G}
 * Enchantment
 * Whenever an artifact an opponent controls becomes tapped or an opponent activates an artifact's
 * ability without {T} in its activation cost, you gain 1 life.
 *
 * Same "tap / activate an artifact" punisher template as Haunting Wind, but scoped to opponents'
 * artifacts and rewarding the enchantment's controller with life instead of dealing damage:
 *  - tap half: [Triggers.becomesTapped] over `Artifact.opponentControls()` ("an artifact an
 *    opponent controls becomes tapped").
 *  - ability half: [Triggers.activatesAbilityWithoutTap] with [Player.EachOpponent] and NO
 *    controller restriction on the artifact — oracle only requires that "an opponent activates
 *    an artifact's ability", so an opponent activating an any-player ability of an artifact
 *    you control (e.g. Armageddon Clock) also triggers it.
 *
 * The reward is [Effects.GainLife] for the controller (default target), so no triggering-entity
 * reference is needed.
 */
val Powerleech = card("Powerleech") {
    manaCost = "{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Whenever an artifact an opponent controls becomes tapped or an opponent " +
        "activates an artifact's ability without {T} in its activation cost, you gain 1 life."

    triggeredAbility {
        trigger = Triggers.becomesTapped(
            binding = TriggerBinding.ANY,
            filter = GameObjectFilter.Artifact.opponentControls()
        )
        effect = Effects.GainLife(1)
    }

    triggeredAbility {
        trigger = Triggers.activatesAbilityWithoutTap(
            player = Player.EachOpponent,
            sourceFilter = GameObjectFilter.Artifact
        )
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "34"
        artist = "Christopher Rush"
        flavorText = "The Forest of Argoth has developed a resistance to mechanical intrusion."
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae1d7b09-3a1f-410f-b330-04ae768b0455.jpg?1562931798"
    }
}
