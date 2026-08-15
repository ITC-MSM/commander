package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Aura Shards
 * {1}{G}{W}
 * Enchantment
 * Whenever a creature you control enters, you may destroy target artifact or enchantment.
 *
 * The trigger fires for every creature you control entering (including the creature whose ETB
 * triggered it). It is optional ("you may"), so a controller with no desirable target can decline;
 * the target is chosen when the ability is put on the stack (CR 603.3d).
 */
val AuraShards = card("Aura Shards") {
    manaCost = "{1}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature you control enters, you may destroy target artifact or enchantment."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY
        )
        optional = true
        val t = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "233"
        artist = "Ron Spencer"
        imageUri = "https://cards.scryfall.io/normal/front/d/f/df4039ef-af72-4267-ade9-fdb7c921279e.jpg?1562939873"
    }
}
