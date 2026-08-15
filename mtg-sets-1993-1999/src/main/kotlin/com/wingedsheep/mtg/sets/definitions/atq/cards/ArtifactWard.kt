package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.CantBeTargetedBySourceTypeAbilities
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.PreventDamage
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.events.SourceFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Artifact Ward
 * {W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature can't be blocked by artifact creatures.
 * Prevent all damage that would be dealt to enchanted creature by artifact sources.
 * Enchanted creature can't be the target of abilities from artifact sources.
 *
 * Three clauses, all keyed to the enchanted creature:
 *  - [CantBeBlockedBy] artifact creatures (block restriction),
 *  - a continuous [PreventDamage] replacement (CR 615) with recipient = the enchanted creature and
 *    source = any artifact, and
 *  - [CantBeTargetedBySourceTypeAbilities] with [CardType.ARTIFACT] — hexproof keyed to the source
 *    *card type* (blocks abilities from artifact sources, regardless of who controls them).
 *    Deliberately NOT modelled as protection from artifacts, which would also stop the creature from
 *    being equipped/enchanted by artifacts; this clause does not.
 */
val ArtifactWard = card("Artifact Ward") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature can't be blocked by artifact creatures.\n" +
        "Prevent all damage that would be dealt to enchanted creature by artifact sources.\n" +
        "Enchanted creature can't be the target of abilities from artifact sources."

    auraTarget = Targets.Creature

    // Can't be blocked by artifact creatures.
    staticAbility {
        ability = CantBeBlockedBy(
            blockerFilter = GameObjectFilter.ArtifactCreature,
            filter = GroupFilter.attachedCreature()
        )
    }

    // Prevent all damage dealt to the enchanted creature by artifact sources.
    replacementEffect(
        PreventDamage(
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.EnchantedCreature,
                source = SourceFilter.Matching(GameObjectFilter.Artifact)
            )
        )
    )

    // Can't be the target of abilities from artifact sources.
    staticAbility {
        ability = CantBeTargetedBySourceTypeAbilities(CardType.ARTIFACT, GroupFilter.attachedCreature())
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Douglas Shuler"
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3a5101a-ec66-4658-950c-9ad49c29b836.jpg?1562932896"
    }
}
