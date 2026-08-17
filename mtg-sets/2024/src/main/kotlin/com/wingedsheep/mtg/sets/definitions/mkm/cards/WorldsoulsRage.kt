package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.AnyTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Worldsoul's Rage — Murders at Karlov Manor #244
 * {X}{R}{G} · Sorcery
 *
 * Worldsoul's Rage deals X damage to any target. Put up to X land cards from your hand and/or
 * graveyard onto the battlefield tapped.
 *
 * One X buys both halves, which is why the card is a ramp payoff rather than a burn spell: X=4 is a
 * four-point Rolling Thunder *and* four lands, so every point spent on the removal is also a point of
 * mana development. The lands come from either zone, so a stocked graveyard (fetch lands, Aftermath
 * Analyst) fuels it as well as a hand full of them.
 *
 * The two halves are one resolution, in printed order — damage first, then the lands. That ordering is
 * observable: the damage can kill a creature whose death fills the graveyard, but not in time to be
 * selected here, because the collection is gathered after the damage has already been dealt.
 *
 * "From your hand **and/or** graveyard" is a single decision over both zones, not two prompts:
 * `CardSource.FromMultipleZones` gathers them together and the client renders the choice with zone
 * labels (`MultiZoneSelectionUI`). A flat list would leave the player unable to tell a land in hand
 * from the same land in the graveyard.
 *
 * `SelectionMode.ChooseUpTo(DynamicAmount.XValue)` carries the printed "up to X" — fewer than X is
 * legal, and X=0 makes the whole spell a legal cast that deals no damage and puts no lands. The X here
 * is `XValue`, the transient cast-time payment read during this spell's own resolution, not the
 * object-scoped `CastX` a permanent would need.
 *
 * `ZonePlacement.Tapped` on the move step is what "onto the battlefield tapped" means — the same
 * placement MKM's own Aftermath Analyst uses for its graveyard-land return. Nothing here is a land
 * *play*, so the once-per-turn land drop is untouched and any number of lands can arrive.
 */
val WorldsoulsRage = card("Worldsoul's Rage") {
    manaCost = "{X}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Sorcery"
    oracleText = "Worldsoul's Rage deals X damage to any target. Put up to X land cards from your " +
        "hand and/or graveyard onto the battlefield tapped."

    spell {
        val victim = target("any target", AnyTarget())
        effect = Effects.Composite(
            Effects.DealXDamage(victim),
            GatherCardsEffect(
                source = CardSource.FromMultipleZones(
                    zones = listOf(Zone.HAND, Zone.GRAVEYARD),
                    player = Player.You,
                    filter = GameObjectFilter.Land
                ),
                storeAs = "worldsoulLands"
            ),
            SelectFromCollectionEffect(
                from = "worldsoulLands",
                selection = SelectionMode.ChooseUpTo(DynamicAmount.XValue),
                storeSelected = "worldsoulLandsChosen"
            ),
            MoveCollectionEffect(
                from = "worldsoulLandsChosen",
                destination = CardDestination.ToZone(
                    zone = Zone.BATTLEFIELD,
                    placement = ZonePlacement.Tapped
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "244"
        artist = "Lius Lasahido"
        flavorText = "\"Hoarding supplies, profiting from death and destruction, colluding with the " +
            "invaders—every one of them betrayed Ravnica in its time of greatest need. They all " +
            "deserved to die. And I'm not done yet!\""
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc3340bd-1d8c-4c21-a59d-e092fcbe02e3.jpg?1783912832"
    }
}
