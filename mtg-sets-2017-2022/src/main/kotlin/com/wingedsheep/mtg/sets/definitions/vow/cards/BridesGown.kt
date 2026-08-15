package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Bride's Gown
 * {1}{W}
 * Artifact — Equipment
 *
 * Equipped creature gets +2/+0. It gets an additional +0/+2 and has first strike as long as an
 * Equipment named Groom's Finery is attached to a creature you control.
 * Equip {2}
 *
 * Wedding pair with [GroomsFinery]. The unconditional +2/+0 is a plain [ModifyStats] on the
 * equipped creature. The conditional half — additional +0/+2 and first strike — is two statics
 * gated by the same [Conditions] existence check: *anyone's* battlefield ([Player.Any]) holds an
 * Equipment subtyped Equipment, named "Groom's Finery", that is `attachedTo` a creature **you**
 * control (the controller predicate rides on the host filter, resolved against this Equipment's
 * controller by the `AttachedTo` predicate). Control of Groom's Finery itself is irrelevant — only
 * that it is attached to a creature you control — so the wording is honored literally.
 */
val BridesGown = card("Bride's Gown") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+0. It gets an additional +0/+2 and has first strike as " +
        "long as an Equipment named Groom's Finery is attached to a creature you control.\n" +
        "Equip {2}"

    staticAbility {
        ability = ModifyStats(2, 0, Filters.EquippedCreature)
    }
    staticAbility {
        condition = Exists(
            Player.Any,
            Zone.BATTLEFIELD,
            GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT)
                .named("Groom's Finery")
                .attachedTo(GameObjectFilter.Creature.youControl())
        )
        ability = ModifyStats(0, 2, Filters.EquippedCreature)
    }
    staticAbility {
        condition = Exists(
            Player.Any,
            Zone.BATTLEFIELD,
            GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT)
                .named("Groom's Finery")
                .attachedTo(GameObjectFilter.Creature.youControl())
        )
        ability = GrantKeyword(Keyword.FIRST_STRIKE, Filters.EquippedCreature)
    }
    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "4"
        artist = "Eric Deschamps"
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3e5dbbe-ca99-41a5-901d-511b9f3ccea6.jpg?1783924926"
    }
}
