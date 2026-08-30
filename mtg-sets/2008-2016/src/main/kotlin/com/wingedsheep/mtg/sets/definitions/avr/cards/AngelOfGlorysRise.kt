package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Angel of Glory's Rise — Avacyn Restored #1
 * {5}{W}{W} · Creature — Angel · 4/6
 *
 * Flying
 * When this creature enters, exile all Zombies, then return all Human creature cards from your
 * graveyard to the battlefield.
 *
 * The two halves are sequenced inside one [Effects.Composite] so the exile finishes before the
 * reanimation begins — a Human Zombie is exiled by the first half and therefore never seen by the
 * second, which is the printed ordering.
 *
 * "All Zombies" is a bare tribal noun, so it names *permanents* with the subtype (a Zombie
 * artifact or land counts), not only creatures; "Human creature cards" is explicit about the card
 * type and keeps [GameObjectFilter.Creature] as its base. The exile half is a per-permanent sweep
 * ([Effects.ForEachInGroup] with `EffectTarget.Self` bound to each member); the return half is the
 * gather → move pipeline the corpus writes for a mass graveyard return.
 */
val AngelOfGlorysRise = card("Angel of Glory's Rise") {
    manaCost = "{5}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 4
    toughness = 6
    oracleText = "Flying\n" +
        "When this creature enters, exile all Zombies, then return all Human creature cards from your " +
        "graveyard to the battlefield."
    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Permanent.withSubtype("Zombie")),
                Effects.Exile(EffectTarget.Self)
            ),
            Effects.Composite(
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        Zone.GRAVEYARD,
                        Player.You,
                        GameObjectFilter.Creature.withSubtype("Human")
                    ),
                    storeAs = "graveyard_lands",
                ),
                MoveCollectionEffect(
                    from = "graveyard_lands",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                ),
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "1"
        artist = "James Ryman"
        flavorText = "\"Justice isn't done until undeath is undone.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a8be765-0949-491c-875c-0385fb83e4b9.jpg?1783940744"
    }
}
