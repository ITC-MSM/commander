package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Groom's Finery
 * {1}{B}
 * Artifact — Equipment
 *
 * Equipped creature gets +2/+0. It gets an additional +0/+2 and has deathtouch as long as an
 * Equipment named Bride's Gown is attached to a creature you control.
 * Equip {2}
 *
 * Mirror of [BridesGown]: the conditional half grants deathtouch (not first strike) and keys off
 * an Equipment named "Bride's Gown" being `attachedTo` a creature you control. See BridesGown for
 * the shape of the `Exists(Player.Any, …, attachedTo(Creature.youControl()))` gate.
 */
val GroomsFinery = card("Groom's Finery") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+0. It gets an additional +0/+2 and has deathtouch as " +
        "long as an Equipment named Bride's Gown is attached to a creature you control.\n" +
        "Equip {2}"

    staticAbility {
        ability = ModifyStats(2, 0, Filters.EquippedCreature)
    }
    staticAbility {
        condition = Exists(
            Player.Any,
            Zone.BATTLEFIELD,
            GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT)
                .named("Bride's Gown")
                .attachedTo(GameObjectFilter.Creature.youControl())
        )
        ability = ModifyStats(0, 2, Filters.EquippedCreature)
    }
    staticAbility {
        condition = Exists(
            Player.Any,
            Zone.BATTLEFIELD,
            GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT)
                .named("Bride's Gown")
                .attachedTo(GameObjectFilter.Creature.youControl())
        )
        ability = GrantKeyword(Keyword.DEATHTOUCH, Filters.EquippedCreature)
    }
    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "117"
        artist = "Eric Deschamps"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/43bc65cf-4444-4db3-9bb3-a7d91e560470.jpg?1783924860"
    }
}
