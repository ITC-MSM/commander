package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Reckless Fireweaver
 * {1}{R}
 * Creature — Human Artificer
 * 1/3
 * Whenever an artifact you control enters, this creature deals 1 damage to each opponent.
 *
 * The Weldfast Wingsmith trigger shape — [Triggers.entersBattlefield] over
 * `Artifact.youControl()` with [TriggerBinding.ANY], so it watches every artifact rather than only
 * the source. "Each opponent" is a single [EffectTarget.PlayerRef] over [Player.EachOpponent];
 * the damage source defaults to the ability's own source, so no `damageSource` is spelled here.
 */
val RecklessFireweaver = card("Reckless Fireweaver") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Artificer"
    oracleText = "Whenever an artifact you control enters, this creature deals 1 damage to each opponent."
    power = 1
    toughness = 3

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
        description = "This creature deals 1 damage to each opponent."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "126"
        artist = "Deruchenko Alexander"
        flavorText = "Architects find inspiration in the dragon's beauty of form, while artificers are influenced by the function of its fire."
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63ffac51-62c4-4170-85b3-a43d7cfae7d7.jpg?1783937190"
    }
}
