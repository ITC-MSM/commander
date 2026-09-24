package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Chant of Vitu-Ghazi — Ravnica: City of Guilds #7
 * {6}{W}{W} · Instant
 *
 * Convoke
 * Prevent all damage that would be dealt by creatures this turn. You gain life equal to the damage
 * prevented this way.
 *
 * One source-side prevention shield ([Effects.PreventAllDamageFrom]) over every creature, with
 * `gainLifeFromPrevented`: *all* damage, not only combat damage — a creature's pinging ability is
 * stopped too — and the creature group is re-read each time damage would be dealt, so a creature
 * that enters later this turn is covered. The life is the amount the shield actually prevents, as
 * it prevents it ("you gain life each time that shield prevents 1 or more damage"): a combat damage
 * step is one simultaneous event and gives one combined gain, while each noncombat instance gives
 * its own. Damage some other effect has already prevented, and damage that can't be prevented, is
 * never counted.
 */
val ChantOfVituGhazi = card("Chant of Vitu-Ghazi") {
    manaCost = "{6}{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this " +
        "spell pays for {1} or one mana of that creature's color.)\n" +
        "Prevent all damage that would be dealt by creatures this turn. You gain life equal to the damage " +
        "prevented this way."

    keywords(Keyword.CONVOKE)

    spell {
        effect = Effects.PreventAllDamageFrom(
            GroupFilter(GameObjectFilter.Creature),
            gainLifeFromPrevented = true
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "7"
        artist = "Stephen Tappin"
        imageUri = "https://cards.scryfall.io/normal/front/2/3/23c22d01-1d14-4f73-a2db-a77f96a9a0e3.jpg?1783943705"
        ruling("2005-10-01", "The damage prevention shield lasts for the rest of the turn, and you gain life each time that shield prevents 1 or more damage.")
    }
}
