package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.ManaSolver
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.apc.cards.BattlefieldForge
import com.wingedsheep.mtg.sets.definitions.fin.cards.StartingTown
import com.wingedsheep.mtg.sets.definitions.tla.AvatarTheLastAirbenderSet
import com.wingedsheep.mtg.sets.definitions.tla.cards.BadgermoleCub
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.basicLand
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Repro for the auto-tap bugs:
 *  - Starting Town's colored ability pays 1 life as part of the *cost* — auto-tap must
 *    still deduct it (bug: "Starting Town -> Autotapper does not give pain").
 *  - Badgermole Cub's "Whenever you tap a creature for mana, add an additional {G}" — the
 *    auto-tap solver must both know about and deliver the bonus (bug: "Autotapper does not
 *    know that Badgermole Cub gives extra mana").
 */
class AutoTapPainAndBonusReproTest : FunSpec({

    val TestForest = basicLand("Forest") {}

    // A green mana-dork creature (stands in for an earthbended land — a creature that taps
    // for mana, which is what actually triggers Badgermole Cub).
    val tapForGreenCreature = card("Tap-for-Green Elf") {
        manaCost = "{G}"
        colorIdentity = "G"
        typeLine = "Creature — Elf"
        power = 1
        toughness = 1
        oracleText = "{T}: Add {G}."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.AddMana(Color.GREEN, 1)
            manaAbility = true
        }
    }

    // A vanilla {G}{G} sorcery used to force an auto-tap of a {G}{G} cost.
    val greenGreenSpell = card("Green Green Test Spell") {
        manaCost = "{G}{G}"
        colorIdentity = "G"
        typeLine = "Sorcery"
        oracleText = "Draw a card."
        spell { effect = Effects.DrawCards(1) }
    }

    // A white one-drop used to force an auto-tap of a single colored pip.
    val whiteSpell = card("White Test Spell") {
        manaCost = "{W}"
        colorIdentity = "W"
        typeLine = "Sorcery"
        oracleText = "Draw a card."
        spell { effect = Effects.DrawCards(1) }
    }

    // A {2}{G} sorcery used to force an auto-tap with generic pips (the Surrak repro shape).
    val twoGenericGreenSpell = card("Generic Green Test Spell") {
        manaCost = "{2}{G}"
        colorIdentity = "G"
        typeLine = "Sorcery"
        oracleText = "Draw a card."
        spell { effect = Effects.DrawCards(1) }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + AvatarTheLastAirbenderSet.cards + listOf(
                TestForest, BadgermoleCub, StartingTown, BattlefieldForge,
                tapForGreenCreature, greenGreenSpell, whiteSpell, twoGenericGreenSpell
            )
        )
        return driver
    }

    fun createRegistry(): CardRegistry {
        val registry = CardRegistry()
        registry.register(
            TestCards.all + AvatarTheLastAirbenderSet.cards + listOf(
                TestForest, BadgermoleCub, StartingTown, BattlefieldForge,
                tapForGreenCreature, greenGreenSpell, whiteSpell, twoGenericGreenSpell
            )
        )
        return registry
    }

    test("Starting Town pain: auto-paying a colored spell deducts 1 life") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Forest" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        // Starting Town is the only source of white — the auto-tap solver must use its
        // "{T}, Pay 1 life: Add one mana of any color" ability, incurring the pain.
        val town = driver.putPermanentOnBattlefield(activePlayer, "Starting Town")
        val spell = driver.putCardInHand(activePlayer, "White Test Spell")

        val before = driver.getLifeTotal(activePlayer)
        val result = driver.castSpell(activePlayer, spell)
        result.isSuccess shouldBe true

        driver.isTapped(town) shouldBe true
        driver.getLifeTotal(activePlayer) shouldBe (before - 1)
    }

    test("Starting Town generic: auto-paying generic pips uses the free colorless ability, no life loss") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Forest" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        // The Surrak repro: Forest pays the {G}, the two Starting Towns pay the {2}.
        // Generic mana must flow through "{T}: Add {C}", not "{T}, Pay 1 life: Add one
        // mana of any color".
        val forest = driver.putLandOnBattlefield(activePlayer, "Forest")
        val town1 = driver.putPermanentOnBattlefield(activePlayer, "Starting Town")
        val town2 = driver.putPermanentOnBattlefield(activePlayer, "Starting Town")
        val spell = driver.putCardInHand(activePlayer, "Generic Green Test Spell")

        val before = driver.getLifeTotal(activePlayer)
        val result = driver.castSpell(activePlayer, spell)
        result.isSuccess shouldBe true

        driver.isTapped(forest) shouldBe true
        driver.isTapped(town1) shouldBe true
        driver.isTapped(town2) shouldBe true
        driver.getLifeTotal(activePlayer) shouldBe before
    }

    test("Battlefield Forge generic: auto-paying generic pips uses the free colorless ability, no damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Forest" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        // Same shape as Starting Town but with pain modeled as a self-damage side effect
        // ("{T}: Add {R} or {W}. This land deals 1 damage to you.") instead of a cost atom.
        val forest = driver.putLandOnBattlefield(activePlayer, "Forest")
        val forge1 = driver.putPermanentOnBattlefield(activePlayer, "Battlefield Forge")
        val forge2 = driver.putPermanentOnBattlefield(activePlayer, "Battlefield Forge")
        val spell = driver.putCardInHand(activePlayer, "Generic Green Test Spell")

        val before = driver.getLifeTotal(activePlayer)
        val result = driver.castSpell(activePlayer, spell)
        result.isSuccess shouldBe true

        driver.isTapped(forest) shouldBe true
        driver.isTapped(forge1) shouldBe true
        driver.isTapped(forge2) shouldBe true
        driver.getLifeTotal(activePlayer) shouldBe before
    }

    test("Battlefield Forge colored pip: pain still applies when the colored ability is required") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Forest" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        // The forge is the only white source, so its painful "{T}: Add {R} or {W}" ability
        // must be used — and its self-damage side effect must still land.
        val forge = driver.putPermanentOnBattlefield(activePlayer, "Battlefield Forge")
        val spell = driver.putCardInHand(activePlayer, "White Test Spell")

        val before = driver.getLifeTotal(activePlayer)
        val result = driver.castSpell(activePlayer, spell)
        result.isSuccess shouldBe true

        driver.isTapped(forge) shouldBe true
        driver.getLifeTotal(activePlayer) shouldBe (before - 1)
    }

    test("Badgermole Cub bonus: auto-paying {G}{G} needs only one green source") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Forest" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        driver.putCreatureOnBattlefield(activePlayer, "Badgermole Cub")
        val elf = driver.putCreatureOnBattlefield(activePlayer, "Tap-for-Green Elf")
        driver.removeSummoningSickness(elf)
        val spell = driver.putCardInHand(activePlayer, "Green Green Test Spell")

        // Only one green source (the Elf), but Badgermole Cub's bonus supplies the 2nd {G}.
        val result = driver.castSpell(activePlayer, spell)
        result.isSuccess shouldBe true
        driver.isTapped(elf) shouldBe true
    }

    test("Badgermole Cub bonus for an EARTHBENDED LAND: solver knows and auto-pays {G}{G}") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40, "Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.activePlayer!!

        // Earthbend a Forest into a creature-land via Earthbending Lesson.
        val forest = driver.putLandOnBattlefield(you, "Forest")
        val lesson = driver.putCardInHand(you, "Earthbending Lesson")
        driver.giveMana(you, Color.GREEN, 4)
        driver.castSpell(you, lesson, listOf(forest)).isSuccess shouldBe true
        driver.bothPass()

        // Badgermole Cub in play (static only — putCreatureOnBattlefield doesn't fire its ETB).
        driver.putCreatureOnBattlefield(you, "Badgermole Cub")

        // The earthbended Forest is the ONLY green source. Tapping it (a creature now) should
        // yield {G} + Badgermole Cub's bonus {G} — the autotapper must count both.
        val solver = ManaSolver(createRegistry())
        solver.canPay(driver.state, you, ManaCost.parse("{G}{G}")) shouldBe true

        val spell = driver.putCardInHand(you, "Green Green Test Spell")
        driver.castSpell(you, spell).isSuccess shouldBe true
        driver.isTapped(forest) shouldBe true
    }
})
