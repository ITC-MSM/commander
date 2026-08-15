package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Haunting Wind
 * {3}{B}
 * Enchantment
 * Whenever an artifact becomes tapped or a player activates an artifact's ability without {T} in
 * its activation cost, this enchantment deals 1 damage to that artifact's controller.
 *
 * The Antiquities "tap / activate an artifact" punisher template (cf. Powerleech, Artifact
 * Possession). It is two triggers:
 *  - the tap half ([Triggers.becomesTapped] over [GameObjectFilter.Artifact]) — "that artifact's
 *    controller" is the controller of the just-tapped artifact, reached via
 *    [EffectTarget.ControllerOfTriggeringEntity].
 *  - the ability half ([Triggers.activatesAbilityWithoutTap]) — keys on the literal "without {T}
 *    in its activation cost" wording (not "isn't a mana ability"), so a non-{T} mana ability also
 *    fires it. "That artifact's controller" is the controller of the activated artifact
 *    ([EffectTarget.ControllerOfTriggeringEntity]) — not the activating player, which differs
 *    when a non-controller activates (e.g. Armageddon Clock's any-player ability).
 */
val HauntingWind = card("Haunting Wind") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "Whenever an artifact becomes tapped or a player activates an artifact's ability " +
        "without {T} in its activation cost, this enchantment deals 1 damage to that artifact's " +
        "controller."

    triggeredAbility {
        trigger = Triggers.becomesTapped(
            binding = TriggerBinding.ANY,
            filter = GameObjectFilter.Artifact
        )
        effect = Effects.DealDamage(1, EffectTarget.ControllerOfTriggeringEntity)
    }

    triggeredAbility {
        trigger = Triggers.activatesAbilityWithoutTap(
            player = Player.Each,
            sourceFilter = GameObjectFilter.Artifact
        )
        effect = Effects.DealDamage(1, EffectTarget.ControllerOfTriggeringEntity)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "17"
        artist = "Jeff A. Menges"
        flavorText = "These devices lured so many spirits that sometimes entire battlefields would " +
            "become haunted at once."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2f6ef2f-a3a2-4e1f-b7eb-59abc8414114.jpg?1562929345"
    }
}
