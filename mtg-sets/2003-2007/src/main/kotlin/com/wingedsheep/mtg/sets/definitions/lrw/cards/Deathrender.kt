package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.ConditionalOnCollectionEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Deathrender — Lorwyn #255
 * {4} · Artifact — Equipment
 *
 * Equipped creature gets +2/+2.
 * Whenever equipped creature dies, you may put a creature card from your hand onto the
 * battlefield and attach this Equipment to it.
 * Equip {2}
 *
 * The trigger is Forebear's Blade's — a battlefield-leave to the graveyard bound with
 * [TriggerBinding.ATTACHED], so it reads the creature the Equipment was on rather than the
 * Equipment itself. Deathrender is still attached at the moment its host dies, which is what the
 * binding needs; the state-based unattach happens afterwards.
 *
 * The cheat-into-play half is the [Patterns.Hand.putFromHand] pipeline (Incandescent Soulstoke,
 * Through the Breach), where the `ChooseUpTo(1)` selection *is* the printed "you may" — a player
 * who declines simply selects nothing. The attach then runs under
 * [ConditionalOnCollectionEffect] so an empty selection doesn't reach it, and names the moved
 * card through `PipelineTarget("putting", 0)` — the collection carries the card's id across the
 * hand-to-battlefield move, so the attach lands on the permanent that just entered.
 *
 * The ruling about a creature that can't be equipped (protection from artifacts) needs no
 * card-level guard: [Effects.AttachEquipment] refuses an illegal host, so the creature enters and
 * Deathrender stays where it is.
 */
val Deathrender = card("Deathrender") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+2.\n" +
        "Whenever equipped creature dies, you may put a creature card from your hand onto the " +
        "battlefield and attach this Equipment to it.\n" +
        "Equip {2}"

    staticAbility {
        ability = ModifyStats(2, 2, Filters.EquippedCreature)
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(to = Zone.GRAVEYARD, binding = TriggerBinding.ATTACHED)
        effect = Patterns.Hand.putFromHand(
            filter = GameObjectFilter.Creature,
            prompt = "Put a creature card from your hand onto the battlefield"
        ).then(
            ConditionalOnCollectionEffect(
                collection = "putting",
                ifNotEmpty = Effects.AttachEquipment(EffectTarget.PipelineTarget("putting", 0))
            )
        )
        description = "Whenever equipped creature dies, you may put a creature card from your " +
            "hand onto the battlefield and attach this Equipment to it."
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "255"
        artist = "Martina Pilcerova"
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bd836fe0-932c-4d5c-9a52-94fc423c3d57.jpg?1783942853"
        ruling("2007-10-01", "If the creature you put onto the battlefield can't be equipped by Deathrender (due to protection from artifacts, for example), the creature enters but Deathrender remains unattached.")
    }
}
