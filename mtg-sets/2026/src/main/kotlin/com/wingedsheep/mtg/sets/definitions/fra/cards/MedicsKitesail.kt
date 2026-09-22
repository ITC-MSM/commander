package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggeredAbility

val MedicsKitesail = card("Medic's Kitesail") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+0 and has flying and \"Whenever this creature attacks, you gain 1 life.\"\n" +
        "Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(+1, +0, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.Attacks.event,
                binding = Triggers.Attacks.binding,
                effect = Effects.GainLife(1)
            ),
            filter = Filters.EquippedCreature
        )
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "173"
        artist = "Paolo Parente"
        flavorText = "An apothecary on wings."
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8b07409a-1dce-461d-95e4-1130521ff4c4.jpg?1789556945"
        inBooster = false
    }
}
