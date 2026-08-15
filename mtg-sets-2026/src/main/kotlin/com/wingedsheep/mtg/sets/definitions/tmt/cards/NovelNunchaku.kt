package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Novel Nunchaku
 * {2}{G}
 * Artifact — Equipment
 *
 * When this Equipment enters, attach it to target creature you control. When you
 * do, equipped creature fights up to one target creature an opponent controls.
 * Equipped creature gets +1/+1 and has trample.
 * Equip {3}
 */
val NovelNunchaku = card("Novel Nunchaku") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, attach it to target creature you control. When you do, equipped creature fights up to one target creature an opponent controls. (Each deals damage equal to its power to the other.)\nEquipped creature gets +1/+1 and has trample.\nEquip {3} ({3}: Attach to target creature you control. Equip only as a sorcery.)"

    // ETB: attach to a creature you control, then that (now equipped) creature fights
    // up to one opponent creature — the Chelonian Tackle "X then fights up to one" shape.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val yourCreature = target(
            "target creature you control",
            TargetCreature(filter = TargetFilter.CreatureYouControl)
        )
        val opponentCreature = target(
            "up to one target creature an opponent controls",
            TargetCreature(optional = true, filter = TargetFilter.CreatureOpponentControls)
        )
        effect = Effects.AttachEquipment(yourCreature)
            .then(Effects.Fight(yourCreature, opponentCreature))
        description = "When this Equipment enters, attach it to target creature you control. When you do, equipped creature fights up to one target creature an opponent controls."
    }

    staticAbility {
        ability = ModifyStats(1, 1, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, Filters.EquippedCreature)
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "127"
        artist = "Marina Ortega Lorente"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f37ef012-c566-4d16-acf8-6079244907be.jpg?1771502723"
    }
}
