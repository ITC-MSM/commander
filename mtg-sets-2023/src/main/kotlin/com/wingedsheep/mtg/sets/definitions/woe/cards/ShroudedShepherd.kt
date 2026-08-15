package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shrouded Shepherd // Cleave Shadows
 * {1}{W}
 * Creature — Spirit Warrior
 * 2/2
 *
 * When this creature enters, target creature you control gets +2/+2 until end of turn.
 *
 * Adventure: Cleave Shadows — {1}{B}, Sorcery — Adventure
 * Creatures your opponents control get -1/-1 until end of turn.
 *
 * The Adventure's mass debuff is a resolution-time [Effects.ForEachInGroup] over
 * [GroupFilter.AllCreaturesOpponentsControl], applying a self-targeted [Effects.ModifyStats] to
 * each. (CR 715: Adventure cards. Casting the Adventure exiles the card on resolution and lets the
 * caster cast it as the creature spell while it remains in exile.)
 */
val ShroudedShepherd = card("Shrouded Shepherd") {
    manaCost = "{1}{W}"
    colorIdentity = "WB"
    typeLine = "Creature — Spirit Warrior"
    oracleText = "When this creature enters, target creature you control gets +2/+2 until end of turn."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.ModifyStats(2, 2, t)
    }

    adventure("Cleave Shadows") {
        manaCost = "{1}{B}"
        typeLine = "Sorcery — Adventure"
        oracleText = "Creatures your opponents control get -1/-1 until end of turn. " +
            "(Then exile this card. You may cast the creature later from exile.)"
        spell {
            effect = Effects.ForEachInGroup(
                GroupFilter.AllCreaturesOpponentsControl,
                Effects.ModifyStats(-1, -1, EffectTarget.Self)
            )
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "236"
        artist = "Randy Vargas"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab03c342-2bf4-41bf-8bb8-472d978d238a.jpg?1783915061"
    }
}
