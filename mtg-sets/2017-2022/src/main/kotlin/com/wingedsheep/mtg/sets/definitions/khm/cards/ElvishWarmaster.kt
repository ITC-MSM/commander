package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Elvish Warmaster
 * {1}{G}
 * Creature — Elf Warrior
 * 2/2
 * Whenever one or more other Elves you control enter, create a 1/1 green Elf Warrior creature token. This ability triggers only once each turn.
 * {5}{G}{G}: Elves you control get +2/+2 and gain deathtouch until end of turn.
 *
 * Two details carry the printed text:
 *
 *  - **"one or more other Elves"** is the batching [Triggers.OneOrMorePermanentsEnter] with
 *    `excludeSource = true`, so a single mass-entry makes one token, not one per Elf, and the
 *    Warmaster's own arrival never counts.
 *  - **"This ability triggers only once each turn"** is the builder's `oncePerTurn` flag, which the
 *    engine's TriggerDetector enforces per `(sourceId, abilityId)` and resets each turn.
 *
 * The activated pump is one [Patterns.Group.pumpAndGrantToAll] pass for the same reason as every
 * other "get +N/+N and gain <keyword>" line: the sentence names its group once.
 */
val ElvishWarmaster = card("Elvish Warmaster") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    oracleText = "Whenever one or more other Elves you control enter, create a 1/1 green Elf Warrior creature token. This ability triggers only once each turn.\n" +
        "{5}{G}{G}: Elves you control get +2/+2 and gain deathtouch until end of turn."
    power = 2
    toughness = 2

    // Whenever one or more other Elves you control enter, create a 1/1 green Elf Warrior token.
    triggeredAbility {
        trigger = Triggers.OneOrMorePermanentsEnter(
            GameObjectFilter.Permanent.withSubtype(Subtype.ELF).youControl(),
            excludeSource = true
        )
        oncePerTurn = true
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Elf", "Warrior")
        )
    }

    // {5}{G}{G}: Elves you control get +2/+2 and gain deathtouch until end of turn.
    activatedAbility {
        cost = Costs.Mana("{5}{G}{G}")
        effect = Patterns.Group.pumpAndGrantToAll(
            power = 2,
            toughness = 2,
            keyword = Keyword.DEATHTOUCH,
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype(Subtype.ELF).youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "167"
        artist = "Alexander Mokhov"
        imageUri = "https://cards.scryfall.io/normal/front/5/8/58da074a-a776-4e3f-be04-9e7f18320ae1.jpg"
    }
}
