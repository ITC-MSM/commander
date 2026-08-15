package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantCardType
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessStatic
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Zoetic Glyph
 * {2}{U}
 * Enchantment — Aura
 * Enchant artifact
 * Enchanted artifact is a Golem creature with base power and toughness 5/4 in addition to its other types.
 * When this Aura is put into a graveyard from the battlefield, discover 3.
 */
val ZoeticGlyph = card("Zoetic Glyph") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant artifact\nEnchanted artifact is a Golem creature with base power and toughness 5/4 in addition to its other types.\nWhen this Aura is put into a graveyard from the battlefield, discover 3."

    auraTarget = Targets.Artifact

    staticAbility {
        ability = GrantCardType("CREATURE", filter = GroupFilter.attachedCreature())
    }
    staticAbility {
        ability = GrantSubtype("Golem", filter = GroupFilter.attachedCreature())
    }
    staticAbility {
        ability = SetBasePowerToughnessStatic(5, 4)
    }

    triggeredAbility {
        trigger = Triggers.PutIntoGraveyardFromBattlefield
        effect = Effects.Discover(3)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "86"
        artist = "Yeong-Hao Han"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2f498ac-179e-4055-9b83-97bcc5ab1bb9.jpg?1782694541"
    }
}
