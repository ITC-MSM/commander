package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * The Neutrinos
 * {2}{R}{W}
 * Legendary Creature — Elf Rebel
 * 2/4
 *
 * Flying
 * Alliance — Whenever another creature you control enters, The Neutrinos get
 * +1/+0 until end of turn.
 * Whenever The Neutrinos attack, exile up to one target creature you own, then
 * return it to the battlefield under your control tapped and attacking.
 */
val TheNeutrinos = card("The Neutrinos") {
    manaCost = "{2}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Elf Rebel"
    oracleText = "Flying\nAlliance — Whenever another creature you control enters, The Neutrinos get +1/+0 until end of turn.\nWhenever The Neutrinos attack, exile up to one target creature you own, then return it to the battlefield under your control tapped and attacking."
    power = 2
    toughness = 4

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "Alliance — Whenever another creature you control enters, The Neutrinos get +1/+0 until end of turn."
    }

    // Exile then return tapped and attacking (ZonePlacement.TappedAndAttacking) — adds
    // the returned creature to combat as a new attacker, like the Sneak / blink-attacking idiom.
    triggeredAbility {
        trigger = Triggers.Attacks
        val creature = target(
            "up to one target creature you own",
            TargetCreature(optional = true, filter = TargetFilter(GameObjectFilter.Creature.ownedByYou()))
        )
        effect = Effects.Move(creature, Zone.EXILE)
            .then(Effects.Move(creature, Zone.BATTLEFIELD, placement = ZonePlacement.TappedAndAttacking))
        description = "Whenever The Neutrinos attack, exile up to one target creature you own, then return it to the battlefield under your control tapped and attacking."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "160"
        artist = "Brandon L. Hunt"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/1308dadc-08a9-40bd-98a4-fb66d792e27d.jpg?1771587028"
    }
}
