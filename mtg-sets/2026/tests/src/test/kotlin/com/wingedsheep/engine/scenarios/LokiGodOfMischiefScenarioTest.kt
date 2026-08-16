package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lea.cards.ProdigalSorcerer
import com.wingedsheep.mtg.sets.definitions.ons.cards.ContestedCliffs
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Loki, God of Mischief (MSH #65) — {1}{U} Legendary Creature — God Sorcerer Villain, 2/1.
 *
 * "Whenever a player or permanent becomes the target of an ability you control, draw a card.
 *  This ability triggers only once each turn."
 *
 * Prodigal Sorcerer ("{T}: This creature deals 1 damage to any target") is the driver for every
 * case: its `AnyTarget` slot can be filled with a player *or* a permanent, which is exactly the two
 * halves of Loki's wording, and it is an activated ability so it is on the right side of
 * `abilitiesOnly`. Lightning Bolt supplies the spell counter-example with the same target choice.
 */
class LokiGodOfMischiefScenarioTest : FunSpec({

    val pingAbility = ProdigalSorcerer.activatedAbilities.first().id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Pass until the stack is empty, so triggers and the ability itself all resolve. */
    fun resolveAll(driver: GameTestDriver) {
        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard < 12) {
            driver.bothPass()
            guard++
        }
    }

    fun handSize(driver: GameTestDriver, player: EntityId) = driver.state.getHand(player).size

    fun ping(driver: GameTestDriver, sorcerer: EntityId, target: ChosenTarget, by: EntityId) =
        driver.submit(
            ActivateAbility(
                playerId = by,
                sourceId = sorcerer,
                abilityId = pingAbility,
                targets = listOf(target)
            )
        )

    test("an ability you control targeting a PLAYER draws a card") {
        val driver = createDriver()
        driver.putCreatureOnBattlefield(driver.player1, "Loki, God of Mischief")
        val sorcerer = driver.putCreatureOnBattlefield(driver.player1, "Prodigal Sorcerer")
        driver.removeSummoningSickness(sorcerer)

        val before = handSize(driver, driver.player1)
        ping(driver, sorcerer, ChosenTarget.Player(driver.player2), driver.player1)
            .error shouldBe null
        resolveAll(driver)

        withClue("the player half of \"a player or permanent\"") {
            handSize(driver, driver.player1) shouldBe before + 1
        }
    }

    test("an ability you control targeting a PERMANENT draws a card") {
        val driver = createDriver()
        driver.putCreatureOnBattlefield(driver.player1, "Loki, God of Mischief")
        val sorcerer = driver.putCreatureOnBattlefield(driver.player1, "Prodigal Sorcerer")
        val victim = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        driver.removeSummoningSickness(sorcerer)

        val before = handSize(driver, driver.player1)
        ping(driver, sorcerer, ChosenTarget.Permanent(victim), driver.player1)
            .error shouldBe null
        resolveAll(driver)

        handSize(driver, driver.player1) shouldBe before + 1
    }

    test("a SPELL you control targeting a player does not draw") {
        val driver = createDriver()
        driver.putCreatureOnBattlefield(driver.player1, "Loki, God of Mischief")
        driver.giveMana(driver.player1, Color.RED, 1)
        val bolt = driver.putCardInHand(driver.player1, "Lightning Bolt")

        val before = handSize(driver, driver.player1)
        driver.castSpell(driver.player1, bolt, listOf(driver.player2)).error shouldBe null
        resolveAll(driver)

        withClue("Loki reads \"an ability\", so a spell must not trigger it (only the Bolt left hand)") {
            handSize(driver, driver.player1) shouldBe before - 1
        }
    }

    test("an OPPONENT's ability targeting a player does not draw") {
        val driver = createDriver()
        driver.putCreatureOnBattlefield(driver.player1, "Loki, God of Mischief")
        val sorcerer = driver.putCreatureOnBattlefield(driver.player2, "Prodigal Sorcerer")
        driver.removeSummoningSickness(sorcerer)

        val before = handSize(driver, driver.player1)
        // Hand priority to the opponent so they may activate at instant speed.
        driver.passPriority(driver.player1)
        ping(driver, sorcerer, ChosenTarget.Player(driver.player1), driver.player2)
            .error shouldBe null
        resolveAll(driver)

        withClue("\"an ability you control\" excludes the opponent's ability") {
            handSize(driver, driver.player1) shouldBe before
        }
    }

    test("two targeting abilities in one turn draw only once") {
        val driver = createDriver()
        driver.putCreatureOnBattlefield(driver.player1, "Loki, God of Mischief")
        val first = driver.putCreatureOnBattlefield(driver.player1, "Prodigal Sorcerer")
        val second = driver.putCreatureOnBattlefield(driver.player1, "Prodigal Sorcerer")
        driver.removeSummoningSickness(first)
        driver.removeSummoningSickness(second)

        val before = handSize(driver, driver.player1)
        ping(driver, first, ChosenTarget.Player(driver.player2), driver.player1)
            .error shouldBe null
        resolveAll(driver)
        ping(driver, second, ChosenTarget.Player(driver.player2), driver.player1)
            .error shouldBe null
        resolveAll(driver)

        withClue("\"This ability triggers only once each turn\"") {
            handSize(driver, driver.player1) shouldBe before + 1
        }
    }

    test("one ability targeting TWO things still draws only once") {
        val driver = createDriver()
        driver.putCreatureOnBattlefield(driver.player1, "Loki, God of Mischief")
        val beast = driver.putCreatureOnBattlefield(driver.player1, "Trample Beast")
        val victim = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        val cliffs = driver.putLandOnBattlefield(driver.player1, "Contested Cliffs")
        driver.giveMana(driver.player1, Color.RED, 1)
        driver.giveMana(driver.player1, Color.GREEN, 1)

        val before = handSize(driver, driver.player1)
        // Contested Cliffs' fight ability targets two permanents at once, so a single ability
        // announcement emits two BecomesTargetEvents in one batch.
        driver.submit(
            ActivateAbility(
                playerId = driver.player1,
                sourceId = cliffs,
                abilityId = ContestedCliffs.activatedAbilities[1].id,
                targets = listOf(ChosenTarget.Permanent(beast), ChosenTarget.Permanent(victim))
            )
        ).error shouldBe null
        resolveAll(driver)

        withClue("two targets on one ability are still one \"triggers only once each turn\" fire") {
            handSize(driver, driver.player1) shouldBe before + 1
        }
    }

    test("a real TRIGGERED ability you control targeting a permanent draws a card") {
        val driver = createDriver()
        driver.putCreatureOnBattlefield(driver.player1, "Loki, God of Mischief")
        val victim = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        driver.giveMana(driver.player1, Color.BLUE, 3)
        val manOWar = driver.putCardInHand(driver.player1, "Man-o'-War")

        val before = handSize(driver, driver.player1)
        driver.castSpell(driver.player1, manOWar).error shouldBe null
        // Resolve the creature; its enters trigger goes on the stack and asks for its target.
        var guard = 0
        while (driver.pendingDecision == null && guard < 3) {
            driver.bothPass()
            guard++
        }
        driver.pendingDecision.shouldNotBeNull()
        driver.submitTargetSelection(driver.player1, listOf(victim)).error shouldBe null
        resolveAll(driver)

        withClue("a triggered ability is an ability too (Man-o'-War left hand, Loki's draw replaced it)") {
            handSize(driver, driver.player1) shouldBe before
        }
    }

    test("the printed characteristics match the card") {
        val loki = TestCards.all.first { it.name == "Loki, God of Mischief" }
        loki.manaCost shouldBe ManaCost.parse("{1}{U}")
        loki.creatureStats shouldBe CreatureStats(2, 1)
        loki.script.triggeredAbilities.single().oncePerTurn shouldBe true
    }
})
